package com.oznakn.ibackup;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class BackupManager {

    private static BackupManager instance = null;

    public static BackupManager getInstance(Context context) {
        if (instance == null) {
            instance = new BackupManager(context);
        } else {
            instance.setContext(context);
        }

        return instance;
    }

    private Context context;

    private BackupManager(Context context) {
        this.setContext(context);
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public Image getImageByUri(Uri uri) {
        Cursor c = context.getContentResolver().query(uri,
                new String[]{"_id", "_data", "date_modified"}, null, null, null);

        Image image = Image.createFromMediaCursor(c);

        c.close();

        return image;
    }

    public ArrayList<Image> getImagesCursor() {
        Cursor c = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id", "_data",  "date_modified"}, null, null, null);

        ArrayList<Image> result = Image.createArrayListFromMediaCursor(c);

        c.close();

        return result;
    }

    public ArrayList<Image> getImagesAfterDate(long date) {
        Cursor c = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id", "_data", "date_modified"}, "date_modified > ?", new String[]{Long.toString(date)}, null);

        ArrayList<Image> result = Image.createArrayListFromMediaCursor(c);

        c.close();

        return result;
    }
}
