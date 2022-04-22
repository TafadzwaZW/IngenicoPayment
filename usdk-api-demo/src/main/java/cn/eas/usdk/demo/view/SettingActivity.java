package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.usdk.apiservice.aidl.constants.RFDeviceName;
import com.usdk.apiservice.aidl.pinpad.DeviceName;
import com.usdk.apiservice.aidl.pinpad.KAPId;

import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;

/**
 * Setting of device type, log level and so on.
 */
public class SettingActivity extends BaseActivity {

    private EditText etRegisionId;
    private EditText etKapNum;
    private Switch logSwitch;

    private boolean usdkLogOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(getString(R.string.setting));
        initRadio();
        initKapId();
        initSwitch();
    }

    private void initRadio() {
        // Pinpad
        RadioGroup pinpadRadioGroup = bindViewById(R.id.pinpadRadioGroup);
        RadioButton innerPinpadRadio = bindViewById(R.id.innerPinpadRadio);
        RadioButton extPinpadRadio = bindViewById(R.id.extPinpadRadio);
        if (DemoConfig.PINPAD_DEVICE_NAME.equals(DeviceName.IPP)) {
            innerPinpadRadio.setChecked(true);
        } else {
            extPinpadRadio.setChecked(true);
        }
        pinpadRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int btnId = radioGroup.getCheckedRadioButtonId();
                if (btnId == R.id.innerPinpadRadio) {
                    DemoConfig.PINPAD_DEVICE_NAME = DeviceName.IPP;
                } else {
                    DemoConfig.PINPAD_DEVICE_NAME = DeviceName.COM_EPP;
                }
            }
        });
        // RfcardReader
        RadioGroup rfreaderRadioGroup = bindViewById(R.id.rfreaderRadioGroup);
        RadioButton innerRfreaderRadio = bindViewById(R.id.innerRfreaderRadio);
        RadioButton extRfreaderRadio = bindViewById(R.id.extRfreaderRadio);
        if (DemoConfig.RF_DEVICE_NAME.equals(RFDeviceName.INNER)) {
            innerRfreaderRadio.setChecked(true);
        } else {
            extRfreaderRadio.setChecked(true);
        }
        rfreaderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int btnId = radioGroup.getCheckedRadioButtonId();
                if (btnId == R.id.innerRfreaderRadio) {
                    DemoConfig.RF_DEVICE_NAME = RFDeviceName.INNER;
                } else {
                    DemoConfig.RF_DEVICE_NAME = RFDeviceName.EXTERNAL;
                }
            }
        });
    }

    public void initKapId() {
        try {
            etRegisionId = bindViewById(R.id.etRegisionId);
            etKapNum = bindViewById(R.id.etKapNum);

            List<KAPId> kapIds = DeviceHelper.me().getDeviceManager().getAppKapIdList();
            if (kapIds == null || kapIds.size() < 1) {
                toast("getAppKapIdList fail!");
            } else {
                KAPId kapId = kapIds.get(0);
                DemoConfig.REGION_ID = kapId.getRegionId();
                DemoConfig.KAP_NUM = kapId.getKapNum();
            }
            etRegisionId.setText("" + DemoConfig.REGION_ID);
            etKapNum.setText("" + DemoConfig.KAP_NUM);
            etRegisionId.setSelection(etRegisionId.getText().toString().length());
            etKapNum.setSelection(etKapNum.getText().toString().length());
        } catch (Exception e) {
            finishWithInfo(e.getMessage());
        }
    }

    private void initSwitch() {
        logSwitch = findViewById(R.id.logSwitch);
        logSwitch.setChecked(DemoConfig.USDK_LOG_OPEN);
        logSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                usdkLogOpen = isChecked;
            }
        });
    }

    public void save(View v) {
        DemoConfig.USDK_LOG_OPEN = usdkLogOpen;
        DeviceHelper.me().debugLog(DemoConfig.USDK_LOG_OPEN);

        DemoConfig.REGION_ID = Integer.valueOf(etRegisionId.getText().toString());
        DemoConfig.KAP_NUM = Integer.valueOf(etKapNum.getText().toString());

        finish();
    }
}
