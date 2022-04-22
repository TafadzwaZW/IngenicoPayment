package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.statusbar.UStatusBar;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class StatusBarActivity extends BaseDeviceActivity {

    private UStatusBar statusBar;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_status_bar);
        setTitle("Status bar Module");
        initSwitch();
    }

    protected void initDeviceInstance() {
        statusBar = DeviceHelper.me().getStatusBar();
    }

    private void initSwitch() {
        Switch switchStatusBar = findViewById(R.id.switch_status_bar);
        switchStatusBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setStatusBar(isChecked);
            }
        });
    }

    public void isPanelExpandEnabled(View v) {
        try {
            outputBlueText(">>> isPanelExpandEnabled");
            BooleanValue booleanValue = new BooleanValue();
            int ret = statusBar.isPanelExpandEnabled(booleanValue);
            if (ret == SystemError.SUCCESS) {
                outputText("isPanelExpandEnabled success: " + booleanValue.isTrue());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void setStatusBar(boolean enable) {
        try {
            outputBlueText(">>> setPanelExpandEnabled");
            int ret = statusBar.setPanelExpandEnabled(enable);
            if (ret == SystemError.SUCCESS) {
                outputText("setPanelExpandEnabled success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }
}
