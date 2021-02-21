package com.oznakn.ibackup;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.io.File;

public class CloudManager {
    private static CloudManager instance = null;

    public static CloudManager getInstance(Context context) {
        if (instance == null) {
            instance = new CloudManager(context);
        } else {
            instance.setContext(context);
        }

        return instance;
    }

    private Context context;

    private CloudManager(Context context) {
        this.setContext(context);
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public void uploadImage(Image image, onUploadListener onUploadListener) {
        Log.d("[CloudManager]", "Uploading new image");

        String url = SettingsManager.getInstance(this.context).getServerUrl();

        File file = new File(image.path);

        if (!file.exists()) {
            onUploadListener.onFileNotFound();
        } else if (!url.isEmpty()) {
            Ion.with(this.context)
                    .load("POST", String.format("http://%s/api/upload", url))
                    .setMultipartParameter("source", Utils.getDeviceName(this.context))
                    .setMultipartParameter("path", image.path)
                    .setMultipartParameter("date", Long.toString(image.date))
                    .setMultipartFile("file", file)
                    .asJsonObject()
                    .setCallback((e, result) -> {
                        if (e == null) {
                            onUploadListener.onSuccess(result);
                        } else {
                            onUploadListener.onServerError(result, e);
                        }
                    });
        }
    }

    public abstract static class onUploadListener {
        abstract void onSuccess(JsonObject result);

        void onFileNotFound() {}

        void onServerError(JsonObject result, Exception e) {
            e.printStackTrace();
        }
    }
}
