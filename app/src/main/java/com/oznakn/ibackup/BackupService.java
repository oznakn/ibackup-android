package com.oznakn.ibackup;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

public class BackupService extends Service {

    Binder binder;
    ImageContentObserver imageContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();

        this.binder = new Binder();

        this.imageContentObserver = new ImageContentObserver(new Handler(getMainLooper()), this::sendMediaByUri);

        this.getApplicationContext().getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this.imageContentObserver
        );
    }

    private void sendMediaByUri(Uri uri) {
        Cursor c = getContentResolver().query(uri, new String[]{"_data", "_display_name", "_id"}, null, null, null);

        c.moveToFirst();

        Media media = new Media(
                c.getInt(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex("_data")),
                c.getString(c.getColumnIndex("_display_name"))
        );

        c.close();

        CloudManager.getInstance(getApplicationContext())
                .upload(media, new CloudManager.onUploadListener() {
                    @Override
                    public void onSuccess(Media media) {

                    }

                    @Override
                    public void onError(Media media) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.getApplicationContext().getContentResolver().unregisterContentObserver(this.imageContentObserver);
        this.imageContentObserver = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }
}
