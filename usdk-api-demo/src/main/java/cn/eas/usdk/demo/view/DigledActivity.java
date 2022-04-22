package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.view.View;

import com.usdk.apiservice.aidl.digled.Align;
import com.usdk.apiservice.aidl.digled.LightName;
import com.usdk.apiservice.aidl.digled.LightStatus;
import com.usdk.apiservice.aidl.digled.UDigled;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class DigledActivity extends BaseDeviceActivity {

    private UDigled digled;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_digled);
        setTitle("Digled Module");
    }

    protected void initDeviceInstance() {
        digled = DeviceHelper.me().getDigled();
    }

    public void clearLine(View v) {
        outputBlueText(">>> clearLine ");
        try {
            int line = 1;
            boolean isSucc = digled.clearLine(line);
            if (isSucc) {
                outputText("clearLine success");
            } else {
                outputRedText("clearLine fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void display(View v) {
        outputBlueText(">>> display ");
        try {
            boolean isSucc = digled.display(1, Align.LEFT, "%0.2f",123.45f);
            if (isSucc) {
                outputText("display [123.45] success");
            } else {
                outputRedText("display fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void displaySeg(View v) {
        outputBlueText(">>> displaySeg");
        try {
            int[] seg = {0x76, 0x79, 0x38, 0x38, 0x3F};
            boolean isSucc = digled.displaySeg(1, Align.RIGHT, seg);
            if (isSucc) {
                outputText("displaySeg [hello] success");
            } else {
                outputRedText("displaySeg fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getLightNumber(View v) {
        outputBlueText(">>> getLightNumber ");
        try {
            int lightNumber = digled.getLightNumber();
            outputText("lightNumber : " + lightNumber);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getLineNumber(View v) {
        outputBlueText(">>> getLineNumber");
        try {
            int lineNumber = digled.getLineNumber();
            outputText("lineNumber : " + lineNumber);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getLineMax(View v) {
        outputBlueText(">>> getLineMax");
        try {
            int line = 1;
            int lineMax = digled.getLineMax(line);
            outputText("The digital tube maximum number of line " + line + " : " + lineMax);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setLightStatus(View v) {
        outputBlueText(">>> setLightStatus");
        try {
            boolean isSucc = digled.setLightStatus(LightName.LIGHT_AMOUNT, LightStatus.ON);
            if (isSucc) {
                outputText("setLightStatus[LIGHT_AMOUNT, ON] success");
            } else {
                outputRedText("setLightStatus[LIGHT_AMOUNT, ON] fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getLightStatus(View v) {
        outputBlueText(">>> getLightStatus");
        try {
            int lineStatus = digled.getLightStatus(LightName.LIGHT_AMOUNT);
            outputText("getLightStatus(LIGHT_AMOUNT) : " + getLightStatusDesc(lineStatus));
        } catch (Exception e) {
            handleException(e);
        }
    }

    private static String getLightStatusDesc(int status) {
        switch (status) {
            case LightStatus.OFF:
                return "OFF";
            case LightStatus.ON:
                return "ON";
             default:
                 return "Unknown status[" + status + "]";
        }
    }
}