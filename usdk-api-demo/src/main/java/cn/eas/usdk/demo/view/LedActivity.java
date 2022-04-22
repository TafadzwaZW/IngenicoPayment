package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.led.LedError;
import com.usdk.apiservice.aidl.led.Light;
import com.usdk.apiservice.aidl.led.ULed;
import com.usdk.apiservice.aidl.led.ULedSetting;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.DeviceHelper;

public class LedActivity extends BaseDeviceActivity {

    private ULed led;
    private Switch setPhyicalLedEnabledSwitch;
    private Switch setSimulateLedEnabledSwitch;
    private Switch setSimulateLedColorSwitch;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_led);
        setTitle("Led Module");
        initLedSettingView();
    }

    protected void initDeviceInstance() {
        led = DeviceHelper.me().getLed(DemoConfig.RF_DEVICE_NAME);
    }

    public void turnOnBlue(View v) {
        try {
            led.turnOn(Light.BLUE);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOffBlue(View v) {
        try {
            led.turnOff(Light.BLUE);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOnYellow(View v) {
        try {
            led.turnOn(Light.YELLOW);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOffYellow(View v) {
        try {
            led.turnOff(Light.YELLOW);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOnGreen(View v) {
        try {
            led.turnOn(Light.GREEN);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOffGreen(View v) {
        try {
            led.turnOff(Light.GREEN);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOnRed(View v) {
        try {
            led.turnOn(Light.RED);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOffRed(View v) {
        try {
            led.turnOff(Light.RED);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOnAllLed(View v) {
        try {
            led.turnOn(Light.BLUE);
            led.turnOn(Light.YELLOW);
             led.turnOn(Light.GREEN);
             led.turnOn(Light.RED);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void turnOffAllLed(View v) {
        try {
             led.turnOff(Light.BLUE);
             led.turnOff(Light.YELLOW);
             led.turnOff(Light.GREEN);
             led.turnOff(Light.RED);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void initLedSettingView() {
        try {
            TextView isSupportPhysicalLedtv = findViewById(R.id.isSupportPhysicalLedtv);
            LinearLayout ledSettingLayout = findViewById(R.id.ledSettingLayout);
            setPhyicalLedEnabledSwitch = findViewById(R.id.setPhyicalLedEnabledSwitch);
            setSimulateLedEnabledSwitch = findViewById(R.id.setSimulateLedEnabledSwitch);
            setSimulateLedColorSwitch = findViewById(R.id.setSimulateLedColorSwitch);

            final ULedSetting uLedSetting = ULedSetting.Stub.asInterface(led.getSetting(new Bundle()));
            if (uLedSetting == null) {
                isSupportPhysicalLedtv.setText("usdk service no support led setting");
                return;
            }
            BooleanValue isSupport = new BooleanValue();
            int ret = uLedSetting.isSupportPhysicalLed(isSupport);
            if (ret != LedError.SUCCESS) {
                isSupportPhysicalLedtv.setText("Get isSupportPhysicalLed error:" + ret);
                ledSettingLayout.setVisibility(View.GONE);
                return;
            }
            ledSettingLayout.setVisibility(View.VISIBLE);

            isSupportPhysicalLedtv.setText("Device isSupportPhysicalLed :" + isSupport.isTrue());
            if (isSupport.isTrue()) {
                setPhyicalLedEnabledSwitch.setChecked(true);
                setSimulateLedEnabledSwitch.setChecked(false);
                setSimulateLedColorSwitch.setChecked(false);
            } else {
                setPhyicalLedEnabledSwitch.setChecked(false);
                setSimulateLedEnabledSwitch.setChecked(true);
                setSimulateLedColorSwitch.setChecked(true);
            }

            setPhyicalLedEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        uLedSetting.setPhyicalLedEnabled(isChecked);
                    } catch (RemoteException e) {
                        handleException(e);
                    }
                }
            });
            setSimulateLedEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        uLedSetting.setSimulateLedEnabled(isChecked);
                    } catch (RemoteException e) {
                        handleException(e);
                    }
                }
            });
            setSimulateLedColorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        uLedSetting.setSimulateLedColor(isChecked);
                    } catch (RemoteException e) {
                        handleException(e);
                    }
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }
}
