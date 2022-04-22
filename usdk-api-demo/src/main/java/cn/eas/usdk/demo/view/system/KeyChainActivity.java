package cn.eas.usdk.demo.view.system;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.security.UKeyChain;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class KeyChainActivity extends BaseDeviceActivity {

    private UKeyChain keyChain;

    private EditText etUid;
    private EditText etAlias;
    private boolean isGrant;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_keychain);
        setTitle("KeyChain Module");
        initView();
    }

    protected void initDeviceInstance() {
        keyChain = DeviceHelper.me().getKeyChain();
    }

    private void initView() {
        etUid = bindViewById(R.id.etUid);
        etUid.setText(getUid());
        etAlias = bindViewById(R.id.etAlias);
        Switch grantSwitch = bindViewById(R.id.grantSwitch);
        grantSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isGrant = isChecked;
            }
        });
    }

    public void setGrant(View v) {
        try {
            hideSoftInput();
            outputBlueText(">>> setGrant");
            String uid = etUid.getText().toString();
            if (TextUtils.isEmpty(uid)) {
                outputRedText("Please input the 'Uid'");
                return;
            }
            String alias = etAlias.getText().toString();
            int ret = keyChain.setGrant(Integer.valueOf(uid), alias, isGrant);
            if (ret == SystemError.SUCCESS) {
                outputText("setGrant success");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    private String getUid() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return ai.uid + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
