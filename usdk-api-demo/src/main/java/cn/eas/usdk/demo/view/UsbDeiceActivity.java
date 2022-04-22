package cn.eas.usdk.demo.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.usbdevice.UUsbDevice;
import com.usdk.apiservice.aidl.usbdevice.UsbDeviceError;
import com.usdk.apiservice.aidl.usbdevice.UsbType;
import org.angmarch.views.NiceSpinner;
import java.util.ArrayList;
import java.util.List;
import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.BytesUtil;

public class UsbDeiceActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> usbTypeList = new ArrayList<>();
    private String usbType = UsbType.HIDKBD;
    static {
        usbTypeList.add(new SpinnerItem(UsbType.HIDKBD, "HIDKBD"));
        usbTypeList.add(new SpinnerItem(UsbType.CDC, "CDC"));
        usbTypeList.add(new SpinnerItem(UsbType.HIDBIDIR, "HIDBIDIR"));
    }

    private UUsbDevice uUsbDevice;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_usbdevice);
        setTitle("UsbDevice Module");
        initView();
    }

    protected void initDeviceInstance() {
        uUsbDevice = DeviceHelper.me().getUsbDevice(usbType);
    }

    private void initView() {
        initSpinner();
    }


    private void initSpinner() {
        NiceSpinner usbTypeListSpinner = (NiceSpinner) findViewById(R.id.usbTypeSpinner);
        usbTypeListSpinner.attachDataSource(usbTypeList);
        usbTypeListSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                usbType = usbTypeList.get(position).getStringValue();
                initDeviceInstance();
            }
        });
    }

    public void open(View v) {
        outputBlueText(">>> open");
        try {
            int ret = uUsbDevice.open(0);
            if (ret != UsbDeviceError.SUCCESS) {
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
            int ret = uUsbDevice.close();
            if (ret != UsbDeviceError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("close success");
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
                    BytesValue buffer = new BytesValue();
                    int ret = uUsbDevice.read(expectLen, buffer, timeout);

                    dialog.dismiss();

                    if (ret != UsbDeviceError.SUCCESS) {
                        outputRedText(getErrorDetail(ret));
                        return;
                    }

                    outputText("Readed length: " + buffer.getData().length);
                    outputText("Data: " + BytesUtil.bytes2HexString(buffer.getData()));
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
            byte[] data = new byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36};
            int lengthOrError = uUsbDevice.write(data, 5000);
            if (lengthOrError < 0) {
                outputRedText(getErrorDetail(lengthOrError));
                return;
            }
            outputText("write success: " + BytesUtil.bytes2HexString(data));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isRxEmpty(View v) {
        outputBlueText(">>> isBufferEmpty" );
        try {
            IntValue status = new IntValue();
            int ret =  uUsbDevice.isRxEmpty(status);
            if (ret != UsbDeviceError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("=> Receive Buffer isEmpty: " + (status.getData() == 0));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void clearRx(View v) {
        outputBlueText(">>> clearRx");
        try {
            int ret = uUsbDevice.clearRx();
            if (ret != UsbDeviceError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("clearRx success");
        } catch (Exception e) {
            handleException(e);
        }
    }


    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case UsbDeviceError.ERROR_DEVICE: message = "The device does not exist or cannot be used"; break;
            case UsbDeviceError.ERROR_HANDLE: message = "The handle is not valid"; break;
            case UsbDeviceError.ERROR_TIMEOUT: message = "Timeout (when data is sent or received)"; break;
            case UsbDeviceError.ERROR_PARAM: message = "The argument is wrong"; break;
            case UsbDeviceError.ERROR_FAIL: message = "Operating fail (device not open, etc.)"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
