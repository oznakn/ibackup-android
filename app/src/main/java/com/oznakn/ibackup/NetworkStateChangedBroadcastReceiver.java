package com.oznakn.ibackup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkStateChangedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && (
                intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE") ||
                intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED") ||
                intent.getAction().equals("android.net.wifi.STATE_CHANGE")
        )) {
            context.startService(new Intent(context, BackupService.class));
        }
    }
}
