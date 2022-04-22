package cn.eas.usdk.demo.rkis.message;

import cn.eas.usdk.demo.constant.DemoConfig;
import cn.eas.usdk.demo.util.BytesUtil;

/**
 * 请求报文
 */
public class Request {

    private byte[] tpdu;
    /**
     * 报文版本号， (“01” “02”“03””04“)
     * RKIS V2 - 01
     * RKIS V3 - 04
     */
    private byte[] define;
    private byte[] tradeType;
    private byte[] body;

    public Request() {
        this("01");
    }

    /**
     * RKIS V2 - "01"
     * RKIS V3 - "04"
     */
    public Request(String version) {
        setTpdu(DemoConfig.KMS_TPDU);
        setDefine(version.getBytes());
        setTradeType(DemoConfig.TRADE_TYPE);
    }

    public void setTpdu(byte[] tpdu) {
        this.tpdu = tpdu;
    }

    public void setDefine(byte[] define) {
        this.define = define;
    }

    public void setTradeType(byte[] tradeType) {
        this.tradeType = tradeType;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] toBytes() {
        return BytesUtil.merage(tpdu, define, tradeType, body);
    }

    public static Request createRkisV2Request() {
        return new Request();
    }

    public static Request createRkisV3Request() {
        return new Request("04");
    }
}
