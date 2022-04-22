package cn.eas.usdk.demo.view.pinpad;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.pinpad.HashAlgorithm;
import com.usdk.apiservice.aidl.pinpad.HmacMode;
import com.usdk.apiservice.aidl.pinpad.KAPId;
import com.usdk.apiservice.aidl.pinpad.KeyAlgorithm;
import com.usdk.apiservice.aidl.pinpad.KeyCfg;
import com.usdk.apiservice.aidl.pinpad.KeyHandle;
import com.usdk.apiservice.aidl.pinpad.KeyModeOfUse;
import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.KeyType;
import com.usdk.apiservice.aidl.pinpad.KeyUsage;
import com.usdk.apiservice.aidl.pinpad.MacKeyType;
import com.usdk.apiservice.aidl.pinpad.MacMode;
import com.usdk.apiservice.aidl.pinpad.PinpadData;
import com.usdk.apiservice.aidl.pinpad.PinpadError;

import java.util.Arrays;

import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;

/**
 * FIXED KEY system
 */
public class FixedKeyActivity extends BasePinpadActivity {

    private final int HMAC_MAX_INPUT_EACH_UPDATE = 1024;

    @Override
    public int getKeySystem() {
        return KeySystem.KS_FIXED_KEY;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_pinpad_fixedkey);
        setTitle("Pinpad Fixed Key Module");

