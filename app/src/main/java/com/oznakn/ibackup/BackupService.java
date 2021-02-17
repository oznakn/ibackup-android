package com.oznakn.ibackup;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

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
            Image image = BackupManager.getInstance(getApplicationContext()).getImageByUri(uri);

            LocalDBHelper.getInstance(BackupService.this).saveImageIfNotExists(image);

            this.runSyncTask();
        });

        long lastSyncTime = SettingsManager.getInstance(this).getLastSyncTime();
        ArrayList<Image> images = BackupManager.getInstance(this).getImagesAfterDate(lastSyncTime);

        for (Image image : images) {
            LocalDBHelper.getInstance(this).saveImageIfNotExists(image);
        }

        this.runSyncTask();

        this.getApplicationContext().getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this.imageContentObserver
        );
    }

    private void runSyncTask() {
        ArrayList<Image> images = LocalDBHelper.getInstance(this).getNotSyncedImages();

        for (Image image : images) {
            this.uploadImage(image);
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
