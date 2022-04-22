package cn.eas.usdk.demo.view.pinpad;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.usdk.apiservice.aidl.pinpad.DeviceName;
import com.usdk.apiservice.aidl.pinpad.KeySystem;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;

public class ExternalActivity extends BasePinpadActivity {

    private int line = 1;
    private EditText etMessage;

    @Override
    public int getKeySystem() {
        return KeySystem.KS_MKSK;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_pinpad_external);
        setTitle("Pinpad External Module");

        checkPinpadConfig();

        initView();
        open();
    }

    private void checkPinpadConfig() {
        if (DemoConfig.PINPAD_DEVICE_NAME.equals(DeviceName.IPP)) {
            finishWithInfo(getString(R.string.pinpad_epp_config));
        }
    }

    private void initView() {
        initSpinner();
        etMessage = bindViewById(R.id.etMessage);
    }

    private void initSpinner() {
        NiceSpinner lineSpinner = (NiceSpinner) findViewById(R.id.lineSpinner);
        final List<Integer> paperNumList = new LinkedList<>(Arrays.asList(1, 2, -1, 0, 3, 4));
        lineSpinner.attachDataSource(paperNumList);
        lineSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                line = paperNumList.get(position);
            }
        });
    }

    public void display(View v) {
        String message = etMessage.getText().toString();
        if (message.length() == 0) {
            message = null;
            outputBlueText("Error argument test case!" );
        }

        outputBlueText(">>> display | " + message);
        try {
            boolean isSucc = pinpad.display(line, message);
            if (isSucc) {
                outputText("display success");
            } else {
                outputPinpadError("display fail");
            }
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
        }
    }

    public void beep(View v) {
        outputBlueText(">>> beep | " + line + " second");
        try {
            boolean isSucc = pinpad.beep(line * 1000);
            if (isSucc) {
                outputText("beep success");
            } else {
                outputPinpadError("beep fail");
            }
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
        }
    }

    public void reset(View v) {
        outputBlueText(">>> reset");
        try {
            boolean isSucc = pinpad.reset();
            if (isSucc) {
                outputText("reset success");
            } else {
                outputPinpadError("reset fail");
            }
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
        }
    }
}
