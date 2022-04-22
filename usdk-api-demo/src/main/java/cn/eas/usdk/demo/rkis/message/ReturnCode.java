package cn.eas.usdk.demo.rkis.message;

/**
 * KMS 错误码
 */
public class ReturnCode {

    /** 成功 */
    public static final String SUCCESS = "00";
    /** 无此交易类型 */
    public static final String TRADE_TYPE_NOT_EXSIT = "01";
    /** 无此厂商标识 */
    public static final String MANUFACTURER_NOT_EXSIT = "02";
    /** 无此终端型号标识 */
    public static final String MODEL_NOT_EXSIT = "03";
    /** 无此硬件序列号 */
    public static final String HARD_SN_NOT_EXSIT = "04";
    /** 商户号、终端号无登记 */
    public static final String TERM_MERCHANT_UNREGISTE = "05";
    /** 认证阶段标识错误 */
    public static final String AUTH_FLAG_ERR = "06";
    /** 双向认证失败 */
    public static final String BOTH_AUTH_FAIL = "07";
    /** 无密钥下载 */
    public static final String NO_KEY_LOAD = "10";
    /** 会话特征码错误 */
    public static final String SESSION_ID_ERR = "70";
    /** 证书非法 */
    public static final String CERT_INVALID = "71";
    /** 数字签名错误 */
    public static final String SIGN_ERR = "72";
    /** 私钥签名失败 */
    public static final String PRKEY_SIGN_ERR = "73";
    /** 公钥加密失败 */
    public static final String PUKEY_ENC_ERR = "74";
    /** 传输密钥数据 MAC 校验失败 */
    public static final String MAC_CHECK_FAIL = "89";
    /** 系统故障 */
    public static final String SYS_TROUBLE = "96";
    /** 密钥错误 */
    public static final String KEY_ERR = "98";
    /** 加密机响应错误 */
    public static final String ENC_MACHINE_ERR = "99";
}
