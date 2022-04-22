package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.data.StringValue;
import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.telephony.APNInfo;
import com.usdk.apiservice.aidl.system.telephony.NetworkType;
import com.usdk.apiservice.aidl.system.telephony.RestoreDefaultAPNListener;
import com.usdk.apiservice.aidl.system.telephony.UTelephony;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class TelephonyActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> slotIdList = new LinkedList<>();
    private static List<SpinnerItem> networkTypeList = new LinkedList<>();
    static {
        slotIdList.add(new SpinnerItem(0, "Card slot 1"));
        slotIdList.add(new SpinnerItem(1, "Card slot 2"));
        networkTypeList.add(new SpinnerItem(NetworkType.WCDMA_PREF, "WCDMA_PREF"));
        networkTypeList.add(new SpinnerItem(NetworkType.GSM_ONLY, "GSM_ONLY"));
        networkTypeList.add(new SpinnerItem(NetworkType.WCDMA_ONLY, "WCDMA_ONLY"));
        networkTypeList.add(new SpinnerItem(NetworkType.GSM_UMTS, "GSM_UMTS"));
        networkTypeList.add(new SpinnerItem(NetworkType.CDMA, "CDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.CDMA_NO_EVDO, "CDMA_NO_EVDO"));
        networkTypeList.add(new SpinnerItem(NetworkType.EVDO_NO_CDMA, "EVDO_NO_CDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.GLOBAL, "GLOBAL"));
        networkTypeList.add(new SpinnerItem(NetworkType.LTE_CDMA_EVDO, "LTE_CDMA_EVDO"));
        networkTypeList.add(new SpinnerItem(NetworkType.LTE_GSM_WCDMA, "LTE_GSM_WCDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.LTE_CDMA_EVDO_GSM_WCDMA, "LTE_CDMA_EVDO_GSM_WCDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.LTE_ONLY, "LTE_ONLY"));
        networkTypeList.add(new SpinnerItem(NetworkType.LTE_WCDMA, "LTE_WCDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_ONLY, "TD_SCDMA_ONLY"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_WCDMA, "TD_SCDMA_WCDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_LTE, "TD_SCDMA_LTE"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_GSM, "TD_SCDMA_GSM"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_GSM_LTE, "TD_SCDMA_GSM_LTE"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_GSM_WCDMA, "TD_SCDMA_GSM_WCDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_WCDMA_LTE, "TD_SCDMA_WCDMA_LTE"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_GSM_WCDMA_LTE, "TD_SCDMA_GSM_WCDMA_LTE"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_CDMA_EVDO_GSM_WCDMA, "TD_SCDMA_CDMA_EVDO_GSM_WCDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.TD_SCDMA_LTE_CDMA_EVDO_GSM_WCDMA, "TD_SCDMA_LTE_CDMA_EVDO_GSM_WCDMA"));
        networkTypeList.add(new SpinnerItem(NetworkType.LTE_CDMA_EVDO_GSM, "LTE_CDMA_EVDO_GSM"));

    }

    private UTelephony telephony;
    private SpinnerItem slotSlt;
    private SpinnerItem networkTypeSlt;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_telephony);
        setTitle("Telephony Module");
        initSpinner();
        initSwitch();
    }

    protected void initDeviceInstance() {
        telephony = DeviceHelper.me().getTelephony();
    }

    private void initSpinner() {
        slotSlt = slotIdList.get(0);
        NiceSpinner slotIdSpinner = findViewById(R.id.slotIdSpinner);
        slotIdSpinner.attachDataSource(slotIdList);
        slotIdSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                slotSlt = slotIdList.get(position);
            }
        });
        networkTypeSlt = networkTypeList.get(0);
        NiceSpinner networkTypeSpinner = findViewById(R.id.networkTypeSpinner);
        networkTypeSpinner.attachDataSource(networkTypeList);
        networkTypeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                networkTypeSlt = networkTypeList.get(position);
            }
        });
    }

    private void initSwitch() {
        Switch enableSwitch = findViewById(R.id.enableSwitch);
        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCellularDataEnabled(isChecked);
            }
        });
    }

    private void setCellularDataEnabled(boolean enabled) {
        try {
            outputBlueText(">>> setCellularDataEnabled : " + enabled);
            int ret = telephony.setCellularDataEnabled(enabled);
            if (ret == SystemError.SUCCESS) {
                outputText("setCellularDataEnabled success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getIMEI(View v) {
        try {
            outputBlueText(">>> getIMEI ");
            int slot = slotSlt.getValue();
            StringValue imeiOut = new StringValue();
            int ret = telephony.getIMEI(slot, imeiOut);
            if (ret == SystemError.SUCCESS) {
                outputText("getIMEI success : " + imeiOut);
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getDeviceId(View v) {
        try {
            outputBlueText(">>> getDeviceId ");
            int slot = slotSlt.getValue();
            StringValue idOut = new StringValue();
            int ret = telephony.getDeviceId(slot, idOut);
            if (ret == SystemError.SUCCESS) {
                outputText("getDeviceId success : " + idOut);
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getPreferredNetworkType(View v) {
        try {
            outputBlueText(">>> getPreferredNetworkType ");
            IntValue typeOut = new IntValue();
            int ret = telephony.getPreferredNetworkType(typeOut);
            if (ret == SystemError.SUCCESS) {
                outputText("getPreferredNetworkType success : " + typeOut.getData());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setPreferredNetworkType(View v) {
        try {
            outputBlueText(">>> setPreferredNetworkType ");
            int type = networkTypeSlt.getValue();
            int ret = telephony.setPreferredNetworkType(type);
            if (ret == SystemError.SUCCESS) {
                outputText("setPreferredNetworkType success ");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isCellularDataEnabled(View v) {
        try {
            outputBlueText(">>> isCellularDataEnabled ");
            BooleanValue enableOut = new BooleanValue();
            int ret = telephony.isCellularDataEnabled(enableOut);
            if (ret == SystemError.SUCCESS) {
                outputText("isCellularDataEnabled : " + enableOut);
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void addAPN(View v) {
        outputBlueText(">>> addAPN ");

        try {
            APNInfo apnInfo = getTestAPNInfo();
            int ret = telephony.addAPN(apnInfo);
            if (ret == SystemError.SUCCESS) {
                outputText("addAPN success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void queryAPN(View v) {
        outputBlueText(">>> queryAPN ");
        String selection = "apn=? and name=?";
        String[] selectionArgs = new String[]{"TEST_APN", "TEST_APN_NAME"};
        List<APNInfo> list = new ArrayList<>();

        try {
            int ret = telephony.queryAPN(selection, selectionArgs, list);
            if (ret == SystemError.SUCCESS) {
                outputText("queryAPN success");

                if (list.size() != 0) {
                    outputText("TEST_APN exists");
                } else {
                    outputText("TEST_APN do not exist");
                }

                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deleteAPN(View v) {
        outputBlueText(">>> deleteAPN ");
        APNInfo apnInfo = getTestAPNInfo();
        try {
            int ret = telephony.deleteAPN(apnInfo);
            if (ret == SystemError.SUCCESS) {
                outputText("deleteAPN success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void restoreDefaultAPN(View v) {
        outputBlueText(">>> restoreDefaultAPN ");

        try {
            int ret = telephony.restoreDefaultAPN(new RestoreDefaultAPNListener.Stub() {
                @Override
                public void onCompleted() throws RemoteException {
                    outputText("restoreDefaultAPN onCompleted");
                }
            });

            if (ret == SystemError.SUCCESS) {
                outputText("restoreDefaultAPN success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private APNInfo getTestAPNInfo() {
        APNInfo apnInfo = new APNInfo();
        apnInfo.setApn("TEST_APN");
        apnInfo.setName("TEST_APN_NAME");
        apnInfo.setMnc("07");
        apnInfo.setMcc("460");
        apnInfo.setNumeric("46007");
        apnInfo.setCarrierEnabled(true);

        return apnInfo;
    }
}
