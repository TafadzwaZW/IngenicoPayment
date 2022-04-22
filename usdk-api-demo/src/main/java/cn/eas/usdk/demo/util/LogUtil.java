package cn.eas.usdk.demo.util;

import android.util.Log;

import cn.eas.usdk.demo.constant.DemoConfig;

public class LogUtil {

    public static void d(String message) {
        Log.d(DemoConfig.TAG, message);
    }

    public static void e(String message) {
        Log.e(DemoConfig.TAG, message);
    }
}
