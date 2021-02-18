package com.oznakn.ibackup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class BackupService extends Service {

    Binder binder;
    ImageContentObserver imageContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        this.binder = new Binder();

        this.imageContentObserver = new ImageContentObserver(new Handler(getMainLooper()), uri -> {
            Image image = MediaStoreManager.getInstance(getApplicationContext()).getImageByUri(uri);

            LocalDBHelper.getInstance(BackupService.this).saveImageIfNotExists(image);

            this.runSyncTask();
        });

        long lastSyncTime = SettingsManager.getInstance(this).getLastSyncTime();
        ArrayList<Image> images = MediaStoreManager.getInstance(this).getImagesAfterDate(lastSyncTime);

        for (Image image : images) {
            LocalDBHelper.getInstance(this).saveImageIfNotExists(image);
        }

        this.runSyncTask();

        this.getApplicationContext().getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this.imageContentObserver
        );

        Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        Log.d("[BackupService]", DatabaseUtils.dumpCursorToString(c));
    }

    private void runSyncTask() {
        if (Utils.isOnline(this)) {
            ArrayList<Image> images = LocalDBHelper.getInstance(this).getNotSyncedImages();

            sendNotification(images.size());

            for (Image image : images) {
                this.uploadImage(image);
            }
        }
    }

    private void uploadImage(Image image) {
        SettingsManager.getInstance(BackupService.this).setLastSyncTime(image.date);

        CloudManager.getInstance(getApplicationContext())
                .uploadImage(image, new CloudManager.onUploadListener() {
                    @Override
                    public void onSuccess(JsonObject result) {
                        JsonElement statusElement = result.get("status");

                        if (statusElement != null) {
                            String status = statusElement.getAsString();

                            if (status.equals("uploaded") || status.equals("exists")) {
                                LocalDBHelper.getInstance(BackupService.this).markImageAsSynced(image);
                            }
                        }
                    }
                });
    }

    private void sendNotification(int count) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationChannel channel = new NotificationChannel("simpleChannel", "Channel", NotificationManager.IMPORTANCE_MIN);
        notificationManager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, "simpleChannel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true)
                .setContentTitle("IBackup is running")
                .setContentText(count + " Images left for backup")
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT)
                )
                .build();

        notificationManager.notify(1, notification);

        startForeground(1, notification);
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

        this.runSyncTask();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    private static class ImageContentObserver extends ContentObserver {
        OnImageChangeListener listener;

        public ImageContentObserver(Handler handler, OnImageChangeListener listener) {
            super(handler);

            this.listener = listener;
        }

        @Override
        public void onChange(boolean selfChange, @Nullable Uri uri, int flags) {
            listener.onChange(uri);
        }

        @Override
        public void onChange(boolean selfChange, @Nullable Uri uri) {
            listener.onChange(uri);
        }

        public interface OnImageChangeListener {
            void onChange(Uri uri);
        }
    }

}
