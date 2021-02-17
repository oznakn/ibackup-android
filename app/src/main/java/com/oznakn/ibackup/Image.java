package com.oznakn.ibackup;

import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class Image implements BaseColumns {
    public static final String TABLE_NAME = "images";
    public static final String COLUMN_NAME_MEDIA_ID = "media_id";
    public static final String COLUMN_NAME_PATH = "path";
    public static final String COLUMN_NAME_SYNCED = "synced";
    public static final String COLUMN_NAME_DATE = "date_modified";

    int mediaID;
    String path;
    long date;

    public Image(int mediaID, String path, long date) {
        this.mediaID = mediaID;
        this.path = path;
        this.date = date;
    }

    public static Image fromMediaCursor(Cursor c) {
        return new Image(
                c.getInt(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex("_data")),
                c.getLong(c.getColumnIndex("date_modified"))
        );
    }

    public static Image createFromMediaCursor(Cursor c) {
        if (!c.moveToFirst()) {
            return null;
        }

        return Image.fromMediaCursor(c);
    }

    public static ArrayList<Image> createArrayListFromMediaCursor(Cursor c) {
        ArrayList<Image> result = new ArrayList<>();

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result.add(Image.fromMediaCursor(c));
        }

        return result;
    }

    public static Image fromCursor(Cursor c) {
        return new Image(
                c.getInt(c.getColumnIndex(COLUMN_NAME_MEDIA_ID)),
                c.getString(c.getColumnIndex(COLUMN_NAME_PATH)),
                c.getLong(c.getColumnIndex(COLUMN_NAME_DATE))
        );
    }

    public static Image createFromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            return null;
        }

        return Image.fromCursor(c);
    }

    public static ArrayList<Image> createArrayListFromCursor(Cursor c) {
        ArrayList<Image> result = new ArrayList<>();

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result.add(Image.fromCursor(c));
        }

        return result;
    }
}
