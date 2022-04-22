package cn.eas.usdk.demo.view.onguard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.usdk.apiservice.aidl.data.BytesValue;
import com.usdk.apiservice.aidl.magreader.IoCtrlCmd;
import com.usdk.apiservice.aidl.magreader.MagData;
import com.usdk.apiservice.aidl.magreader.OnSwipeListener;
import com.usdk.apiservice.aidl.magreader.UMagReader;
import com.usdk.apiservice.aidl.onguard.BlockMode;
import com.usdk.apiservice.aidl.onguard.E2EEConfig;
import com.usdk.apiservice.aidl.onguard.E2EEInput;
import com.usdk.apiservice.aidl.onguard.E2EEOutput;
import com.usdk.apiservice.aidl.onguard.EmvJobTag;
import com.usdk.apiservice.aidl.onguard.EncryptionAlgorithm;
import com.usdk.apiservice.aidl.onguard.EncryptionModel;
import com.usdk.apiservice.aidl.onguard.Job;
import com.usdk.apiservice.aidl.onguard.JobResult;
import com.usdk.apiservice.aidl.onguard.JobType;
import com.usdk.apiservice.aidl.onguard.KSNConfig;
import com.usdk.apiservice.aidl.onguard.KeyId;
import com.usdk.apiservice.aidl.onguard.OnGuardError;
import com.usdk.apiservice.aidl.onguard.PaddingMode;
import com.usdk.apiservice.aidl.onguard.Result;
import com.usdk.apiservice.aidl.onguard.SDEConfig;
import com.usdk.apiservice.aidl.onguard.StandardJobTag;
import com.usdk.apiservice.aidl.onguard.TagValue;
import com.usdk.apiservice.aidl.onguard.UOnGuard;
import com.usdk.apiservice.aidl.pinpad.DeviceName;
import com.usdk.apiservice.aidl.pinpad.KAPId;
import com.usdk.apiservice.aidl.pinpad.KeyAlgorithm;
import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.KeyType;
import com.usdk.apiservice.aidl.pinpad.UPinpad;
import com.usdk.apiservice.limited.pinpad.PinpadLimited;

import java.util.ArrayList;

import cn.eas.usdk.demo.DeviceHelper;
import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.OnGuardConfig;
import cn.eas.usdk.demo.util.BytesUtil;
import cn.eas.usdk.demo.util.DialogUtil;
import cn.eas.usdk.demo.util.SharedPreferencesManager;
import cn.eas.usdk.demo.util.TrackUtil;
import cn.eas.usdk.demo.view.BaseDeviceActivity;

public class OnGuardActivity extends BaseDeviceActivity {

