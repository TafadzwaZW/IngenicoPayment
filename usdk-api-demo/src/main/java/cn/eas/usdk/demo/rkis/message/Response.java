package cn.eas.usdk.demo.rkis.message;

import cn.eas.usdk.demo.util.BytesUtil;

/**
 * 响应报文
 * TPDU[5BCD] + 报文头[报文组数(1ASC) + 当前报文在报文组的序号(1ASC) + 交易类型(12ASC)] + 报文体
 */
public class Response {
    /** 报文长度域的长度 */
    private static final int MESSAGE_LENGTH_FILED_ENGTH = 2;
    /** TPDU的长度 */
    private static final int TPDU_ENGTH = 5;
    /** 报文组数的长度 */
    private static final int MESSAGE_GROUP_NUM_ENGTH = 1;
    /** 当前报文在报文组的序号的长度 */
    private static final int CUR_MESSAGE_SN_ENGTH = 1;
    /**  交易类型的长度 */
    private static final int TRADE_TYPE_ENGTH = 12;
    /** 报文头的长度 */
    private static final int MESSAGE_HEAD_ENGTH = MESSAGE_GROUP_NUM_ENGTH + CUR_MESSAGE_SN_ENGTH + TRADE_TYPE_ENGTH;

    private byte[] tpdu;

    private MessageHead messageHead;
    /**
     * 报文体：数据采用 TLV 格式编码组织
     */
    private byte[] messageContent;

    public static boolean isInValid(byte[] data) {
        return data.length < MESSAGE_LENGTH_FILED_ENGTH + TPDU_ENGTH + MESSAGE_HEAD_ENGTH;
    }

    public Response(byte[] tpdu, MessageHead head, byte[] content) {
        this.tpdu = tpdu;
        this.messageHead = head;
        this.messageContent = content;
    }

    public Response(byte[] data) {
        this.tpdu = getTpdu(data);
        this.messageHead = new MessageHead(getSum(data), getCurSequenceNo(data));
        this.messageContent = getContent(data);
    }

    private byte[] getTpdu(byte[] data) {
        int offset = MESSAGE_LENGTH_FILED_ENGTH;
        return BytesUtil.subBytes(data, offset, TPDU_ENGTH);
    }

    /** 报文组数 */
    private int getSum(byte[] data) {
        int offset = MESSAGE_LENGTH_FILED_ENGTH + TPDU_ENGTH;
        int sum = BytesUtil.ascToInt(data[offset]);
        return sum;
    }

    /** 当前报文序号 */
    private int getCurSequenceNo(byte[] data) {
        int offset = MESSAGE_LENGTH_FILED_ENGTH + TPDU_ENGTH + MESSAGE_GROUP_NUM_ENGTH;
        int curSn = BytesUtil.ascToInt(data[offset]);
        return curSn;
    }

    /** 报文体 */
    private byte[] getContent(byte[] data) {
        int offset = MESSAGE_LENGTH_FILED_ENGTH + TPDU_ENGTH + MESSAGE_HEAD_ENGTH;
        return BytesUtil.subBytes(data, offset, data.length - offset);
    }

    public byte[] getTpdu() {
        return tpdu;
    }

    public byte[] getMessageContent() {
        return messageContent;
    }

    public MessageHead getMessageHead() {
        return messageHead;
    }

    /**
     * 报文头：第 1 字节为本次通讯的报文组总数，第 2 字节为当前报文在报文组的序号。
     * 如：报文定义为“32”，表示本次通讯报文组有 3 个报文，当前收到的报 文为第 2 个报文。
     */
    public static class MessageHead {
        /**
         * 本次通讯的报文组总数
         */
        private int sum;
        /**
         * 当前报文在报文组的序号
         */
        private int currentSn;

        public MessageHead(int sum, int curSn) {
            this.sum = sum;
            this.currentSn = curSn;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }

        public int getCurrentSn() {
            return currentSn;
        }

        public void setCurrentSn(int currentSn) {
            this.currentSn = currentSn;
        }
    }
}
