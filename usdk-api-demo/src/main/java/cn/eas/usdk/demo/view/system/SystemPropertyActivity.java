package cn.eas.usdk.demo.view.system;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.data.StringValue;
import com.usdk.apiservice.aidl.system.SystemError;
import com.usdk.apiservice.aidl.system.systemproperty.EMVL1Type;
import com.usdk.apiservice.aidl.system.systemproperty.EMVL2Type;
import com.usdk.apiservice.aidl.system.systemproperty.USystemProperty;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.util.SystemErrorUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class SystemPropertyActivity extends BaseDeviceActivity {

    private static final List<String> EMVL1TYPE_LIST = Arrays.asList(
            EMVL1Type.IC_EMVL1_PERM.name(),
            EMVL1Type.RFCARD_EMVL1_PERM.name()
    );
    private static final List<String> EMVL2TYPE_LIST = Arrays.asList(
            EMVL2Type.EMV_ALL_FUNC.name(),
            EMVL2Type.EMV_CONTACT.name(),
            EMVL2Type.EMV_MASTERCARD.name(),
            EMVL2Type.EMV_VISA.name(),
            EMVL2Type.EMV_UPI.name(),
            EMVL2Type.EMV_AMEX.name(),
            EMVL2Type.EMV_DISCOVER.name(),
            EMVL2Type.EMV_JCB.name(),
            EMVL2Type.EMV_PURE.name(),
            EMVL2Type.EMV_RUPAY.name(),
            EMVL2Type.EMV_MIR.name(),
            EMVL2Type.EMV_PAGO.name(),
            EMVL2Type.EMV_MB.name(),
            EMVL2Type.EMV_INTERACT.name()
    );

    private EditText edtProperty,edtP2peFlag;
    private RadioGroup rgState;
    private NiceSpinner spEMVL1Type, spEMVL2Type;
    private USystemProperty systemProperty;

    private EMVL1Type emvl1Type = EMVL1Type.IC_EMVL1_PERM;
    private EMVL2Type emvl2Type = EMVL2Type.EMV_ALL_FUNC;
    private int state = 1;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
        setContentView(R.layout.activity_system_property);
        setTitle("System property Module");

        initViews();
    }

    protected void initDeviceInstance() {
        systemProperty = DeviceHelper.me().getSystemProperty();
    }

    private void initViews() {
        edtProperty = findViewById(R.id.edt_property);
        edtP2peFlag = findViewById(R.id.edt_p2pe_flag);
        RadioGroup rgState = findViewById(R.id.rgState);
        rgState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbEnable) {
                    state = 1;
                } else {
                    state = 0;
                }
            }
        });

        NiceSpinner spEMVL1Type = findViewById(R.id.spEMVL1Type);
        spEMVL1Type.attachDataSource(EMVL1TYPE_LIST);
        spEMVL1Type.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                emvl1Type = EMVL1Type.valueOf(EMVL1TYPE_LIST.get(position));
            }
        });

        NiceSpinner spEMVL2Type = findViewById(R.id.spEMVL2Type);
        spEMVL2Type.attachDataSource(EMVL2TYPE_LIST);
        spEMVL2Type.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                emvl2Type = EMVL2Type.valueOf(EMVL2TYPE_LIST.get(position));
            }
        });
    }

    public void getSystemProperty(View v) {
        String property = edtProperty.getText().toString();
        if (TextUtils.isEmpty(property)) {
            toast("Input system property");
            return;
        }

        try {
            outputBlueText(">>> getSystemProperty");
            StringValue stringValue = new StringValue();
            int ret = systemProperty.getSystemProperty(property, "", stringValue);
            if (ret == SystemError.SUCCESS) {
                outputText("getSystemProperty success : " + stringValue.getData());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setEMVL1State(View v) {
        try {
            outputBlueText(">>> setEMVL1State");
            int ret = systemProperty.setEMVL1State(emvl1Type, state);
            if (ret == SystemError.SUCCESS) {
                outputText("setEMVL1State[" + emvl1Type.name() + "] success");
                return;
            }
            outputRedText("setEMVL1State[" + emvl1Type.name() + "] failed: " + SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getEMVL1State(View v) {
        try {
            outputBlueText(">>> getEMVL1State");
            IntValue state = new IntValue();
            int ret = systemProperty.getEMVL1State(emvl1Type, state);
            if (ret == SystemError.SUCCESS) {
                outputText("getEMVL1State[" + emvl1Type.name() + "] success, state = " + state.getData());
                return;
            }
            outputRedText("getEMVL1State[" + emvl1Type.name() + "] failed: " + SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setEMVL2State(View v) {
        try {
            outputBlueText(">>> setEMVL2State");
            int ret = systemProperty.setEMVL2State(emvl2Type, state);
            if (ret == SystemError.SUCCESS) {
                outputText("setEMVL2State[" + emvl2Type.name() + "] success");
                return;
            }
            outputRedText("setEMVL2State[" + emvl2Type.name() + "] failed: " + SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getEMVL2State(View v) {
        try {
            outputBlueText(">>> getEMVL2State");
            IntValue state = new IntValue();
            int ret = systemProperty.getEMVL2State(emvl2Type, state);
            if (ret == SystemError.SUCCESS) {
                outputText("getEMVL2State[" + emvl2Type.name() + "]  success, state = " + state.getData());
                return;
            }
            outputRedText("getEMVL2State[" + emvl2Type.name() + "]  failed: " + SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getP2peFlag(View v) {
        try {
            outputBlueText(">>> getP2peFlag");
            StringValue stringValue = new StringValue();
            int ret = systemProperty.getP2PEFlag(stringValue);
            if (ret == SystemError.SUCCESS) {
                outputText("getP2peFlag success : " + stringValue.getData());
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setP2peFlag(View v) {
        try {
            String p2peFlag = edtP2peFlag.getText().toString();
            if (TextUtils.isEmpty(p2peFlag)) {
                toast("Input p2pe flag");
                return;
            }
            outputBlueText(">>> setP2peFlag");
            int ret = systemProperty.setP2PEFlag(p2peFlag);
            if (ret == SystemError.SUCCESS) {
                outputText("setP2peFlag success ");
                return;
            }
            outputRedText(SystemErrorUtil.getErrorMessage(ret));
        } catch (Exception e) {
            handleException(e);
        }
    }
}
