package cn.eas.usdk.demo.view.icreader;

import android.os.Bundle;
import android.view.View;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.icreader.ICError;
import com.usdk.apiservice.aidl.icreader.USIM4428Reader;
import com.usdk.apiservice.aidl.icreader.Voltage;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class Sim4428Activity extends BaseDeviceActivity {
    private USIM4428Reader sim4428Reader;

    @Override
    protected void onCreateView(Bundle savedInstanceState) throws Exception {
        initDeviceInstance();
        setContentView(R.layout.activity_sim4428);
        setTitle("Sim4428Reader Module");
    }

    protected void initDeviceInstance() {
        sim4428Reader = DeviceHelper.me().getSIM4428Reader();
    }

    public void powerUp(View v) {
        outputBlueText(">>> powerUp");
        try {
            int vol = Voltage.SIM4428Card.VOL_5;
            BytesValue atr = new BytesValue();
            int ret = sim4428Reader.powerUp(vol, atr);
            if (ret != ICError.SUCCESS) {
                outputRedText("powerUp fail: " + getErrorDetail(ret));
                return;
            }
            outputText("powerUp success");
            outputText("atr =  " + atr.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void read(View v) {
        outputBlueText(">>> read");

    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case ICError.ERROR_OTHER: message = "ERROR_OTHER"; break;
            case ICError.ERROR_DEVICE_USED: message = "ERROR_DEVICE_USED"; break;
            case ICError.ERROR_TIMEOUT: message = "ERROR_TIMEOUT"; break;
            case ICError.ERROR_ERRPARAM: message = "ERROR_ERRPARAM"; break;
            case ICError.ERROR_DEVICE_DISABLE: message = "ERROR_DEVICE_DISABLE"; break;
            case ICError.ERROR_FAILED: message = "ERROR_FAILED"; break;
            case ICError.ERROR_IC_ATRERR: message = "ERROR_IC_ATRERR"; break;
            case ICError.ERROR_IC_TIMEOUT: message = "ERROR_IC_TIMEOUT"; break;
            case ICError.ERROR_IC_NEEDRESET: message = "ERROR_IC_NEEDRESET"; break;
            case ICError.ERROR_IC_ERRTYPE: message = "ERROR_IC_ERRTYPE"; break;
            case ICError.ERROR_IC_DATAERR: message = "ERROR_IC_DATAERR"; break;
            case ICError.ERROR_IC_NOPOWER: message = "ERROR_IC_NOPOWER"; break;
            case ICError.ERROR_IC_FORRESP: message = "ERROR_IC_FORRESP"; break;
            case ICError.ERROR_IC_SWDIFF: message = "ERROR_IC_SWDIFF"; break;
            case ICError.ERROR_IC_NOCARD: message = "ERROR_IC_NOCARD"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
