package cn.eas.usdk.demo.view.onguard;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.OnGuardConfig;
import cn.eas.usdk.demo.util.SharedPreferencesManager;
import cn.eas.usdk.demo.util.ToastUtil;
import cn.eas.usdk.demo.view.BaseActivity;

/**
 * OnGuard module configuration information
 */
public class OnGuardSettinigActivity extends BaseActivity {

    private String ipek;
    private String ksn;
    private int regionId = 0;
    private int kapId = 0;
    private int keyId = 0;
    private int nbBin = 0;
    private boolean externalFlag = true;
    private boolean encPanEnd = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onguard_settings);
        setTitle(getString(R.string.setting));
        initViewData();
    }

    private EditText edtIpek, edtKsn, edtRegionId, edtKapId, edtKeyId, edtNbBin;
    private CheckBox ckExtensiveFlag, ckEncPanEnd;
    private RadioButton rbJobTypeEmv;

    private void initViewData() {
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCancel();
            }
        });
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirm();
            }
        });
        edtIpek = findViewById(R.id.edt_ipek);
        edtKsn = findViewById(R.id.edt_ksn);
        edtRegionId = findViewById(R.id.edt_region_id);
        edtKapId = findViewById(R.id.edt_kap_id);
        edtKeyId = findViewById(R.id.edt_key_id);
        edtNbBin = findViewById(R.id.edt_nbbin);

        edtIpek.setText(SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_IPEK, OnGuardConfig.DEF_VALUE_IPEK));
        edtKsn.setText(SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_KSN, OnGuardConfig.DEF_VALUE_KSN));
        edtRegionId.setText(String.valueOf(SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_REGION_ID, OnGuardConfig.DEF_VALUE_REGION_ID)));
        edtKapId.setText(String.valueOf(SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_KAP_ID, OnGuardConfig.DEF_VALUE_KAP_ID)));
        edtKeyId.setText(String.valueOf(SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_KEY_ID, OnGuardConfig.DEF_VALUE_KEY_ID)));
        edtNbBin.setText(String.valueOf(SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_NB_BIN, OnGuardConfig.DEF_VALUE_NB_BIN)));
        ckExtensiveFlag = findViewById(R.id.ck_extensive_flag);
        ckEncPanEnd = findViewById(R.id.ck_extensive_flag);

        ckExtensiveFlag.setChecked(SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_EXTERNAL_Flag, OnGuardConfig.DEF_VALUE_EXTERNAL_FLAG));
        ckEncPanEnd.setChecked(SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_ENC_PAN_END, OnGuardConfig.DEF_VALUE_ENC_PAN_END));

        rbJobTypeEmv = findViewById(R.id.rb_job_type_emv);

        if (SharedPreferencesManager.getInstance()
                .getParameter(this, OnGuardConfig.KEY_JOB_TYPE_IS_EMV, OnGuardConfig.DEF_VALUE_JOB_TYPE_IS_EMV)) {
            rbJobTypeEmv.setChecked(true);
        } else {
            rbJobTypeEmv.setChecked(false);
        }

        rbJobTypeEmv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesManager.getInstance()
                        .setParameter(OnGuardSettinigActivity.this, OnGuardConfig.KEY_JOB_TYPE_IS_EMV, isChecked);
            }
        });
    }

    private boolean checkData() {
        ipek = edtIpek.getText().toString().trim();
        ksn = edtKsn.getText().toString().trim();
        String regionIdStr = edtRegionId.getText().toString().trim();
        String kapIdStr = edtKapId.getText().toString().trim();
        String keyIdStr = edtKeyId.getText().toString().trim();
        String nbBinStr = edtNbBin.getText().toString().trim();
        externalFlag = ckExtensiveFlag.isChecked();
        encPanEnd = ckEncPanEnd.isChecked();
        if (TextUtils.isEmpty(ipek)) {
            ToastUtil.showToast("Illegal IPEK");
            return false;
        }
        if (TextUtils.isEmpty(ksn)) {
            ToastUtil.showToast("Illegal ksn");
            return false;
        }
        if (TextUtils.isEmpty(regionIdStr)) {
            ToastUtil.showToast("Illegal regionId");
            return false;
        }
        regionId = Integer.valueOf(regionIdStr);

        if (TextUtils.isEmpty(kapIdStr)) {
            ToastUtil.showToast("Illegal kapId");
            return false;
        }
        kapId = Integer.valueOf(kapIdStr);

        if (TextUtils.isEmpty(keyIdStr)) {
            ToastUtil.showToast("Illegal keyId");
            return false;
        }
        keyId = Integer.valueOf(keyIdStr);

        if (TextUtils.isEmpty(nbBinStr)) {
            ToastUtil.showToast("Illegal nbBin");
            return false;
        }
        nbBin = Integer.valueOf(nbBinStr);
        return true;
    }

    private void buttonConfirm() {
        if (checkData()) {
            SharedPreferencesManager.getInstance().setParameter(this, OnGuardConfig.KEY_IPEK, ipek);
            SharedPreferencesManager.getInstance().setParameter(this, OnGuardConfig.KEY_KSN, ksn);
            SharedPreferencesManager.getInstance().setParameter(this, OnGuardConfig.KEY_REGION_ID, regionId);
            SharedPreferencesManager.getInstance().setParameter(this, OnGuardConfig.KEY_KAP_ID, kapId);
            SharedPreferencesManager.getInstance().setParameter(this, OnGuardConfig.KEY_KEY_ID, keyId);
            SharedPreferencesManager.getInstance().setParameter(this, OnGuardConfig.KEY_NB_BIN, nbBin);
            SharedPreferencesManager.getInstance()
                    .setParameter(this, OnGuardConfig.KEY_EXTERNAL_Flag, externalFlag);
            SharedPreferencesManager.getInstance().setParameter(this, OnGuardConfig.KEY_ENC_PAN_END, encPanEnd);
            ToastUtil.showToast("save success");
            finish();
        }
    }

    private void buttonCancel() {
        finish();
    }
}
