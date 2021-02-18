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

        Ion.with(this.context)
                .load("POST", "http://cloud.oznakn.com:8080/api/upload")
                .setMultipartParameter("source",Utils.getDeviceName(this.context))
                .setMultipartParameter("path", image.path)
                .setMultipartParameter("date", Long.toString(image.date))
                .setMultipartFile("file", new File(image.path))
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e == null) {
                        onUploadListener.onSuccess(result);
                    } else {
                        onUploadListener.onError(result, e);
                    }
                });
    }

    public abstract static class onUploadListener {
        abstract void onSuccess(JsonObject result);

        void onError(JsonObject result, Exception e) {
            e.printStackTrace();
        }
    }
}
