package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.lki.LKIError;
import com.usdk.apiservice.aidl.lki.OnLKIResultListener;
import com.usdk.apiservice.aidl.lki.ULKITool;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class LKIToolActivity extends BaseDeviceActivity {

    private ULKITool lkiTool;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_lki);
        setTitle("LKITool Module");
    }

    protected void initDeviceInstance() {
        lkiTool = DeviceHelper.me().getLKITool();
    }

    public void injectLKey(View v) {
        outputBlueText(">>> injectLKey");
        try {
            lkiTool.injectLKey(new OnLKIResultListener.Stub() {
                @Override
                public void onResult(int status) throws RemoteException {
                    if (status == LKIError.SUCCESS) {
                        outputText("=> onResult | success");
                    } else {
                        outputRedText("=> onResult | " + getErrorDetail(status));
                    }
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void cancelInjection(View v) {
        outputBlueText(">>> cancelInjection");
        try {
            int ret = lkiTool.cancelInjection();
            if (ret != LKIError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("cancelInjection success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case LKIError.ERROR_OPEN_DEVICE: message = "ERROR_OPEN_DEVICE";break;
            case LKIError.ERROR_PROCESS: message = "ERROR_STATUS";break;
            case LKIError.ERROR_CANCEL: message = "ERROR_CANCEL";break;
            case LKIError.ERROR_CONNECTING: message = "ERROR_CONNECTING";break;
            case LKIError.ERROR_GETTK: message = "ERROR_GETTK";break;
            case LKIError.ERROR_GETTK_END: message = "ERROR_GETTK_END";break;
            case LKIError.ERROR_INJECTKEY: message = "ERROR_INJECTKEY";break;
            case LKIError.ERROR_INJECTKEY_END: message = "ERROR_INJECTKEY_END";break;
            case LKIError.ERROR_INJECTKEY_END_ERROR: message = "ERROR_INJECTKEY_END_ERROR";break;
            case LKIError.ERROR_INJECTKEY_NO_SIGN: message = "ERROR_INJECTKEY_NO_SIGN";break;
            case LKIError.ERROR_INJECTKEY_NO_PINPAD_ADMIN: message = "ERROR_INJECTKEY_NO_PINPAD_ADMIN";break;
            case LKIError.ERROR_INJECTKEY_NO_ACCESSABLE_KAP_ID: message = "ERROR_INJECTKEY_NO_ACCESSABLE_KAP_ID";break;
            case LKIError.ERROR_SERVICE_BE_OCCUPIED: message = "ERROR_SERVICE_BE_OCCUPIED";break;
            case LKIError.SUCCESS: message = "ERROR_END";break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
