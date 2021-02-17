package com.oznakn.ibackup;

import android.app.Application;

import com.koushikdutta.ion.Ion;

public class IBackupApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Ion.getDefault(this)
                .getConscryptMiddleware().enable(false);
    }
}
