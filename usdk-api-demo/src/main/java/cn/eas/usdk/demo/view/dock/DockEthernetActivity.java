package cn.eas.usdk.demo.view.dock;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.usdk.apiservice.aidl.dock.PortName;
import com.usdk.apiservice.aidl.dock.ethernet.ApplyEthernetConfigListener;
import com.usdk.apiservice.aidl.dock.ethernet.DockEthError;
import com.usdk.apiservice.aidl.dock.ethernet.EthernetConfigParam;
import com.usdk.apiservice.aidl.dock.ethernet.EthernetInfo;
import com.usdk.apiservice.aidl.dock.ethernet.GetEthernetConfigListener;
import com.usdk.apiservice.aidl.dock.ethernet.GetEthernetInfoListener;
import com.usdk.apiservice.aidl.dock.ethernet.SetEthernetInfoListener;
import com.usdk.apiservice.aidl.dock.ethernet.UDockEthernet;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class DockEthernetActivity extends BaseDeviceActivity {
    private static List<String> portNameList = new ArrayList<>();
    static {
        portNameList.add(PortName.ETH);
    }

    private TextView dockEthInfoText;
    private RadioButton dhcpRadio;
    private RadioButton staticRadio;
    private EditText etIp;
    private EditText etInnerIp;
    private EditText etGateway;
    private EditText etMask;
    private EditText etDns1;
    private EditText etDns2;
    private EditText etDns3;
    private EditText etMac;

    private CheckBox cbMode;
    private CheckBox cbIp;
    private CheckBox cbGateway;
    private CheckBox cbMask;
    private CheckBox cbDns1;
    private CheckBox cbDns2;
    private CheckBox cbDns3;
    private CheckBox cbMac;

    private UDockEthernet ethernet;
    private String portName = portNameList.get(0);
    private int mode;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dock_ethernet);
        setTitle("DockEthernet Module");
        initDeviceInstance();
        initView();
    }

    protected void initDeviceInstance() {
        ethernet = DeviceHelper.me().getWifiDockEthernet(portName);
    }

    private void initView() {
        initSpinner();
        initRadio();
        initFoldView();

        etIp = findViewById(R.id.etIp);
        etInnerIp = findViewById(R.id.etInnerIp);
        etGateway = findViewById(R.id.etGateway);
        etMask = findViewById(R.id.etMask);
        etDns1 = findViewById(R.id.etDns1);
        etDns2 = findViewById(R.id.etDns2);
        etDns3 = findViewById(R.id.etDns3);
        etMac = findViewById(R.id.etMac);

        etIp.setText("172.20.45.86");
        etInnerIp.setText("172.20.45.86");
        etGateway.setText("172.20.45.1");
        etMask.setText("255.255.255.0");
        etDns1.setText("10.10.16.66");
        etDns2.setText("10.10.16.17");

        cbMode = findViewById(R.id.cb_mode);
        cbIp = findViewById(R.id.cb_ip);
        cbGateway = findViewById(R.id.cb_gateway);
        cbMask = findViewById(R.id.cb_mask);
        cbDns1 = findViewById(R.id.cb_dns_1);
        cbDns2 = findViewById(R.id.cb_dns_2);
        cbDns3 = findViewById(R.id.cb_dns_3);
        cbMac = findViewById(R.id.cb_mac);
    }

    private void initSpinner() {
        NiceSpinner portNameSpinner = findViewById(R.id.portNameSpinner);
        portNameSpinner.attachDataSource(portNameList);
        portNameSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                portName = portNameList.get(position);
                initDeviceInstance();
            }
        });
    }

    private void initRadio() {
        RadioGroup modeRadioGroup = bindViewById(R.id.modeRadioGroup);
        dhcpRadio = bindViewById(R.id.dhcpRadio);
        staticRadio = bindViewById(R.id.staticRadio);
        modeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int btnId = radioGroup.getCheckedRadioButtonId();
                mode = btnId == R.id.dhcpRadio ? 0 : 1;
            }
        });
    }

    private void initFoldView() {
        // 以太网参数
        View ethInfoSlipLayout = findViewById(R.id.ethInfoSlipLayout);
        final LinearLayout dockEthInfoLayout = findViewById(R.id.dockEthInfoLayout);
        dockEthInfoText = findViewById(R.id.dockEthInfoText);
        ethInfoSlipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView(dockEthInfoText, dockEthInfoLayout);
            }
        });
        resetView(dockEthInfoText, dockEthInfoLayout);
    }

    private void resetView(TextView v, LinearLayout layout) {
        boolean isFold = layout.getVisibility() == View.GONE;

        Drawable drawable  = getResources().getDrawable(isFold ? R.drawable.fold : R.drawable.unfold);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        v.setCompoundDrawables(null, null, drawable, null);

        v.setText(isFold ? R.string.fold : R.string.unfold);
        layout.setVisibility(isFold ? View.VISIBLE : View.GONE);
    }

    public void open(View v) {
        hideSoftInput();
        outputBlueText(">>> open");
        try {
            int ret = ethernet.open();
            if (ret != DockEthError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("open success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void close(View v) {
        hideSoftInput();
        outputBlueText(">>> close");
        try {
            int ret = ethernet.close();
            if (ret != DockEthError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("close success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setEthernetInfo(View v) {
        hideSoftInput();
        outputBlueText(">>> setEthernetInfo");
        try {
            EthernetInfo info = new EthernetInfo();
            info.setMode(mode);
            info.setIp(etIp.getText().toString());
            info.setInnerIp(etInnerIp.getText().toString());
            info.setGateway(etGateway.getText().toString());
            info.setNetmask(etMask.getText().toString());
            info.setDns1(etDns1.getText().toString());
            info.setDns2(etDns2.getText().toString());
            info.setDns3(etDns3.getText().toString());
            ethernet.setEthernetInfo(info, new SetEthernetInfoListener.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    outputText("=> onSuccess");
                }

                @Override
                public void onError(int errCode) throws RemoteException {
                    outputRedText("=> onError | " + getErrorDetail(errCode));
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getEthernetInfo(View v) {
        hideSoftInput();
        outputBlueText(">>> getEthernetInfo");
        try {
            ethernet.getEthernetInfo(new GetEthernetInfoListener.Stub() {
                @Override
                public void onSuccess(final EthernetInfo info) throws RemoteException {
                    outputText("=> onSuccess | " + getString(info));
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (info.getMode() == 0) {
                                dhcpRadio.setChecked(true);
                            } else {
                                staticRadio.setChecked(true);
                            }

                            etIp.setText(info.getIp().trim());
                            etInnerIp.setText(info.getInnerIp().trim());
                            etGateway.setText(info.getGateway().trim());
                            etMask.setText(info.getNetmask().trim());
                            etDns1.setText(info.getDns1().trim());
                            etDns2.setText(info.getDns2().trim());
                            etDns3.setText(info.getDns3().trim());
                        }
                    });

                }

                @Override
                public void onError(int errCode) throws RemoteException {
                    outputRedText("=> onError | " + getErrorDetail(errCode));
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setConfig(View v) {
        hideSoftInput();

        if (!cbMode.isChecked() && !cbIp.isChecked() && !cbGateway.isChecked()
                && !cbMask.isChecked() && !cbDns1.isChecked() && !cbDns2.isChecked()
                && !cbDns3.isChecked() && !cbMac.isChecked()) {
            toast("Please choose at least one");
            return;
        }

        outputBlueText(">>> setConfig");

        if (cbMode.isChecked()) {
            setConfigInner(EthernetConfigParam.CONFIG_WAN0_MODE, String.valueOf(mode));
        }
        if (cbIp.isChecked()) {
            setConfigInner(EthernetConfigParam.CONFIG_WAN0_IP, etIp.getText().toString());
        }
        if (cbGateway.isChecked()) {
            setConfigInner(EthernetConfigParam.CONFIG_WAN0_GATEWAY, etGateway.getText().toString());
        }
        if (cbMask.isChecked()) {
            setConfigInner(EthernetConfigParam.CONFIG_WAN0_NETMASK, etMask.getText().toString());
        }
        if (cbDns1.isChecked()) {
            setConfigInner(EthernetConfigParam.CONFIG_WAN0_DNS1, etDns1.getText().toString());
        }
        if (cbDns2.isChecked()) {
            setConfigInner(EthernetConfigParam.CONFIG_WAN0_DNS2, etDns2.getText().toString());
        }
        if (cbDns3.isChecked()) {
            setConfigInner(EthernetConfigParam.CONFIG_WAN0_DNS3, etDns3.getText().toString());
        }
        if (cbMac.isChecked()) {
            setConfigInner(EthernetConfigParam.CONFIG_WAN0_MAC, etMac.getText().toString());
        }
    }

    private void setConfigInner(String key, String value) {
        try {
            int ret = ethernet.setConfig(key, value);
            outputText(String.format("=> setConfig(%s, %s) | ret = %d", key, value, ret));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void applyConfig(View v) {
        hideSoftInput();
        outputBlueText(">>> applyConfig");

        try {
            ethernet.applyConfig(new ApplyEthernetConfigListener.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    outputText("=> onSuccess");
                }

                @Override
                public void onError(int errCode) throws RemoteException {
                    outputRedText("=> onError | " + getErrorDetail(errCode));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getConfig(View v) {
        hideSoftInput();
        outputBlueText(">>> getConfig");

        List<String> configParams = new ArrayList<>();
        if (cbMode.isChecked()) {
            configParams.add(EthernetConfigParam.CONFIG_WAN0_MODE);
        }
        if (cbIp.isChecked()) {
            configParams.add(EthernetConfigParam.CONFIG_WAN0_IP);
        }
        if (cbGateway.isChecked()) {
            configParams.add(EthernetConfigParam.CONFIG_WAN0_GATEWAY);
        }
        if (cbMask.isChecked()) {
            configParams.add(EthernetConfigParam.CONFIG_WAN0_NETMASK);
        }
        if (cbDns1.isChecked()) {
            configParams.add(EthernetConfigParam.CONFIG_WAN0_DNS1);
        }
        if (cbDns2.isChecked()) {
            configParams.add(EthernetConfigParam.CONFIG_WAN0_DNS2);
        }
        if (cbDns3.isChecked()) {
            configParams.add(EthernetConfigParam.CONFIG_WAN0_DNS3);
        }
        if (cbMac.isChecked()) {
            configParams.add(EthernetConfigParam.CONFIG_WAN0_MAC);
        }

        if (configParams.isEmpty()) {
            toast("Please choose at least one");
            return;
        }

        try {
            ethernet.getConfig(configParams, new GetEthernetConfigListener.Stub() {
                @Override
                public void onSuccess(Map map) throws RemoteException {
                    outputText("=> onSuccess");

                    HashMap<String, String> hashMap = new HashMap<>(map);
                    for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                        outputText(String.format("=> %s = %s", entry.getKey(), entry.getValue()));
                    }
                }

                @Override
                public void onError(int errCode) throws RemoteException {
                    outputRedText("=> onError | " + getErrorDetail(errCode));
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String getString(EthernetInfo info) {
        StringBuffer buf = new StringBuffer("\n");
        buf.append("mode = " + info.getMode()).append("\n");
        buf.append("IP = " + info.getIp()).append("\n");
        buf.append("InnerIp = " + info.getInnerIp()).append("\n");
        buf.append("GateWay = " + info.getGateway()).append("\n");
        buf.append("Netmask = " + info.getNetmask()).append("\n");
        buf.append("Dns1 = " + info.getDns1()).append("\n");
        buf.append("Dns2 = " + info.getDns2()).append("\n");
        buf.append("Dns3 = " + info.getDns3()).append("\n");
        return buf.toString();
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case DockEthError.OTHER: message = "OTHER"; break;
            case DockEthError.DEVICE_USED: message = "DEVICE_USED"; break;
            case DockEthError.TIMEOUT: message = "TIMEOUT"; break;
            case DockEthError.ERROR_PARAM: message = "ERROR_PARAM"; break;
            case DockEthError.DEVICE_DISABLE: message = "DEVICE_DISABLE"; break;
            case DockEthError.ERROR_COMM: message = "ERROR_COMM"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
