package cn.eas.usdk.demo.rkis.message;

import cn.eas.usdk.demo.util.BytesUtil;

/**
 * RKIS 定义的TLV格式
 * @author linll
 *
 */
public class RkisTLV {
	private static final int TAG_LENGTH = 4;
	private static final int LENGTH_LENGTH = 4;
	private String tag; // 4ASC
	private String length; // 4ASC
	private byte[] value; // nASC

	public RkisTLV(String tag, byte[] val) {
		checkNotNull(tag);
		checkNotNull(val);

		init(tag, val);
	}

	public RkisTLV(String tag, String val) {
		checkNotNull(tag);
		checkNotNull(val);

		init(tag, val.getBytes());
	}

	private void init(String tag, byte[] val) {
		this.tag = tag;
		String len = ("0000" + val.length);
		this.length = len.substring(len.length() - 4, len.length());
		this.value = val;
	}

	public byte[] toBinary() {
		if (tag == null || length == null) {
			return null;
		}
		return BytesUtil.merage(tag.getBytes(), length.getBytes(), value);
	}

	public static RkisTLV fromTLVListData(byte[] data, int offset) {
		String tag = new String(BytesUtil.subBytes(data, offset, TAG_LENGTH));
		offset += TAG_LENGTH;

		String len = new String(BytesUtil.subBytes(data, offset, LENGTH_LENGTH));
		offset += LENGTH_LENGTH;

		int valLength = Integer.valueOf(len);
		byte[] value = BytesUtil.subBytes(data, offset, valLength);

		return new RkisTLV(tag, value);
	}

	public int getDataLength() {
		int tagLen = 4;
		int lengthLen = 4;
		int valueLen = Integer.valueOf(length);
		return tagLen + lengthLen + valueLen;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public boolean isValid() {
		if (tag == null || value == null) {
			return false;
		}
		return true;
	}

	private static void checkNotNull(Object param) {
		if (param == null) {
			throw new NullPointerException();
		}
	}
}
