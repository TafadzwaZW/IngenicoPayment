package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.usb.UUsb;
import com.usdk.apiservice.aidl.system.usb.UsbMode;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class UsbActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> usbModeList = new LinkedList<>();
    static {
        usbModeList.add(new SpinnerItem(UsbMode.EPAY, "EPAY"));
        usbModeList.add(new SpinnerItem(UsbMode.MTP, "MTP"));
        usbModeList.add(new SpinnerItem(UsbMode.PTP, "PTP"));
        usbModeList.add(new SpinnerItem(UsbMode.MASS_STORAGE, "MASS_STORAGE"));
        usbModeList.add(new SpinnerItem(UsbMode.DISABLED_UPORT, "DISABLED_UPORT"));
        usbModeList.add(new SpinnerItem(999, "error param"));
    }

    private UUsb usb;
    private SpinnerItem modeSlt;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_usb);
        setTitle("USB Module");
        initSpinner();
    }

    protected void initDeviceInstance() {
        usb = DeviceHelper.me().getUsb();
    }

    private void initSpinner() {
        modeSlt = usbModeList.get(0);
        NiceSpinner usbModeSpinner = (NiceSpinner) findViewById(R.id.usbModeSpinner);
        usbModeSpinner.attachDataSource(usbModeList);
        usbModeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                modeSlt = usbModeList.get(position);
            }
        });
    }

    public void setMode(View v) {
        try {
            outputBlueText(">>> setMode : " + modeSlt);
            int ret = usb.setMode(modeSlt.getValue());
            if (ret == SystemError.SUCCESS) {
                outputText("setMode success ");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isEnabled(View v) {
        try {
            BooleanValue enabled = new BooleanValue();
            int ret = usb.isEnabled(modeSlt.getValue(), enabled);
            if (ret == SystemError.SUCCESS) {
                outputBlueText(">>> is "  + modeSlt + " Enabled : " + (enabled.isTrue() ? "yes" : "no"));
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }
}
