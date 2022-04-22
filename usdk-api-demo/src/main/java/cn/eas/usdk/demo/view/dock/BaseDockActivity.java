package cn.eas.usdk.demo.view.dock;

import android.content.Intent;

import com.usdk.apiservice.aidl.DeviceServiceData;
import com.usdk.apiservice.aidl.dock.DockStatus;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public abstract class BaseDockActivity extends BaseDeviceActivity {

    protected void startActivity(Class<?> cls, String dockName) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(DeviceServiceData.DOCK_NAME, dockName);
        startActivity(intent);
        overridePendingTransition(R.anim.dync_in_from_right, R.anim.dync_out_to_left);
    }

    protected static String getStatusDescription(int status) {
        String message;
        switch (status) {
            case DockStatus.OTHER: message = "Other status"; break;
            case DockStatus.CHANNEL_NOT_OPENED: message = "Channel not opened"; break;
            case DockStatus.UNPAIRED: message = "Unpaired"; break;
            case DockStatus.PAIRED: message = "Paired"; break;
            case DockStatus.CHANNEL_CONNECTED: message = "Channel connected"; break;
            default:
                message = "Unknown status";
        }
        return message + String.format("[0x%02X]", status);
    }

}
