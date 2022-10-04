/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.preference.PreferenceManager
 *  android.support.annotation.Nullable
 *  java.lang.Object
 *  java.lang.String
 */
package com.harlharjj.softhub;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

public class SharedPrefsUtil {

    public static final String DEVICE_NAME_KEY = "devicename";
    public static final String MAC_ADDRESS_KEY = "macadress";

    public static boolean getBooleanOrDefault(Context context, String string, boolean bl) {
        return PreferenceManager.getDefaultSharedPreferences((Context)context).getBoolean(string, bl);
    }

    public static String getStringOrDefault(Context context, String string, String string2) {
        return PreferenceManager.getDefaultSharedPreferences((Context)context).getString(string, string2);
    }

    @Nullable
    public static String getStringOrNull(Context context, String string) {
        return PreferenceManager.getDefaultSharedPreferences((Context)context).getString(string, null);
    }

    public static void save(Context context, String string, String string2) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences((Context)context).edit();
        editor.putString(string, string2);
        editor.apply();
    }
}

