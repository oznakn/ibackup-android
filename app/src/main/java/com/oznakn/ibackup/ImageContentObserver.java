package com.oznakn.ibackup;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.Nullable;

public class ImageContentObserver extends ContentObserver {
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
