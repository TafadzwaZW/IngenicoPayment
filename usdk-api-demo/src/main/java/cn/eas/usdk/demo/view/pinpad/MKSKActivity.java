package cn.eas.usdk.demo.view.pinpad;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.usdk.apiservice.aidl.algorithm.AlgError;
import com.usdk.apiservice.aidl.algorithm.AlgMode;
import com.usdk.apiservice.aidl.algorithm.UAlgorithm;
import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.data.IntValue;
import com.usdk.apiservice.aidl.pinpad.BlockFormat;
import com.usdk.apiservice.aidl.pinpad.DESMode;
import com.usdk.apiservice.aidl.pinpad.EccGenerateParam;
import com.usdk.apiservice.aidl.pinpad.EccGroupId;
import com.usdk.apiservice.aidl.pinpad.EncKeyBundleFmt;
import com.usdk.apiservice.aidl.pinpad.EncKeyFmt;
import com.usdk.apiservice.aidl.pinpad.EnumKeyInfoMode;
import com.usdk.apiservice.aidl.pinpad.GetKeyInfoMode;
import com.usdk.apiservice.aidl.pinpad.HashAlgorithm;
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
import com.usdk.apiservice.aidl.pinpad.MacKeyType;
import com.usdk.apiservice.aidl.pinpad.MacMode;
import com.usdk.apiservice.aidl.pinpad.OnEnumKeyInfoListener;
import com.usdk.apiservice.aidl.pinpad.PinpadData;
import com.usdk.apiservice.aidl.pinpad.PinpadError;
import com.usdk.apiservice.aidl.pinpad.PinpadSkin;
import com.usdk.apiservice.aidl.pinpad.RKGenerateMode;

import java.util.ArrayList;
import java.util.Random;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;

public class MKSKActivity extends BasePinpadActivity {

    final String plainTextMainKey = "111111111111111111111111111111111111111111111111";

    @Override
    public int getKeySystem() {
        return KeySystem.KS_MKSK;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_pinpad_mksk);
        setTitle("Pinpad MKSK Module");

