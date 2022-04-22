package cn.eas.usdk.demo.rkis.message;

/**
 * RKIS 标签常量定义
 * @author linll
 *
 */
public interface RkisTag {
    /** 交易返回码 (RET_CODE) **/
    String RET_CODE = "0000";
    /** 厂商标识 (FID)  **/
    String FID = "0002";
    /** 终端型号标识 (MID) **/
    String MID = "0003";
    /** PINPAD 硬件序列号 (PINPAD_SN)  **/
    String PINPAD_SN = "0004";
    /** POS 主机硬件序列号 (POS_SN) **/
    String POS_SN = "0005";
    /** 厂家给每一个KMS后台分配的编号，并告知由应用程序维护的一个16位ASC的字符串，形如：KMS00000ID000001 **/
    String KMS_ID = "0007";
    /** 认证阶段标志 (AUTH_FLAG) **/
    String AUTH_FLAG = "0012";
    /** 加密算法 (ENCRYPT_ALGO) **/
    String ENCRYPT_ALGO = "0014";
    /** 终端号 (TERM_ID)  **/
    String TERM_ID = "0015";
    /** 商户号 (MERCH_ID)   **/
    String MERCH_ID = "0016";

    /** 会话特征码（SESSION_ID） **/
    String SESSION_ID = "1001";
    /** 终端证书（CERT_POS） **/
    String CERT_POS = "1002";
    /** 系统证书链（CERT_KMS） **/
    String CERT_KMS = "1003";
    /** 临时加密密钥（EPHEMERAL_KEY） **/
    String EPHEMERAL_KEY = "1004";
    /** 机密数据块（SECRECY_DATA_BLOCK） **/
    String SECRECY_DATA_BLOCK = "1005";
    /** 签名数据（SIGN_DATA） **/
    String SIGN_DATA = "1006";
    /**
     * 密钥属性域
     * 数据格式为:
     * attrFormat1,KeyAttrBlock1; attrFormat2,KeyAttrBlock2; …, attrFormatn,KeyAttrBlockn;
     * attrFormat说明：
     * ‘D’：表示DUKPT密钥属性，KeyAttrBlock格式为：KapID1,KeyIndex1,KSN1
     * ‘M’: 表示MKSK密钥属性，其KeyAttrBlock格式待定义。
     * ‘F’: 表示FIXKEY密钥属性，其KeyAttrBlock格式定义如下：KapID1,KeyIndex1,IV, 如果没有初始向量，置为空格。
     *
     * 举例：
     * 1个DUKPT密钥的属性：
     * D,0000,01,FFFF9876543210E00000;
     * 1个DUKPT密钥和1个FIX的属性：
     * D,0000,01,FFFF9876543210E00000;F,0000,02, ,;
     * 2个DUKPT密钥的属性：
     * D,0000,01,FFFF9876543210E00000;D,0000,03, FFFF9876543211000000;
     */
    String KEY_ATTR_DATA = "1007";
    /** 终端签名证书 **/
    String CERT_POS_S = "1011";
    /** 终端加密证书 **/
    String CERT_POS_E = "1012";
}

