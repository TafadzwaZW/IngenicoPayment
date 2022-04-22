package cn.eas.usdk.demo.view.dock;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.DeviceServiceData;
import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.dock.DockName;
import com.usdk.apiservice.aidl.dock.serialport.DockPortError;
import com.usdk.apiservice.aidl.dock.serialport.UDockPort;

import java.util.Arrays;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;

public class DockPortActivity extends BaseDockActivity {

    private UDockPort dockPort;
    private String dockName;

    private EditText etTimeout;
    private EditText etReadExpLength;
    private EditText etLengthWrite;
    private EditText etDataWrite;

    private int timeout;
    private int expLengthRead;
    private int lengthWrite;
    private byte[] sendData;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dock_port);
        setTitle("DockPort Module");

        dockName = getIntent().getStringExtra(DeviceServiceData.DOCK_NAME);
        initView(dockName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDeviceInstance();
    }

    protected void initDeviceInstance() {
        if (DockName.BT_DOCK.equals(dockName)) {
            dockPort = DeviceHelper.me().getBTDockPort(DemoConfig.BT_DOCK_PORT_NAME);
        } else {
            dockPort = DeviceHelper.me().getWifiDockPort(DemoConfig.WIFI_DOCK_PORT_NAME);
        }
    }

    private void initView(final String dockName) {
        setSettingClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DockPortSettingActivity.class, dockName);
            }
        });
        etTimeout = bindViewById(R.id.etTimeout);
        etReadExpLength = bindViewById(R.id.etReadExpLength);
        etLengthWrite = bindViewById(R.id.etLengthWrite);
        etDataWrite = bindViewById(R.id.etDataWrite);

        initWriteData();
    }

    private void initWriteData() {
        etLengthWrite.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (!checkLengthWriteInput()) {
                        return;
                    }

                    String data = genBytesData(lengthWrite);
                    etDataWrite.setText(data);
                }
            }
        });
    }

    public void open(View v) {
        hideSoftInput();
        outputBlueText(">>> open");
        try {
            int ret = dockPort.open(DemoConfig.WIFI_DOCK_BPS, DemoConfig.DOCK_PAR, DemoConfig.DOCK_DBS);
            if (ret != DockPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("open success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void updateConfig(View v) {
        hideSoftInput();
        outputBlueText(">>> updateConfig");
        try {
            int ret = dockPort.updateConfig(DemoConfig.WIFI_DOCK_BPS, DemoConfig.DOCK_PAR, DemoConfig.DOCK_DBS);
            if (ret != DockPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("updateConfig success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void read(View v) {
        outputBlueText(">>> read");
        if (!checkTimeoutInput() || !checkExpLengthInput()) {
            return;
        }
        hideSoftInput();

        final AlertDialog dialog = showProgress( "Reading...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BytesValue outData = new BytesValue();
                    int ret = dockPort.read(expLengthRead, outData, timeout);

                    dialog.dismiss();

                    if (ret != DockPortError.SUCCESS) {
                        outputRedText(getErrorDetail(ret));
                        return;
                    }

                    outputText("Readed length: " + outData.getData().length);
                    outputText("Data: " + BytesUtil.bytes2HexString(outData.getData()));
                } catch (Exception e) {
                    dialog.dismiss();
                    handleException(e);
                }
            }
        }).start();
    }

    public void write(View v) {
        outputBlueText(">>> write");
        if (!checkTimeoutInput() || !checkDataInput()) {
            return;
        }
        hideSoftInput();

        try {
            int ret = dockPort.write(sendData, timeout);
            if (ret != DockPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("write success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isBufferEmpty(View v) {
        outputBlueText(">>> isBufferEmpty" );
        try {
            boolean isBufferEmpty = dockPort.isBufferEmpty();
            outputText("=> isBufferEmpty: " + isBufferEmpty);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void clearBuffer(View v) {
        outputBlueText(">>> clearBuffer");
        try {
            int ret = dockPort.clearBuffer();
            if (ret != DockPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("clearBuffer success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void close(View v) {
        outputBlueText(">>> close");
        try {
            int ret = dockPort.close();
            if (ret != DockPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("close success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void test(View v) {
        try {
            outputBlueText(">>> Inner test");
            if (!checkTimeoutInput() || !checkExpLengthInput() || !checkDataInput()) {
                return;
            }
            hideSoftInput();

            int ret = dockPort.open(DemoConfig.WIFI_DOCK_BPS, DemoConfig.DOCK_PAR, DemoConfig.DOCK_DBS);
            if (ret != DockPortError.SUCCESS) {
                outputRedText("open fail:" + ret);
                return;
            }
            outputText("open success");

            ret = dockPort.clearBuffer();
            if (ret != DockPortError.SUCCESS) {
                outputRedText("clearBuffer fail:" + ret);
                return;
            }
            outputText("clearBuffer success");

            // start the timer
            long startTime = System.currentTimeMillis();

            ret = dockPort.write(sendData, timeout);
            if (ret != DockPortError.SUCCESS) {
                outputRedText("write fail:" + ret);
                return;
            }
            outputText("write success");

            String expectLen = etReadExpLength.getText().toString();
            BytesValue outData = new BytesValue();
            ret = dockPort.read(Integer.valueOf(expectLen), outData, timeout);

            outputTimeSpace(startTime);

            if (ret != DockPortError.SUCCESS) {
                outputRedText("read fail:" + ret);
                return;
            }

            //Compare read and write data consistency
            byte[] readData = outData.getData();
           if (Arrays.equals(sendData, readData)) {
               outputText(Color.DKGRAY, "Read and write data comparison results: consistent");
           } else {
               outputRedText("Read and write data comparison results: inconsistent");
           }

            Log.d(DemoConfig.TAG, "length readed: " + readData.length);
            Log.d(DemoConfig.TAG, "data readed: " + BytesUtil.bytes2HexString(readData));
            outputText("Readed length: " + readData.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void outputTimeSpace(long startTime) {
        long space = System.currentTimeMillis() - startTime;
        Log.d(DemoConfig.TAG, "write-read timespace: " + space);
        outputText(Color.DKGRAY, "write-read timespace:" + space);
    }

    private boolean checkTimeoutInput() {
        String timeoutStr = etTimeout.getText().toString();
        if (timeoutStr.isEmpty()) {
            toast(getString(R.string.request_timeout));
            etTimeout.requestFocus();
            return false;
        }
        timeout = Integer.valueOf(timeoutStr);
        return true;
    }

    private boolean checkExpLengthInput() {
        String expLengthStr = etReadExpLength.getText().toString();
        if (expLengthStr.isEmpty()) {
            toast(getString(R.string.request_read_exp_length));
            etReadExpLength.requestFocus();
            return false;
        }
        expLengthRead = Integer.valueOf(expLengthStr);
        return true;
    }

    private boolean checkLengthWriteInput() {
        String expLengthStr = etLengthWrite.getText().toString();
        if (expLengthStr.isEmpty()) {
            toast(getString(R.string.request_write_length));
            etLengthWrite.requestFocus();
            return false;
        }
        lengthWrite = Integer.valueOf(expLengthStr);
        return true;
    }

    private boolean checkDataInput() {
        String dataHexStr = etDataWrite.getText().toString();
        if (dataHexStr.isEmpty()) {
            toast(getString(R.string.request_data_written));
            etDataWrite.requestFocus();
            return false;
        }
        sendData = BytesUtil.hexString2Bytes(dataHexStr);
        return true;
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case DockPortError.OTHER: message = "OTHER"; break;
            case DockPortError.DEVICE_USED: message = "DEVICE_USED"; break;
            case DockPortError.TIMEOUT: message = "TIMEOUT"; break;
            case DockPortError.ERROR_PARAM: message = "ERROR_PARAM"; break;
            case DockPortError.DEVICE_DISABLE: message = "DEVICE_DISABLE"; break;
            case DockPortError.INIT_ERR: message = "INIT_ERR"; break;
            case DockPortError.ERROR_COMM: message = "ERROR_COMM"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }

    static String genBytesData(int len) {
        StringBuffer dataBuf = new StringBuffer();
        for (int i = 1; i <= len; i++) {
            dataBuf.append(String.format("%02X", (byte)i));
        }
        return dataBuf.toString();
    }
}
