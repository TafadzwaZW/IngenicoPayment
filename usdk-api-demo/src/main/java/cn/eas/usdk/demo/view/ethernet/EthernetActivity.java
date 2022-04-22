package cn.eas.usdk.demo.view.ethernet;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.usdk.apiservice.aidl.ethernet.EthernetData;
import com.usdk.apiservice.aidl.ethernet.UEthernet;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class EthernetActivity extends BaseDeviceActivity {
    private static final long REFRESH_INTERVAL = 1000;
    private static final String ETH0 = "eth0";

    private UEthernet ethernet;
    private String iface = ETH0;

    private ImageView existIV;
    private Switch ethSwitch;
    private TextView tvEthState;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_ethernet);
        setTitle("Ethernet Module");

        initView();
        refreshEthStatus();
    }

    protected void initDeviceInstance() {
        ethernet = DeviceHelper.me().getEthernet();
    }

    private void initView() {
        existIV = bindViewById(R.id.existIV);
        ethSwitch = bindViewById(R.id.ethSwitch);
        tvEthState = bindViewById(R.id.tvEthState);

        initSpinner();
        initSwitch();

        setSettingClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(EthernetSettingActivity.class);
            }
        });
    }

    private void initSpinner() {
        NiceSpinner deviceSpinner = bindViewById(R.id.deviceSpinner);
        final List<String> devNameList = new LinkedList<>(Arrays.asList(ETH0, "", "null", "123"));
        deviceSpinner.attachDataSource(devNameList);
        deviceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                iface = devNameList.get(position);
                // this is only for test!!
                if ("null".equals(iface)) {
                    iface = null;
                }
            }
        });
    }

    private void initSwitch() {
        ethSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    open();
                }else {
                    close();
                }
            }
        });
    }

    private void open() {
        outputBlueText(">>> open ");
        try {
            boolean isSucc = ethernet.open();
            if (isSucc) {
                outputText("open success");
            } else {
                outputRedText("open fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void close() {
        outputBlueText(">>> close ");
        try {
            boolean isSucc = ethernet.close();
            if (isSucc) {
                outputText("close success");
            } else {
                outputRedText("close fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ethSwitch.setChecked(isEthernetSwitchOn());
    }

    private boolean isEthernetSwitchOn() {
        try {
            return ethernet.getEthernetEnabledState();
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public void isEthernetEnabled(View v) {
        outputBlueText(">>> isEthernetEnabled ");
        try {
            if (ethernet.isEthernetEnabled()) {
                outputText("Ethernet is enable");
                return;
            }
            outputRedText("Ethernet is unEnable");
        } catch (Exception e ) {
            handleException(e);
        }
    }

    public void getInfo(View v) {
        outputBlueText(">>> getInfo ");
        try {
            Bundle bundle = ethernet.getInfo();
            outputText(" localIp = " + bundle.getString(EthernetData.LOCAL_IP));
            outputText(" gateway = " + bundle.getString(EthernetData.GATEWAY));
            outputText(" mask = " + bundle.getString(EthernetData.MASK));
            outputText(" dns1 = " + bundle.getString(EthernetData.DNS1));
            outputText(" dns2 = " + bundle.getString(EthernetData.DNS2));
            outputText(" mac = " + bundle.getString(EthernetData.MAC));
            outputText(" isDhcp = " + bundle.getBoolean(EthernetData.IS_DHCP));
        } catch (Exception e ) {
            handleException(e);
        }
    }

    private void refreshEthStatus() {
        uiHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                existIV.setEnabled(ethernet.isEthernetExist(iface));

                int state = ethernet.getEthernetState();
                tvEthState.setText(getStateDescription(state));
                tvEthState.setTextColor(getStateColor(state));
            } catch (Exception e) {
                outputRedText("getEthernetState fail!" + e.getMessage());
                return;
            }
            refreshEthStatus();
        }
    };

    private int getStateColor(int state) {
        int colorId;
        switch (state) {
            case 0:
                colorId = R.color.gray;
                break;
            case 2: ;
                colorId = R.color.blue_light;
                break;
            case 3:
                colorId = R.color.blue_normal;
                break;
            case 1:
            case 4:
            default:
                colorId = R.color.red;
        }
        return ContextCompat.getColor(this, colorId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeCallbacks(refreshRunnable);
    }

    public static String getStateDescription(int state) {
        switch (state) {
            case 0: return "ETHERNET_STATE_DISABLING";
            case 1: return "ETHERNET_STATE_DISABLED";
            case 2: return "ETHERNET_STATE_ENABLING";
            case 3: return "ETHERNET_STATE_ENABLED";
            case 4:
            default: return "ETHERNET_STATE_UNKNOWN";
        }
    }
}
