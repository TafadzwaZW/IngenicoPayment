package cn.eas.usdk.demo.view;

import android.os.Bundle;
import android.view.View;

import com.usdk.apiservice.aidl.paramfile.UParamFile;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;

public class ParamFileActivity extends BaseDeviceActivity {

    private UParamFile paramFile;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_paramfile);
        setTitle("ParamFile Module");
    }

    protected void initDeviceInstance() {
        paramFile = DeviceHelper.me().getParamFile("MYMODULE", "MYFILE");
    }

    public void isExists(View v) {
        try {
            boolean ret = paramFile.isExists();
            if (ret) {
                outputText("The parameter file exist!");
            } else {
                outputRedText("The parameter file does not exist!");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void isFirstRun(View v) {
        try {
            boolean ret = paramFile.isFirstRun();
            if (ret) {
                outputText("The parameter file is run for the first time!");
            } else {
                outputRedText("The parameter file is non-first run!");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getParameter(View v) {
        outputBlueText(">>> getParameter");
        try {
            String merchantNo = paramFile.getString("01000001", "");
            String timeout = paramFile.getString("01000002", "1");
            boolean supportSM = paramFile.getBoolean("01000003", false);
            outputText("merchantNo: " + merchantNo);
            outputText("timeout: " + timeout);
            outputText("supportSM: " + supportSM);
        } catch (Exception e) {
            handleException(e);
        }
    }

}
