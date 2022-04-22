package cn.eas.usdk.demo.rkis;

import android.content.Context;


import cn.eas.usdk.demo.R;
import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.rkis.message.Request;
import cn.eas.usdk.demo.rkis.message.ReturnCode;
import cn.eas.usdk.demo.rkis.message.RkisTLV;
import cn.eas.usdk.demo.rkis.message.RkisTag;
import cn.eas.usdk.demo.util.BytesUtil;

import static com.usdk.apiservice.aidl.pinpad.KMSMode.SCHEME_V2;

/**
 * 远程密钥注入系统对接管理者
 * 实现组包，报文通讯。
 */
public class RkisManager {

    private Context context;
    private KmsCommunication communication;

    private String model;
    private String manufacture;
    private String hardWareSn;
    private String serialNo;
    private String terminalNo;
    private String merchantNo;

    public RkisManager(Context ctx) {
        context = ctx;
        communication = new KmsCommunication(ctx);
    }

    public void setSocketListener(KmsCommunication.SocketListener listener) {
        communication.setSocketListener(listener);
    }

    public void setURL(String ip, String port) {
        communication.setURL(ip, port);
    }

    /**
     * 发起POS认证请求
     */
    public void requestAuthPOSCrt(byte[] R1, byte[] terminalCrt, ResponseHandler resultListener) {
        Request request;
        switch (DemoConfig.KMS_MODE) {
            case SCHEME_V2:
            default:
                request = createAuthRequestV2Message(R1, terminalCrt);
                break;
        }
        communication.communicate(request.toBytes(), false, resultListener);
    }

    private Request createAuthRequestV1Message(byte[] R1, byte[] terminalCrt) {
        final String AUTH_FLAG = "01";
        byte[] t0002 = new RkisTLV(RkisTag.FID, manufacture).toBinary();
        byte[] t0003 = new RkisTLV(RkisTag.MID, model).toBinary();
        byte[] t0004 = new RkisTLV(RkisTag.PINPAD_SN, hardWareSn).toBinary();
        byte[] t0005 = new RkisTLV(RkisTag.POS_SN, serialNo).toBinary();
        byte[] t1001 = new RkisTLV(RkisTag.SESSION_ID, R1).toBinary();
        byte[] t1002 = new RkisTLV(RkisTag.CERT_POS, terminalCrt).toBinary();
        byte[] t0012 = new RkisTLV(RkisTag.AUTH_FLAG, AUTH_FLAG).toBinary();

        Request request = Request.createRkisV2Request();
        byte[] body = BytesUtil.merage(t0002, t0003, t0004, t0005, t1001, t1002, t0012);
        request.setBody(body);
        return request;
    }

    private Request createAuthRequestV2Message(byte[] R1, byte[] terminalCrt) {
        final String AUTH_FLAG = "01";
        byte[] t0002 = new RkisTLV(RkisTag.FID, manufacture).toBinary();
        byte[] t0003 = new RkisTLV(RkisTag.MID, model).toBinary();
        byte[] t0004 = new RkisTLV(RkisTag.PINPAD_SN, hardWareSn).toBinary();
        byte[] t0005 = new RkisTLV(RkisTag.POS_SN, serialNo).toBinary();
        byte[] t0007 = new RkisTLV(RkisTag.KMS_ID,  DemoConfig.KMS_ID).toBinary();
        byte[] t1001 = new RkisTLV(RkisTag.SESSION_ID, R1).toBinary();
        byte[] t1011 = new RkisTLV(RkisTag.CERT_POS_S, terminalCrt).toBinary();
        byte[] t0012 = new RkisTLV(RkisTag.AUTH_FLAG, AUTH_FLAG).toBinary();

        Request request = Request.createRkisV3Request();
        byte[] body = BytesUtil.merage(t0002, t0003, t0004, t0005, t0007, t1001, t1011, t0012);
        request.setBody(body);
        return request;
    }

    public void requestRemoteKey(byte[] R, byte[] terminalCrtE, byte[] signture, ResponseHandler resultListener) {
        Request request;
        switch (DemoConfig.KMS_MODE) {
            case SCHEME_V2:
            default:
                request = createRemoteKeyRequestV2Message(R, terminalCrtE, signture);
                break;
        }

        communication.communicate(request.toBytes(), true, resultListener);
    }

