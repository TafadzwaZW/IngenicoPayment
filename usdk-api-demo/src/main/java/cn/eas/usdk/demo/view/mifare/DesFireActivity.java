package cn.eas.usdk.demo.view.mifare;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.mifare.ApduStyleMode;
import com.usdk.apiservice.aidl.mifare.DesFireInitData;
import com.usdk.apiservice.aidl.mifare.MifareKey;
import com.usdk.apiservice.aidl.mifare.UDesFireManager;
import com.usdk.apiservice.aidl.mifare.UMifareKeyManager;
import com.usdk.apiservice.aidl.rfreader.OnPassAndActiveListener;
import com.usdk.apiservice.aidl.rfreader.URFReader;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class DesFireActivity extends BaseDeviceActivity {
    private static final byte[] MAIN_KEY = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final byte[] APP1_KEY = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final int APP1_ID = 1;

    private static List<SpinnerItem> apduStyleModeList = new LinkedList<>();
    static {
        apduStyleModeList.add(new SpinnerItem(ApduStyleMode.NATIVE_MODE, "Native Mode"));
        apduStyleModeList.add(new SpinnerItem(ApduStyleMode.WRAPPING_MODE, "Wrapping Mode"));
    }
    private static List<SpinnerItem> chainingModeList = new LinkedList<>();
    static {
        chainingModeList.add(new SpinnerItem(MifareKey.CHAINING_CBC_MODE, "CBC Mode"));
        chainingModeList.add(new SpinnerItem(MifareKey.CHAINING_DESFIRE_CBCMODE, "Desfire CBC Mode"));
    }

    private UDesFireManager desFireManager;
    private URFReader rfReader;
    private UMifareKeyManager mifareKeyManager;

    private int apduStyleMode = ApduStyleMode.NATIVE_MODE;
    private int chainingMode = MifareKey.CHAINING_CBC_MODE;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_desfire);
        setTitle("DesFire Module");
        initSpinner();
    }

    protected void initDeviceInstance() {
        desFireManager = DeviceHelper.me().getDesFireManager(DemoConfig.RF_DEVICE_NAME);
        rfReader = DeviceHelper.me().getRFReader(DemoConfig.RF_DEVICE_NAME);
        mifareKeyManager = DeviceHelper.me().getMifareKeyManager();
    }

    private void initSpinner() {
        NiceSpinner apduStyleModeSpinner = findViewById(R.id.apduStyleModeSpinner);
        apduStyleModeSpinner.attachDataSource(apduStyleModeList);
        apduStyleModeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                apduStyleMode = apduStyleModeList.get(position).getValue();
            }
        });

        NiceSpinner chainingModeSpinner = findViewById(R.id.chainingModeSpinner);
        chainingModeSpinner.attachDataSource(chainingModeList);
        chainingModeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                chainingMode = chainingModeList.get(position).getValue();
            }
        });
    }

    public void searchAndActivate(View v) {
        try {
            rfReader.searchCardAndActivate(new OnPassAndActiveListener.Stub() {
                @Override
                public void onActivate(byte[] response) throws RemoteException {
                    outputText("=> onActivate | responseData = " + BytesUtil.bytes2HexString(response));
                }

                @Override
                public void onFail(int i) throws RemoteException {
                    outputRedText("=> onFail: " + getErrorDetail(i));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopSearch(View v) {
        try {
            rfReader.stopSearch();
            rfReader.halt();
            outputText("=> stopSearch");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void writeAndRead(View v) {
        try {
            selectMainApp();
            selectApp1AndAuth();
            writeAndRead();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ReturnException e) {
            e.printStackTrace();
        }
    }

    public void creditAndGetValue(View v) {
        try {
            selectMainApp();
            selectApp1AndAuth();
            creditAndGetValue();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ReturnException e) {
            e.printStackTrace();
        }
    }

    private int selectMainApp() throws RemoteException, ReturnException {
        // init
        Bundle bundle = new Bundle();
        bundle.putInt(DesFireInitData.APDU_STYLE, apduStyleMode);
        desFireManager.init(bundle);
        // newDesKey
        MifareKey mifareKey = mifareKeyManager.generateDesKey(MAIN_KEY, chainingMode);
        int ret;

        // Select PICC main application，Can be omitted
        ret = desFireManager.selectApplication(0x00);
        outputText("=> selectApplication 0x00 | ret = " + ret);
        checkRet(ret);

        ret = desFireManager.authenticateIso(0, mifareKey);
        outputText("=> authenticateIso 0 | ret = " + ret);
        checkRet(ret);

        return ret;
    }

    private void checkRet(int ret) throws ReturnException {
        if (ret != 0) {
            throw new ReturnException("ret is NOT SUCCESS");
        }
    }

    private int selectApp1AndAuth() throws RemoteException, ReturnException {
        // Select application
        int ret = desFireManager.selectApplication(APP1_ID);
        outputText("=> selectApplication 986895 | ret = " + ret);
        checkRet(ret);

        MifareKey mifareKey = mifareKeyManager.generateDesKey(MAIN_KEY, chainingMode);

        // Main key verification
        ret = desFireManager.authenticateIso(0, mifareKey);
        outputText("=> authenticateIso 0 | ret = " + ret);
        checkRet(ret);

        mifareKey = mifareKeyManager.generateDesKey(APP1_KEY, chainingMode);

        //APP1 Key verification
        ret = desFireManager.authenticateIso(1, mifareKey);
        outputText("=> authenticateIso 1 | ret = " + ret);
        checkRet(ret);

        return ret;
    }

    private int writeAndRead() throws RemoteException, ReturnException {
        int fileId = 0x01;
        int length = 1;

        // Write data
        byte[] data = new byte[length];
        new Random().nextBytes(data);
        int ret = desFireManager.writeData(fileId, 0x00, data);
        outputText(String.format("=> writeData fileId = %d, length = %d, ret = %d",
                fileId, length, ret));
        checkRet(ret);

        // Read data
        BytesValue bytesValue = new BytesValue();
        ret = desFireManager.readData(fileId, 0x00, length, bytesValue);
        outputText(String.format("=> readData fileId = %d, length = %d, ret = %d",
                fileId, length, ret));
        checkRet(ret);

        outputText(Arrays.equals(data, bytesValue.getData()) ? "=> RESULT EQUAL" : "");

        return ret;
    }

    private int creditAndGetValue() throws RemoteException, ReturnException {
        int fileId = 0x04;
        int ret = desFireManager.credit(fileId, 1);
        outputText("=> credit 0x04 | ret = " + ret);
        checkRet(ret);

        ret = desFireManager.commitTransaction();
        outputText("=> commitTransaction | ret = " + ret);
        checkRet(ret);

        BytesValue bytesValue = new BytesValue();
        ret = desFireManager.getValue(fileId, bytesValue);
        outputText("=> getValue 0x04 | ret = " + ret + ", value = " + BytesUtil.bytes2HexString(bytesValue.getData()));
        checkRet(ret);

        return ret;
    }

    class ReturnException extends Exception {
        public ReturnException(String message) {
            super(message);
        }
    }
}
