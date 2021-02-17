package com.oznakn.ibackup;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class SettingsManager {
    private final static String SETTINGS_NAME = "com.oznakn.ybackup";

    private final static String SETTING_FIRST_RUN = "first_run";
    private final static String SETTING_LAST_SYNC = "last_sync";

    private static SettingsManager instance = null;

    public static SettingsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsManager(context);
        } else {
            instance.setContext(context);
        }

        return instance;
    }

    private Context context;
    private SharedPreferences preferences;

    private SettingsManager(Context context) {
        this.setContext(context);

        preferences = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public boolean getFirstRun() {
        return preferences.getBoolean(SETTING_FIRST_RUN, true);
    }

    public void setFirstRun(boolean value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(SETTING_FIRST_RUN, value);

        editor.apply();
    }

    public long getLastSyncTime() {
        return preferences.getLong(SETTING_LAST_SYNC, Calendar.getInstance().getTimeInMillis());

    }

    public void setLastSyncTime(long time) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(SETTING_LAST_SYNC, time);

        editor.apply();
    }
}
