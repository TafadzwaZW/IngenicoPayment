package cn.eas.usdk.demo.view.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.emv.LogLevel;
import com.usdk.apiservice.aidl.emv.UEMV;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class EMVSettingActivity extends BaseDeviceActivity {

    private final static List<SpinnerItem> emvLevelList = new LinkedList<>();

    static {
        emvLevelList.add(new SpinnerItem(LogLevel.AFTERTRANS, "AFTERTRANS"));
        emvLevelList.add(new SpinnerItem(LogLevel.REALTIME, "REALTIME"));
        emvLevelList.add(new SpinnerItem(LogLevel.CLOSE, "CLOSE"));
    }

    protected UEMV emv;
    private int emvLogLevel;

    @Override
    protected void onCreateView(Bundle savedInstanceState) throws Exception {
        initDeviceInstance();
        setContentView(R.layout.activity_emv_setting);
        setTitle(getString(R.string.emv_setting));
        initLogLevel();
    }

    protected void initDeviceInstance() {
        emv = DeviceHelper.me().getEMV();
    }

    private void initLogLevel() {
        NiceSpinner emvLogLevelSpinner = bindViewById(R.id.emvLogLevel);
        emvLogLevelSpinner.attachDataSource(emvLevelList);
        emvLogLevelSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                emvLogLevel = emvLevelList.get(position).getValue();

            }
        });
        setSpinnerDefaultValue(emvLogLevelSpinner, emvLevelList, DemoConfig.EMV_LOG_LEVEL);
    }

    public void save(View v) {
        try {
            emv.switchDebug(emvLogLevel);
            DemoConfig.EMV_LOG_LEVEL = emvLogLevel;
            finish();
        } catch (RemoteException e) {
            handleException(e);
        }
    }
}
