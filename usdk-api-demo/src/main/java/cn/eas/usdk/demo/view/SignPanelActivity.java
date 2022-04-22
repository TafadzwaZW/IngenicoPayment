package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.printer.FactorMode;
import com.usdk.apiservice.aidl.printer.OnPrintListener;
import com.usdk.apiservice.aidl.printer.UPrinter;
import com.usdk.apiservice.aidl.serialport.DeviceName;
import com.usdk.apiservice.aidl.signpanel.DataFormat;
import com.usdk.apiservice.aidl.signpanel.OnSignListener;
import com.usdk.apiservice.aidl.signpanel.Parameter;
import com.usdk.apiservice.aidl.signpanel.SignPanelData;
import com.usdk.apiservice.aidl.signpanel.SignPanelError;
import com.usdk.apiservice.aidl.signpanel.USignPanel;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class SignPanelActivity extends BaseDeviceActivity {
    final List<String> devNameList = new LinkedList<>(Arrays.asList( DeviceName.USBH));

    private USignPanel signPanel;
    private UPrinter printer;

    private CheckBox shieldCBox;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_signpanel);
        setTitle("SignPanel Module");
        initSpinner();
        initCheckBox();
    }

    protected void initDeviceInstance(String deviceName) {
        signPanel = DeviceHelper.me().getSignPanel(deviceName);
        printer = DeviceHelper.me().getPrinter();
    }

    private void initSpinner() {
        initDeviceInstance(devNameList.get(0));
        NiceSpinner deviceSpinner = bindViewById(R.id.deviceSpinner);
        deviceSpinner.attachDataSource(devNameList);
        deviceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                initDeviceInstance(devNameList.get(position));
            }
        });
    }

    private void initCheckBox() {
        shieldCBox = bindViewById(R.id.shieldCBox);
        shieldCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSlted) {
                switchCheckSigned(isSlted);
            }
        });
    }

    /**
     * @param isShield 是否屏蔽确认键 Whether to disable the confirm key when the signature is empty.
     */
    private void switchCheckSigned(boolean isShield) {
        try {
            int value = isShield ? 0x01 : 0x00;
            boolean isSucc = signPanel.setParameter(Parameter.CHECKSIGNED, value);
            if (isSucc) {
                outputText("setParameter CHECKSIGNED success");
            } else {
                outputRedText("setParameter CHECKSIGNED fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void open(View v) {
        outputBlueText(">>> open");
        try {
            boolean ret = signPanel.open();
            if (!ret) {
                outputRedText("open fail");
                return;
            }
            outputText("open success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getParameter(View v) {
        try {
            IntValue paramValue = new IntValue();
            boolean isSucc = signPanel.getParameter(Parameter.CHECKSIGNED, paramValue);
            if (isSucc) {
                shieldCBox.setChecked(paramValue.getData() == 0x01);
                outputText("getParameter CHECKSIGNED success");
                return;
            }
            outputRedText("getParameter CHECKSIGNED fail");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void close(View v) {
        outputBlueText(">>> close");
        try {
            signPanel.close();
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void startSign(View v) {
        outputBlueText(">>> startSign");
        try {
            Bundle param = new Bundle();
            param.putString(SignPanelData.SPECIAL_CODE, "abc");
            param.putInt(SignPanelData.TIMEOUT, 10);
            param.putInt(SignPanelData.DATA_FORMAT, DataFormat.BMP);
            signPanel.startSign(param, new OnSignListener.Stub(){
                @Override
                public void onSuccess(byte[] signData) throws RemoteException {
                    outputText("=> onSuccess | signData size = " + signData.length);

                    printBitmap(signData);
                }

                @Override
                public void onCancel() throws RemoteException {
                    outputRedText("=> onCancel");
                }

                @Override
                public void onTimeout() throws RemoteException {
                    outputRedText("=> onTimeout");
                }

                @Override
                public void onError(int err) throws RemoteException {
                    outputRedText("=> onError | " + getErrorDetail(err));
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void printBitmap(byte[] imageData) {
        outputBlueText(">>> print bitmap");
        try {
            printer.addBmpImage(0, FactorMode.BMP1X1, imageData);
            printer.feedLine(4);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onFinish() throws RemoteException {
                    outputText("=> onFinish");
                }

                @Override
                public void onError(int err) throws RemoteException {
                    outputRedText("=> onError | " + err);
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case SignPanelError.ERROR_ABOLISH: message = "ERROR_ABOLISH"; break;
            case SignPanelError.ERROR_ALLOCERR: message = "ERROR_ALLOCERR"; break;
            case SignPanelError.ERROR_DEVICE_DISABLE: message = "ERROR_DEVICE_DISABLE"; break;
            case SignPanelError.ERROR_DEVICE_USED: message = "ERROR_DEVICE_USED"; break;
            case SignPanelError.ERROR_HANDLE: message = "ERROR_HANDLE"; break;
            case SignPanelError.ERROR_OTHER: message = "ERROR_OTHER"; break;
            case SignPanelError.ERROR_PARAM: message = "ERROR_PARAM"; break;
            case SignPanelError.ERROR_SIGN_COMMERR: message = "ERROR_SIGN_COMMERR"; break;
            case SignPanelError.ERROR_CANCEL: message = "ERROR_CANCEL"; break;
            case SignPanelError.ERROR_TIMEOUT: message = "ERROR_TIMEOUT"; break;
            case SignPanelError.ERROR_RECONNECT: message = "ERROR_RECONNECT"; break;
            case SignPanelError.ERROR_DISCONNECT: message = "ERROR_DISCONNECT"; break;

            case SignPanelError.ERROR_GETSIGNDATA: message = "ERROR_GETSIGNDATA"; break;
            case SignPanelError.ERROR_DECOMPRESSION: message = "ERROR_DECOMPRESSION"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
