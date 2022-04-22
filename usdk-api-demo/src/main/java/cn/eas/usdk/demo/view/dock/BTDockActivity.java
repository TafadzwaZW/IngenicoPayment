package cn.eas.usdk.demo.view.dock;

import android.os.Bundle;
import android.view.View;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.dock.DockName;
import com.usdk.apiservice.aidl.dock.UBTDock;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class BTDockActivity extends BaseDockActivity {

    private UBTDock dock;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bt_dock);
        setTitle("BT Dock Module");

        initDeviceInstance();
    }

    protected void initDeviceInstance() {
        dock = DeviceHelper.me().getBTDock();
    }

    public void getDockStatus(View v) {
        outputBlueText(">>> getDockStatus");
        try {
            IntValue status = new IntValue();
            int ret = dock.getDockStatus(status);
            if (ret != 0) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("=> status : " + getStatusDescription(status.getData()));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void startDockPortDemo(View v) {
        startActivity(DockPortActivity.class, DockName.BT_DOCK);
    }

}
