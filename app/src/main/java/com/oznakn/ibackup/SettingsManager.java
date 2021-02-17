package com.oznakn.ibackup;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private final static String SETTINGS_NAME = "com.oznakn.ybackup";

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

    private void setLastSyncDate() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.apply();
    }
}
