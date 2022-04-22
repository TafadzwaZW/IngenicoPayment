package cn.eas.usdk.demo.view.mifare;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.data.LongValue;
import com.usdk.apiservice.aidl.mifare.ApduStyleMode;
import com.usdk.apiservice.aidl.mifare.DesFireVersionInfo;
import com.usdk.apiservice.aidl.mifare.MifareKey;

import java.util.ArrayList;
import java.util.List;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BytesUtil;

public class DesFireApplicationActivity extends BaseDesFireActivity {
    private static final byte[] APP1_KEY = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final int APP1_ID = 1;

    private EditText edtAppId;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_desfire_application);
        setTitle("DesFire Application Module");

        edtAppId = findViewById(R.id.edt_app_id);
    }

    public void getFreeMemory(View view) throws RemoteException {
        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        LongValue size = new LongValue();
        int ret = desFireManager.getFreeMemory(size);
        outputRet("getFreeMemory", ret);
        outputText("=> getFreeMemory | size = " + size.getData());
    }

    public void getApplicationIds(View view) throws RemoteException {
        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }
        List<LongValue> aids = new ArrayList<>();
        int ret = desFireManager.getApplicationIds(aids);
        outputRet("getApplicationIds", ret);
        for (LongValue item : aids) {
            outputText("=> getApplicationIds | aid = " + item.getData());
        }
    }

    public void getVersion(View view) throws RemoteException {
        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        DesFireVersionInfo versionInfo = new DesFireVersionInfo();
        int ret = desFireManager.getVersion(versionInfo);
        outputRet("getVersion", ret);
        outputText("UID = " + BytesUtil.bytes2HexString(versionInfo.getUid()));
        outputText("BatchNumber = " + BytesUtil.bytes2HexString(versionInfo.getBatchNumber()));
        outputText("ProductionWeek = " + versionInfo.getProductionWeek());
        outputText("ProductionYear = " + versionInfo.getProductionYear());

        outputText("Hardware VendorId = " + versionInfo.getHardware().getVendorId());
        outputText("Hardware Type = " + versionInfo.getHardware().getType());
        outputText("Hardware Subtype = " + versionInfo.getHardware().getSubtype());
        outputText("Hardware VersionMajor = " + versionInfo.getHardware().getVersionMajor());
        outputText("Hardware VersionMinor = " + versionInfo.getHardware().getVersionMinor());
        outputText("Hardware Protocol = " + versionInfo.getHardware().getProtocol());
        outputText("Hardware StorageSize = " + versionInfo.getHardware().getStorageSize());

        outputText("Software VendorId = " + versionInfo.getSoftware().getVendorId());
        outputText("Software Type = " + versionInfo.getSoftware().getType());
        outputText("Software Subtype = " + versionInfo.getSoftware().getSubtype());
        outputText("Software VersionMajor = " + versionInfo.getSoftware().getVersionMajor());
        outputText("Software VersionMinor = " + versionInfo.getSoftware().getVersionMinor());
        outputText("Software Protocol = " + versionInfo.getSoftware().getProtocol());
        outputText("Software StorageSize = " + versionInfo.getSoftware().getStorageSize());
    }

    public void getKeySetting(View view) throws RemoteException {
        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        IntValue setting = new IntValue();
        IntValue maxKeys = new IntValue();
        int ret = desFireManager.getKeySetting(setting, maxKeys);
        outputRet("getKeySetting", ret);
        outputText("=> getKeySetting | setting = " + setting.getData() + ", maxKeys = " + maxKeys.getData());
    }

    public void formatPicc(View view) throws RemoteException {
        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        int ret = desFireManager.formatPicc();
        outputRet("formatPicc", ret);
    }

    public void createApplication(View view) throws RemoteException {
        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (TextUtils.isEmpty(edtAppId.getText().toString())) {
            outputRedText("=> APP ID is null");
            return;
        }

        long aid = Long.parseLong(edtAppId.getText().toString());
        int setting1 = 0x0f;
        int setting2 = 0x08;
        int isoFileId = -1;
        String isoFileName = "";

        int ret = desFireManager.createApplication(aid, setting1, setting2, isoFileId, isoFileName);
        outputRet("createApplication", ret);
    }

    public void deleteApplication(View view) throws RemoteException {
        if (selectMainApp(ApduStyleMode.NATIVE_MODE, MifareKey.CHAINING_CBC_MODE) != SUCCESS) {
            return;
        }

        if (TextUtils.isEmpty(edtAppId.getText().toString())) {
            outputRedText("=> APP ID is null");
            return;
        }

        long aid = Long.parseLong(edtAppId.getText().toString());
        int ret = desFireManager.deleteApplication(aid);
        outputRet("deleteApplication", ret);
    }

}
