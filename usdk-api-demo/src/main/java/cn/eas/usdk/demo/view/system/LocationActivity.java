package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.location.LocationMode;
import com.usdk.apiservice.aidl.system.location.ULocation;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class LocationActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> modeList = new LinkedList<>();

    static {
        modeList.add(new SpinnerItem(LocationMode.OFF, "OFF"));
        modeList.add(new SpinnerItem(LocationMode.SENSORS_ONLY, "SENSORS_ONLY"));
        modeList.add(new SpinnerItem(LocationMode.BATTERY_SAVING, "BATTERY_SAVING"));
        modeList.add(new SpinnerItem(LocationMode.HIGH_ACCURACY, "HIGH_ACCURACY"));
    }

    private ULocation location;
    private int mode = LocationMode.OFF;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_location);
        setTitle("Location Module");
        initSpinner();
    }

    protected void initDeviceInstance() {
        location = DeviceHelper.me().getLocation();
    }

    private void initSpinner() {
        NiceSpinner spinnerMode = findViewById(R.id.spinner_mode);
        spinnerMode.attachDataSource(modeList);
        spinnerMode.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mode = modeList.get(position).getValue();
            }
        });
    }

    public void getLocationMode(View v) {
        try {
            outputBlueText(">>> getLocationMode");
            IntValue intValue = new IntValue();
            int ret = location.getLocationMode(intValue);
            if (ret == SystemError.SUCCESS) {
                outputText("getLocationMode success: " + intValue.getData());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setLocationMode(View v) {
        try {
            outputBlueText(">>> setLocationMode");
            int ret = location.setLocationMode(mode);
            if (ret == SystemError.SUCCESS) {
                outputText("setLocationMode success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

}