    private UOnGuard onGuard;
    private UPinpad pinpad;
    private PinpadLimited pinpadLimited;
    private UMagReader magReader;
    private String IPEK;
    private String KSN;
    private int regionId = 0;
    private int kapNum = 0;
    private int keyIndex = 0;
    private int nbBin = 6;
    private boolean externalFlag = true;
    private boolean endPanEnd = true;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_onguard);
        setTitle("OnGuard Module");
        setSettingClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(OnGuardSettinigActivity.class);
            }
        });
        initDeviceIntance();
    }

    private void initDeviceIntance() {
        pinpad = DeviceHelper.me().getPinpad(new KAPId(regionId, kapNum), KeySystem.KS_DUKPT, DeviceName.IPP);
        try {
            pinpadLimited = new PinpadLimited(getApplicationContext(), new KAPId(regionId, kapNum), KeySystem.KS_DUKPT, DeviceName.IPP);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new IllegalStateException("PinpadLmited initialize failed");
        }
        magReader = DeviceHelper.me().getMagReader();
        onGuard = DeviceHelper.me().getOnGuard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IPEK = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_IPEK, OnGuardConfig.DEF_VALUE_IPEK);
        KSN = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_KSN, OnGuardConfig.DEF_VALUE_KSN);
        regionId = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_REGION_ID, OnGuardConfig.DEF_VALUE_REGION_ID);
        kapNum = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_KAP_ID, OnGuardConfig.DEF_VALUE_KAP_ID);
        keyIndex = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_KEY_ID, OnGuardConfig.DEF_VALUE_KEY_ID);
        nbBin = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_NB_BIN, OnGuardConfig.DEF_VALUE_NB_BIN);
        externalFlag = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_EXTERNAL_Flag, OnGuardConfig.DEF_VALUE_EXTERNAL_FLAG);
        endPanEnd = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_ENC_PAN_END, OnGuardConfig.DEF_VALUE_ENC_PAN_END);
    }

    public void loadPlainTextKey(View view) {
        outputBlueText(">>> loadPlainTextKey");
        try {
            boolean result = false;
            result = pinpad.open();
            if (!result) {
                outputRedText("pinpad open fail.");
                return;
            }
            byte[] ipek = BytesUtil.hexString2Bytes(IPEK);
            char keyAlgorithm = KeyAlgorithm.KA_TDEA;
            outputText("ipek:【" + BytesUtil.bytes2HexString(ipek) + "】");
            outputText("keyAlgorithm:【" + keyAlgorithm + "】");
            pinpad.setKeyAlgorithm(keyAlgorithm);
            result = pinpadLimited.loadPlainTextKey(KeyType.MAIN_KEY, keyIndex, ipek);
            if (!result) {
                outputRedText("loadPlainTextKey | fail");
            } else {
                outputBlackText("loadPlainTextKey | success.");
            }
            pinpad.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void initDukptKsn(View view) {
        outputBlueText(">>> initDukptKsn");
        try {
            outputText("ksn:【" + KSN + "】");
            KeyId keyId = new KeyId(regionId, kapNum, keyIndex);
            KSNConfig ksnConfig = new KSNConfig(EncryptionModel.MODEL_2017, keyId);
            int result = onGuard.initDukptKsn(ksnConfig, BytesUtil.hexString2Bytes(KSN));
            if (result != OnGuardError.ERR_NONE) {
                outputRedText("initDukptKsn fail | errorCode:" + result);
                return;
            }
            outputBlackText("initDukptKsn success");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void fpeCipher(View view) {
        outputBlueText(">>> fpeCipher");
        try {
            outputBlackText("start search mag card");
            final AlertDialog alertDialog = DialogUtil.showMessage(this, null, "Please swip mag card", new DialogUtil.OnCancelListener() {
                @Override
                public void onCancel() {
                    try {
                        outputBlackText("stop search mag card");
                        magReader.stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

            magReader.enableTrack(7);
            magReader.magIOControl(IoCtrlCmd.SET_TRKDATA_TYPE, new byte[]{(byte) 7}, new BytesValue());
            magReader.searchCard(30, new OnSwipeListener.Stub() {
                @Override
                public void onSuccess(Bundle bundle) throws RemoteException {
                    outputBlackText("searchCard success | track data:");
                    alertDialog.dismiss();
                    String track1 = bundle.getString(MagData.TRACK1);
                    String track2 = bundle.getString(MagData.TRACK2);
                    String track3 = bundle.getString(MagData.TRACK3);
                    String pan = TrackUtil.getCardNoByTrack2(track2);
                    String chn = TrackUtil.getCardHolderNameByTrack1(track1);
                    String expiredDate = TrackUtil.getExpiredDateByTrack2(track2);
                    String serviceCode = TrackUtil.getServiceCodeByTrack2(track2);
                    String cvv = TrackUtil.getCvvByTrack2(track2);

                    outputText("track1:【" + track1 + "】");
                    outputText("track2:【" + track2 + "】");
                    outputText("track3:【" + track3 + "】");
                    outputText("pan:【" + pan + "】");
                    outputText("expiredDate:【" + expiredDate + "】");
                    outputText("serviceCode:【" + serviceCode + "】");
                    outputText("chn:【" + chn + "】");
                    outputText("cvv:【" + cvv + "】");


                    outputText("nbBin:【" + nbBin + "】");
                    outputText("externalFlag:【" + externalFlag + "】");
                    outputText("endPanEnd:【" + endPanEnd + "】");

                    KeyId keyId = new KeyId(regionId, kapNum, keyIndex);
                    E2EEConfig config = new E2EEConfig(EncryptionModel.MODEL_2017, keyId);

                    E2EEInput inputData = new E2EEInput();
                    inputData.setTrack1(track1);
                    inputData.setTrack2(track2);
                    inputData.setPan(pan);
                    inputData.setExpiryDate(expiredDate);
                    inputData.setCvv(cvv);
                    inputData.setChn(chn);
                    inputData.setNbBin(nbBin);
                    inputData.setExtensiveFlag(externalFlag);
                    inputData.setEncPanEnd(endPanEnd);
                    E2EEOutput outputData = new E2EEOutput();
                    Result result = onGuard.fpeCipher(config, inputData, outputData);
                    if (result.getError() == 0) {
                        outputBlackText("fpeCipher success | code:" + result.getError() + " ,warnings:" + result.getWarnings() + ", track data encrypted:");
                        outputText("track1:【" + outputData.getTrack1() + "】");
                        outputText("track2:【" + outputData.getTrack2() + "】");
                        outputText("pan:【" + outputData.getPan() + "】");
                        outputText("expiryDate:【" + outputData.getExpiryDate() + "】");
                        outputText("CHN:【" + outputData.getChn() + "】");
                        outputText("CVV:【" + outputData.getCvv() + "】");
                        outputText("KSN:【" + outputData.getKsn() + "】");
                    } else {
                        outputRedText("fpeCipher fail | code:" + result.getError() + " ,desc:" + OnGuardError.getErrorDesc(result.getError()) + " ,warnings:" + result.getWarnings());
                    }
                }

                @Override
                public void onError(int code) throws RemoteException {
                    outputRedText("searchCard error | code:" + code);
                    alertDialog.dismiss();
                }

                @Override
                public void onTimeout() throws RemoteException {
                    outputRedText("searchCard timeout");
                    alertDialog.dismiss();
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void sdeCipher(View view) {
        outputBlueText(">>> sdeCipher");
        try {
            KeyId keyId = new KeyId(regionId, kapNum, keyIndex);
            SDEConfig config = new SDEConfig(
                    keyId, EncryptionModel.MODEL_2009, EncryptionAlgorithm.ALGO_TDES_16,
                    PaddingMode.PADDING_FF, BlockMode.ECB, com.usdk.apiservice.aidl.onguard.KeyType.DATA_ENCRYPTION_BOTH_WAYS_KEY
                    , "|"
            );
            Job job = SharedPreferencesManager.getInstance().getParameter(this, OnGuardConfig.KEY_JOB_TYPE_IS_EMV, OnGuardConfig.DEF_VALUE_JOB_TYPE_IS_EMV) ?
                    getEmvTypeJob() : getStandardTypeJob();
            JobResult jobResult = new JobResult();

            outputText("jobType:【" + job.getType().name() + "】");

            onGuard.sdeCipher(config, null, job, jobResult);
            if (jobResult.getErrorCode() == 0) {
                outputBlackText("sdeCipher success");
            } else {
                outputRedText("sdeCipher fail");
            }
            outputText("errorCode:【" + jobResult.getErrorCode() + "】");
            outputText("cryptogram:【" + jobResult.getCryptogram() + "】");
            outputText("errorPosition:【" + jobResult.getErrorPosition() + "】");
        } catch (Exception e) {
            handleException(e);
        }
    }

    private Job getEmvTypeJob() {
        ArrayList<TagValue> emvTagList = new ArrayList<TagValue>();
        TagValue tagPan = new TagValue();
        tagPan.setTag(EmvJobTag.TAG_EMV_APPLI_PAN);
        tagPan.setValue("5017671200008975");
        TagValue tagChn = new TagValue();
        tagChn.setTag(EmvJobTag.TAG_EMV_CARDHOLDER_NAME);
        tagChn.setValue("zheng");
        TagValue tagTrack1 = new TagValue();
        tagTrack1.setTag(EmvJobTag.TAG_EMV_TRACK_1);
        tagTrack1.setValue("B5017671200008975^CARTE DE TEST             ^1512901854747000000000000000000");
        emvTagList.add(tagPan);
        emvTagList.add(tagChn);
        emvTagList.add(tagTrack1);
        Job job = new Job();
        job.setType(JobType.JOB_TYPE_EMV);
        job.setItems(emvTagList);
        return job;
    }

    private Job getStandardTypeJob() {
        ArrayList<TagValue> tagList = new ArrayList<TagValue>();
        TagValue tag1 = new TagValue();
        tag1.setTag(StandardJobTag.TAG_CHD_CHN_FROM_TRACK1_MS);
        tag1.setValue("1234567890");
        tag1.setValue("B5017671200008975^CARTE DE TEST             ^1512901854747000000000000000000");
        TagValue tag2 = new TagValue();
        tag2.setTag(StandardJobTag.TAG_CHD_CHN_ICC);
        tag2.setValue("helloworld");
        TagValue tag3 = new TagValue();
        tag3.setTag(StandardJobTag.TAG_CHD_PAN_MANUAL);
        tag3.setValue("5017671200008975");
        tagList.add(tag1);
        tagList.add(tag2);
        tagList.add(tag3);
        Job job = new Job();
        job.setType(JobType.JOB_TYPE_STANDARD);
        job.setItems(tagList);
        return job;
    }

    public void nextKey(View view) {
        outputBlueText(">>> nextKey");
        try {
            KeyId keyId = new KeyId(regionId, kapNum, keyIndex);
            BytesValue ksnBuffer = new BytesValue();
            int ret = onGuard.nextKey(keyId, ksnBuffer);
            if (ret != OnGuardError.ERR_NONE) {
                outputRedText("nextKey fail | errorCode:" + ret);
                return;
            }
            outputBlackText("nextKey success | ksn:" + BytesUtil.bytes2HexString(ksnBuffer.getData()));
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void getKeyKsn(View view) {
        outputBlueText(">>> getKeyKsn");
        try {
            KeyId keyId = new KeyId(regionId, kapNum, keyIndex);
            BytesValue ksnBuffer = new BytesValue();
            KSNConfig ksnConfig = new KSNConfig(EncryptionModel.MODEL_2017, keyId);
            int ret = onGuard.getKeyKsn(ksnConfig, ksnBuffer);
            if (ret != OnGuardError.ERR_NONE) {
                outputRedText("getKeyKsn fail | errorCode:" + ret);
                return;
            }
            outputBlackText("getKeyKsn success | ksn:" + BytesUtil.bytes2HexString(ksnBuffer.getData()));
        } catch (Exception e) {
            handleException(e);
        }
    }
}
