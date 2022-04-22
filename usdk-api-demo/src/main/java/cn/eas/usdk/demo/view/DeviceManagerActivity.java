package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.usdk.apiservice.aidl.device.DeviceInfo;
import com.usdk.apiservice.aidl.device.Mode;
import com.usdk.apiservice.aidl.device.UDeviceManager;
import com.usdk.apiservice.aidl.pinpad.KAPId;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.DialogUtil;

public class DeviceManagerActivity extends BaseDeviceActivity {

    private UDeviceManager deviceManager;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_devicemanager);
        setTitle("Device Manager Module");
    }

    protected void initDeviceInstance() {
        deviceManager = DeviceHelper.me().getDeviceManager();
    }

    public void getDeviceInfo(View v) {
        outputBlueText(">>> getDeviceInfo ");
        try {
            DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
            outputText("SerialNo: " + deviceInfo.getSerialNo());
            outputText("Model: " + deviceInfo.getModel());
            outputText("Manufacture: " + deviceInfo.getManufacture());
            outputText("IMSI: " + deviceInfo.getIMSI());
            outputText("IMEI: " + deviceInfo.getIMEI());
            outputText("ICCID: " + deviceInfo.getICCID());
            outputText("ROMVersion: " + deviceInfo.getRomVersion());
            outputText("AndroidKernelVersion: " + deviceInfo.getAndroidKernelVersion());
            outputText("AndroidOSVersion: " + deviceInfo.getAndroidOSVersion());
            outputText("FirmwareVersion: " + deviceInfo.getFirmwareVersion());
            outputText("HardwareVersion: " + deviceInfo.getHardwareVersion());
            outputText("HardWareSn: " + deviceInfo.getHardWareSn());
            outputText("Mode: " + getModeDescription(deviceInfo.getMode()));
            outputText("isOvsTerm: " + deviceInfo.isOvsTerm());
            outputText("CSN: " + deviceInfo.getCSN());
            outputText("ProductName: " + deviceInfo.getProductName());
            outputText("ProcessorInfo: " + deviceInfo.getProcessorInfo());
            outputText("BasebandVersion: " + deviceInfo.getBasebandVersion());
            outputText("BootVersion: " + deviceInfo.getBootVersion());
            outputText("BuildNumber: " + deviceInfo.getBuildNumber());
            outputText("PayComponentVersion: " + deviceInfo.getPayComponentVersion());
            outputText("SecfwVersion: " + deviceInfo.getSecfwVersion());
            outputText("pn: " + deviceInfo.getPn());
            outputText("TerminalType: " + deviceInfo.getTerminalType());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void updateSystemDatetime(View v) {
        outputBlueText(">>> updateSystemDatetime ");
        try {
            String datetime = "20170819185220";
            deviceManager.updateSystemDatetime(datetime);

            String currentTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
            if (datetime.equals(currentTime)) {
                outputText("Update system time succ: " + currentTime);
            } else {
                outputRedText("Update system time fail: " + currentTime);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getSystemModulesVersion(View v) {
        outputBlueText(">>> getSystemModulesVersion");
        try {
            List<String> moduleNames = new ArrayList<String>();
            moduleNames.add("domain_cfg");
            moduleNames.add("domain_cfg_tkey");
            moduleNames.add("rfcard");
            moduleNames.add("iccard");
            moduleNames.add("printer");
            moduleNames.add("masterControl");
            moduleNames.add("HelperService");
            moduleNames.add("EMVKernel");
            moduleNames.add("libEMV");
            moduleNames.add("pinpad");
            moduleNames.add("s-module");
            moduleNames.add("dataacq");
            moduleNames.add("cashbox");
            moduleNames.add("SEAndroid");
            moduleNames.add("fwinfo_jni");
            moduleNames.add("fam");
            moduleNames.add("BTBase");
            moduleNames.add("titan_sysfunc");
            Bundle versions = deviceManager.getSystemModulesVersion(moduleNames);

            for (String name : moduleNames) {
                outputText(name + " : " + versions.getString(name));
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void reboot(View v) {
        outputBlueText(">>> reboot ");
        Log.i("DeviceManagerActivity", ">>> reboot: ");
        DialogUtil.showOption(this, "Warm prompt", "Are you sure to reboot the POS?",
                new DialogUtil.OnChooseListener() {
                    @Override
                    public void onConfirm() {
                        try {
                            deviceManager.reboot();
                        } catch (Exception e) {
                            handleException(e);
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
         });
    }

    public void shutdown(View v) {
        outputBlueText(">>> shutdown ");
        Log.i("DeviceManagerActivity", ">>> shutdown: ");
        DialogUtil.showOption(this, "Warm prompt", "Are you sure to shutdown the POS?",
                new DialogUtil.OnChooseListener() {
                    @Override
                    public void onConfirm() {
                        try {
                            deviceManager.shutdown();
                        } catch (Exception e) {
                            handleException(e);
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
         });
    }

    public void getAppKapIdList(View v) {
        outputBlueText(">>> getAppKapIdList");
        try {
            List<KAPId> kapIdList =  deviceManager.getAppKapIdList();
            if (kapIdList == null) {
                outputRedText("getPinpadInfo fail!");
                return;
            }
            if (kapIdList.size() == 0) {
                outputRedText("getAccessableKapIds fail!");
                return;
            }

            for (KAPId kap : kapIdList) {
                outputText("regionID:" + kap.getRegionId()+ ", kapNum:" + kap.getKapNum());
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static String getModeDescription(int mode) {
        switch (mode) {
            case Mode.PRODUCE_STATE: return "0 - Produce state";
            case Mode.NORMAL_STATE: return "1 - Normal state";
            case Mode.REPAIR_STATE: return "2 - Repair state";
            case Mode.MOCKUP_STATE: return "3 - Mockup state";
            default: return "Error mode: " + mode;
        }
    }
}
