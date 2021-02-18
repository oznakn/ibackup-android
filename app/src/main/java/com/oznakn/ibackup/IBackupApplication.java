package com.oznakn.ibackup;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.koushikdutta.ion.Ion;

import java.util.Calendar;

public class IBackupApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, BackupService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 34534, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), AlarmManager.INTERVAL_DAY / 2, pendingIntent);

        Ion.getDefault(this)
                .getConscryptMiddleware().enable(false);
    }
}