        open();
    }

    public void isKeyExist(View v) {
        outputBlueText(">>> isKeyExist");
        int[] keyIds = new int[]{KEYID_PIN, KEYID_TRACK, KEYID_MAC};
        isKeyExist(keyIds);
    }

    public void deleteKey(View v) {
        outputBlueText(">>> deleteKey");
        int[] keyIds = new int[]{KEYID_PIN, KEYID_TRACK, KEYID_MAC};
        deleteKey(keyIds);
    }

    public void loadPlainTextKey(View v) {
        try {
            outputBlueText(">>> getKapMode");
            IntValue kapMode = new IntValue();
            boolean isSucc = pinpad.getKapMode(kapMode);
            if (isSucc) {
                outputText("getKapMode success[0 - LPTK_MODE; 1 - WORK_MODE]: " + kapMode.getData());
            } else {
                outputPinpadError("getKapMode fail");
                return;
            }

            if (kapMode.getData() != 0) {
                outputBlueText(">>> format");
                isSucc = pinpadLimited.format();
                if (isSucc) {
                    outputText("format success");
                } else {
                    outputPinpadError("format fail");
                    return;
                }
            }

            outputBlueText(">>> loadPlainTextKey");
            int[] keyTypes = new int[]{KeyType.PIN_KEY, KeyType.TDK_KEY, KeyType.MAC_KEY};
            int[] keyIds = new int[]{KEYID_PIN, KEYID_TRACK, KEYID_MAC};
            String[] keys = new String[]{"11111111111111111111111111111111", "222222222222222222222222222222222222222222222222", "44444444444444444444444444444444"};
            for (int i = 0; i < keyIds.length; i++) {
                int keyType = keyTypes[i];
                int keyId = keyIds[i];
                String key = keys[i];
                isSucc = pinpadLimited.loadPlainTextKey(keyType, keyId, BytesUtil.hexString2Bytes(key));
                if (isSucc) {
                    outputText(String.format("loadPlainTextKey(keyType = %s, keyId = %s) success",keyType, keyId));
                } else {
                    outputPinpadError("loadPlainTextKey fail");
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void calcMAC(View v) {
        calcMAC(KEYID_MAC, MacMode.MAC_PADDING_MODE_1, MacMode.MAC_ALG_ISO9797);
    }

    public void encryptMagTrack(View v) {
        encryptMagTrack(KEYID_TRACK);
    }

    public void startPinEntry(View v) {
        startPinEntry(KEYID_PIN);
    }

    /**
     * Realize the calculation of hmac for input data, suitable for input data <=1024 bytes
     *
     * Not recommended
     *
     * @param v
     */
    public void calcHMAC(View v) {
        outputBlueText(">>> calcHMAC");
        try {
            boolean ret = loadKeyForHMAC();
            if (!ret) {
                return;
            }

            byte[] data = BytesUtil.hexString2Bytes("0000000000000000000000000000000000000000000000000000000000000000");
            byte[] hmac256 = pinpad.calcHMAC(getHMACKeyHandle(KEYID_HMAC256), HashAlgorithm.HASH_ID_SHA256, MacKeyType.MAC_USING_NORMAL_KEY, data);
            if (hmac256 != null) {
                outputBlueText(">>> calcHMAC SHA256 SUCCESS " + BytesUtil.bytes2HexString(hmac256));
            } else {
                outputRedText(">>> calcHMAC SHA256 Failure " + pinpad.getLastError());
            }
            byte[] hmac384 = pinpad.calcHMAC(getHMACKeyHandle(KEYID_HMAC384), HashAlgorithm.HASH_ID_SHA384, MacKeyType.MAC_USING_NORMAL_KEY, data);
            if (hmac384 != null) {
                outputBlueText(">>> calcHMAC SHA384 SUCCESS "  + BytesUtil.bytes2HexString(hmac384));
            } else {
                outputRedText(">>> calcHMAC SHA384 Failure " + pinpad.getLastError());
            }
            byte[] hmac512 = pinpad.calcHMAC(getHMACKeyHandle(KEYID_HMAC512), HashAlgorithm.HASH_ID_SHA512, MacKeyType.MAC_USING_NORMAL_KEY, data);
            if (hmac512 != null) {
                outputBlueText(">>> calcHMAC SHA512 SUCCESS " + BytesUtil.bytes2HexString(hmac512));
            } else {
                outputRedText(">>> calcHMAC SHA512 Failure " + pinpad.getLastError());
            }
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    /**
     * Realize the calculation of hmac for input data.
     *
     * Recommended to use this method
     *
     * @param view
     */
    public void calcHmacDofinal(View view) {
        outputBlueText(">>> calcHmacDofinal");
        try {

            String key = "0123456789abcdeffedcba98765432100123456789abcdeffedcba9876543210";

            boolean ret = pinpadLimited.loadPlainTextKey(getHMACKeyHandle(KEYID_HMAC256), getHMACKeyCfg(), BytesUtil.hexString2Bytes(key));
            if (!ret) {
                outputRedText(">>> loadHMAC256 key  failure " + pinpad.getLastError());
                return;
            }

            Bundle hmacParam = new Bundle();
            hmacParam.putChar(PinpadData.HASH_ALGORITHM, HashAlgorithm.HASH_ID_SHA256);
            hmacParam.putParcelable(PinpadData.SRC_KEY_HANDLE, getHMACKeyHandle(KEYID_HMAC256));
            hmacParam.putChar(PinpadData.MAC_KEY_TYPE, MacKeyType.MAC_USING_NORMAL_KEY);

            int result = pinpad.hmacInit(HmacMode.HM_GENERATE_HMAC, hmacParam);
            if (result != PinpadError.SUCCESS) {
                outputRedText(">>> hmacInit failure " + pinpad.getLastError());
                return;
            }

            byte[] data = BytesUtil.hexString2Bytes("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012");
            //byte[] toBeVerified = BytesUtil.hexString2Bytes("E3A5F5BD1503FDDD05383B08743DC7E8AC483237CA303E206F94D95C6937B5D8");

            int offset = 0, size;
            int piece = data.length / HMAC_MAX_INPUT_EACH_UPDATE + ((data.length % HMAC_MAX_INPUT_EACH_UPDATE) == 0 ? 0 : 1);
            for (int i = 0; i < piece; i++) {
                if (data.length - offset >= HMAC_MAX_INPUT_EACH_UPDATE) {
                    size = HMAC_MAX_INPUT_EACH_UPDATE;
                } else {
                    size = data.length - offset;
                }
                byte[] block = Arrays.copyOfRange(data, offset, offset + size);
                result = pinpad.hmacUpdate(block);
                if (result != PinpadError.SUCCESS) {
                    outputRedText(">>> hmacUpdate failure " + pinpad.getLastError());
                    return;
                }
                offset += size;
            }
            BytesValue out = new BytesValue();
            result = pinpad.hmacFinal(out);
            if (result != PinpadError.SUCCESS) {
                outputRedText(">>> hmacFinal failure " + pinpad.getLastError());
                return;
            }
            outputText(">>> hmacFinal success " + BytesUtil.bytes2HexString(out.getData()));

        } catch (RemoteException e) {
            handleException(e);
        }
    }

    /**
     * Realize the verification of hmac value for input data, only suitable for input data <=(1008 - 12 - toBeVerified.length) bytes.
     *
     * Not recommended
     *
     * @param view
     */
    public void hmacVerifyOneshot(View view) {
        outputBlueText(">>> hmacVerifyOneshot");
        try {

            String key = "0123456789abcdeffedcba98765432100123456789abcdeffedcba9876543210";

            boolean ret = pinpadLimited.loadPlainTextKey(getHMACKeyHandle(KEYID_HMAC256), getHMACKeyCfg(), BytesUtil.hexString2Bytes(key));
            if (!ret) {
                outputRedText(">>> loadHMAC256 key  failure " + pinpad.getLastError());
                return;
            }

            Bundle hmacParam = new Bundle();
            hmacParam.putChar(PinpadData.HASH_ALGORITHM, HashAlgorithm.HASH_ID_SHA256);
            hmacParam.putParcelable(PinpadData.SRC_KEY_HANDLE, getHMACKeyHandle(KEYID_HMAC256));
            hmacParam.putChar(PinpadData.MAC_KEY_TYPE, MacKeyType.MAC_USING_NORMAL_KEY);

            byte[] data = BytesUtil.hexString2Bytes("4E6F77206973207468652074696D6520666F7220616C6C20");
            byte[] toBeVerified = BytesUtil.hexString2Bytes("D5D43139956B819D4B7F0D8863A7E1A9D2BE7A9A0B8F6E2ED75E3B87B2E72724");
            //byte[] data = new byte[1008 - toBeVerified.length - getHMACParamLength()];

            BooleanValue isMatch = new BooleanValue();
            int result = pinpad.hmacVerifyOneshot(HmacMode.HM_VERIFY_HMAC, hmacParam, data, toBeVerified, isMatch);
            if (result != PinpadError.SUCCESS) {
                outputRedText(">>> hmacVerifyOneshot failure " + pinpad.getLastError());
                return;
            }
            outputText(">>> hmacVerifyOneshot isMatch " + isMatch.isTrue());
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    /**
     * Realize the verification of hmac value for input data.
     *
     * Recommended to use this method
     *
     * @param view
     */
    public void hmacVerify(View view) {
        outputBlueText(">>> hmacVerify");
        try {

            String key = "0123456789abcdeffedcba98765432100123456789abcdeffedcba9876543210";

            boolean ret = pinpadLimited.loadPlainTextKey(getHMACKeyHandle(KEYID_HMAC256), getHMACKeyCfg(), BytesUtil.hexString2Bytes(key));
            if (!ret) {
                outputRedText(">>> loadHMAC256 key  failure " + pinpad.getLastError());
                return;
            }

            Bundle hmacParam = new Bundle();
            hmacParam.putChar(PinpadData.HASH_ALGORITHM, HashAlgorithm.HASH_ID_SHA256);
            hmacParam.putParcelable(PinpadData.SRC_KEY_HANDLE, getHMACKeyHandle(KEYID_HMAC256));
            hmacParam.putChar(PinpadData.MAC_KEY_TYPE, MacKeyType.MAC_USING_NORMAL_KEY);

            int result = pinpad.hmacInit(HmacMode.HM_VERIFY_HMAC, hmacParam);
            if (result != PinpadError.SUCCESS) {
                outputRedText(">>> hmacInit failure " + pinpad.getLastError());
                return;
            }

            byte[] data = BytesUtil.hexString2Bytes("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012");
            byte[] toBeVerified = BytesUtil.hexString2Bytes("E3A5F5BD1503FDDD05383B08743DC7E8AC483237CA303E206F94D95C6937B5D8");

            int offset = 0, size;
            int piece = data.length / HMAC_MAX_INPUT_EACH_UPDATE + ((data.length % HMAC_MAX_INPUT_EACH_UPDATE) == 0 ? 0 : 1);
            for (int i = 0; i < piece; i++) {
                if (data.length - offset >= HMAC_MAX_INPUT_EACH_UPDATE) {
                    size = HMAC_MAX_INPUT_EACH_UPDATE;
                } else {
                    size = data.length - offset;
                }
                byte[] block = Arrays.copyOfRange(data, offset, offset + size);
                result = pinpad.hmacUpdate(block);
                if (result != PinpadError.SUCCESS) {
                    outputRedText(">>> hmacUpdate failure " + pinpad.getLastError());
                    return;
                }
                offset += size;
            }
            BooleanValue isMatch = new BooleanValue();
            result = pinpad.hmacVerify(toBeVerified, isMatch);
            if (result != PinpadError.SUCCESS) {
                outputRedText(">>> hmacVerify failure " + pinpad.getLastError());
                return;
            }
            outputText(">>> hmacVerify isMatch " + isMatch.isTrue());
        } catch (RemoteException e) {
            handleException(e);
        }
    }


    public void getHmacKcv(View view) {
        outputBlueText(">>> getHmacKcv");
        try {

            String key = "0123456789abcdeffedcba98765432100123456789abcdeffedcba9876543210";
            boolean ret = pinpadLimited.loadPlainTextKey(getHMACKeyHandle(KEYID_HMAC256), getHMACKeyCfg(), BytesUtil.hexString2Bytes(key));
            if (!ret) {
                outputRedText(">>> loadHMAC256 key  failure " + pinpad.getLastError());
                return;
            }
            BytesValue kcv = new BytesValue();
            int result = pinpad.getHmacKcv(0, getHMACKeyHandle(KEYID_HMAC256), HashAlgorithm.HASH_ID_SHA256, kcv);
            if (result != PinpadError.SUCCESS) {
                outputRedText(">>> getHmacKcv  failure " + pinpad.getLastError());
                return;
            }
            outputText(">>> kcv " + BytesUtil.bytes2HexString(kcv.getData()));
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void loadPinKeyWithFixKey(View view) {
        outputBlueText(">>> loadPinKeyWithFixKey");
        try {

            String key = "0123456789abcdeffedcba9876543210";
            KeyHandle keyHandle = new KeyHandle();
            keyHandle.setKapId(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM));
            keyHandle.setKeySystem(KeySystem.KS_FIXED_KEY);
            keyHandle.setKeyId(122);

            KeyCfg keyCfg = new KeyCfg();
            keyCfg.setKeyUsage(KeyUsage.KU_PIN_ENCRYPTION);
            keyCfg.setModeOfUse(KeyModeOfUse.MOU_ENC_OR_WRAP_ONLY);
            keyCfg.setKeyAlgorithm(KeyAlgorithm.KA_TDEA);
            boolean ret = pinpadLimited.loadPlainTextKey(keyHandle, keyCfg, BytesUtil.hexString2Bytes(key));
            if (!ret) {
                outputRedText(">>> loadPinKeyWithFixKey key  failure " + pinpad.getLastError());
                return;
            }
            outputText(">>> loadPinKeyWithFixKey success");
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    private boolean loadKeyForHMAC() throws RemoteException {
        //Load the SHA256 key, the length needs to be 32-64 bytes
        boolean ret = pinpadLimited.loadPlainTextKey(getHMACKeyHandle(KEYID_HMAC256), getHMACKeyCfg(), BytesUtil.hexString2Bytes("1111111111111111111111111111111111111111111111111111111111111111"));
        if (!ret) {
            outputRedText(">>> loadHMAC256 key  failure "+ pinpad.getLastError());
            return false;
        }
        //Load the SHA384 key, the length needs to be 48-128 bytes
        ret = pinpadLimited.loadPlainTextKey(getHMACKeyHandle(KEYID_HMAC384), getHMACKeyCfg(), BytesUtil.hexString2Bytes("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"));
        if (!ret) {
            outputRedText(">>> loadHMAC384 key  failure "+ pinpad.getLastError());
            return false;
        }
        //Load the SHA512 key, the length needs to be 64-128 bytes
        ret = pinpadLimited.loadPlainTextKey(getHMACKeyHandle(KEYID_HMAC512), getHMACKeyCfg(), BytesUtil.hexString2Bytes("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"));
        if (!ret) {
            outputRedText(">>> loadHMAC512 key  failure "+ pinpad.getLastError());
            return false;
        }
        return true;
    }

    private KeyHandle getHMACKeyHandle(int hmacKeyId) {
        KeyHandle keyHandle = new KeyHandle();
        keyHandle.setKapId(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM));
        keyHandle.setKeySystem(KeySystem.KS_FIXED_KEY);
        keyHandle.setKeyId(hmacKeyId);
        return keyHandle;
    }
    private KeyCfg getHMACKeyCfg() {
        KeyCfg keyCfg = new KeyCfg();
        keyCfg.setKeyUsage(KeyUsage.KU_HMAC);
        keyCfg.setModeOfUse(KeyModeOfUse.MOU_GENERATE_AND_VERIFY);
        keyCfg.setKeyAlgorithm(KeyAlgorithm.KA_HMAC_SHA_1);
        return keyCfg;
    }
}
