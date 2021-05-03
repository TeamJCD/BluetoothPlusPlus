package com.teamjcd.bpp.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static android.provider.BaseColumns._ID;
import static com.teamjcd.bpp.db.BluetoothDeviceClassDatabaseHelper.DEVICE_CLASS_VALUE;
import static com.teamjcd.bpp.db.BluetoothDeviceClassDatabaseHelper.TABLE_NAME;

public class BluetoothDeviceClassContentProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String AUTHORITY = "com.teamjcd.bpp.db";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    private static final String DEVICE_CLASS_TABLE = "device_class";
    public static final Uri DEVICE_CLASS_URI = Uri.withAppendedPath(BASE_URI, DEVICE_CLASS_TABLE);
    public static final int DEFAULT_ID = 1;

    public static final String CONTENT_TYPE_ROOT = "vnd.android.cursor.dir/vnd.com.github.teamjcd.android.settings.bluetooth.db.BluetoothDeviceClassContentProvider.device_class";
    public static final String CONTENT_TYPE_ID = "vnd.android.cursor.item/vnd.com.github.teamjcd.android.settings.bluetooth.db.BluetoothDeviceClassContentProvider.device_class";

    private static final int ROOT = 0;
    private static final int ID = 1;

    static {
        uriMatcher.addURI(AUTHORITY, DEVICE_CLASS_TABLE, ROOT);
        uriMatcher.addURI(AUTHORITY, DEVICE_CLASS_TABLE + "/#", ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        BluetoothDeviceClassDatabaseHelper dbHelper = new BluetoothDeviceClassDatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);

        String where = null;
        String[] whereArgs = null;
        if (match < 0) {
            return null;
        } else if (match == ID) {
            String lastPathSegment = uri.getLastPathSegment();
            where = _ID + " = ?";
            whereArgs = new String[]{lastPathSegment};
        }

        return database.query(TABLE_NAME,
                projection,
                where,
                whereArgs,
                null,
                null,
                null);
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ROOT:
                return CONTENT_TYPE_ROOT;
            case ID:
                return CONTENT_TYPE_ID;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        if (match != ROOT) {
            return null;
        }
        return Uri.withAppendedPath(
                uri, String.valueOf(database.insert(TABLE_NAME, null, values)));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        String lastPathSegment = uri.getLastPathSegment();
        if (match != ID || String.valueOf(DEFAULT_ID).equals(lastPathSegment)) {
            return 0;
        }

        return database.delete(TABLE_NAME, _ID + " = ?", new String[]{lastPathSegment});
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        String lastPathSegment = uri.getLastPathSegment();
        if (match != ID || String.valueOf(DEFAULT_ID).equals(lastPathSegment)) {
            values.remove(DEVICE_CLASS_VALUE);
        }

        return database.update(TABLE_NAME, values, _ID + " = ?", new String[]{lastPathSegment});
    }
}
