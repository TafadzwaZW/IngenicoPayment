package cn.eas.usdk.demo.view.pinpad;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.CallSuper;

import com.usdk.apiservice.aidl.data.BooleanValue;
import com.usdk.apiservice.aidl.pinpad.DESMode;
import com.usdk.apiservice.aidl.pinpad.KAPId;
import com.usdk.apiservice.aidl.pinpad.KeyHandle;
import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.MagTrackEncMode;
import com.usdk.apiservice.aidl.pinpad.OnPinEntryListener;
import com.usdk.apiservice.aidl.pinpad.PinTriggerMode;
import com.usdk.apiservice.aidl.pinpad.PinpadData;
import com.usdk.apiservice.aidl.pinpad.PinpadError;
import com.usdk.apiservice.aidl.pinpad.PinpadSkin;
import com.usdk.apiservice.aidl.pinpad.UPinpad;
import com.usdk.apiservice.limited.pinpad.PinpadLimited;
import java.util.List;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public abstract class BasePinpadActivity extends BaseDeviceActivity {
    protected static final int KEYID_MAIN = 0;
    protected static final int KEYID_PIN = DemoConfig.KEYID_PIN;
    protected static final int KEYID_DES = 11;
    protected static final int KEYID_TRACK = 12;
    protected static final int KEYID_MAC = 13;
    protected static final int KEYID_HMAC256 = 14;
    protected static final int KEYID_HMAC384 = 15;
    protected static final int KEYID_HMAC512 = 16;
    protected static final int KEYID_ENC = 17;
    protected static final int KEYID_CMAC = 18;

    protected UPinpad pinpad;
    protected PinpadLimited pinpadLimited;

    private OnPinEntryListener onPinEntryListener = new OnPinEntryListener.Stub() {
        @Override
        public void onInput(int pinLen, int v) throws RemoteException {
            outputBlackText("onInput | pinLen = " + pinLen + ", v = " + v);
        }

        @Override
        public void onConfirm(byte[] data, boolean isNonePin) throws RemoteException {
            outputText( "=> onConfirm | pinData = " + BytesUtil.bytes2HexString(data));
        }

        @Override
        public void onCancel() throws RemoteException {
            outputText("=> onCancel");
        }

        @Override
        public void onError(int err) throws RemoteException {
            outputRedText("=> onError: " + getErrorDetail(err));
        }
    };

    public int getKeySystem() {
        return KeySystem.KS_MKSK;
    }

    public String getKeySystemDecs(int keySystem) {
        switch (keySystem) {
            case KeySystem.KS_DUKPT:
                return "DUKPT";
            case KeySystem.KS_FIXED_KEY:
                return "FIX";
            default:
            case KeySystem.KS_MKSK:
                return "MKSK";
        }
    }

    @CallSuper
    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        initDeviceInstance();
    }

    protected void initDeviceInstance() {
        pinpad = createPinpad(new KAPId(DemoConfig.REGION_ID, DemoConfig.KAP_NUM), getKeySystem(), DemoConfig.PINPAD_DEVICE_NAME);
    }

    public UPinpad createPinpad(KAPId kapId, int keySystem, String deviceName) {
        try {
            pinpadLimited = new PinpadLimited(getApplicationContext(), kapId, keySystem, deviceName);
            return DeviceHelper.me().getPinpad(kapId, keySystem, deviceName);
        } catch (RemoteException e) {
           e.printStackTrace();
           return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
    }

    public void open() {
        outputBlueText(">>> open");
        try {
            boolean isSucc = pinpad.open();
            if (isSucc) {
                outputText("open success");
            } else {
                outputPinpadError("open fail");
            }
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void format() {
        outputBlueText(">>> format");
        try {
            boolean isSucc = pinpadLimited.format();
            if (isSucc) {
                outputText("format success");
            } else {
                outputPinpadError("format fail");
            }
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void close() {
        outputBlueText(">>> close");
        try {
            boolean isSucc = pinpad.close();
            if (isSucc) {
                outputText("close success");
            } else {
                outputPinpadError("close fail");
            }
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void isKeyExist(int... keyIds) {
        outputBlueText(">>> isKeyExist");
        try {
            for (int keyId : keyIds) {
                boolean isExist = pinpad.isKeyExist(keyId);
                if (isExist) {
                    outputText(String.format("The key(keyId = %s) is exist", keyId));
                } else {
                    outputRedText(String.format("The key(keyId = %s) is non-existent", keyId));
                }
            }
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void deleteKey(int[] keyIds) {
        outputBlueText(">>> deleteKey");
        try {
            for (int keyId : keyIds) {
                boolean isSucc = pinpadLimited.deleteKey(keyId);
                if (isSucc) {
                    outputText(String.format("Delete key(keyId = %s) success", keyId));
                } else {
                    outputPinpadError(String.format("Delete key(keyId = %s) fail", keyId));
                }
            }
        } catch (RemoteException e) {
            handleException(e);
        }
    }

    public void calcMAC(int keyId, int paddingMode, int algorithm) {
        outputBlueText(">>> calcMAC algorithm = " + algorithm);
        try {
            String data = "0200702406C020C09A361662242421000000460000000000000001000000592908071000000012366224242100000046D290820165890310000931303030303030313131303239303035343131303030313135368CFC12A2B2B5E0BD260000000000000001679F3501229F3303E0F8C89F40056000F0A0019F02060000000001009F030600000000000057126224242100000046D29082016589031000095F3401009F101307001703A00000010A01000000000074F8DB8A9F2701809F2608D160D6BC6B05FE66950500000000009B0200009F360208B582027C009F3704B9B14D8C5F2003636F6D9A031710209F21031505124F08A0000003330101029F5D060000000000009F660427C040800123413230363930313030323034303230323030303030303130343138384D434137343631393730333030363030303034363034303038374539464236424230353030383332303030332020413530343430313031303132312E343434343336303230303933312E32383534343630353031304244303920202020202000142200026900060100463830323030333136313030303331353835322020202020202020202020203031202020202020202020202020202001254355500120202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020573330312020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020";
            byte[] result = pinpad.calcMAC(keyId, paddingMode, algorithm, null, BytesUtil.hexString2Bytes(data));
            if (result == null) {
                outputPinpadError("calcMAC fail");
                return;
            }
            outputText("Mac = " + BytesUtil.bytes2HexString(result));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void encryptMagTrack(int keyId) {
        outputBlueText(">>> encryptMagTrack");
        try {
            int mode = MagTrackEncMode.MTEM_ECBMODE;
            String trackData = "12345678";
            byte[] result =  pinpad.encryptMagTrack(mode, keyId, BytesUtil.hexString2Bytes(trackData));
            if (result == null) {
                outputPinpadError("encryptMagTrack fail");
                return;
            }
            outputText("ECB encrypt result = " + BytesUtil.bytes2HexString(result));
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Decrypt rule ：not 8 integer times, fill 0x80 and then fill 0x00 to 8 integer times
     */
    public void calculateDes(int keyId) {
        outputBlueText(">>> calculateDes");
        try {
            DESMode desMode = new DESMode(DESMode.DM_ENC, DESMode.DM_OM_TECB);
            String data = "12345678";
            byte[] encResult = pinpad.calculateDes(keyId, desMode, null, BytesUtil.hexString2Bytes(data));
            if (encResult == null) {
                outputPinpadError("calculateDes fail");
                return;
            }
            outputText("TECB encrypt result = " + BytesUtil.bytes2HexString(encResult));

            desMode = new DESMode(DESMode.DM_DEC, DESMode.DM_OM_TECB);
            byte[] decResult = pinpad.calculateDes(keyId, desMode, null, encResult);
            if (decResult == null) {
                outputPinpadError("calculateDes fail");
                return;
            }
            outputText("TECB decrypt result = " + BytesUtil.bytes2HexString(decResult));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void startPinEntry(int keyId) {
        outputBlueText(">>> startPinEntry");
        try {
            Bundle param = new Bundle();
            param.putInt(PinpadData.TIMEOUT, 350);
            param.putInt(PinpadData.BETWEEN_PINKEY_TIMEOUT, 10);
            param.putByteArray(PinpadData.PIN_LIMIT, new byte[] { 0, 4});
            param.putByteArray(PinpadData.PAN_BLOCK, new byte[] { 0, 4, 5});

            pinpad.startPinEntry(keyId, param, onPinEntryListener);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void viPin(int keyId) {
        outputBlueText(">>> viPin");
        try {
            //use vi pinpad skin
            Bundle skinParams = new Bundle();
            skinParams.putString(PinpadSkin.SKIN_NAME, "VIPIN_DEFAULT");
            pinpad.setSkinProperties(skinParams);

            //set vi pinpad cfg
            Bundle config = new Bundle();
            config.putInt(PinpadData.PIN_TRIGGER_MODE, PinTriggerMode.LONG_PRESS_DOUBLE_TAP);
            config.putInt(PinpadData.T_LONG_DOWN_MS, 100);
            boolean isSucces = pinpad.setPinEntryExtraConfig(config);
            if (!isSucces) {
                outputRedText(">>> setPinEntryExtraConfig failure ret = " + pinpad.getLastError());
                return;
            }
            // start pinpad input
            Bundle param = new Bundle();
            param.putInt(PinpadData.TIMEOUT, 350);
            param.putInt(PinpadData.BETWEEN_PINKEY_TIMEOUT, 70);
            param.putByteArray(PinpadData.PIN_LIMIT, new byte[] { 0, 4});
            param.putByteArray(PinpadData.PAN_BLOCK, new byte[] { 0, 4, 5});
            pinpad.startPinEntry(keyId, param, onPinEntryListener);
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Offline pin length maximum limit: overseas -12 bit, domestic -16 bit
     * Legal pin length range: 0, 4 to 12
     */
    public void startOfflinePinEntry() {
        outputBlueText(">>> startOfflinePinEntry");
        try {
            Bundle param = new Bundle();
            param.putInt(PinpadData.TIMEOUT, 30);
            param.putInt(PinpadData.BETWEEN_PINKEY_TIMEOUT, 300);
            param.putByteArray(PinpadData.PIN_LIMIT, new byte[] { 0, 4, 5, 6, 7, 8, 9, 10, 11, 12});

            pinpad.startOfflinePinEntry(param, onPinEntryListener);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void setPinpadSerialNum(String sn) {
        outputBlueText(">>> setPinpadSerialNum");
        try {
            boolean isSucc = pinpadLimited.setPinpadSerialNum(sn.getBytes());
            if (isSucc) {
                outputText("setPinpadSerialNum success");
            } else {
                outputPinpadError("setPinpadSerialNum fail");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getRandom() {
        outputBlueText(">>> getRandom");
        try {
            byte[] random =  pinpad.getRandom(6);
            outputText("random = " + BytesUtil.bytes2HexString(random));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getAccessibleKapIds() {
        outputBlueText(">>> getAccessibleKapIds");
        try {
            List<KAPId> kapIds = pinpad.getAccessibleKapIds(50);
            if (kapIds == null || kapIds.size() == 0) {
                outputPinpadError("Accessible KapIds is null");
                return;
            }

            for (KAPId kap : kapIds) {
                outputText("regionID:" + kap.getRegionId()+ ", kapNum:" + kap.getKapNum());
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getExistentKapIds() {
        outputBlueText(">>> getExistentKapIds");
        try {
            List<KAPId> kapIds = pinpad.getExistentKapIds();
            if (kapIds == null || kapIds.size() == 0) {
                outputPinpadError("Existent KapIds is null");
                return;
            }

            for (KAPId kap : kapIds) {
                outputText("regionID:" + kap.getRegionId()+ ", kapNum:" + kap.getKapNum());
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getPinpadInfo() {
        outputBlueText(">>> getPinpadInfo");
        try {
            Bundle pinpadInfo = pinpad.getPinpadInfo();

            StringBuffer buf = new StringBuffer();
            for (String key: pinpadInfo.keySet()) {
                buf.append(key).append("=");
                Object val = pinpadInfo.get(key);
                if (val instanceof byte[]) {
                    buf.append(new String((byte[]) val));
                } else {
                    buf.append(val);
                }
                buf.append("\n");
            }
            outputBlueText( buf.toString());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void outputPinpadError(String message) {
        try {
            outputRedText(message + " : " + getErrorDetail(pinpad.getLastError()));
        } catch (RemoteException e) {
            outputRedText("RemoteException | getLastError | " + e.getMessage());
        }
    }

    public void verifyMAC(Bundle mode, char macKeyType, KeyHandle keyHandle, String toBeVerified) {
        try {
            String data = "0200702406C020C09A361662242421000000460000000000000001000000592908071000000012366224242100000046D290820165890310000931303030303030313131303239303035343131303030313135368CFC12A2B2B5E0BD260000000000000001679F3501229F3303E0F8C89F40056000F0A0019F02060000000001009F030600000000000057126224242100000046D29082016589031000095F3401009F101307001703A00000010A01000000000074F8DB8A9F2701809F2608D160D6BC6B05FE66950500000000009B0200009F360208B582027C009F3704B9B14D8C5F2003636F6D9A031710209F21031505124F08A0000003330101029F5D060000000000009F660427C040800123413230363930313030323034303230323030303030303130343138384D434137343631393730333030363030303034363034303038374539464236424230353030383332303030332020413530343430313031303132312E343434343336303230303933312E32383534343630353031304244303920202020202000142200026900060100463830323030333136313030303331353835322020202020202020202020203031202020202020202020202020202001254355500120202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020573330312020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020";
            BooleanValue isMatched = new BooleanValue();
            int result = pinpad.macInit(mode, null, macKeyType, keyHandle);
            outputText("verifyMAC | macInit result = " + result);
            if (result == PinpadError.SUCCESS) {
                result = pinpad.macLoadData(fillFooterData(BytesUtil.hexString2Bytes(data), 8, (byte) 0));
                outputText("verifyMAC | macLoadData result = " + result);
            }
            if (result == PinpadError.SUCCESS) {
                pinpad.macVerify(BytesUtil.hexString2Bytes(toBeVerified), isMatched);
                outputText("verifyMAC | macVerify " + result + ", isMatched = " + isMatched);
            }
        } catch (Exception e) {
            outputRedText("verifyMAC | " + e.toString());
        }
    }

    private byte[] fillFooterData(byte[] data, int multiple, byte fillData) {
        int remainder = data.length % multiple;
        if (remainder != 0) {
            byte[] dataBuf = new byte[multiple - remainder];
            for (int i = 0; i < dataBuf.length; i++) {
                dataBuf[i] = fillData;
            }
            data = BytesUtil.merage(data, dataBuf);
        }
        return data;
    }

    @Override
    public String getErrorMessage(int error) {
        String message;
        switch (error) {
            case PinpadError.ERROR_ABOLISH: message = "ERROR_ABOLISH"; break;
            case PinpadError.ERROR_ACCESSING_KAP_DENY: message = "ERROR_ACCESSING_KAP_DENY"; break;
            case PinpadError.ERROR_BAD_KEY_USAGE: message = "ERROR_BAD_KEY_USAGE"; break;
            case PinpadError.ERROR_BAD_MODE_OF_KEY_USE: message = "ERROR_BAD_MODE_OF_KEY_USE"; break;
            case PinpadError.ERROR_BAD_STATUS: message = "ERROR_BAD_STATUS"; break;
            case PinpadError.ERROR_BUSY: message = "ERROR_BUSY"; break;
            case PinpadError.ERROR_CANCELLED_BY_USER: message = "ERROR_CANCELLED_BY_USER"; break;
            case PinpadError.ERROR_COMM_ERR: message = "ERROR_COMM_ERR"; break;
            case PinpadError.ERROR_DUKPT_COUNTER_OVERFLOW: message = "ERROR_DUKPT_COUNTER_OVERFLOW"; break;
            case PinpadError.ERROR_DUKPT_NOT_INITED: message = "ERROR_DUKPT_NOT_INITED"; break;
            case PinpadError.ERROR_ENC_KEY_FMT_TOO_SIMPLE: message = "ERROR_ENC_KEY_FMT_TOO_SIMPLE"; break;
            case PinpadError.ERROR_ENCRYPT_MAG_TRACK_TOO_FREQUENTLY: message = "ERROR_ENCRYPT_MAG_TRACK_TOO_FREQUENTLY"; break;
            case PinpadError.ERROR_OTHERERR: message = "ERROR_OTHERERR"; break;
            case PinpadError.ERROR_FAIL_TO_AUTH: message = "ERROR_FAIL_TO_AUTH"; break;
            case PinpadError.ERROR_INCOMPATIBLE_KEY_SYSTEM: message = "ERROR_INCOMPATIBLE_KEY_SYSTEM"; break;
            case PinpadError.ERROR_INVALID_ARGUMENT: message = "ERROR_INVALID_ARGUMENT"; break;
            case PinpadError.ERROR_INVALID_KEY_HANDLE: message = "ERROR_INVALID_KEY_HANDLE"; break;
            case PinpadError.ERROR_KAP_ALREADY_EXIST: message = "ERROR_KAP_ALREADY_EXIST"; break;
            case PinpadError.ERROR_ARGUMENT_CONFLICT: message = "ERROR_ARGUMENT_CONFLICT"; break;
            case PinpadError.ERROR_KEYBUNDLE_ERR: message = "ERROR_KEYBUNDLE_ERR"; break;
            case PinpadError.ERROR_NO_ENOUGH_SPACE: message = "ERROR_NO_ENOUGH_SPACE"; break;
            case PinpadError.ERROR_NO_PIN_ENTERED: message = "ERROR_NO_PIN_ENTERED"; break;
            case PinpadError.ERROR_NO_SUCH_KAP: message = "ERROR_NO_SUCH_KAP"; break;
            case PinpadError.ERROR_NO_SUCH_KEY: message = "ERROR_NO_SUCH_KEY"; break;
            case PinpadError.ERROR_NO_SUCH_PINPAD: message = "ERROR_NO_SUCH_PINPAD"; break;
            case PinpadError.ERROR_PERMISSION_DENY: message = "ERROR_PERMISSION_DENY"; break;
            case PinpadError.ERROR_PIN_ENTRY_TOO_FREQUENTLY: message = "ERROR_PIN_ENTRY_TOO_FREQUENTLY"; break;
            case PinpadError.ERROR_REFER_TO_KEY_OUTSIDE_KAP: message = "ERROR_REFER_TO_KEY_OUTSIDE_KAP"; break;
            case PinpadError.ERROR_REOPEN_PINPAD: message = "ERROR_REOPEN_PINPAD"; break;
            case PinpadError.ERROR_SAME_KEY_VALUE_DETECTED: message = "ERROR_SAME_KEY_VALUE_DETECTED"; break;
            case PinpadError.ERROR_SERVICE_DIED: message = "ERROR_SERVICE_DIED"; break;
            case PinpadError.ERROR_TIMEOUT: message = "ERROR_TIMEOUT"; break;
            case PinpadError.ERROR_UNSUPPORTED_FUNC: message = "ERROR_UNSUPPORTED_FUNC"; break;
            case PinpadError.ERROR_WRONG_KAP_MODE: message = "ERROR_WRONG_KAP_MODE"; break;
            case PinpadError.ERROR_KCV: message = "ERROR_KCV"; break;
            case PinpadError.ERROR_INPUT_TIMEOUT: message = "ERROR_INPUT_TIMEOUT"; break;
            case PinpadError.ERROR_INPUT_COMM_ERR: message = "ERROR_INPUT_COMM_ERR"; break;
            case PinpadError.ERROR_INPUT_UNKNOWN: message = "ERROR_INPUT_UNKNOWN"; break;
            case PinpadError.ERROR_NOT_CERT: message = "ERROR_NOT_CERT"; break;
            default:
                message = super.getErrorMessage(error);
        }
        return message;
    }
}
