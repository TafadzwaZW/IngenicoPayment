package cn.eas.usdk.demo.rkis.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.eas.usdk.demo.util.BytesUtil;

public class RkisTLVList {
    private List<RkisTLV> data = new ArrayList();

    public RkisTLVList() {
    }

    public static RkisTLVList fromBinary(byte[] data) {
        RkisTLVList list = new RkisTLVList();
        for(int offset = 0; offset < data.length; ) {
            RkisTLV tlv = RkisTLV.fromTLVListData(data, offset);
            list.addTLV(tlv);
            offset += tlv.getDataLength();
        }

        return list;
    }

    public int size() {
        return data.size();
    }

    public byte[] toBinary() {
        byte[][] allData = new byte[data.size()][];

        for(int i = 0; i < data.size(); ++i) {
            allData[i] = data.get(i).toBinary();
        }

        return BytesUtil.merage(allData);
    }

    public boolean contains(String tag) {
        return null != this.getTLV(tag);
    }

    public RkisTLV getTLV(String tag) {
        RkisTLV targetTLV;
        Iterator iterator = this.data.iterator();
        do {
            if (!iterator.hasNext()) {
                return null;
            }

            targetTLV = (RkisTLV)iterator.next();
        } while(!targetTLV.getTag().equals(tag));

        return targetTLV;
    }

    public void addTLV(RkisTLV tlv) {
        if (tlv.isValid()) {
            this.data.add(tlv);
        } else {
            throw new IllegalArgumentException("tlv is not valid!");
        }
    }

    @Override
    public String toString() {
        return this.data.isEmpty() ? "" : BytesUtil.bytes2HexString(this.toBinary());
    }
}
