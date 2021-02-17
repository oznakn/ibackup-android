package com.oznakn.ibackup;

import android.content.Context;
import android.util.Log;

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

    public void upload(Media media, onUploadListener onUploadListener) {
        Log.d("[CloudManager]", "Uploading new media");

        File file = new File(media.path);

        Ion.getDefault(context)
            .getConscryptMiddleware().enable(false);

        Ion.with(this.context)
                .load("POST", "http://mac.oznakn.com:8080/api/upload")
                .setMultipartParameter("source","Android")
                .setMultipartParameter("path","/device")
                .setMultipartFile("file", file)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e == null) {
                        Log.d("[CloudManager]", result.toString());

                        onUploadListener.onSuccess(media);
                    } else {
                        e.printStackTrace();
                        onUploadListener.onError(media);
                    }
                });
    }

    public interface onUploadListener {
        void onSuccess(Media media);
        void onError(Media media);
    }
}
