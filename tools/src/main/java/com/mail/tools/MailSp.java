package com.mail.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mail.tools.context.AppContext;


public class MailSp {

    private static final String SPLIT_FLAG = "#@FLAG!=#@";
    private static MailSp instance = new MailSp();
    private SharedPreferences mSp;
    private Editor mEditor;

    private static SharedPreferences getSp() {
        if (instance.mSp == null) {
            if (AppContext.getAppContext() != null) {
                instance.mSp = AppContext.getAppContext().getSharedPreferences("sp_email", Context.MODE_PRIVATE);
            }
        }
        return instance.mSp;
    }

    private static Editor getEditor() {
        if (instance.mEditor == null) {
            instance.mEditor = getSp().edit();
        }
        return instance.mEditor;
    }


    public static void putString(String key, String value) {
        getEditor().putString(key, value);
        getEditor().commit();
    }

    public static String getString(String key, String defValue) {
        return getSp().getString(key, defValue);
    }

    public static void putInt(String key, int value) {
        getEditor().putInt(key, value);
        getEditor().commit();
    }

    public static int getInt(String key, int defValue) {
        return getSp().getInt(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value);
        getEditor().commit();
    }

    public static Boolean getBoolean(String key, boolean defValue) {
        return getSp().getBoolean(key, defValue);
    }

    public static void putFloat(String key, float value) {
        getEditor().putFloat(key, value);
        getEditor().commit();
    }

    public static float getFloat(String key, float defValue) {
        return getSp().getFloat(key, defValue);
    }

    public static String[] getStringArray(String key) {
        String text = getString(key, "");
        if (text == null || text.length() == 0) {
            return new String[]{};
        }
        return text.split(SPLIT_FLAG);
    }

    public static void appendSaveArray(String key, String val) {
        if (val == null || key == null) {
            return;
        }
        String text = getString(key, "");
        if (text.contains(SPLIT_FLAG + val + SPLIT_FLAG) || text.startsWith(val + SPLIT_FLAG)) {
            return;
        }
        if (text.length() > 0) {
            text = text + val + SPLIT_FLAG;
        } else {
            text = val + SPLIT_FLAG;
        }
        putString(key, text);
    }

    public static void clear(String key) {
        putString(key, "");
    }
}
