package cn.eas.usdk.demo.view.pinpad;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import org.angmarch.views.NiceSpinner;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.entity.SpinnerItem;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.view.BaseActivity;

/**
 * RKIS related data settings
 */
public class RkisSettingActivity extends BaseActivity {

    private NiceSpinner ipSpinner;
    private EditText etPort;
    private EditText etUserDefinedIp;
    private EditText etTpdu;
    private EditText etKmsId;
    private EditText etTerminalNo;
    private EditText etMerchantNo;

    private EditText etRegionID;
    private EditText etKapNum;
    private EditText etKeyId;

    private SpinnerItem kmsMode;
    private SpinnerItem lrkmMode;
    private SpinnerItem keySystem;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rkis_setting);
        setTitle("RKIS Setting");
        initView();
    }

    private void initView() {
        etRegionID = bindViewById(R.id.etRegionID);
        etKapNum = bindViewById(R.id.etKapNum);
        etKeyId = bindViewById(R.id.etKeyId);

        etPort = bindViewById(R.id.etPort);
        etUserDefinedIp = bindViewById(R.id.etUserDefinedIp);
        etKmsId = bindViewById(R.id.etKmsId);
        etTpdu = bindViewById(R.id.etTpdu);
        etTerminalNo = bindViewById(R.id.etTerminalNo);
        etMerchantNo = bindViewById(R.id.etMerchantNo);

        etRegionID.setText("" + DemoConfig.RKIS_REGION_ID);
        etKapNum.setText("" + DemoConfig.RKIS_KAP_NUM);
        etKeyId.setText("" + DemoConfig.RKIS_KEY_ID);
        etPort.setText(DemoConfig.KMS_PORT);
        etKmsId.setText(DemoConfig.KMS_ID);
        etTpdu.setText(BytesUtil.bytes2HexString(DemoConfig.KMS_TPDU));
        etTerminalNo.setText(DemoConfig.TERM_ID);
        etMerchantNo.setText(DemoConfig.MERCH_ID);

        initSpinner();
    }

    private void initSpinner() {
        NiceSpinner kmsModeSpinner = findViewById(R.id.kmsModeSpinner);
        kmsModeSpinner.attachDataSource(DemoConfig.LIST_KMS_MODE);
        kmsModeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                kmsMode = DemoConfig.LIST_KMS_MODE.get(position);
            }
        });
        setSpinnerDefaultValue(kmsModeSpinner, DemoConfig.LIST_KMS_MODE, DemoConfig.KMS_MODE);
        kmsMode = DemoConfig.LIST_KMS_MODE.get(0);

        NiceSpinner lrkmModeSpinner = findViewById(R.id.lrkmModeSpinner);
        lrkmModeSpinner.attachDataSource(DemoConfig.LIST_LRKM_MODE);
        lrkmModeSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                lrkmMode = DemoConfig.LIST_LRKM_MODE.get(position);
            }
        });
        setSpinnerDefaultValue(lrkmModeSpinner, DemoConfig.LIST_LRKM_MODE, DemoConfig.LRKM_MODE);
        lrkmMode = DemoConfig.LIST_LRKM_MODE.get(0);

        NiceSpinner keySystemSpinner = findViewById(R.id.keySystemSpinner);
        keySystemSpinner.attachDataSource(DemoConfig.LIST_KEY_SYSTEM);
        keySystemSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                keySystem = DemoConfig.LIST_KEY_SYSTEM.get(position);
            }
        });
        setSpinnerDefaultValue(keySystemSpinner, DemoConfig.LIST_KEY_SYSTEM, DemoConfig.RKIS_KEY_SYSTEM);
        keySystem = DemoConfig.LIST_KEY_SYSTEM.get(0);

        ipSpinner = findViewById(R.id.ipSpinner);
        ipSpinner.attachDataSource(DemoConfig.LIST_KMS_IP);
        ipSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String text = DemoConfig.LIST_KMS_IP.get(position);
                if ("user defined".equalsIgnoreCase(text)) {
                    etUserDefinedIp.setVisibility(View.VISIBLE);
                    ip = etUserDefinedIp.getText().toString().trim();
                } else {
                    etUserDefinedIp.setVisibility(View.GONE);
                    ip = text;
                }
            }
        });
        setSpinnerDefaultValue(ipSpinner, DemoConfig.LIST_KMS_IP, DemoConfig.KMS_IP);
        ip = DemoConfig.LIST_KMS_IP.get(0);
    }

    public void save(View v) {
        if ("user defined".equalsIgnoreCase(ipSpinner.getText().toString().trim())) {
            ip = etUserDefinedIp.getText().toString().trim();
            if (ip.isEmpty()) {
                toast(getString(R.string.request_ip));
                return;
            }
        }

        String port = etPort.getText().toString();
        if (port.isEmpty()) {
            toast(getString(R.string.request_port));
            etPort.requestFocus();
            return;
        }
        String cid = etKmsId.getText().toString();
        if (cid.isEmpty()) {
            toast(getString(R.string.request_cid));
            etKmsId.requestFocus();
            return;
        }
        String tpdu = etTpdu.getText().toString();
        if (tpdu.isEmpty()) {
            toast(getString(R.string.request_tpdu));
            etTpdu.requestFocus();
            return;
        }
        String terminalNo = etTerminalNo.getText().toString();
        if (terminalNo.isEmpty()) {
            toast(getString(R.string.request_terminalno));
            etTerminalNo.requestFocus();
            return;
        }
        String merchantNo = etMerchantNo.getText().toString();
        if (merchantNo.isEmpty()) {
            toast (getString(R.string.request_merchantno));
            etMerchantNo.requestFocus();
            return;
        }
        hideSoftInput();

        DemoConfig.KMS_MODE = kmsMode.getValue();
        DemoConfig.LRKM_MODE = lrkmMode.getValue();
        DemoConfig.RKIS_KEY_SYSTEM = keySystem.getValue();
        DemoConfig.RKIS_REGION_ID = getRegionID();
        DemoConfig.RKIS_KAP_NUM = getKapNum();
        DemoConfig.RKIS_KEY_ID = getKeyId();

        DemoConfig.KMS_IP = ip;
        DemoConfig.KMS_PORT = port;
        DemoConfig.KMS_ID = cid;
        DemoConfig.KMS_TPDU = BytesUtil.hexString2Bytes(tpdu);
        DemoConfig.MERCH_ID = merchantNo;
        DemoConfig.TERM_ID = terminalNo;

        finish();
    }


    private int getRegionID() {
        String regionID = etRegionID.getText().toString();
        if (regionID.isEmpty()) {
            regionID = etRegionID.getHint().toString();
        }
        return Integer.parseInt(regionID);
    }

    private int getKapNum() {
        String kapNum = etKapNum.getText().toString();
        if (kapNum.isEmpty()) {
            kapNum = etKapNum.getHint().toString();
        }
        return Integer.parseInt(kapNum);
    }

    private int getKeyId() {
        String keyId = etKeyId.getText().toString();
        if (keyId.isEmpty()) {
            keyId = etKeyId.getHint().toString();
        }
        return Integer.parseInt(keyId);
    }
}
