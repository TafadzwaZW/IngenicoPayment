package cn.eas.usdk.demo.view.pinpad;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.pinpad.KAPId;
import com.usdk.apiservice.aidl.pinpad.KeyCfg;
import com.usdk.apiservice.aidl.pinpad.KeyGenerateMode;
import com.usdk.apiservice.aidl.pinpad.KeyHandle;
import com.usdk.apiservice.aidl.pinpad.KeyModeOfUse;
import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.KeyType;
import com.usdk.apiservice.aidl.pinpad.KeyUsage;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;

public class DeriveKeyActivity extends BasePinpadActivity {
    private int srcKeyId;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_pinpad_derive_key);
        setTitle("Derive Key Module");

        // pinpad#open
        open();
    }

    public void loadPlainTextKey(View v) {
        boolean isSucc;
        try {
            int keyId = KEYID_MAIN;

            pinpadLimited.format();
            isSucc = pinpadLimited.loadPlainTextKey(KeyType.DERIVE_KEY, keyId, BytesUtil.hexString2Bytes("111111111111111111111111111111111111111111111111"));

            if (isSucc) {
                srcKeyId = keyId;
                outputText(String.format("loadPlainTextKey(DERIVE_KEY, keyId = %s) success", keyId));
            } else {
                outputPinpadError("loadPlainTextKey DERIVE_KEY fail");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void loadEncKey(View v) {
        boolean isSucc;
        try {
            outputBlueText(">>> loadPlainTextKey");
            int mainKeyId = KEYID_MAIN;
            String key = "111111111111111111111111111111111111111111111111";

            pinpadLimited.format();
            isSucc = pinpadLimited.loadPlainTextKey(KeyType.MAIN_KEY, mainKeyId, BytesUtil.hexString2Bytes(key));

            if (isSucc) {
                outputText(String.format("loadPlainTextKey(MAIN_KEY, keyId = %s) success", mainKeyId));
            } else {
                outputPinpadError("loadPlainTextKey fail");
                return;
            }

            outputBlueText(">>> switchToWorkMode");
            isSucc = pinpadLimited.switchToWorkMode();
            if (isSucc) {
                outputText("switchToWorkMode success");
            } else {
                outputPinpadError("switchToWorkMode fail");
            }

            int keyId = 14;
            byte[] encKey = BytesUtil.hexString2Bytes("116817D8855150C9116817D8855150C9116817D8855150C9");
            byte[] kcv = BytesUtil.hexString2Bytes("0F2FCF4A");
            isSucc = pinpad.loadEncKey(KeyType.DERIVE_KEY, mainKeyId, keyId, encKey, kcv);
            if (isSucc) {
                srcKeyId = keyId;
                outputText(String.format("loadEncKey(DERIVE_KEY, keyId = %s) success", keyId));
            } else {
                outputPinpadError(String.format("loadEncKey(DERIVE_KEY, keyId = %s) fail", keyId));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void generateKeyMode7(View v) {
        // first: loadPlainTextKey or loadEncKey

        boolean isSucc;
        KeyCfg keyCfg = new KeyCfg(KeyUsage.KU_PIN_ENCRYPTION, KeyModeOfUse.MOU_ENC_OR_WRAP_ONLY);

        // InData format is as follows  :
        // DivTypeAlgo (4B, dispersion mode， small end mode) + DivParam(nB, input factor)
        // Currently，DivTypeAlgo only support ALGO_KEY_ENCRYPT_TDES_ECB(0x02)，and input factor length of 16 bytes.
        byte[] in = BytesUtil.hexString2Bytes("02000000" + "22222222222222222222222222222222");
        BytesValue out = new BytesValue();
        try {
            int keyId = 15;
            isSucc = pinpad.generateKey(KeyGenerateMode.KGM_MODE7, srcKeyId ,keyId, keyCfg, in, out);

            if (isSucc) {
                outputText(String.format("generateKey(KGM_MODE7, keyId = %s) success", keyId));
            } else {
                outputPinpadError(String.format("generateKey(KGM_MODE7, keyId = %s) fail", keyId));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void generateKeyByKeyHandleMode7(View v) {
        // first: loadPlainTextKey or loadEncKey
        boolean isSucc;
        KeyCfg keyCfg = new KeyCfg(KeyUsage.KU_PIN_ENCRYPTION, KeyModeOfUse.MOU_ENC_OR_WRAP_ONLY);

        // inData format is as follows :
        // DivTypeAlgo (4B, dispersion mode， small end mode) + DivParam(nB, input factor)
        // Currently，DivTypeAlgo only support ALGO_KEY_ENCRYPT_TDES_ECB(0x02)，and input factor length of 16 bytes.
        byte[] in = BytesUtil.hexString2Bytes("02000000" + "22222222222222222222222222222222");
        BytesValue out = new BytesValue();
        try {
            isSucc = pinpad.generateKeyByKeyHandle(KeyGenerateMode.KGM_MODE7, getKeyHandle(srcKeyId), getKeyHandle(1), keyCfg, in, out);
            if (isSucc) {
                outputText(String.format("generateKeyByKeyHandle(KGM_MODE7) success"));
            } else {
                outputPinpadError(String.format("generateKey(KGM_MODE7) fail"));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    protected KeyHandle getKeyHandle(int keyId) {
        KeyHandle keyHandle = new KeyHandle();
        keyHandle.setKapId(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM));
        keyHandle.setKeySystem(KeySystem.KS_MKSK);
        keyHandle.setKeyId(keyId);
        return keyHandle;
    }

}
