package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.dock.PortName;
import com.usdk.apiservice.aidl.exscanner.ExScannerChannel;
import com.usdk.apiservice.aidl.exscanner.ExScannerData;
import com.usdk.apiservice.aidl.exscanner.OnExScanListener;
import com.usdk.apiservice.aidl.exscanner.UExScanner;
import com.usdk.apiservice.aidl.serialport.DeviceName;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;

public class ExScannerActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> scannerTypeList = new LinkedList<>();
    private static List<String> devNameList = new LinkedList<>();
    private static List<SpinnerItem> channelList = new LinkedList<>();

    static {
        scannerTypeList.add(new SpinnerItem(0,"BR200-B01/B02"));
        scannerTypeList.add(new SpinnerItem(1,"BR200-A01/A02/C01/C02"));

        devNameList.add(DeviceName.USB_CDC_ACM);
        devNameList.add(DeviceName.USBH);
        devNameList.add(DeviceName.USBD);
        devNameList.add(DeviceName.COM1);
        devNameList.add(DeviceName.COM2);

        devNameList.add(PortName.ETH);
        devNameList.add(PortName.BASEUSBD);
        devNameList.add(PortName.BASECOM0);
        devNameList.add(PortName.BASEUSBCOM1);
        devNameList.add(PortName.BASEUSBCOM1_1);
        devNameList.add(PortName.BASEUSBCOM1_1);
        devNameList.add(PortName.BASEUSBCOM1_2);
        devNameList.add(PortName.BASEUSBCOM1_3);
        devNameList.add(PortName.BASEUSBCOM1_4);

        channelList.add(new SpinnerItem(ExScannerChannel.WIRED, "WIRED"));
        channelList.add(new SpinnerItem(ExScannerChannel.WIFI, "WIFI"));
        channelList.add(new SpinnerItem(ExScannerChannel.BT, "BT"));

    }

    private UExScanner exScanner;
    /**
     * Type of scan gun，0:BR200 B01 scan gun，1:other scan gun(BR200-A01/A02)
     */
    private int scanType;
    private String deviceName;
    private int channel;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_exscanner);
        setTitle("ExScanner Module");

        scanType = scannerTypeList.get(0).getValue();
        deviceName = devNameList.get(0);
        channel = channelList.get(0).getValue();
        initDeviceInstance();
        initSpinner();
    }

    protected void initDeviceInstance() {
        exScanner = DeviceHelper.me().getExScanner(scanType, deviceName);
    }

    private void initSpinner() {
        NiceSpinner scannerTypeSpinner = findViewById(R.id.scannerTypeSpinner);
        scannerTypeSpinner.attachDataSource(scannerTypeList);
        scannerTypeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                scanType = scannerTypeList.get(position).getValue();
                stopScan();
                initDeviceInstance();
            }
        });

        NiceSpinner deviceSpinner = findViewById(R.id.deviceSpinner);
        deviceSpinner.attachDataSource(devNameList);
        deviceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                deviceName = devNameList.get(position);
                stopScan();
                initDeviceInstance();
            }
        });

        NiceSpinner channelSpinner = findViewById(R.id.channelSpinner);
        channelSpinner.attachDataSource(channelList);
        channelSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                channel = channelList.get(position).getValue();
            }
        });
    }

    public void initChannel(View v) {
        outputBlueText(">>> initChannel");

        try {
            exScanner.initChannel(channel);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startScan(View v) {
        outputBlueText(">>> startScan");
        try {
            Bundle param = new Bundle();
            param.putString(ExScannerData.CHARSET_NAME, "UTF-8");
            exScanner.startScan(null, new OnExScanListener.Stub() {
                @Override
                public void onSuccess(String s) throws RemoteException {
                    outputText("=> onSuccess : " + s);
                }

                @Override
                public void onError(int i) throws RemoteException {
                    outputRedText("=> onError : " + i);
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void stopScan(View v) {
        outputBlueText(">>> stopScan");
        stopScan();
    }

    private void stopScan() {
        try {
            boolean isSucc = exScanner.stopScan();
            if (isSucc) {
                outputText("stop success");
            } else {
                outputRedText("stop fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }
}
