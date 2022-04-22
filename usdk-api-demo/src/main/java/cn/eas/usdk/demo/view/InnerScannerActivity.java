package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.constants.InnerScannerDeviceName;
import com.usdk.apiservice.aidl.innerscanner.InnerScanError;
import com.usdk.apiservice.aidl.innerscanner.OnInnerScanListener;
import com.usdk.apiservice.aidl.innerscanner.UInnerScanner;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class InnerScannerActivity extends BaseDeviceActivity {

    private static final List<String> LIST_DEVICES = Arrays.asList(
            InnerScannerDeviceName.SCAN,
            InnerScannerDeviceName.SCAN_FRONT,
            InnerScannerDeviceName.SCAN_MAIN,
            InnerScannerDeviceName.SCAN_MODULE);

    private NiceSpinner spDevices;
    private UInnerScanner innerScanner;
    private String device;


    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_innerscanner);
        setTitle("InnerScanner Module");

        initViews();
    }

    private void initViews() {
        spDevices = findViewById(R.id.spDevices);
        spDevices.attachDataSource(LIST_DEVICES);
        spDevices.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                device = LIST_DEVICES.get(position);
            }
        });
        setSpinnerDefaultValue(spDevices, LIST_DEVICES, InnerScannerDeviceName.SCAN);
        device = LIST_DEVICES.get(0);
    }

    protected void initDeviceInstance() {
        innerScanner = DeviceHelper.me().getInnerScanner();
    }

    public void startScan(View v) {
        try {
            outputBlueText(">>> startScan ");
            innerScanner.setOnScanListener(new OnInnerScanListener.Stub() {
                @Override
                public void onSuccess(String barcode) throws RemoteException {
                    outputText("=> " + barcode);
                }

                @Override
                public void onFail(int error) throws RemoteException {
                    outputRedText("=> onError | " + getErrorDetail(error));
                }
            });
            innerScanner.setDevice(device, 0);
            innerScanner.startScan(10);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void stopScan(View v) {
        try {
            outputBlueText(">>> stopScan ");
            innerScanner.stopListener();
            innerScanner.stopScan();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setParameter(View v) {
        outputBlueText(">>> setParameter");
        try {
            //  enable code 11 barcode scanning
            int ret = innerScanner.setParameter(new byte[]{2, 0x0A, 1});
            if (ret != InnerScanError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("setParameter success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case InnerScanError.ERROR_EMERR: message = "ERROR_EMERR"; break;
            case InnerScanError.ERROR_ERRPARAM: message = "ERROR_ERRPARAM"; break;
            case InnerScanError.ERROR_FAIL: message = "ERROR_FAIL"; break;
            case InnerScanError.ERROR_NOINIT: message = "ERROR_NOINIT"; break;
            case InnerScanError.ERROR_PARAM_NOTSUPPOR: message = "ERROR_PARAM_NOTSUPPOR"; break;
            case InnerScanError.ERROR_TIMEOUT: message = "ERROR_TIMEOUT"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
