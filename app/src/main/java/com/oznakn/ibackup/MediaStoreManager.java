package com.oznakn.ibackup;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MediaStoreManager {

    private static MediaStoreManager instance = null;

    public static MediaStoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new MediaStoreManager(context);
        } else {
            instance.setContext(context);
        }

        return instance;
    }

    private Context context;

    private MediaStoreManager(Context context) {
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

    public ArrayList<String> getImageDirectories() {
        Cursor c = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_data", "relative_path"}, null, null, null);

        Set<String> set = new HashSet<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String path = c.getString(c.getColumnIndex("relative_path"));

            set.add(path);
        }

        c.close();

        ArrayList<String> result = new ArrayList<>(set);
        Collections.sort(result);

        return result;
    }
}
