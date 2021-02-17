package com.oznakn.ibackup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LocalDBHelper extends SQLiteOpenHelper {
    private static LocalDBHelper instance = null;

    public static LocalDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocalDBHelper(context);
        }
        return instance;
    }

    private static final String SQL_CREATE_IMAGES =
            "CREATE TABLE " + Image.TABLE_NAME + " (" +
                    Image._ID + " INTEGER PRIMARY KEY," +
                    Image.COLUMN_NAME_MEDIA_ID + " INTEGER," +
                    Image.COLUMN_NAME_PATH + " TEXT," +
                    Image.COLUMN_NAME_SYNCED + " INTEGER," +
                    Image.COLUMN_NAME_DATE + " INTEGER)";

    private Context context;

    public LocalDBHelper(Context context) {
        super(context, "IBackup", null, 1);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveImageIfNotExists(Image image) {
        SQLiteDatabase db = LocalDBHelper.getInstance(this.context).getReadableDatabase();

        Cursor c = db.query(
                Image.TABLE_NAME,
                new String[]{Image.COLUMN_NAME_MEDIA_ID},
                Image.COLUMN_NAME_MEDIA_ID + " = ?",
                new String[]{Integer.toString(image.mediaID)},
                null,
                null,
                null
        );

        int count = c.getCount();
        c.close();

        if (count == 0) {
            saveImage(image);
        }
    }

    public void saveImage(Image image) {
        SQLiteDatabase db = LocalDBHelper.getInstance(this.context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Image.COLUMN_NAME_MEDIA_ID, image.mediaID);
        values.put(Image.COLUMN_NAME_PATH, image.path);
        values.put(Image.COLUMN_NAME_SYNCED, false);
        values.put(Image.COLUMN_NAME_DATE, image.date);

        db.insert(Image.TABLE_NAME, null, values);
    }

    public void markImageAsSynced(Image image) {
        SQLiteDatabase db = LocalDBHelper.getInstance(this.context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Image.COLUMN_NAME_SYNCED, true);

        db.update(Image.TABLE_NAME, values, Image.COLUMN_NAME_MEDIA_ID + " = ?",
                new String[]{Integer.toString(image.mediaID)});
    }

    public ArrayList<Image> getNotSyncedImages() {
        SQLiteDatabase db = LocalDBHelper.getInstance(this.context).getReadableDatabase();

        Cursor c = db.query(
                Image.TABLE_NAME,
                null,
                Image.COLUMN_NAME_SYNCED + " = 0",
                null,
                null,
                null,
                Image.COLUMN_NAME_DATE + " ASC"
        );

        ArrayList<Image> result = Image.createArrayListFromCursor(c);

        c.close();

        return result;
    }
}
