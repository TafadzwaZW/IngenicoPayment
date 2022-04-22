package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.RadioGroup;

import com.usdk.apiservice.aidl.resetfactory.ResetListener;
import com.usdk.apiservice.aidl.resetfactory.UResetFactory;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class ResetFactoryActivity extends BaseDeviceActivity {

    private UResetFactory resetFactory;
    private boolean cleanData = true;


    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_resetfactory);
        setTitle("ResetFactory Module");

        initDeviceInstance();
        initViews();
    }

    private void initViews() {
        ((RadioGroup) findViewById(R.id.rgIfCleanData)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbCleanData) {
                    cleanData = true;
                } else {
                    cleanData = false;
                }
            }
        });
    }

    protected void initDeviceInstance() {
        resetFactory = DeviceHelper.me().getResetFactory();
    }

    public void resetDeep(View v) {
        outputBlueText(">>> resetDeep ");
        try {
            resetFactory.resetDeep(cleanData, new ResetListener.Stub() {
                @Override
                public void onResult(int result) throws RemoteException {
                    outputText("reset result = " + result);
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

}
