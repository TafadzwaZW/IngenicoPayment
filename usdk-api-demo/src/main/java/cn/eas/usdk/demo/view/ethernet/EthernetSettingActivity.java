package cn.eas.usdk.demo.view.ethernet;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.usdk.apiservice.aidl.ethernet.EthernetData;
import com.usdk.apiservice.aidl.ethernet.UEthernet;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

/**
 * Set the Ethernet connection parameters
 */
public class EthernetSettingActivity extends BaseDeviceActivity {

    private UEthernet ethernet;

    private RadioButton dhcpRadio;
    private RadioButton staticRadio;
    private LinearLayout llStatic;
    private EditText etLocalIp;
    private EditText etGateway;
    private EditText etMask;
    private EditText etDns1;
    private EditText etDns2;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_ethernet_setting);
        setTitle("Ethernet Setting");
        initView();
    }

    protected void initDeviceInstance() {
        ethernet = DeviceHelper.me().getEthernet();
    }

    private void initView() {
        RadioGroup typeRadioGroup = bindViewById(R.id.typeRadioGroup);
        dhcpRadio = bindViewById(R.id.dhcpRadio);
        staticRadio = bindViewById(R.id.staticRadio);
        typeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                hideSoftInput();
                llStatic.setVisibility(radioGroup.getCheckedRadioButtonId() == R.id.dhcpRadio ? View.GONE : View.VISIBLE);
            }
        });

        llStatic = bindViewById(R.id.llStatic);
        etLocalIp = bindViewById(R.id.etLocalIp);
        etGateway = bindViewById(R.id.etGateway);
        etMask = bindViewById(R.id.etMask);
        etDns1 = bindViewById(R.id.etDns1);
        etDns2 = bindViewById(R.id.etDns2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Bundle configInfo = ethernet.getConfigInfo();
            boolean isDhcp = configInfo.getBoolean(EthernetData.IS_DHCP);
            dhcpRadio.setChecked(isDhcp);
            staticRadio.setChecked(! isDhcp);
            llStatic.setVisibility(isDhcp ? View.GONE : View.VISIBLE);

            etLocalIp.setText(configInfo.getString(EthernetData.LOCAL_IP));
            etGateway.setText(configInfo.getString(EthernetData.GATEWAY));
            etMask.setText(configInfo.getString(EthernetData.MASK));
            etDns1.setText(configInfo.getString(EthernetData.DNS1));
            etDns2.setText(configInfo.getString(EthernetData.DNS2));
        } catch (Exception e){
            handleException(e);
        }
    }

    @Override
    protected <T extends View> T bindViewById(int id) {
        return (T) getWindow().findViewById(id);
    }

    public void save(View v) {
        try {
            boolean isSucc = ethernet.config(getConfigBundle());
            if (isSucc) {
                toast("config success");
            } else {
                toast("config fail!!");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private Bundle getConfigBundle() {
        Bundle param = new Bundle();
        if (dhcpRadio.isChecked()) {
            param.putBoolean(EthernetData.IS_DHCP, true);
            return param;
        }

        hideSoftInput();
        String localIp = etLocalIp.getText().toString();
        String gateway = etGateway.getText().toString();
        String mask = etMask.getText().toString();
        String dns1 = etDns1.getText().toString();
        String dns2 = etDns2.getText().toString();

        // This is only for test!!
        if ("null".equals(dns2)) {
            return null;
        }

        param.putBoolean(EthernetData.IS_DHCP, false);
        param.putString(EthernetData.LOCAL_IP, localIp);
        param.putString(EthernetData.GATEWAY, gateway);
        param.putString(EthernetData.MASK, mask);
        param.putString(EthernetData.DNS1, dns1);
        param.putString(EthernetData.DNS2, dns2);

        return param;
    }
}
