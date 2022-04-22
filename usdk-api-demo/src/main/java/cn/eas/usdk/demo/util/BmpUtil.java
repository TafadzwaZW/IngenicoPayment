package cn.eas.usdk.demo.util;

/**
 * Created by caizl on 2020/6/17.
 */
public class BmpUtil {

    /**********************************************************
     * getBmpGreybytes
     * 	从bmp文件中提取出8位灰度数据。
     * 	当前仅支持1位或8位的bmp图。
     *
     * 参数：
     * 	imageData -- bmp文件数据流
     *
     * 返回值：
     * 	GreyBmpData.ret=
     * 				0-成功
     *  			1-格式错误
     *   			2-该位数无法支持
     *    			3-图像大小错误
     *     			4-图像大小错误
     * */
    public static GreyBmpData getGreyBmpData(byte[] imageData) {
        GreyBmpData greyBmpData = new GreyBmpData();
        int bmpSize = 0;// 整个bmp文件的大小，包括head
        int bfOffBits = 0;// 像素数据的起始地址
        int biSizeImage = 0;// 图像数据的大小
        short biBitCount = 0;// 图像位数，1,4,8,16,24,32

        greyBmpData.data = null;
        greyBmpData.width = 0;
        greyBmpData.height = 0;
        greyBmpData.ret = 0;

        if ((imageData[0] != 'B') || (imageData[1] != 'M')) {
            greyBmpData.ret = 1;//格式错误
            return greyBmpData;
        }

        bmpSize = toInt_littleEndian(imageData, 2);//整个bmp文件的大小，包括head
        bfOffBits = toInt_littleEndian(imageData, 0xa);//像素数据的起始地址
        greyBmpData.width = toInt_littleEndian(imageData, 0x12);//图像宽
        greyBmpData.height = toInt_littleEndian(imageData, 0x16);//图像高，如果是正数说明图像是倒向的，如果是负数说明图像是正向的。大多数都是倒向。
        biBitCount = toShort_littleEndian(imageData, 0x1c);//图像位数，1,4,8,16,24,32
        biSizeImage = toInt_littleEndian(imageData, 0x22);//图像大小

        if ((biBitCount != 1) && (biBitCount != 8)) {
            //只支持位数为1或8的bmp
            greyBmpData.ret = 2;//该位数无法支持
            return greyBmpData;
        }

        if (biBitCount == 8) {
            //8位
            int bitPerPix = 8;//1个像素8个bit
            int bytePerLine = (greyBmpData.width * bitPerPix + 31) / 32 * 4;
            greyBmpData.width = bytePerLine;

            biSizeImage = greyBmpData.width * Math.abs(greyBmpData.height);
            if (biSizeImage != (imageData.length - bfOffBits)) {
                greyBmpData.ret = 3;//图像大小错误
                return greyBmpData;
            }
            greyBmpData.data = new byte[biSizeImage];
            if (greyBmpData.height < 0) {
                //图像是正向的
                for (int i = 0; i < biSizeImage; i++) {
                    greyBmpData.data[i] = imageData[bfOffBits + i];
                }
            } else {
                //图像是倒向的
                int pos = 0;
                for (int i = greyBmpData.height - 1; i >= 0; i--) {
                    for (int j = 0; j < bytePerLine; j++) {
                        greyBmpData.data[pos++] = imageData[bfOffBits + i * bytePerLine + j];
                    }
                }
            }
        } else if (biBitCount == 1) {
            //1位
            int bitPerPix = 1;//1个像素一个bit
            int bytePerLine = (greyBmpData.width * bitPerPix + 31) / 32 * 4;
            greyBmpData.width = bytePerLine * 8;

            biSizeImage = greyBmpData.width * Math.abs(greyBmpData.height);
            if ((biSizeImage / 8) != (imageData.length - bfOffBits)) {
                greyBmpData.ret = 4;//图像大小错误
                return greyBmpData;
            }
            greyBmpData.data = new byte[biSizeImage];
            if (greyBmpData.height < 0) {
                //图像是正向的
                for (int i = 0; i < biSizeImage / 8; i++) {
                    //bit0是byte7, bit1是byte6，... bit7是byte0
                    greyBmpData.data[i * 8 + 7] = (byte) (((imageData[bfOffBits + i] >> 0) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 6] = (byte) (((imageData[bfOffBits + i] >> 1) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 5] = (byte) (((imageData[bfOffBits + i] >> 2) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 4] = (byte) (((imageData[bfOffBits + i] >> 3) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 3] = (byte) (((imageData[bfOffBits + i] >> 4) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 2] = (byte) (((imageData[bfOffBits + i] >> 5) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 1] = (byte) (((imageData[bfOffBits + i] >> 6) & 0x1) == 0 ? 0 : 255);
                    greyBmpData.data[i * 8 + 0] = (byte) (((imageData[bfOffBits + i] >> 7) & 0x1) == 0 ? 0 : 255);
                }
            } else {
                //图像是倒向的
                int pos = 0;
                for (int i = greyBmpData.height - 1; i >= 0; i--) {
                    for (int j = 0; j < bytePerLine; j++) {
                        //bit0是byte7, bit1是byte6，... bit7是byte0
                        greyBmpData.data[pos * 8 + 7] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 0) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 6] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 1) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 5] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 2) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 4] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 3) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 3] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 4) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 2] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 5) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 1] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 6) & 0x1) == 0 ? 0 : 255);
                        greyBmpData.data[pos * 8 + 0] = (byte) (((imageData[bfOffBits + i * bytePerLine + j] >> 7) & 0x1) == 0 ? 0 : 255);
                        pos++;
                    }

                }
            }
        }

        greyBmpData.ret = 0;
        return greyBmpData;
    }


    public static class GreyBmpData {
        // ret返回0代表成功，其他值代表失败
        public int ret = -1;
        public byte[] data;
        public int width;
        public int height;
    }

    public static int toInt_littleEndian(byte[] b, int pos) {
        int ret = 0;
        ret = b[pos] & 0xFF;
        ret |= (b[pos+1]<<8) & 0xFF00;
        ret |= (b[pos+2]<<16) & 0xFF0000;
        ret |= (b[pos+3]<<24) & 0xFF000000;
        return ret;
    }

    public static short toShort_littleEndian(byte[] b, int pos) {
        int d = 0;
        short ret;

        d = b[pos] & 0xFF;
        d |= (b[pos+1]<<8) & 0xFF00;

        ret = (short)(d&0xffff);
        return ret;
    }

}
