package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.deviceadmin.UDeviceAdmin;
import com.usdk.apiservice.aidl.led.Light;
import com.usdk.apiservice.aidl.system.SystemError;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;


public class DeviceAdminActivity extends BaseDeviceActivity {

    private UDeviceAdmin deviceAdmin;
    private EditText edtFuncKey;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_device_admin);
        setTitle("DeviceAdmin Module");
        initViews();
    }

    protected void initDeviceInstance() {
        deviceAdmin = DeviceHelper.me().getDeviceAdmin();
    }

    private void initViews() {
        edtFuncKey = findViewById(R.id.edt_func_key);

    }

    public void setFotaEnabled(View view) {
        try {
            String s = edtFuncKey.getText().toString();
            int ret;
            if (TextUtils.isDigitsOnly(s) && Integer.parseInt(s) >= 1) {
                outputBlackText("setFotaEnabled true(EditText num >=1)");
                ret = deviceAdmin.setFotaEnabled(true);

            } else {
                outputBlackText("setFotaEnabled false(EditText num < 1)");
                ret = deviceAdmin.setFotaEnabled(false);
            }
            outputBlackText("ret " + ret);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isFotaEnabled(View view) {
        try {
            outputBlackText("isFotaEnabled");
            BooleanValue isEnable = new BooleanValue();
            int ret = deviceAdmin.isFotaEnabled(isEnable);
            if (ret == SystemError.SUCCESS) {
                outputBlackText("isEnable " + isEnable.isTrue());
            } else {
                outputRedText(" error = " + ret);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getDeviceFuncEnabled(View view) {
        try {
            outputBlackText("getDeviceFuncEnabled " + Integer.parseInt(edtFuncKey.getText().toString()));
            BooleanValue isEnable = new BooleanValue();
            int ret = deviceAdmin.getDeviceFuncEnabled(Integer.parseInt(edtFuncKey.getText().toString()), isEnable);
            if (ret == SystemError.SUCCESS) {
                outputBlackText("isEnable " + isEnable.isTrue());
            } else {
                outputRedText(" error = " + ret);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setDeviceFuncEnabled(View view) {
        try {
            outputBlackText("setDeviceFuncEnabled " + Integer.parseInt(edtFuncKey.getText().toString()));
            int ret = deviceAdmin.setDeviceFuncEnabled(Integer.parseInt(edtFuncKey.getText().toString()), true);
            if (ret == SystemError.SUCCESS) {
                outputBlackText("setDeviceFuncEnabled seccess ");
            } else {
                outputRedText(" setDeviceFuncEnabled error = " + ret);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setDeviceFuncDisabled(View view) {
        try {
            outputBlackText("setDeviceFuncDisabled " + Integer.parseInt(edtFuncKey.getText().toString()));
            int ret = deviceAdmin.setDeviceFuncEnabled(Integer.parseInt(edtFuncKey.getText().toString()), false);
            if (ret == SystemError.SUCCESS) {
                outputBlackText("setDeviceFuncDisabled seccess ");
            } else {
                outputRedText(" setDeviceFuncDisabled error = " + ret);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }
}
