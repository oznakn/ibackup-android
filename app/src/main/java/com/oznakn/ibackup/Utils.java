package com.oznakn.ibackup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;

public class Utils {
    public static String getDeviceName(Context context) {
        return Build.MODEL+ "("+ Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) + ")";
    }

    public static boolean isOnlineWithWiFi(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
