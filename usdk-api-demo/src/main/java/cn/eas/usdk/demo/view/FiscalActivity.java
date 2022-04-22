package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.fiscal.FiscalError;
import com.usdk.apiservice.aidl.fiscal.UFiscal;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BytesUtil;

public class FiscalActivity extends BaseDeviceActivity {
    private UFiscal fiscal;

    private EditText writeDataEdit, readSizeEdit;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_fiscal);
        setTitle("Fiscal Module");

        writeDataEdit = findViewById(R.id.writeDataEdit);
        readSizeEdit = findViewById(R.id.readSizeEdit);
    }

    protected void initDeviceInstance() {
        fiscal = DeviceHelper.me().getFiscal();
    }

    public void open(View v) {
        outputBlueText(">>> open");
        try {
            int ret = fiscal.open(0);
            if (ret != FiscalError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("open success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void close(View v) {
        outputBlueText(">>> close");
        try {
            int ret = fiscal.close();
            if (ret != FiscalError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("close success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void writeData(View v) {
        outputBlueText(">>> writeData ");
        try {
            String data = writeDataEdit.getText().toString();
            byte[] writeData = BytesUtil.hexString2Bytes(data);
            int ret = fiscal.writeData(writeData);
            if (ret != FiscalError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("writeData success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void readData(View v) {
        outputBlueText(">>> readData ");
        try {
            BytesValue dataOut = new BytesValue();
            int ret = fiscal.readData(getReadSize(), dataOut);
            if (ret != FiscalError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("read success : " + dataOut.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    private int getReadSize() {
        String readSize = readSizeEdit.getText().toString();
        if (TextUtils.isEmpty(readSize)) {
            return -1;
        }
        return Integer.valueOf(readSize);
    }

    public void powerOn(View v) {
        outputBlueText(">>> powerOn ");
        try {
            int ret = fiscal.powerOn();
            if (ret != FiscalError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("powerOn success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void powerOff(View v) {
        outputBlueText(">>> powerOff ");
        try {
            int ret = fiscal.powerOff();
            if (ret != FiscalError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("powerOff success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isPowerOn(View v) {
        outputBlueText(">>> isPowerOn ");
        try {
            BooleanValue isPowerOn = new BooleanValue();
            int ret = fiscal.isPowerOn(isPowerOn);
            if (ret != FiscalError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("power state: " + (isPowerOn.isTrue() ? "on" : "off"));
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case FiscalError.ERROR_DEVICE_DISABLE: message = "ERROR_DEVICE_DISABLE"; break;
            case FiscalError.ERROR_PARAM: message = "ERROR_PARAM"; break;
            case FiscalError.ERROR_FAIL: message = "ERROR_FAIL"; break;
            case FiscalError.ERROR_HANDLE: message = "ERROR_HANDLE(Maybe it's not open)"; break;
            case FiscalError.ERROR_NOTPOWER: message = "ERROR_NOTPOWER"; break;
            case FiscalError.ERROR_CONN_FAIL: message = "ERROR_CONN_FAIL"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
