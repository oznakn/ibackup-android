package com.oznakn.ibackup;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

public class Utils {
    public static String getDeviceName(Context context) {
        return Build.MODEL+ "("+ Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) + ")";
    }
}
