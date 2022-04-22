package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.nfc.NfcState;
import com.usdk.apiservice.aidl.system.nfc.UNfc;

import org.angmarch.views.NiceSpinner;

import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class NfcActivity extends BaseDeviceActivity {
    private static List<SpinnerItem> stateList = new LinkedList<>();

    static {
        stateList.add(new SpinnerItem(NfcState.NOT_SUPPORTED, "NOT_SUPPORTED"));
        stateList.add(new SpinnerItem(NfcState.ENABLED, "ENABLED"));
        stateList.add(new SpinnerItem(NfcState.DISENABLED, "DISENABLED"));
    }

    private UNfc nfc;
    private int state = NfcState.NOT_SUPPORTED;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_nfc);
        setTitle("NFC Module");
        initSpinner();
    }

    protected void initDeviceInstance() {
        nfc = DeviceHelper.me().getNfc();
    }

    private void initSpinner() {
        NiceSpinner spinnerMode = findViewById(R.id.spinner_state);
        spinnerMode.attachDataSource(stateList);
        spinnerMode.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                state = stateList.get(position).getValue();
            }
        });
    }

    public void getNfcState(View v) {
        try {
            outputBlueText(">>> getNfcState");
            IntValue intValue = new IntValue();
            int ret = nfc.getNfcState(intValue);
            if (ret == SystemError.SUCCESS) {
                outputText("getNfcState success: " + intValue.getData());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setNfcState(View v) {
        try {
            outputBlueText(">>> setNfcState");
            int ret = nfc.setNfcState(state);
            if (ret == SystemError.SUCCESS) {
                outputText("setNfcState success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

}
