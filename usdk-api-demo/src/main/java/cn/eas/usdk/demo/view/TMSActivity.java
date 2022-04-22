package cn.eas.usdk.demo.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.usdk.apiservice.aidl.tms.OnResultListener;
import com.usdk.apiservice.aidl.tms.TMSData;
import com.usdk.apiservice.aidl.tms.UTMS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.FileUtil;

public class TMSActivity extends BaseDeviceActivity {
    private static final String FILES_PATH = DemoConfig.TEST_FILES_PATH;

    private Button installBtn;
    private Button uninstallBtn;
    private TextView filePathText;
    private AlertDialog dialog;

    private UTMS tms;
    private OnResultListener listener =  new OnResultListener.Stub() {
        @Override
        public void onSuccess() throws RemoteException {
            dialogDismiss();
            outputText("=> onSuccess");
        }

        @Override
        public void onError(List<Bundle> errorList) throws RemoteException {
            dialogDismiss();
            outputRedText("=> onError");
            for (Bundle item : errorList) {
                outputRedText(item.getString(TMSData.ERROR_MESSAGE));
            }
        }
    };

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_tms);
        setTitle("TMS Module");

        installBtn = bindViewById(R.id.installBtn);
        uninstallBtn = bindViewById(R.id.uninstallBtn);
        filePathText = bindViewById(R.id.filePathText);
        filePathText.setText(FILES_PATH);

        copyTestFilesToSD();
    }

    protected void initDeviceInstance() {
        tms = DeviceHelper.me().getTMS();
    }

    private void copyTestFilesToSD() {
        outputBlueText(">>> Copying application installation files to SD card for test...");
        installBtn.setEnabled(false);
        uninstallBtn.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] fileNames = new String[] {
                        "updatetest.apk",
                        "updatetest.uns" };
                FileUtil.copyFilesToSD(TMSActivity.this, FILES_PATH, fileNames);

                outputText("=> Copy file to SD card success!");
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        installBtn.setEnabled(true);
                        uninstallBtn.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    public void selectFilePath(View v) {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(this, properties);
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files.length == 0) {
                    return;
                }
                filePathText.setText(files[0]);
            }
        });
        dialog.show();
    }

    public void install(View v) {
        String filePath = filePathText.getText().toString();
        outputBlueText(">>> install | filePath: " + filePath);
        dialog = showProgress(" The applications are installing...");

        try {
            Bundle param = new Bundle();
            param.putString(TMSData.FILE_PATH, filePath);
            tms.install(param, listener);
        } catch (Exception e) {
            dialogDismiss();
            handleException(e);
        }
    }

    public void uninstall(View v) {
        outputBlueText(">>> uninstall");
        dialog = showProgress(" The applications are uninstalling...");
        try {
            ArrayList<String> packageNames = new ArrayList<String>();
            packageNames.add("com.usdk.apiservice.test");
            packageNames.add("cn.eas.usdk.demo.amazon");
            packageNames.add("cn.eas.usdk.demo.baboon");
            packageNames.add("cn.eas.usdk.demo.cheetah");
            packageNames.add("cn.eas.usdk.demo.dinosaur");
            packageNames.add("cn.eas.national.deviceapisample");

            Bundle param = new Bundle();
            param.putStringArrayList(TMSData.PACKAGE_NAMES, packageNames);
            tms.uninstall(param,listener);
        } catch (Exception e) {
            dialogDismiss();
            handleException(e);
        }
    }

    public void getLastErrorDescription(View v) throws RemoteException {
        outputBlueText(">>> getLastErrorDescription");
        List<Bundle> list = tms.getLastErrorDescription();
        if (list != null) {
            for (Bundle item : list) {
                outputRedText(item.getString(TMSData.ERROR_MESSAGE));
            }
        }
    }

    private void dialogDismiss() {
        dialog.dismiss();
    }
}
