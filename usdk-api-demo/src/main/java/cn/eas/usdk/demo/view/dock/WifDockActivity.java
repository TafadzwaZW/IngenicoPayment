package cn.eas.usdk.demo.view.dock;

import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.data.StringValue;
import com.usdk.apiservice.aidl.dock.DockConfig;
import com.usdk.apiservice.aidl.dock.DockError;
import com.usdk.apiservice.aidl.dock.DockName;
import com.usdk.apiservice.aidl.dock.GetDmzStateListener;
import com.usdk.apiservice.aidl.dock.ModuleType;
import com.usdk.apiservice.aidl.dock.OnApplyConfigListener;
import com.usdk.apiservice.aidl.dock.OnConfigWithFileListener;
import com.usdk.apiservice.aidl.dock.OnGetConfigListener;
import com.usdk.apiservice.aidl.dock.OnUpdateDockListener;
import com.usdk.apiservice.aidl.dock.UWifiDock;
import com.usdk.apiservice.aidl.dock.VersionType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class WifDockActivity extends BaseDockActivity {

    private UWifiDock dock;

    private boolean dmzEnabled;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wifi_dock);
        setTitle("Wifi Dock Module");

        initDeviceInstance();

        initRadio();
    }

    protected void initDeviceInstance() {
        dock = DeviceHelper.me().getWifiDock();
    }

    private void initRadio() {
        RadioGroup rgDmz = bindViewById(R.id.rg_dmz);
        rgDmz.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int btnId = radioGroup.getCheckedRadioButtonId();
                dmzEnabled = btnId == R.id.rb_dmz_enable;
            }
        });
    }

    public void getDockStatus(View v) {
        outputBlueText(">>> getDockStatus");
        try {
            IntValue status = new IntValue();
            int ret = dock.getDockStatus(status);
            if (ret != DockError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("=> status : " + getStatusDescription(status.getData()));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void defaultPairUI(View v) {
        outputBlueText(">>> callPairActivity");
        try {
            int ret = dock.callPairActivity();
            if (ret != DockError.SUCCESS) {
                outputRedText(getErrorDetail(ret));
                return;
            }
            outputText("=> callPairActivity success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void customPairUI(View v) {
        startActivity(WifiDockCustomPairActivity.class);
    }

    public void startDockEthernetDemo(View v) {
        startActivity(DockEthernetActivity.class);
    }

    public void startDockPortDemo(View v) {
        startActivity(DockPortActivity.class, DockName.WIFI_DOCK);
    }

    public void setDmzEnabled(View v) {
        outputBlueText(">>> setDmzEnabled: " + dmzEnabled);
        try {
            int ret = dock.setDmzEnabled(dmzEnabled);
            outputText("=> setDmzEnabled, ret = " + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getDmzEnableState(View v) {
        outputBlueText(">>> getDmzEnableState");
        try {
            dock.getDmzEnableState(new GetDmzStateListener.Stub() {
                @Override
                public void onSuccess(int state) throws RemoteException {
                    outputText("=> getDmzEnableState success, state = " + state);
                }

                @Override
                public void onError(int errorCode) throws RemoteException {
                    outputRedText("=> getDmzEnableState error, errorCode = " + errorCode);
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getVersion(View v) {
        outputBlueText(">>> getVersion");
        try {
            StringValue version = new StringValue();
            int ret = dock.getVersion(VersionType.CTRL, ModuleType.ALL, version);
            if (ret != DockError.SUCCESS) {
                outputRedText("=> getVersion error, errorCode = " + ret);
                return;
            }
            outputText("=> getVersion success, version = " + version.getData());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getConfig(View v) {
        outputBlueText(">>> getConfig");
        try {
            List<String> queryKeyList = new ArrayList<>();
            queryKeyList.add(DockConfig.PRODUCT_NAME);
            queryKeyList.add(DockConfig.ACCESS_IP);
            queryKeyList.add(DockConfig.ACCESS_MAC);
            queryKeyList.add(DockConfig.ACCESS_SN);
            queryKeyList.add(DockConfig.WEB_LANG);
            int ret = dock.getConfig(queryKeyList, new OnGetConfigListener.Stub() {
                @Override
                public void onSuccess(Map map) throws RemoteException {
                    outputText("=> getConfig onSuccess, map = " + map);
                }

                @Override
                public void onError(int errorCode) throws RemoteException {
                    outputRedText("=> getConfig onError, errorCode = " + errorCode);
                }
            });
            if (ret != DockError.SUCCESS) {
                outputRedText("=> getConfig error, errorCode = " + ret);
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getConfigsWithFile(View v) {
        outputBlueText(">>> getConfigsWithFile");
        try {
            String saveFilePath = Environment.getExternalStorageDirectory() + "/usdk/wifiDockConfig";
            File saveFile = new File(saveFilePath);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            Log.d("yy", "yy saveFilePath = " + saveFilePath);
            int ret = dock.getConfigsWithFile(saveFilePath, new OnConfigWithFileListener.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    outputText("=> getConfigsWithFile onSuccess");
                }

                @Override
                public void onError(int errorCode) throws RemoteException {
                    outputRedText("=> getConfigsWithFile onError, errorCode = " + errorCode);
                }
            });
            if (ret != DockError.SUCCESS) {
                outputRedText("=> getConfigsWithFile error, errorCode = " + ret);
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getHardwareSupportDevices(View v) {
        outputBlueText(">>> getHardwareSupportDevices");
        try {
            List<String> supportDevices = new ArrayList<>();
            int ret = dock.getHardwareSupportDevices(supportDevices);
            if (ret != DockError.SUCCESS) {
                outputRedText("=> getHardwareSupportDevices error, errorCode = " + ret);
                return;
            }
            outputText("=> getConfigsWithFile success supportDevices = " + supportDevices);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setConfig(View v) {
        outputBlueText(">>> setConfig");
        try {
            int ret = dock.setConfig(DockConfig.WEB_LANG, "1");
            if (ret != DockError.SUCCESS) {
                outputRedText("=> setConfig error, errorCode = " + ret);
                return;
            }
            outputText(">>> setConfig success");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setConfigsWithFile(View v) {
        outputBlueText(">>> setConfigsWithFile");
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/usdk/wifiDockConfig";
            int ret = dock.setConfigsWithFile(filePath, new OnConfigWithFileListener.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    outputText("=> setConfigsWithFile onSuccess");
                }

                @Override
                public void onError(int errorCode) throws RemoteException {
                    outputRedText("=> setConfigsWithFile onError, errorCode = " + errorCode);
                }
            });
            if (ret != DockError.SUCCESS) {
                outputRedText("=> setConfigsWithFile error, errorCode = " + ret);
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void applyConfig(View v) {
        outputBlueText(">>> applyConfig");
        try {
            int ret = dock.applyConfig(new OnApplyConfigListener.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    outputText("=> applyConfig onSuccess");
                }

                @Override
                public void onError(int errorCode) throws RemoteException {
                    outputRedText("=> applyConfig onError, errorCode = " + errorCode);
                }
            });
            if (ret != DockError.SUCCESS) {
                outputRedText("=> applyConfig error, errorCode = " + ret);
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateDock(View v) {
        outputBlueText(">>> updateDock");
        try {
            int ret = dock.updateDock(new OnUpdateDockListener.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    outputText("=> updateDock onSuccess");
                }

                @Override
                public void onError(int errorCode) throws RemoteException {
                    outputRedText("=> updateDock onError, errorCode = " + errorCode);
                }
            });
            if (ret != DockError.SUCCESS) {
                outputRedText("=> updateDock error, errorCode = " + ret);
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
