package cn.eas.usdk.demo.view.pinpad;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.pinpad.DESMode;
import com.usdk.apiservice.aidl.pinpad.EncKeyBundleFmt;
import com.usdk.apiservice.aidl.pinpad.EncKeyFmt;
import com.usdk.apiservice.aidl.pinpad.EnumKeyInfoMode;
import com.usdk.apiservice.aidl.pinpad.GetKeyInfoMode;
import com.usdk.apiservice.aidl.pinpad.KAPId;
import com.usdk.apiservice.aidl.pinpad.KeyAlgorithm;
import com.usdk.apiservice.aidl.pinpad.KeyCfg;
import com.usdk.apiservice.aidl.pinpad.KeyExportability;
import com.usdk.apiservice.aidl.pinpad.KeyHandle;
import com.usdk.apiservice.aidl.pinpad.KeyInfo;
import com.usdk.apiservice.aidl.pinpad.KeyModeOfUse;
import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.KeyType;
import com.usdk.apiservice.aidl.pinpad.KeyUsage;
import com.usdk.apiservice.aidl.pinpad.KeyVersionNumber;
import com.usdk.apiservice.aidl.pinpad.MacMode;
import com.usdk.apiservice.aidl.pinpad.OnEnumKeyInfoListener;
import com.usdk.apiservice.aidl.pinpad.PinpadData;
import com.usdk.apiservice.aidl.pinpad.PinpadError;
import com.usdk.apiservice.aidl.pinpad.RKGenerateMode;

import java.util.ArrayList;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;

public class ControlledApiActivity extends BasePinpadActivity {

    final String plainTextMainKey = "111111111111111111111111111111111111111111111111";

    @Override
    public int getKeySystem() {
        return KeySystem.KS_MKSK;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_pinpad_controlled);
        setTitle("Pinpad Controlled api");

