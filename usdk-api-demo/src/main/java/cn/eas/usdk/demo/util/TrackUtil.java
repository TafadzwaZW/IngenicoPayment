package cn.eas.usdk.demo.util;

import android.text.TextUtils;

public class TrackUtil {

    private TrackUtil() {
    }

    public static String getCardNoByTrack1(String track1) {
        try {
            if (TextUtils.isEmpty(track1)) {
                return "";
            }
            char[] trackData = track1.toCharArray();
            StringBuffer panStr = new StringBuffer();
            if (trackData[0] == '%' && trackData[1] == 'B') {
                for (int i = 2; i < trackData.length; i++) {
                    if (trackData[i] >= '0' && trackData[i] <= '9') {
                        panStr.append(trackData[i]);
                    }
                    if (trackData[i] == '^') {
                        break;
                    }
                }
            }
            if (panStr.length() <= 0 || panStr.length() > 19) {
                return "";
            }
            return panStr.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCardNoByTrack2(String track2) {
        try {
            if (TextUtils.isEmpty(track2)) {
                return "";
            }
            char[] trackData = track2.toCharArray();
            StringBuffer panStr = new StringBuffer();
            if (trackData[0] == ';') {
                for (int i = 1; i < trackData.length; i++) {
                    if (trackData[i] >= '0' && trackData[i] <= '9') {
                        panStr.append(trackData[i]);
                    }
                    if (trackData[i] == '=') {
                        break;
                    }
                }
            }
            if (panStr.length() <= 0 || panStr.length() > 19) {
                return "";
            }
            return panStr.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getExpireDateByTrack2(String track2) {
        int index = findCardSeparator(track2, '=', 'D') + 1;
        if (index == 0 || index + 4 > track2.length()) {
            return "";
        }
        return track2.substring(index, index + 4);
    }

    public static String getCardHolderNameByTrack1(String track1) {
        if (TextUtils.isEmpty(track1)) {
            return "";
        }
        int firstIndex = track1.indexOf('^');
        if (firstIndex <= 0) {
            return "";
        }
        int secondIndex = track1.indexOf('^', firstIndex + 1);
        if (secondIndex <= 0) {
            return "";
        }
        int chnLen = secondIndex - firstIndex;
        if (chnLen >= 2 && chnLen <= 26) {
            String chnStr = track1.substring(firstIndex + 1, secondIndex);
            return chnStr;
        }
        return "";
    }

    public static String getExpiredDateByTrack2(String track2) {
        if (TextUtils.isEmpty(track2)) {
            return "";
        }
        int firstIndex = track2.indexOf('=');
        if (firstIndex <= 0) {
            firstIndex = track2.indexOf('D');
            if (firstIndex <= 0) {
                return "";
            }
        }
        String expiredData = track2.substring(firstIndex + 1, firstIndex + 1 + 4);
        return expiredData;
    }

    public static String getServiceCodeByTrack2(String track2) {
        if (TextUtils.isEmpty(track2)) {
            return "";
        }
        int firstIndex = track2.indexOf('=');
        if (firstIndex <= 0) {
            firstIndex = track2.indexOf('D');
            if (firstIndex <= 0) {
                return "";
            }
        }
        String expiredData = track2.substring(firstIndex + 1 + 4, firstIndex + 1 + 4 + 3);
        return expiredData;
    }

    public static String getCvvByTrack2(String track2) {
        if (TextUtils.isEmpty(track2)) {
            return "";
        }
        //CVV一般在2磁道分号开始的第29~31位
        String cvv = track2.substring(30, 33);
        return cvv;
    }

    private static int findCardSeparator(String track2, char separator) {
        if (track2 == null || track2.isEmpty()) {
            return -1;
        }
        return track2.indexOf(separator);
    }

    private static int findCardSeparator(String track2, char separator, char replaceSeparator) {
        if (track2 == null || track2.isEmpty()) {
            return -1;
        }
        int index = track2.indexOf(separator);
        return (index >= 0) ? index : track2.indexOf(replaceSeparator);
    }
}
