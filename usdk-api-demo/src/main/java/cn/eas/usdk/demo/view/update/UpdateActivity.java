package cn.eas.usdk.demo.view.update;

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
import com.usdk.apiservice.aidl.update.OnProcessListener;
import com.usdk.apiservice.aidl.update.ResultCode;
import com.usdk.apiservice.aidl.update.UUpdate;
import com.usdk.apiservice.aidl.update.UpdateData;
import com.usdk.apiservice.aidl.update.UpdateResult;
import com.usdk.apiservice.aidl.update.UpdateTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.FileUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class UpdateActivity extends BaseDeviceActivity {
    private static final String FILES_PATH = DemoConfig.TEST_FILES_PATH;

    private TextView filePathText;
    private AlertDialog dialog;
    private Button updateBtn;
    private UUpdate update;
    private UpdateManager updateManager;
    private long currentUpdateId;
    private String[] fileNames = new String[] {
            "updatetest.apk",
            "updatetest.uns",
            //"DX8000-OTA-20200827.uns",//You need to use the correct ota upgrade package!!!
    };

    private UpdateManager.UpdateListener listener = new UpdateManager.UpdateListener() {
        @Override
        public void onFinish(long updateId, List<UpdateResult> resultsList) {
            dialogDismiss();
            outputBlueText(">>> onFinish, updateId = " + updateId);
            if (updateId != currentUpdateId) {
                outputRedText(">>> updateId error, currentUpdateId = "
                        + currentUpdateId + ", but receiver = " + updateId);
                return;
            }

            if (resultsList != null) {
                for (UpdateResult updateResult: resultsList) {
                    outputBlueText(">>> updateResult |" +
                            " taskId = " + updateResult.getUpdateTaskId()+
                            ", code = " + updateResult.getErrorCode()
                            + ", msg = " + updateResult.getErrorMsg()
                            + ", errorFileName" + updateResult.getErrorFileName());
                }
            }
        }
    };

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_update);
        setTitle("Update Module");

        updateBtn = bindViewById(R.id.updateBtn);
        filePathText = bindViewById(R.id.filePathText);
        filePathText.setText(FILES_PATH);
        updateBtn.setEnabled(false);

        copyTestFilesToSD();
    }

    protected void initDeviceInstance() {
        update = DeviceHelper.me().getUpdate();
        updateManager = UpdateManager.getInstance();

        updateManager.init(update);
        updateManager.setUpdateListener(listener);
    }

    private void copyTestFilesToSD() {
        outputBlueText(">>> Copying application installation files to SD card for test...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                FileUtil.copyFilesToSD(UpdateActivity.this, FILES_PATH, fileNames);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateBtn.setEnabled(true);
                    }
                });
                outputText("=> Copy file to SD card success!");
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

    public void getUpdateStatus(View v) {
        outputBlackText(">>> getUpdateStatus");
        int status = 0;
        try {
            status = updateManager.getUpdateStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        outputBlueText(">>> getUpdateStatus | status: " + status);
    }

    public void getTermInfo(View v) {
        outputBlackText(">>> getTermInfo");
        String termInfo = null;
        try {
            termInfo = updateManager.getTermInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        outputBlueText(">>> getTermInfo | termInfo: " + termInfo);
    }

    public void getUpdateId(View v) {
        outputBlackText(">>> getUpdateId");
        try {
            currentUpdateId = updateManager.getUpdateId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        outputBlueText(">>> getUpdateId | updateId:" + currentUpdateId);
    }

    public void update(View v) {
        outputBlackText(">>> update");
        dialog = showProgress(" The files are updating...");

        ArrayList<UpdateTask> list = new ArrayList<>();
        UpdateTask updateTask = new UpdateTask();
        updateTask.setTaskId("task1");
        //Get the file list path to be upgraded
        ArrayList<String> needUpdateFilePath = new ArrayList<>();
        //You can add multiple files that need to be installed in List
        needUpdateFilePath.add(filePathText.getText().toString());
        outputBlueText(">>> needUpdateFilePath | path: " + filePathText.getText().toString());
        updateTask.setNeedUpdateFilePathList(needUpdateFilePath);
        list.add(updateTask);

        Bundle params = new Bundle();
        params.putLong(UpdateData.UPDATE_ID, currentUpdateId);
        params.putParcelableArrayList(UpdateData.UPDATE_TASK, list);

        int ret = -1;
        try {
            // The return value of the update interface only represents the call result of the interface.
            // The update result needs to be obtained by listening to the broadcast.
            ret = updateManager.update(params, new OnProcessListener.Stub() {
                @Override
                public void onProcess(int i) {
                    outputBlueText(">>> onProcess | process: " + i);
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
            dialogDismiss();
        }

        outputBlueText(">>> update | ret: " + ret);

    }

    public void getUpdateResultInfo(View view) {
        outputBlackText(">>> getUpdateResultInfo");
        try {
            List<UpdateResult> results = updateManager.getUpdateResultInfo(currentUpdateId);

            if (results != null) {
                for (UpdateResult result : results) {
                    outputBlueText(">>> updateTaskId: " + result.getUpdateTaskId());
                    outputBlueText(">>> errorCode: " + result.getErrorCode());
                    outputBlueText(">>> errorMsg: " + result.getErrorMsg());
                    outputBlueText(">>> errorFileName: " + result.getErrorFileName());

                    if (ResultCode.SUCCESS == result.getErrorCode()) {
                        outputBlueText(result.getUpdateTaskId() + ", this updateTask update successfully");
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void dialogDismiss() {
        dialog.dismiss();
    }


}
