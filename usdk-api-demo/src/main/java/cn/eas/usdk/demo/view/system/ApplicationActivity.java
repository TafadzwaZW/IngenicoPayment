package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.application.AppDataObserver;
import com.usdk.apiservice.aidl.system.application.AppSignInfo;
import com.usdk.apiservice.aidl.system.application.AppSizeInfo;
import com.usdk.apiservice.aidl.system.application.AppSizeInfoObserver;
import com.usdk.apiservice.aidl.system.application.UApplication;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class ApplicationActivity extends BaseDeviceActivity {

    private EditText edtPackageName;
    private UApplication application;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_application);
        setTitle("Application Module");

        edtPackageName = findViewById(R.id.edt_package_name);
    }

    protected void initDeviceInstance() {
        application = DeviceHelper.me().getApplication();
    }

    public void getAppSignInfo(View v) {
        String packageName = edtPackageName.getText().toString();
        if (TextUtils.isEmpty(packageName)) {
            toast("Input package name");
            return;
        }

        try {
            outputBlueText(">>> getAppSignInfo");
            AppSignInfo appSignInfo = new AppSignInfo();
            int ret = application.getAppSignInfo(packageName, appSignInfo);
            if (ret == SystemError.SUCCESS) {
                outputText("getAppSignInfo success ");
                outputText(String.format("CAId = %s, RootCAId = %s, RootCAOwer = %s, Signer = %s",
                        appSignInfo.getCAId(), appSignInfo.getRootCAId(),
                        appSignInfo.getRootCAOwer(), appSignInfo.getSigner()));
                for (String permission : appSignInfo.getExtendPermissions()) {
                    outputText("permission = " + permission);
                }
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getAppSizeInfo(View v) {
        String packageName = edtPackageName.getText().toString();
        if (TextUtils.isEmpty(packageName)) {
            toast("Input package name");
            return;
        }

        try {
            outputBlueText(">>> getAppSizeInfo");
            int ret = application.getAppSizeInfo(packageName, new AppSizeInfoObserver.Stub() {
                @Override
                public void onCompleted(AppSizeInfo info, boolean success) throws RemoteException {
                    if (!success) {
                        outputRedText("getAppSizeInfo fail");
                        return;
                    }
                    outputText("getAppSizeInfo success ");
                    outputText(String.format("DataSize = %d, CodeSize = %d, CacheSize = %d, PackageName = %s, UserHandle = %d, ExternalDataSize = %d, ExternalCacheSize = %d, ExternalCodeSize = %d, ExternalMediaSize = %d, ExternalObbSize = %d",
                            info.getDataSize(), info.getCodeSize(), info.getCacheSize(),
                            info.getPackageName(), info.getUserHandle(), info.getExternalDataSize(),
                            info.getExternalCacheSize(), info.getExternalCodeSize(),
                            info.getExternalMediaSize(), info.getExternalObbSize()));
                }
            });

            if (ret != SystemError.SUCCESS) {
                outputRedText(SystemErrorUtil.getErrorMessage(ret));
                return;
            }

        } catch (Exception e) {
            handleException(e);
        }
    }

    public void clearApplicationUserData(View v) {
        String packageName = edtPackageName.getText().toString();
        if (TextUtils.isEmpty(packageName)) {
            toast("Input package name");
            return;
        }

        try {
            outputBlueText(">>> clearApplicationUserData");
            int ret = application.clearApplicationUserData(packageName, new AppDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName, boolean success) throws RemoteException {
                    if (!success) {
                        outputRedText("clearApplicationUserData fail");
                        return;
                    }
                    outputText("clearApplicationUserData success ");
                }
            });

            if (ret != SystemError.SUCCESS) {
                outputRedText(SystemErrorUtil.getErrorMessage(ret));
                return;
            }

        } catch (Exception e) {
            handleException(e);
        }
    }

}
