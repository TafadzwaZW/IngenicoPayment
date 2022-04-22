package cn.eas.usdk.demo.view.mifare;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.mifare.DesFireError;
import com.usdk.apiservice.aidl.mifare.DesFireInitData;
import com.usdk.apiservice.aidl.mifare.MifareKey;
import com.usdk.apiservice.aidl.mifare.UDesFireManager;
import com.usdk.apiservice.aidl.mifare.UMifareKeyManager;
import com.usdk.apiservice.aidl.rfreader.OnPassAndActiveListener;
import com.usdk.apiservice.aidl.rfreader.URFReader;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class BaseDesFireActivity extends BaseDeviceActivity {
    private static final byte[] MAIN_KEY = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final byte[] APP1_KEY = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final int APP1_ID = 1;
    protected static final int SUCCESS = 0;
    protected UDesFireManager desFireManager;
    protected URFReader rfReader;
    protected UMifareKeyManager mifareKeyManager;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
    }

    protected void initDeviceInstance() {
        desFireManager = DeviceHelper.me().getDesFireManager(DemoConfig.RF_DEVICE_NAME);
        rfReader = DeviceHelper.me().getRFReader(DemoConfig.RF_DEVICE_NAME);
        mifareKeyManager = DeviceHelper.me().getMifareKeyManager();
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

    protected int selectMainApp(int apduStyleMode, int chainingMode) throws RemoteException {
        // init
        Bundle bundle = new Bundle();
        bundle.putInt(DesFireInitData.APDU_STYLE, apduStyleMode);
        desFireManager.init(bundle);
        // newDesKey
        MifareKey mifareKey = mifareKeyManager.generateDesKey(MAIN_KEY, chainingMode);
        int ret;

        // Select PICC main application，Can be omitted
        ret = desFireManager.selectApplication(0x00);
        outputRet("selectApplication", ret);
        if (ret != SUCCESS) {
            return ret;
        }

        ret = desFireManager.authenticateIso(0, mifareKey);
        outputRet("authenticateIso", ret);
        if (ret != SUCCESS) {
            return ret;
        }

        return ret;
    }

    protected int selectApp1AndAuth() throws RemoteException {
        // Select application
        int ret = desFireManager.selectApplication(APP1_ID);
        outputRet("selectApplication", ret);
        if (ret != SUCCESS) {
            return ret;
        }

        MifareKey mifareKey = mifareKeyManager.generateDesKey(MAIN_KEY, MifareKey.CHAINING_CBC_MODE);
        ret = desFireManager.authenticateIso(0, mifareKey);// Main key verification
        outputRet("authenticateIso", ret);
        if (ret != SUCCESS) {
            return ret;
        }

        mifareKey = mifareKeyManager.generateDesKey(APP1_KEY, MifareKey.CHAINING_CBC_MODE);
        ret = desFireManager.authenticateIso(1, mifareKey);//APP1 Key verification

        outputRet("authenticateIso", ret);
        if (ret != SUCCESS) {
            return ret;
        }
        return SUCCESS;
    }

    protected void outputRet(String function, int ret) {
        StringBuilder sb = new StringBuilder();
        sb.append("=> ")
                .append(function)
                .append(" | ret = ")
                .append(Integer.toHexString(ret).toUpperCase())
                .append(" : ")
                .append(getErrorMessage(ret));
        if (ret != SUCCESS) {
            outputRedText(sb.toString());
        } else {
            outputText(sb.toString());
        }
    }

    @Override
    public String getErrorMessage(int error) {
        switch (error) {
            case DesFireError.DESFIRE_OPERATION_OK:
                return "SUCCESS";
            case DesFireError.DESFIRE_NO_CHANGES:
                return "No changes done to backup files";
            case DesFireError.DESFIRE_OUT_OF_EEPROM_ERROR:
                return "Insufficient NV-Memory to complete command";
            case DesFireError.DESFIRE_ILLEGAL_COMMAND:
                return "Command not supported";
            case DesFireError.DESFIRE_INTEGRITY_ERROR:
                return "CRC or MAC does not match data. Padding bytes not valid";
            case DesFireError.DESFIRE_NO_SUCH_KEY:
                return "Invalid key number specified";
            case DesFireError.DESFIRE_LENGTH_ERROR:
                return "Length of command string invalid";
            case DesFireError.DESFIRE_PERMISSION_DENIED:
                return "Current configuration/status does not allow the requested command";
            case DesFireError.DESFIRE_PARAMETER_ERROR:
                return "Value of the parameter(s) invalid";
            case DesFireError.DESFIRE_APPLICATION_NOT_FOUND:
                return "Requested AID not present on PICC";
            case DesFireError.DESFIRE_APPL_INTEGRITY_ERROR:
                return "Unrecoverable error within application, application will be disabled";
            case DesFireError.DESFIRE_AUTHENTICATION_ERROR:
                return "Current authentication status does not allow the request command";
            case DesFireError.DESFIRE_ADDITIONAL_FRAME:
                return "Additional data frame is expected to be sent";
            case DesFireError.DESFIRE_BOUNDARY_ERROR:
                return "Attempt to read/write data from/to beyond the file's/record's limits. Attempt to exceed the limits of a value file";
            case DesFireError.DESFIRE_PICC_INTEGRITY_ERROR:
                return "Unrecoverable error within PICC. PICC will be disabled ";
            case DesFireError.DESFIRE_COMMAND_ABORTED:
                return "Previous command was not fully completed. Not all frames were requested or provded by the PICC";
            case DesFireError.DESFIRE_PICC_DISABLED:
                return "PICC was disabled by an unrecoverable error";
            case DesFireError.DESFIRE_COUNT_ERROR:
                return "Number of applications limited to 28";
            case DesFireError.DESFIRE_DUPLICATE_ERROR:
                return "Creation of file/application failed because file/application with same number already exists";
            case DesFireError.DESFIRE_EEPROM_ERROR:
                return "Could not completed NV-write operation due to loss of powedn internal backup/rollback mechanism activated";
            case DesFireError.DESFIRE_FILE_NOT_FOUND:
                return "Specified file number does not exists";
            case DesFireError.DESFIRE_FILE_INTEGRITY_ERROR:
                return "Unrecoverable error within file. File will be disabled";
            case DesFireError.DESFIRE_PROCESSING_ERROR:
                return "Internal processing error(from 0x200 to 0x2FF) ";
            case DesFireError.DESFIRE_DLL_NOT_LOADED:
                return "The mifare DLL is not loaded";
            case DesFireError.DESFIRE_CHECK_MAC_FAIL:
                return "MAC check failed";
            case DesFireError.DESFIRE_CARD_RETURN_DATA_ILLEGAL:
                return "Card return data is illegal";
            case DesFireError.DESFIRE_SAM_POWERUP_FAIL:
                return "SAM powerup fail ";
            case DesFireError.DESFIRE_SAM_EXCHANGE_APDU_FAIL:
                return "SAM exchange apdu fail";
            case DesFireError.DESFIRE_SAM_POWERDOWN_FAIL:
                return "SAM powerdown fail";
            default:
                return super.getErrorMessage(error);
        }
    }
}
