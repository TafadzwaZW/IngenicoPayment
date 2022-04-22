package cn.eas.usdk.demo.view.icreader;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.data.ApduResponse;
import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.icreader.ICError;
import com.usdk.apiservice.aidl.icreader.OnInsertListener;
import com.usdk.apiservice.aidl.icreader.PowerMode;
import com.usdk.apiservice.aidl.icreader.UPSamReader;
import com.usdk.apiservice.aidl.icreader.Voltage;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class PSamActivity extends BaseDeviceActivity {
    private UPSamReader psamReader;

    @Override
    protected void onCreateView(Bundle savedInstanceState) throws Exception {
        initDeviceInstance();
        setContentView(R.layout.activity_icpsam);
        setTitle("PSamReader Module");
    }

    protected void initDeviceInstance() {
        psamReader = DeviceHelper.me().getPSamReader();
    }


    public void powerUp(View v) {
        outputBlueText(">>> powerUp");
        try {
            psamReader.initModule(Voltage.ICCpuCard.VOL_5, PowerMode.EMV);
            BytesValue atr = new BytesValue();
            IntValue protocol = new IntValue();
            int ret = psamReader.powerUp(atr, protocol);
            if (ret != ICError.SUCCESS) {
                outputRedText("powerUp fail: " + getErrorDetail(ret));
                return;
            }
            outputText("powerUp success");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void exchangeApdu(View v) {
        outputBlueText(">>> exchangeApdu");
        try {
            byte[] apdu = new byte[]{0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x3F, 0x00};
            ApduResponse response = psamReader.exchangeApdu(apdu);
            outputApduMsg(response);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void outputApduMsg(ApduResponse response) {
        if (response.getAPDURet() != ICError.SUCCESS) {
            outputRedText("exchangeApdu fail: " + getErrorDetail(response.getAPDURet()));
        } else {
            outputText("exchangeApdu success");
        }
        outputText("APDURet = " + Integer.toHexString(response.getAPDURet()));
        outputText("sw1 = " + Integer.toHexString(response.getSW1() & 0xFF));
        outputText("sw2 = " + Integer.toHexString(response.getSW2() & 0xFF));
        outputText("data = " + BytesUtil.bytes2HexString(response.getData()));
    }

    public void powerDown(View v) {
        outputBlueText(">>> powerDown");
        try {
            int ret = psamReader.powerDown();
            if (ret != ICError.SUCCESS) {
                outputRedText("powerDown fail: " + getErrorDetail(ret));
                return;
            }
            outputText("powerDown success");
        } catch (Exception e) {
            handleException(e);
        }
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