    private Request createRemoteKeyRequestV1Message(byte[] R, byte[] signture) {
        final String AUTH_FLAG = "02";
        byte[] pinpadSn = new RkisTLV(RkisTag.PINPAD_SN, hardWareSn).toBinary();
        byte[] termId = new RkisTLV(RkisTag.TERM_ID, terminalNo).toBinary();
        byte[] merchId = new RkisTLV(RkisTag.MERCH_ID, merchantNo).toBinary();
        byte[] sessionId = new RkisTLV(RkisTag.SESSION_ID, R).toBinary();
        byte[] signData = new RkisTLV(RkisTag.SIGN_DATA, signture).toBinary();
        byte[] authFlag = new RkisTLV(RkisTag.AUTH_FLAG, AUTH_FLAG).toBinary();

        Request request = Request.createRkisV2Request();
        byte[] body = BytesUtil.merage(pinpadSn, termId, merchId, sessionId, signData, authFlag);
        request.setBody(body);
        return request;
    }

    private Request createRemoteKeyRequestV2Message(byte[] R, byte[] terminalCrtE, byte[] signture) {
        final String AUTH_FLAG = "02";
        byte[] pinpadSn = new RkisTLV(RkisTag.PINPAD_SN, hardWareSn).toBinary();
        byte[] termId = new RkisTLV(RkisTag.TERM_ID, terminalNo).toBinary();
        byte[] merchId = new RkisTLV(RkisTag.MERCH_ID, merchantNo).toBinary();
        byte[] sessionId = new RkisTLV(RkisTag.SESSION_ID, R).toBinary();
        byte[] certPosE = new RkisTLV(RkisTag.CERT_POS_E, terminalCrtE).toBinary();
        byte[] signData = new RkisTLV(RkisTag.SIGN_DATA, signture).toBinary();
        byte[] kmsId = new RkisTLV(RkisTag.KMS_ID,  DemoConfig.KMS_ID).toBinary();
        byte[] authFlag = new RkisTLV(RkisTag.AUTH_FLAG, AUTH_FLAG).toBinary();

        Request request = Request.createRkisV3Request();
        byte[] body = BytesUtil.merage(pinpadSn, termId, merchId, sessionId, certPosE, signData, kmsId, authFlag);
        request.setBody(body);
        return request;
    }

    public String getRetInfo(String retCode) {
        String retInfo;
        if (ReturnCode.SUCCESS.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_00) ;
        } else if (ReturnCode.TRADE_TYPE_NOT_EXSIT.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_01) ;
        } else if (ReturnCode.MANUFACTURER_NOT_EXSIT.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_02) ;
        } else if (ReturnCode.MODEL_NOT_EXSIT.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_03) ;
        } else if (ReturnCode.HARD_SN_NOT_EXSIT.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_04) ;
        } else if (ReturnCode.TERM_MERCHANT_UNREGISTE.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_05) ;
        } else if (ReturnCode.AUTH_FLAG_ERR.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_06) ;
        } else if (ReturnCode.BOTH_AUTH_FAIL.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_07) ;
        } else if (ReturnCode.NO_KEY_LOAD.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_10) ;
        } else if (ReturnCode.SESSION_ID_ERR.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_70) ;
        } else if (ReturnCode.CERT_INVALID.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_71) ;
        } else if (ReturnCode.SIGN_ERR.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_72) ;
        } else if (ReturnCode.PRKEY_SIGN_ERR.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_73) ;
        } else if (ReturnCode.PUKEY_ENC_ERR.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_74) ;
        } else if (ReturnCode.MAC_CHECK_FAIL.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_89) ;
        } else if (ReturnCode.SYS_TROUBLE.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_96) ;
        } else if (ReturnCode.KEY_ERR.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_98) ;
        } else if (ReturnCode.ENC_MACHINE_ERR.equals(retCode)) {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_99) ;
        } else {
            retInfo = context.getResources().getString(R.string.rkis_retinfo_unknow) ;
        }

        return retInfo + "[" + retCode + "]";
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public void setHardWareSn(String hardWareSn) {
        this.hardWareSn = hardWareSn;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getHardWareSn() {
        return hardWareSn;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }
}