package cn.eas.usdk.demo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static String DEFALUT_FILE_NAME = "defalut";

    private SharedPreferencesManager() {
    }

    public static SharedPreferencesManager getInstance() {
        return SharedPreferencesManagerHolder.INSTANCE;
    }

    public void setParameter(SharedPreferences preferences, String key, String value) {
        try {
            SharedPreferences.Editor edi = preferences.edit();
            edi.putString(key, value);
            edi.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParameter(Context context, String key, String value) {
        try {
            SharedPreferences.Editor edi = getAppSharedPreferences(context, DEFALUT_FILE_NAME).edit();
            edi.putString(key, value);
            edi.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParameter(Context context, String key, boolean value) {
        try {
            SharedPreferences.Editor edi = getAppSharedPreferences(context, DEFALUT_FILE_NAME).edit();
            edi.putBoolean(key, value);
            edi.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParameter(Context context, String key, int value) {
        try {
            SharedPreferences.Editor edi = getAppSharedPreferences(context, DEFALUT_FILE_NAME).edit();
            edi.putInt(key, value);
            edi.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getParameter(Context context, SharedPreferences preferences, String key, String defaultValue) {
        try {
            return preferences.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public String getParameter(Context context, String key, String defaultValue) {
        try {
            return getAppSharedPreferences(context, DEFALUT_FILE_NAME).getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public boolean getParameter(Context context, String key, boolean defaultValue) {
        try {
            return getAppSharedPreferences(context, DEFALUT_FILE_NAME).getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public int getParameter(Context context, String key, int defaultValue) {
        try {
            return getAppSharedPreferences(context, DEFALUT_FILE_NAME).getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private SharedPreferences getAppSharedPreferences(Context context, String fileName) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    private static class SharedPreferencesManagerHolder {
        static SharedPreferencesManager INSTANCE = new SharedPreferencesManager();
    }
}
