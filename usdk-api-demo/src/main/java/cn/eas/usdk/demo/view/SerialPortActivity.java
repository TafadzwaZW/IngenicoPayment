package cn.eas.usdk.demo.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.serialport.BaudRate;
import com.usdk.apiservice.aidl.serialport.DataBit;
import com.usdk.apiservice.aidl.serialport.DeviceName;
import com.usdk.apiservice.aidl.serialport.ParityBit;
import com.usdk.apiservice.aidl.serialport.SerialPortError;
import com.usdk.apiservice.aidl.serialport.USerialPort;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.BytesUtil;

public class SerialPortActivity extends BaseDeviceActivity {
    private static List<String> devNameList = new ArrayList<>();
    private static List<SpinnerItem> bpsList = new ArrayList<>();
    private static List<SpinnerItem> parList = new ArrayList<>();
    private static List<SpinnerItem> dbsList = new ArrayList<>();

    static {
        devNameList.add(DeviceName.USBD);
        devNameList.add(DeviceName.USBH);
        devNameList.add(DeviceName.BASECOM0);

        bpsList.add(new SpinnerItem(BaudRate.BPS_1200, "1200"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_2400, "2400"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_4800, "4800"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_9600, "9600"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_14400, "14400"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_19200, "19200"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_28800, "28800"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_38400, "38400"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_57600, "57600"));
        bpsList.add(new SpinnerItem(BaudRate.BPS_115200, "115200"));

        parList.add(new SpinnerItem(ParityBit.NOPAR, "NOPAR"));
        parList.add(new SpinnerItem(ParityBit.EVEN, "EVEN"));
        parList.add(new SpinnerItem(ParityBit.ODD, "ODD"));

        dbsList.add(new SpinnerItem(DataBit.DBS_8, "DBS_8"));
        dbsList.add(new SpinnerItem(DataBit.DBS_7, "DBS_7"));
    }

    private USerialPort serialPort;
    private String deviceName = devNameList.get(0);
    private int bps = bpsList.get(0).getValue();
    private int par = parList.get(0).getValue();
    private int dbs = dbsList.get(0).getValue();

