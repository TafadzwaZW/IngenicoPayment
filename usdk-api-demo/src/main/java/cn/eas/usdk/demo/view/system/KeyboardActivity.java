package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.keyboard.PowerKeyState;
import com.usdk.apiservice.aidl.system.keyboard.UKeyboard;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class KeyboardActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> stateList = new LinkedList<>();
    static {
        stateList.add(new SpinnerItem(PowerKeyState.SHORT_LONG_PRESS_ENABLED, "SHORT_LONG_PRESS_ENABLED"));
        stateList.add(new SpinnerItem(PowerKeyState.SHORT_LONG_PRESS_DISABLED, "SHORT_LONG_PRESS_DISABLED"));
        stateList.add(new SpinnerItem(PowerKeyState.ONLY_SHORT_PRESS_DISABLED, "ONLY_SHORT_PRESS_DISABLED"));
    }

    private UKeyboard keyboard;
    private int state = PowerKeyState.SHORT_LONG_PRESS_ENABLED;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_keyboard);
        setTitle("Keyboard Module");
        initSwitch();
        initSpinner();
    }

    protected void initDeviceInstance() {
        keyboard = DeviceHelper.me().getKeyboard();
    }

    private void initSwitch() {
        Switch switchHomekey = findViewById(R.id.switch_home_key);
        switchHomekey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setHomeKeyEnabled(isChecked);
            }
        });
        Switch switchNavBar = findViewById(R.id.switch_nav_bar);
        switchNavBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNavigationBarEnabled(isChecked);
            }
        });
    }

    private void initSpinner() {
        NiceSpinner spinnerState = findViewById(R.id.spinner_state);
        spinnerState.attachDataSource(stateList);
        spinnerState.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                state = stateList.get(position).getValue();
            }
        });
    }

    public void isHomeKeyEnabled(View v) {
        try {
            outputBlueText(">>> isHomeKeyEnabled");
            BooleanValue booleanValue = new BooleanValue();
            int ret = keyboard.isHomeKeyEnabled(booleanValue);
            if (ret == SystemError.SUCCESS) {
                outputText("isHomeKeyEnabled success: " + booleanValue.isTrue());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void setHomeKeyEnabled(boolean enable) {
        try {
            outputBlueText(">>> setHomeKeyEnabled");
            int ret = keyboard.setHomeKeyEnabled(enable);
            if (ret == SystemError.SUCCESS) {
                outputText("setHomeKeyEnabled success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isNavigationBarEnabled(View v) {
        try {
            outputBlueText(">>> isNavigationBarEnabled");
            BooleanValue booleanValue = new BooleanValue();
            int ret = keyboard.isNavigationBarEnabled(booleanValue);
            if (ret == SystemError.SUCCESS) {
                outputText("isNavigationBarEnabled success: " + booleanValue.isTrue());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void setNavigationBarEnabled(boolean enable) {
        try {
            outputBlueText(">>> setNavigationBarEnabled");
            int ret = keyboard.setNavigationBarEnabled(enable);
            if (ret == SystemError.SUCCESS) {
                outputText("setNavigationBarEnabled success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getPowerKeyState(View v) {
        try {
            outputBlueText(">>> getPowerKeyState");
            IntValue intValue = new IntValue();
            int ret = keyboard.getPowerKeyState(intValue);
            if (ret == SystemError.SUCCESS) {
                outputText("getPowerKeyState success: " + intValue.getData());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setPowerKeyState(View v) {
        try {
            outputBlueText(">>> setPowerKeyState");
            int ret = keyboard.setPowerKeyState(state);
            if (ret == SystemError.SUCCESS) {
                outputText("setPowerKeyState success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }
}
