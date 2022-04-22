package cn.eas.usdk.demo.view.icreader;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.data.ApduResponse;
import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.icreader.ICError;
import com.usdk.apiservice.aidl.icreader.OnInsertListener;
import com.usdk.apiservice.aidl.icreader.UICCpuReader;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.util.ToastUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class ICCpuActivity extends BaseDeviceActivity {

    private EditText edtApdu;
    private UICCpuReader icCpuReader;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_iccpu);
        setTitle("ICCpuReader Module");

        initViews();
    }

    protected void initDeviceInstance() {
        icCpuReader = DeviceHelper.me().getICCpuReader();
    }

    private void initViews() {
        this.edtApdu = findViewById(R.id.edtApdu);
    }

    public void searchCard(View v) {
        outputBlueText(">>> searchCard");
        try {
            icCpuReader.searchCard(new OnInsertListener.Stub() {
                @Override
                public void onCardInsert() throws RemoteException {
                    outputText("=> onCardInsert");
                }

                @Override
                public void onFail(int i) throws RemoteException {
                    outputRedText("=> onFail: " + getErrorDetail(i));
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void stopSearch(View v) {
        outputBlueText(">>> stopSearch");
        try {
            icCpuReader.stopSearch();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isCardIn(View v) {
        try {
            outputBlueText(">>> isCardIn | " + icCpuReader.isCardIn());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void powerUp(View v) {
        outputBlueText(">>> powerUp");
        try {
            BytesValue atr = new BytesValue();
            IntValue protocol = new IntValue();
            int ret = icCpuReader.powerUp(atr, protocol);
            if (ret != ICError.SUCCESS) {
                outputRedText("powerUp fail: " + getErrorDetail(ret));
                return;
            }
            outputText("powerUp success");
            outputText("atr =  " + atr.toHexString());
            outputText("protocol = " + protocol.getData());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void exchangeApdu(View v) {
        String apdu = this.edtApdu.getText().toString().trim();
        if (TextUtils.isEmpty(apdu)) {
            ToastUtil.showToast("Please input apdu");
            return;
        }

        outputBlueText(">>> exchangeApdu");
        try {
            outputBlueText(">>> apdu = " + apdu);
            ApduResponse response = icCpuReader.exchangeApdu(BytesUtil.hexString2Bytes(apdu));
            outputApduMsg(response);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void outputApduMsg(ApduResponse response) {
        if (response.getAPDURet() != ICError.SUCCESS) {
            outputRedText("exchangeApdu fail: " + getErrorDetail(response.getAPDURet()));
        } else {
            outputText("exchangeApdu success");
        }
        outputText("APDURet = " + Integer.toHexString(response.getAPDURet()));
        outputText("sw1 = " + Integer.toHexString(response.getSW1() & 0xFF));
        outputText("sw2 = " + Integer.toHexString(response.getSW2() & 0xFF));
        outputText("data = " + BytesUtil.bytes2HexString(response.getData()));
    }

    public void exchangeApduExtend(View v) {
        String apdu = this.edtApdu.getText().toString().trim();
        if (TextUtils.isEmpty(apdu)) {
            ToastUtil.showToast("Please input apdu");
            return;
        }

        outputBlueText(">>> exchangeApduExtend");
        try {
            outputBlueText(">>> apdu = " + apdu);
            ApduResponse response = icCpuReader.exchangeApduExtend(BytesUtil.hexString2Bytes(apdu));
            outputApduMsg(response);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void powerDown(View v) {
        outputBlueText(">>> powerDown");
        try {
            int ret = icCpuReader.powerDown();
            if (ret != ICError.SUCCESS) {
                outputRedText("powerDown fail: " + getErrorDetail(ret));
                return;
            }
            outputText("powerDown success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case ICError.ERROR_OTHER: message = "ERROR_OTHER"; break;
            case ICError.ERROR_DEVICE_USED: message = "ERROR_DEVICE_USED"; break;
            case ICError.ERROR_TIMEOUT: message = "ERROR_TIMEOUT"; break;
            case ICError.ERROR_ERRPARAM: message = "ERROR_ERRPARAM"; break;
            case ICError.ERROR_DEVICE_DISABLE: message = "ERROR_DEVICE_DISABLE"; break;
            case ICError.ERROR_FAILED: message = "ERROR_FAILED"; break;
            case ICError.ERROR_IC_ATRERR: message = "ERROR_IC_ATRERR"; break;
            case ICError.ERROR_IC_TIMEOUT: message = "ERROR_IC_TIMEOUT"; break;
            case ICError.ERROR_IC_NEEDRESET: message = "ERROR_IC_NEEDRESET"; break;
            case ICError.ERROR_IC_ERRTYPE: message = "ERROR_IC_ERRTYPE"; break;
            case ICError.ERROR_IC_DATAERR: message = "ERROR_IC_DATAERR"; break;
            case ICError.ERROR_IC_NOPOWER: message = "ERROR_IC_NOPOWER"; break;
            case ICError.ERROR_IC_FORRESP: message = "ERROR_IC_FORRESP"; break;
            case ICError.ERROR_IC_SWDIFF: message = "ERROR_IC_SWDIFF"; break;
            case ICError.ERROR_IC_NOCARD: message = "ERROR_IC_NOCARD"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
