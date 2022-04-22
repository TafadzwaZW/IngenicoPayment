package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.felicareader.CommandListener;
import com.usdk.apiservice.aidl.felicareader.FelicaError;
import com.usdk.apiservice.aidl.felicareader.UFelicaReader;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.BytesUtil;

public class FelicaReaderActivity extends BaseDeviceActivity {

    private UFelicaReader felicaReader;

    private EditText timeoutEt;
    private EditText sendDataEt;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_felicareader);
        setTitle("FelicaReader Module");
        timeoutEt = bindViewById(R.id.timeoutEt);
        sendDataEt = bindViewById(R.id.sendDataEt);
    }

    protected void initDeviceInstance() {
        felicaReader = DeviceHelper.me().getFelicaReader();
    }

    public void open(View v) {
        outputBlueText(">>> open");
        try {
            int ret = felicaReader.open();
            if (ret != FelicaError.SUCCESS) {
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
            int ret = felicaReader.close();
            if (ret != FelicaError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("close success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void commandSyn(View v) {
        outputBlueText(">>> commandSyn ");
        try {
            BytesValue dataOut = new BytesValue();
            int ret = felicaReader.commandSyn(getTimeout(), getSendData(), dataOut);
            if (ret != FelicaError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("=> responseData = " + dataOut.toHexString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void commandAsyn(View v) {
        outputBlueText(">>> commandAsyn ");
        try {
            felicaReader.commandAsyn(getTimeout(), getSendData(), new CommandListener.Stub() {
                @Override
                public void onSuccess(byte[] responseData) throws RemoteException {
                    outputText("=> onSuccess");
                    outputText("responseData = " + BytesUtil.bytes2HexString(responseData));
                }

                @Override
                public void onError(int error) throws RemoteException {
                    outputRedText("=> onError: " + getErrorDetail(error));
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private int getTimeout() throws IllegalStateException {
        String timeout = timeoutEt.getText().toString();
        if (timeout.length() == 0) {
            throw new IllegalStateException("Please input timeout");
        }
        return Integer.valueOf(timeout);
    }

    private byte[] getSendData() throws IllegalStateException {
        String sendData = sendDataEt.getText().toString();
        if (sendData.length() == 0) {
            throw new IllegalStateException("Please input sendData");
        }
        return BytesUtil.hexString2Bytes(sendData);
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case FelicaError.ERROR_PARAM: message = "Parameter error"; break;
            case FelicaError.ERROR_NOT_OPEN: message = "ERROR_NOT_OPEN"; break;
            case FelicaError.ERROR_TIMEOUT: message = "ERROR_TIMEOUT"; break;
            case FelicaError.ERROR_TRANSERR: message = "ERROR_TRANSERR"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
