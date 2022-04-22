package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.input.InputEventListener;
import com.usdk.apiservice.aidl.system.input.UInputManager;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class InputActivity extends BaseDeviceActivity {

    private UInputManager inputManager;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_input);
        setTitle("Input Module");
    }

    protected void initDeviceInstance() {
        inputManager = DeviceHelper.me().getInputManager();
    }

    public void registerInputEventListener(View v) {
        try {
            outputBlueText(">>> registerInputEventListener");
            int ret = inputManager.registerInputEventListener(new InputEventListener.Stub() {
                @Override
                public void onInput(int action) throws RemoteException {
                    outputText("onInput | action = " + action);
                }
            });
            if (ret == SystemError.SUCCESS) {
                outputText("registerInputEventListener success ");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void unregisterInputEventListener(View v) {
        try {
            outputBlueText(">>> unregisterInputEventListener");
            int ret = inputManager.unregisterInputEventListener();
            if (ret == SystemError.SUCCESS) {
                outputText("unregisterInputEventListener success ");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

}
