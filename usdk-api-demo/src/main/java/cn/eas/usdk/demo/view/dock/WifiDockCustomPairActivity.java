package cn.eas.usdk.demo.view.dock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.dock.DockError;
import com.usdk.apiservice.aidl.dock.DockStatus;
import com.usdk.apiservice.aidl.dock.PairResultListener;
import com.usdk.apiservice.aidl.dock.UWifiDock;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class WifiDockCustomPairActivity extends BaseDockActivity {

    private UWifiDock wifiDock;

    private EditText etSsid;

    private boolean isRegister = false;
    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                String wifiName = info.getExtraInfo().replace("\"", "");
                switch (info.getState()) {
                    case CONNECTING:
                        // connecting
                        outputBlackText("Network connecting...");
                        break;

                    case CONNECTED:
                        outputBlackText("Network connected: " + wifiName);
                        if (wifiName.equals(getSSID())) {
                            outputBlackText("Dock wifi connected, then start to pair");
                            pair();
                            unregisterNetworkStateChangedReceiver();
                            return;
                        }

                        outputBlackText("The wifi connection is not the dock wifi, so change to connect dock wifi");
                        addNetwork();
                        break;

                    case DISCONNECTED:
                        outputBlackText("Network disconnected, then to connect dock wifi");
                        addNetwork();
                        break;

                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wifi_dock_custom_pair);
        setTitle("Custom Pair UI");

        initDeviceInstance();
        initView();
    }

    protected void initDeviceInstance() {
        wifiDock = DeviceHelper.me().getWifiDock();
    }

    private void initView() {
        etSsid = bindViewById(R.id.etSsid);
    }

    public void startPair(View v) {
        checkWifiStatus(new Runnable() {
            @Override
            public void run() {
                if (needToPair()) {
                    registerNetworkStateChangedReceiver();
                }
            }
        });
    }

    private void checkWifiStatus(final Runnable next) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) {
            next.run();
            return;
        }
        outputRedText("Wifi not open! Opening...");

        if (!wifiManager.setWifiEnabled(true)) {
            outputRedText("Wifi open fail! Try again!");
            return;
        }
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                next.run();
            }
        }, 10000);
    }

    private boolean needToPair() {
        outputBlueText(">>> getDockStatus");
        try {
            IntValue status = new IntValue();
            int ret = wifiDock.getDockStatus(status);
            if (ret != DockError.SUCCESS) {
                outputRedText(getErrorDetail(ret) + "!! try again");
                return false;
            }

            switch (status.getData()) {
                case DockStatus.CHANNEL_NOT_OPENED:
                    outputRedText("Terminal channel not open! Need to check the dock status");
                    break;

                case DockStatus.UNPAIRED:
                    outputText("Dock status is unpaired!");
                    return true;

                case DockStatus.PAIRED:
                    outputText("Dock status is paired! No need to pair again");
                    break;
                case DockStatus.CHANNEL_CONNECTED:
                    outputText("Channel is connected! No need to pair again");
                    break;

                default:
                    outputRedText("Other status");
                    break;
            }
        } catch (Exception e) {
            handleException(e);
        }
        return false;
    }

    /**
     * Monitor network connection status and pair after Dock WIFI connected
     */
    public void registerNetworkStateChangedReceiver() {
        outputBlueText(">>> registerNetworkStateChangedReceiver");
        if (isRegister) {
            return;
        }
        isRegister = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, filter);
    }

    private void addNetwork() {
        outputBlueText(">>> addNetwork");
        try {
            int networkId = wifiDock.addNetwork(getSSID());
            if (networkId == -1) {
                outputRedText("addNetwork fail");
                return;
            }
            outputText("addNetwork success, dock wifi connecting...");
        } catch (IllegalArgumentException e) {
            toast(e.getMessage());
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void pair() {
        outputBlueText(">>> pairing...");
        try {
            wifiDock.pair(new PairResultListener.Stub() {
                @Override
                public void onPaired() throws RemoteException {
                    outputText("=> pair success");
                }

                @Override
                public void onError(int errCode) throws RemoteException {
                    outputRedText("=> pair error:" + getErrorDetail(errCode));
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private String getSSID() {
        String ssid = etSsid.getText().toString();
        if (TextUtils.isEmpty(ssid)) {
            throw new IllegalArgumentException("Please input the SSID");
        }
        return ssid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkStateChangedReceiver();
    }

    private void unregisterNetworkStateChangedReceiver() {
        outputBlueText(">>> unregisterNetworkStateChangedReceiver");
        if (isRegister) {
            isRegister = false;
            unregisterReceiver(wifiStateReceiver);
        }
    }
}