        open();
    }

    public void loadPlainTextKeyControlled(View v) {
        try {
            outputBlueText(">>> loadPlainTextKeyControlled");
            boolean isSucc = pinpadLimited.formatControlled();
            if (isSucc) {
                outputText("formatControlled success");
            } else {
                outputPinpadError("formatControlled fail");
                return;
            }

            outputBlueText(">>> loadPlainTextKeyControlled");
            int keyId = KEYID_MAIN;
            isSucc = pinpadLimited.loadPlainTextKeyControlled(KeyType.MAIN_KEY, keyId, BytesUtil.hexString2Bytes(plainTextMainKey));
            if (isSucc) {
                outputText(String.format("loadPlainTextKeyControlled(MAIN_KEY, keyId = %s) success", keyId));
            } else {
                outputPinpadError("loadPlainTextKeyControlled fail");
                return;
            }

            outputBlueText(">>> switchToWorkModeControlled");
            isSucc = pinpadLimited.switchToWorkModeControlled();
            if (isSucc) {
                outputText("switchToWorkModeControlled success");
            } else {
                outputPinpadError("switchToWorkModeControlled fail");
            }


        } catch (Exception e) {
            outputRedText("Exception: " + e.getMessage());
        }
    }


    public void deleteKeyControlled(View v) {
        try {
            outputBlueText(">>> deleteKeyControlled");
            boolean isSuccess = false;
            if (pinpad.isKeyExist(KEYID_MAIN)) {
                isSuccess = pinpadLimited.deleteKeyControlled(getMainKeyHandle());
                if (isSuccess) {
                    outputText("call deleteKeyControlled to delete main key success, main key isExist = " + pinpad.isKeyExist(KEYID_MAIN));
                } else {
                    outputRedText("call deleteKeyControlled to delete main key failure! error = " + pinpad.getLastError());
                }
            } else {
                outputRedText(" main key no exist !! please click loadPlainTextKeyControlled first");
            }

            int[] keyIds = new int[] {KEYID_PIN, KEYID_MAC, KEYID_TRACK, KEYID_DES};
            for (int keyId: keyIds) {
                if (pinpad.isKeyExist(keyId)) {
                    isSuccess = pinpadLimited.deleteKeyControlled(keyId);
                    if (isSuccess) {
                        outputText("call deleteKeyControlled to delete keyId " + keyId + " is success, key isExist = " + pinpad.isKeyExist(keyId));
                    } else {
                        outputRedText("call deleteKeyControlled to delete keyId " + keyId + " is failure! error = " + pinpad.getLastError());
                    }
                }
            }

        } catch (Exception e) {
            outputRedText("Exception: " + e.getMessage());
        }
    }


    public void setPinpadSerialNumControlled(View v) {
        try {
            outputBlueText(">>> setPinpadSerialNumControlled");
            byte[] sn = new byte[32];
            for (int i=0; i < sn.length; i ++) {
                sn[i] = (byte) i;
            }
            boolean isSuccess = pinpadLimited.setPinpadSerialNumControlled(sn);
            if (isSuccess) {
                outputText("call setPinpadSerialNumControlled success " + BytesUtil.bytes2HexString(pinpad.getPinpadInfo().getByteArray(PinpadData.SERIAL_NUM)));
            } else {
                outputRedText("call setPinpadSerialNumControlled failure! error = " + pinpad.getLastError());
            }
        } catch (RemoteException e) {
            outputRedText("Exception: " + e.getMessage());
        }
    }

    public void loadWorkKeys(View v) {
        outputBlueText(">>> loadEncKey");
        try {
            int[] keyIds = new int[] {KEYID_PIN, KEYID_MAC, KEYID_TRACK, KEYID_DES};
            int[] keyTypes = new int[] {KeyType.PIN_KEY, KeyType.MAC_KEY, KeyType.TDK_KEY, KeyType.DEK_KEY};
            String[] keys = new String[] {
                    // value is 24 bytes of 9
                    "116817D8855150C9116817D8855150C9116817D8855150C9",
                    // value is 24 bytes of 8
                    "2DE2F089C15D9E992DE2F089C15D9E992DE2F089C15D9E99",
                    // value is 24 bytes of 7
                    "BDE3888C42CE9DECBDE3888C42CE9DECBDE3888C42CE9DEC",
                    // value is 24 bytes of 6
                    "A30FE2C1D07BCC11A30FE2C1D07BCC11A30FE2C1D07BCC11"
            };
            String[] kcvs = new String[] {
                    "0F2FCF4A",
                    "F9F4FBD3",
                    "4CBE91BE",
                    "B0B563C2"
            };

            for (int i = 0; i < keyIds.length; i++) {
                int keyType = keyTypes[i];
                int keyId = keyIds[i];
                boolean isSucc = pinpad.loadEncKey(keyType, KEYID_MAIN, keyId, BytesUtil.hexString2Bytes(keys[i]), BytesUtil.hexString2Bytes(kcvs[i]));
                if (isSucc) {
                    outputText(String.format("loadEncKey(keyType = %s, keyId = %s) success",keyType, keyId));
                } else {
                    outputPinpadError(String.format("loadEncKey(keyType = %s, keyId = %s) fail",keyType, keyId));
                }
            }
        } catch (Exception e) {
            outputRedText("Exception: " + e.getMessage());
        }
    }


    public void test(View v) {
        try {
            outputBlueText(">>> formatControlled");
            boolean isSucc = pinpadLimited.formatControlled();
            if (isSucc) {
                outputText("formatControlled success");
            } else {
                outputPinpadError("formatControlled fail");
                return;
            }

        } catch (Exception e) {
            outputRedText("formatControlled Exception: " + e.getMessage());
        }

        try {
            outputBlueText(">>> loadPlainTextKeyControlled");
            int keyId = KEYID_MAIN;
            boolean isSucc = pinpadLimited.loadPlainTextKeyControlled(KeyType.MAIN_KEY, keyId, BytesUtil.hexString2Bytes(plainTextMainKey));
            if (isSucc) {
                outputText(String.format("loadPlainTextKeyControlled(MAIN_KEY, keyId = %s) success", keyId));
            } else {
                outputPinpadError("loadPlainTextKeyControlled fail");
                return;
            }
        } catch (Exception e) {
            outputRedText(" loadPlainTextKeyControlled Exception: " + e.getMessage());
        }

        try {
            outputBlueText(">>> switchToWorkModeControlled");
            boolean isSucc = pinpadLimited.switchToWorkModeControlled();
            if (isSucc) {
                outputText("switchToWorkModeControlled success");
            } else {
                outputPinpadError("switchToWorkModeControlled fail");
            }
        } catch (Exception e) {
            outputRedText("switchToWorkModeControlled Exception: " + e.getMessage());
        }

        try {
            outputBlueText(">>> deleteKeyControlled");
            boolean isSuccess = pinpadLimited.deleteKeyControlled(getMainKeyHandle());
            if (isSuccess) {
                outputText("call deleteKeyControlled to delete main key success, main key isExist = " + pinpad.isKeyExist(KEYID_MAIN));
            } else {
                outputRedText("call deleteKeyControlled to delete main key failure! error = " + pinpad.getLastError());
            }
        } catch (Exception e) {
            outputRedText("deleteKeyControlled Exception: " + e.getMessage());
        }

        try {
            outputBlueText(">>> deleteKeyControlled2");
            boolean isSuccess = pinpadLimited.deleteKeyControlled(KEYID_MAIN);
            if (isSuccess) {
                outputText("call deleteKeyControlled2 to delete main key success, main key isExist = " + pinpad.isKeyExist(KEYID_MAIN));
            } else {
                outputRedText("call deleteKeyControlled2 to delete main key failure! error = " + pinpad.getLastError());
            }
        } catch (Exception e) {
            outputRedText("deleteKeyControlled2 Exception: " + e.getMessage());
        }

        try {
            outputBlueText(">>> setPinpadSerialNumControlled");
            byte[] sn = new byte[32];
            for (int i=0; i < sn.length; i ++) {
                sn[i] = (byte) i;
            }
            boolean isSuccess = pinpadLimited.setPinpadSerialNumControlled(sn);
            if (isSuccess) {
                outputText("call setPinpadSerialNumControlled success " + BytesUtil.bytes2HexString(pinpad.getPinpadInfo().getByteArray(PinpadData.SERIAL_NUM)));
            } else {
                outputRedText("call setPinpadSerialNumControlled failure! error = " + pinpad.getLastError());
            }
        } catch (Exception e) {
            outputRedText("setPinpadSerialNumControlled Exception: " + e.getMessage());
        }
    }

    private KeyHandle getMainKeyHandle() {
        return new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), getKeySystem(), KEYID_MAIN);
    }
}