        open();
    }

    public void isKeyExist(View v) {
        int[] keyIds = new int[] {KEYID_MAIN, KEYID_PIN, KEYID_MAC, KEYID_TRACK, KEYID_DES};
        isKeyExist(keyIds);
    }

    public void deleteKey(View v) {
        outputBlueText(">>> deleteKey");
        int[] keyIds = new int[] {KEYID_MAIN, KEYID_PIN, KEYID_MAC, KEYID_TRACK, KEYID_DES};
        deleteKey(keyIds);
    }

    public void setPinpadSerialNum(View v) {
        setPinpadSerialNum("1234567890abcdef1234567890abcdef");
    }

    public void loadMainKey(View v) {
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

//            if (kapMode.getData() != 0) {
            outputBlueText(">>> format");
            isSucc = pinpadLimited.format();
            if (isSucc) {
                outputText("format success");
            } else {
                outputPinpadError("format fail");
                return;
            }
//            }

            outputBlueText(">>> loadPlainTextKey");
            int keyId = KEYID_MAIN;
            String key = "111111111111111111111111111111111111111111111111";
            isSucc = pinpadLimited.loadPlainTextKey(KeyType.MAIN_KEY, keyId, BytesUtil.hexString2Bytes(key));
            if (isSucc) {
                outputText(String.format("loadPlainTextKey(MAIN_KEY, keyId = %s) success", keyId));
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
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
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
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
        }
    }

    public void calcMAC(View v) {
        calcMAC(KEYID_MAC, MacMode.MAC_PADDING_MODE_1, MacMode.MAC_ALG_ISO9797);
    }

    public void calcCMAC(View v) {
        try {
            KeyHandle srcKeyHandle = new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), getKeySystem(), KEYID_MAIN);
            KeyHandle destKeyHandle = new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), getKeySystem(), KEYID_CMAC);
            KeyCfg keyCfg = new KeyCfg();
            keyCfg.setKeyAlgorithm(KeyAlgorithm.KA_AES);
            keyCfg.setKeyUsage(KeyUsage.KU_ISO_9797_1_MAC_ALGORITHM_5_CMAC);
            keyCfg.setModeOfUse(KeyModeOfUse.MOU_GENERATE_AND_VERIFY);
            keyCfg.setExportability((byte)KeyExportability.KE_NON_EXPORTABLE);
            boolean isSuccess = pinpad.loadEncKeyByKeyHandleWithKeyCfg(srcKeyHandle, destKeyHandle, EncKeyFmt.ENC_KEY_FMT_NORMAL, keyCfg,
                    BytesUtil.hexString2Bytes( "2DE2F089C15D9E992DE2F089C15D9E992DE2F089C15D9E99"), BytesUtil.hexString2Bytes("F9F4FBD3"));
            if (!isSuccess) {
                outputRedText(">>> load cmac key error = " + pinpad.getLastError());
                return;
            }
            calcMAC(KEYID_CMAC, MacMode.MAC_PADDING_MODE_1, MacMode.MAC_ALG_CMAC);
        } catch (Exception e) {
            handleException(e);
        }

    }

    public void encryptMagTrack(View v) {
        encryptMagTrack(KEYID_TRACK);
    }

    public void calculateDes(View v) {
        calculateDes(KEYID_DES);
    }

    public void getRandom(View v) {
        getRandom();
    }

    public void startPinEntry(View v) {
        startPinEntry(KEYID_PIN);
    }

    public void startOfflinePinEntry(View v) {
        startOfflinePinEntry();
    }

    public void viPin(View v) {
        viPin(KEYID_PIN);
    }
    public void useDefaultSkin(View v) {
        //use default pinpad skin
        try {
            pinpad.setSkinProperties(new Bundle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getAccessibleKapIds(View v) {
        getAccessibleKapIds();
    }

    public void getExistentKapIds(View v) {
        getExistentKapIds();
    }

    public void getPinpadInfo(View v) {
        getPinpadInfo();
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
            int keyId = KEYID_MAIN;
            KeyHandle handle = new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), getKeySystem(), keyId);
            KeyCfg keyCfg = new KeyCfg(KeyUsage.KU_KEY_ENC_OR_WRAP, KeyModeOfUse.MOU_DEC_OR_UNWRAP_ONLY);

            isSucc = pinpadLimited.loadPlainTextKey(handle, keyCfg, BytesUtil.hexString2Bytes(plainTextMainKey));

            if (isSucc) {
                outputText(String.format("loadPlainTextKey(MAIN_KEY, keyId = %s) success", keyId));
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
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
        }
    }

    public void deleteKeyByHandle(View view) {
        outputBlueText(">>> deleteKeyByHandle");

        KeyHandle[] keyHandles = new KeyHandle[] {
                new KeyHandle(getKeySystem(), KEYID_MAIN),
                new KeyHandle(getKeySystem(), KEYID_PIN),
                new KeyHandle(getKeySystem(), KEYID_MAC),
                new KeyHandle(getKeySystem(), KEYID_TRACK),
                new KeyHandle(getKeySystem(), KEYID_DES)};
        try {
            for (KeyHandle keyHandle : keyHandles) {
                boolean isSucc = pinpadLimited.deleteKey(keyHandle);
                if (isSucc) {
                    outputText(String.format("Delete key by handle(keyId = %s) success", keyHandle.getKeyId()));
                } else {
                    outputPinpadError(String.format("Delete key by handle(keyId = %s) fail", keyHandle.getKeyId()));
                }
            }
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void getKeyInfo(View view) {
        byte[] bytes = new byte[3];
        bytes[0] = 0x11;
        bytes[1] = 0x12;
        outputBlueText(">>> getKapMode " + BytesUtil.bytes2HexString(bytes));
        outputBlueText(">>> getKeyInfo");
        KeyHandle[] keyHandles = new KeyHandle[] {
                new KeyHandle(getKeySystem(), KEYID_MAIN),
                new KeyHandle(getKeySystem(), KEYID_PIN),
                new KeyHandle(getKeySystem(), KEYID_MAC),
                new KeyHandle(getKeySystem(), KEYID_TRACK),
                new KeyHandle(getKeySystem(), KEYID_DES)};
        try {
            Bundle params = new Bundle();
            params.putInt(PinpadData.MODE, GetKeyInfoMode.DEFAULT);
            for (KeyHandle keyHandle: keyHandles) {
                params.putParcelable(PinpadData.KEY_HANDLE, keyHandle);
                Bundle result = pinpad.getKeyInfo(params);
                result.setClassLoader(getClass().getClassLoader());
                int ret = result.getInt(PinpadData.RESULT, PinpadError.ERROR_OTHERERR);
                if (ret == PinpadError.SUCCESS) {
                    outputText(String.format("getKeyInfo by handle(keyId = %s) success", keyHandle.getKeyId()));
                    KeyInfo keyInfo = result.getParcelable(PinpadData.KEY_INFO);
                    KeyHandle kh = keyInfo.getKeyHandle();
                    KeyCfg keyCfg = keyInfo.getKeyCfg();
                    outputBlueText(String.format("KeyHandle { keySystem = %d, keyId = %d, kapId ={RegionId = %d, KapNum = %d}}" , kh.getKeySystem(), kh.getKeyId(), kh.getKapId().getRegionId(), kh.getKapId().getKapNum()));
                    outputBlueText(String.format("KeyCfg { KeyAlgorithm = %c, Exportability = %d, KeyUsage = %d, ModeOfUse = %c, version = %d}" , keyCfg.getKeyAlgorithm(), keyCfg.getExportability(), keyCfg.getKeyUsage(), keyCfg.getModeOfUse(), keyCfg.getVersionNumber()));
                    outputBlueText(String.format("keyLength = %d", keyInfo.getKeyLength()));
                } else {
                    outputRedText(String.format("getKeyInfo by handle(keyId = %s) FAILURE (errCode = %d)", keyHandle.getKeyId(), ret));
                }
            }

        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void enumKeyInfo(View view) {
        outputBlueText(">>> enumKeyInfo");
        try {
            Bundle params = new Bundle();
            params.putInt(PinpadData.MODE, EnumKeyInfoMode.ENUM_KEY_HANDLE);
            params.putParcelable(PinpadData.KAP_ID, new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM));
            pinpad.enumKeyInfo(params, new OnEnumKeyInfoListener.Stub() {
                @Override
                public void onSuccess(Bundle bundle) throws RemoteException {
                    outputBlueText("call enumKeyInfo callBack onSuccess getLastError " + pinpad.getLastError());
                    bundle.setClassLoader(getClass().getClassLoader());
                    int retMode = bundle.getInt(PinpadData.MODE);
                    ArrayList<KeyInfo> keyInfoArrayList = bundle.getParcelableArrayList(PinpadData.KEY_INFO);
                    outputText(String.format("enumKeyInfo success retMode = %d, key number = %d", retMode, keyInfoArrayList.size()));
                    for (KeyInfo keyInfo: keyInfoArrayList) {
                        KeyHandle kh = keyInfo.getKeyHandle();
                        outputBlueText(String.format("KeyHandle { keySystem = %d, keyId = %d, kapId ={RegionId = %d, KapNum = %d}}" , kh.getKeySystem(), kh.getKeyId(), kh.getKapId().getRegionId(), kh.getKapId().getKapNum()));
                        if (retMode == EnumKeyInfoMode.ENUM_KEY_INFO) {
                            KeyCfg keyCfg = keyInfo.getKeyCfg();
                            outputBlueText(String.format("KeyCfg { KeyAlgorithm = %c, Exportability = %d, KeyUsage = %d, ModeOfUse = %c, version = %d}" , keyCfg.getKeyAlgorithm(), keyCfg.getExportability(), keyCfg.getKeyUsage(), keyCfg.getModeOfUse(), keyCfg.getVersionNumber()));
                            outputBlueText(String.format("keyLength = %d", keyInfo.getKeyLength()));
                        }
                    }
                }

                @Override
                public void onError(int errorCode) throws RemoteException {
                    outputRedText("call enumKeyInfo callBack error " + errorCode + "," + pinpad.getLastError());
                }
            });
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void loadEncKeyWithBundle(View view) {
        outputBlueText(">>> loadEncKeyWithBundle");
        try {
            KeyHandle srcKeyHandle = new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, KEYID_MAIN);
            KeyHandle dstKeyHandle = new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, KEYID_MAC);
            KeyCfg keyCfg = new KeyCfg(KeyUsage.KU_ISO_9797_1_MAC_ALGORITHM_3, KeyAlgorithm.KA_TDEA, KeyModeOfUse.MOU_GENERATE_AND_VERIFY, KeyVersionNumber.KVN_NOT_USED, (byte) KeyExportability.KE_NON_EXPORTABLE);
            int encKeyFmt = EncKeyFmt.ENC_KEY_FMT_BUNDLE;
            int format = EncKeyBundleFmt.BUNDLE_FMT_CBC;
            byte[] encKey = BytesUtil.hexString2Bytes("2DE2F089C15D9E992DE2F089C15D9E992DE2F089C15D9E99");
            byte[] iv = BytesUtil.hexString2Bytes("0102030405060708");

            Bundle params = new Bundle();
            params.putParcelable(PinpadData.SRC_KEY_HANDLE, srcKeyHandle);
            params.putParcelable(PinpadData.DST_KEY_HANDLE, dstKeyHandle);
            params.putParcelable(PinpadData.KEY_CFG, keyCfg);
            params.putInt(PinpadData.ENC_KEY_FMT, encKeyFmt);
            params.putInt(PinpadData.FORMAT, format);
            params.putByteArray(PinpadData.ENC_KEY, encKey);
            params.putByteArray(PinpadData.IV, iv);

            boolean isSucc = pinpad.loadEncKeyWithBundle(params);
            if (isSucc) {
                outputText("loadEncKeyWithBundle successfully");
            } else {
                outputPinpadError("loadEncKeyWithBundle failed");
            }
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
        }
    }


    public void loadEncKeyWithBundleAESPublicKey(View view) {
        outputBlueText(">>> loadEncKeyWithBundleAESPublicKey");
        try {
            KeyHandle srcKeyHandle = new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, 111);
            KeyCfg srcKeyCfg = new KeyCfg(KeyUsage.KU_KEY_ENC_OR_WRAP, KeyAlgorithm.KA_AES, KeyModeOfUse.MOU_DEC_OR_UNWRAP_ONLY, KeyVersionNumber.KVN_NOT_USED, (byte) KeyExportability.KE_NON_EXPORTABLE);
            boolean isSuccess = pinpadLimited.loadPlainTextKey(srcKeyHandle, srcKeyCfg, BytesUtil.hexString2Bytes("C1D0F8FB4958670DBA40AB1F3752EF0D"));
            if (!isSuccess) {
                outputRedText("load srcKey error :" + pinpad.getLastError());
                return;
            }

            KeyHandle dstKeyHandle = new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, 112);
            KeyCfg keyCfg = new KeyCfg(KeyUsage.KU_DATA_ENCRYPTION, KeyAlgorithm.KA_AES, KeyModeOfUse.MOU_DEC_OR_UNWRAP_ONLY, KeyVersionNumber.KVN_NOT_USED, (byte) KeyExportability.KE_NON_EXPORTABLE);
            int encKeyFmt = EncKeyFmt.ENC_KEY_FMT_BUNDLE;
            int format = EncKeyBundleFmt.BUNDLE_FMT_CBC;
            byte[] encKey = BytesUtil.hexString2Bytes("1FFF38700D12716E273F6E199675FB64");
            byte[] iv = BytesUtil.hexString2Bytes("C1D0F8FB4958670DBA40AB1F3752EF0D");

            Bundle params = new Bundle();
            params.putParcelable(PinpadData.SRC_KEY_HANDLE, srcKeyHandle);
            params.putParcelable(PinpadData.DST_KEY_HANDLE, dstKeyHandle);
            params.putParcelable(PinpadData.KEY_CFG, keyCfg);
            params.putInt(PinpadData.ENC_KEY_FMT, encKeyFmt);
            params.putInt(PinpadData.FORMAT, format);
            params.putByteArray(PinpadData.ENC_KEY, encKey);
            params.putByteArray(PinpadData.IV, iv);

            boolean isSucc = pinpad.loadEncKeyWithBundle(params);
            if (isSucc) {
                outputText("loadEncKeyWithBundleAESPublicKey successfully ");
            } else {
                outputPinpadError("loadEncKeyWithBundleAESPublicKey failed");
            }
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
        }
    }

    public void generateRandomKey(View view) {
        outputBlueText(">>> generateRandomKey");
        try {
            Bundle params = new Bundle();
            //Generate random key-related parameters
            params.putChar(PinpadData.GENERATE_MODE, RKGenerateMode.RKGM_SYMMETRIC);
            params.putParcelable(PinpadData.DST_KEY_HANDLE, getEncKeyHandle());
            params.putParcelable(PinpadData.KEY_CFG, getEncKeyCfg());
            params.putInt(PinpadData.KEY_SIZE, 16);
            BytesValue outKey = new BytesValue();
            int ret = pinpad.generateRandomKey(RKGenerateMode.RKGM_SYMMETRIC, params, outKey);
            if (ret != PinpadError.SUCCESS) {
                outputRedText("generateRandomKey failed, ret = " + ret);
                return;
            }
            outputBlueText("generateRandomKey success");
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void generateRandomKeyEcc(View view) {
        outputBlueText(">>> generateRandomKeyEcc");
        try {
            EccGenerateParam eccGenerateParam = new EccGenerateParam(
                    new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_PKI_KEY, KEYID_MAIN),
                    new KeyCfg(KeyUsage.KU_ASYMMETRIC_KEY_FOR_KEY_AGREEMENT_OR_KEY_WRAPPING, KeyAlgorithm.KA_ELLIPTIC_CURVE, KeyModeOfUse.MOU_DERIVE_KEYS, KeyVersionNumber.KVN_NOT_USED, (byte) KeyExportability.KE_NON_EXPORTABLE),
                    EccGroupId.ECP_DP_SECP521R1
            );

            Bundle params = new Bundle();
            params.putParcelable(PinpadData.ECC_GENERATE_PARAM, eccGenerateParam);

            BytesValue result = new BytesValue();
            int ret = pinpad.generateRandomKey(RKGenerateMode.RKGM_ECC, params, result);
            if (ret == PinpadError.SUCCESS) {
                outputText("generate successfully, result = " + BytesUtil.bytes2HexString(result.getData()));
            } else {
                outputRedText("generate failed, ret = " + ret);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void generateRandomPinBlock(View view) {

        try {
            outputBlueText(">>> generateRandomPinBlock ");
            KeyHandle destKeyHandle = new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, 66);
            KeyCfg destKeyCfg = new KeyCfg(KeyUsage.KU_PIN_ENCRYPTION, KeyAlgorithm.KA_TDEA, KeyModeOfUse.MOU_ENC_OR_WRAP_ONLY,
                    KeyVersionNumber.KVN_NOT_USED, (byte)KeyExportability.KE_NON_EXPORTABLE);

            //byte[] plain_pin_key = BytesUtils.hexToByteArray("042666B49184CF5C68DE9628D0397B36");
            boolean isSuccess = pinpad.loadEncKeyByKeyHandleWithKeyCfg(getMainKeyHandle(), destKeyHandle, EncKeyFmt.ENC_KEY_FMT_NORMAL, destKeyCfg,
                    BytesUtil.hexString2Bytes("3B3F09767E25A932F9498B0E0EDCBEFB"),
                    null);
            if (!isSuccess) {
                outputBlueText(">>> loadPlainTextKey plainPinKey error = " + pinpad.getLastError());
                return;
            }
            Bundle params = new Bundle();
            params.putByte(PinpadData.BLOCK_FORMAT, BlockFormat.BLOCK_FORMAT_8_AS);
            params.putParcelable(PinpadData.KEY_HANDLE, destKeyHandle);
            params.putByteArray(PinpadData.PAN_BLOCK, BytesUtil.hexString2Bytes("0000401234567890"));
            BytesValue outPinBlock = new BytesValue();
            int ret = pinpad.generateRandomPinBlock(params, outPinBlock);
            if (ret != PinpadError.SUCCESS) {
                outputRedText("generateRandomPinBlock failed, ret = " + ret);
                return;
            }
            outputBlueText("generateRandomKey success, outPinBlock = " + BytesUtil.bytes2HexString(outPinBlock.getData()));
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void loadDataKeyAndMacKeyWithLength8(View view) {
        outputBlueText(">>> loadDataKeyAndMacKeyWithLength8");
        try {
            int[] keyIds = new int[] {18, 19};
            int[] keyTypes = new int[] {KeyType.MAC_KEY, KeyType.DEK_KEY};
            String[] keys = new String[] {
                    // value is 24 bytes of 9
                    "116817D8855150C9",
                    // value is 24 bytes of 8
                    "2DE2F089C15D9E99",
                    // value is 24 bytes of 7
            };
            for (int i = 0; i < keyIds.length; i++) {
                int keyType = keyTypes[i];
                int keyId = keyIds[i];
                boolean isSucc = pinpad.loadEncKey(keyType, KEYID_MAIN, keyId, BytesUtil.hexString2Bytes(keys[i]), null);
                if (isSucc) {
                    outputText(String.format("loadEncKey(keyType = %s, keyId = %s) success",keyType, keyId));
                } else {
                    outputPinpadError(String.format("loadEncKey(keyType = %s, keyId = %s) fail",keyType, keyId));
                }
            }
        } catch (RemoteException e) {
            outputRedText("RemoteException: " + e.getMessage());
        }
    }
    public void verifyMAC(View view) {
        String toBeVerified = "3FD8AD86F4513564";
        Bundle mode = new Bundle();
        mode.putInt(PinpadData.PADDING_MODE, MacMode.MAC_PADDING_MODE_1);
        mode.putInt(PinpadData.MAC_ALGORITHM, MacMode.MAC_ALG_ISO9797);
        mode.putInt(PinpadData.MAC_WORK_RESULT_TYPE, 1);
        verifyMAC(mode, MacKeyType.MAC_USING_NORMAL_KEY, getMacKeyHandle(), toBeVerified);
    }
    private KeyHandle getMainKeyHandle() {
        return new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), getKeySystem(), KEYID_MAIN);
    }

    private KeyCfg getEncKeyCfg() {
        KeyCfg keyCfg = new KeyCfg(KeyUsage.KU_KEY_ENC_OR_WRAP,
                KeyAlgorithm.KA_TDEA,
                KeyModeOfUse.MOU_DEC_OR_UNWRAP_ONLY,
                KeyVersionNumber.KVN_NOT_USED,
                (byte) KeyExportability.KE_EXPORTABLE_UNDER_KEK);
        return keyCfg;
    }

    private KeyHandle getEncKeyHandle() {
        return new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, KEYID_ENC);
    }

    private KeyHandle getPinKeyHandle() {
        return new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, KEYID_PIN);
    }

    private KeyHandle getMacKeyHandle() {
        return new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, KEYID_MAC);
    }

    private KeyHandle getDesKeyHandle() {
        return new KeyHandle(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), KeySystem.KS_MKSK, KEYID_DES);
    }
}