    private Button getBaseConnStateBtn;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_serialport);
        setTitle("SerialPort Module");
        initView();
    }

    protected void initDeviceInstance() {
        serialPort = DeviceHelper.me().getSerialPort(deviceName);
    }

    private void initView() {
        getBaseConnStateBtn = findViewById(R.id.getBaseConnStateBtn);
        initBtn();
        initSpinner();
    }

    private void initBtn() {
        if (DeviceName.BASECOM0.equals(deviceName)) {
            getBaseConnStateBtn.setText("getBaseConnState");
        } else {
            String textSource = "getBaseConnState(<font color='#FF6100'> doesn't apply to '" + deviceName + "' </font>)";
            getBaseConnStateBtn.setText(Html.fromHtml(textSource));
        }
    }

    private void initSpinner() {
        NiceSpinner deviceSpinner = (NiceSpinner) findViewById(R.id.deviceSpinner);
        deviceSpinner.attachDataSource(devNameList);
        deviceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                deviceName = devNameList.get(position);
                initDeviceInstance();
                initBtn();
            }
        });

        // 波特率 Baud rate
        NiceSpinner bpsSpinner = (NiceSpinner) findViewById(R.id.bpsSpinner);
        bpsSpinner.attachDataSource(bpsList);
        bpsSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                bps = bpsList.get(position).getValue();
            }
        });
        // 效验位 Check bit
        NiceSpinner parSpinner = (NiceSpinner) findViewById(R.id.parSpinner);
        parSpinner.attachDataSource(parList);
        parSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               par = parList.get(position).getValue();
            }
        });
        // 数据位 Data bit
        NiceSpinner dbsSpinner = (NiceSpinner) findViewById(R.id.dbsSpinner);
        dbsSpinner.attachDataSource(dbsList);
        dbsSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dbs = dbsList.get(position).getValue();
            }
        });
    }

    public void open(View v) {
        outputBlueText(">>> open");
        try {
            int ret = serialPort.open();
            if (ret != SerialPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("open success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void init(View v) {
        outputBlueText(">>> init");
        try {
            int ret = serialPort.init(bps, par, dbs);
            if (ret != SerialPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("init success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void read(View v) {
        outputBlueText(">>> read");
        final int expectLen = 50;
        final int timeout = 5000;
        final AlertDialog dialog = showProgress(
                "Reading...\n" + String.format("Receive [%d] bytes data, timeout is [%d] ms", expectLen, timeout));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] data = new byte[expectLen];
                    int lengthOrError = serialPort.read(data, timeout);

                    dialog.dismiss();

                    if (lengthOrError < 0) {
                        outputRedText(getErrorDetail(lengthOrError));
                        return;
                    }

                    outputText("Readed length: " + lengthOrError);
                    outputText("Data: " + BytesUtil.bytes2HexString(BytesUtil.subBytes(data, 0, lengthOrError)));
                } catch (Exception e) {
                    dialog.dismiss();
                    handleException(e);
                }
            }
        }).start();
    }

    public void readData(View v) {
        outputBlueText(">>> readData");
        final int expectLen = 50;
        final int timeout = 5000;
        final AlertDialog dialog = showProgress(
                "Reading...\n" + String.format("Receive [%d] bytes data, timeout is [%d] ms", expectLen, timeout));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BytesValue outData = new BytesValue();
                    int ret = serialPort.readData(expectLen, outData, timeout);

                    dialog.dismiss();

                    if (ret != SerialPortError.SUCCESS) {
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
        try {
            byte[] data = new byte[]{0x31, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36};
            int lengthOrError = serialPort.write(data, 5000);
            if (lengthOrError < 0) {
                outputRedText(getErrorDetail(lengthOrError));
                return;
            }
            outputText("write success: " + BytesUtil.bytes2HexString(data));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isBufferEmpty(View v) {
        outputBlueText(">>> isBufferEmpty" );
        try {
            boolean isBufferEmpty =  serialPort.isBufferEmpty(true);
            outputText("=> Receive Buffer isEmpty: " + isBufferEmpty);

            isBufferEmpty =  serialPort.isBufferEmpty(false);
            outputText("=> Send Buffer isEmpty: " + isBufferEmpty);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void clearInputBuffer(View v) {
        outputBlueText(">>> clearInputBuffer");
        try {
            int ret = serialPort.clearInputBuffer();
            if (ret != SerialPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("clearInputBuffer success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getBaseConnState(View v) {
        outputBlueText(">>> getBaseConnState");
        try {
            int state = serialPort.getBaseConnState();
            outputText("=> state = " + getStateDecription(state));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void close(View v) {
        outputBlueText(">>> close");
        try {
            int ret = serialPort.close();
            if (ret != SerialPortError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("close success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static String getStateDecription(int state) {
        switch (state) {
            case SerialPortError.SUCCESS: return "Bluetooth base is connected";
            case SerialPortError.ERROR_BT_DISENABLE: return "Bluetooth Not Opened";
            case SerialPortError.ERROR_BTBASE_DISCONN: return "Bluetooth base is disconnected";
            case SerialPortError.ERROR_BTBASE_UNPAIRED: return "Bluetooth base is unpaired";
            case SerialPortError.ERROR_BTSERVICE_DISCONN: return "BT-service is disconnected";
            default: return "Unknown state[" + state + "]";
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case SerialPortError.ERROR_DEVICE_DISABLE: message = "The device does not exist or cannot be used"; break;
            case SerialPortError.ERROR_OTHERERR: message = "Other errors (operating system errors, etc.)"; break;
            case SerialPortError.ERROR_TIMEOUT: message = "Timeout (when data is sent or received)"; break;
            case SerialPortError.ERROR_PARAMERR: message = "Device not open or Parameter error"; break;
            case SerialPortError.ERROR_FAIL: message = "Operating fail (device not open, etc.)"; break;
            case SerialPortError.ERROR_BT_DISENABLE: message = "Bluetooth Not Opened"; break;
            case SerialPortError.ERROR_BTBASE_DISCONN: message = "Bluetooth base is disconnected"; break;
            case SerialPortError.ERROR_BTBASE_UNPAIRED: message = "Bluetooth base is unpaired"; break;
            case SerialPortError.ERROR_BTSERVICE_DISCONN: message = "BT service is disconnected"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
