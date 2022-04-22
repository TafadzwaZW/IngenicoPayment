package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.view.View;

import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.process.UProcess;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;
import mabbas007.tagsedittext.TagsEditText;

public class  ProcessActivity extends BaseDeviceActivity {

    private UProcess process;
    private List<String> excludePackageNames;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_process);
        setTitle("Process Module");
        initView();
    }

    protected void initDeviceInstance() {
        process = DeviceHelper.me().getProcess();
    }

    private void initView() {
        excludePackageNames = Arrays.asList(getPackageName());
        TagsEditText packageNamesTET = bindViewById(R.id.packageNamesTET);
        packageNamesTET.setTags(excludePackageNames.toArray(new String[0]));
        packageNamesTET.setTagsListener(new TagsEditText.TagsEditListener() {
            @Override
            public void onTagsChanged(Collection<String> packageNames) {
                excludePackageNames = (List<String>) packageNames;
            }

            @Override
            public void onEditingFinished() {
            }
        });
    }

    public void clearRecentTasks(View v) {
        try {
            outputBlueText(">>> clearRecentTasks" );
            int ret = process.clearRecentTasks(excludePackageNames);
            if (ret == SystemError.SUCCESS) {
                outputText("clearRecentTasks success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }
}
