package com.github.teamjcd.bpp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.github.teamjcd.bpp.provider.BppDeviceClassColumns;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.github.teamjcd.bpp.content.BppDeviceClassContentProvider.DEFAULT_DEVICE_CLASS;
import static com.github.teamjcd.bpp.content.BppDeviceClassContentProvider.DEVICE_CLASS_URI;
import static com.github.teamjcd.bpp.provider.BppDeviceClassColumns.readFromCursor;
import static com.github.teamjcd.bpp.db.BppDeviceClassDatabaseHelper.PROJECTION;


public class BppDeviceClassStore {
    private final Context context;

    private BppDeviceClassStore(Context context) {
        this.context = context;
    }

    public static BppDeviceClassStore getBluetoothDeviceClassStore(Context context) {
        return new BppDeviceClassStore(context);
    }

    public List<BppDeviceClassColumns> getAll() {
        Cursor cursor = context.getContentResolver().query(
                DEVICE_CLASS_URI,
                PROJECTION,
                null, //selection
                null, //selectionArgs
                null //sortOrder
        );

        List<BppDeviceClassColumns> btDevices = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                btDevices.add(readFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return btDevices;
    }

    public BppDeviceClassColumns get(int id) {
        return get(Uri.withAppendedPath(DEVICE_CLASS_URI, String.valueOf(id)));
    }

    public BppDeviceClassColumns getDefault() {
        return get(Uri.withAppendedPath(DEVICE_CLASS_URI, DEFAULT_DEVICE_CLASS));
    }

    public BppDeviceClassColumns get(Uri btDeviceClassUri) {
        Cursor cursor = context.getContentResolver().query(
                btDeviceClassUri,
                PROJECTION,
                null,
                null,
                _ID
        );
        if (cursor == null) {
            return null;
        }
        try {
            return getFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    private BppDeviceClassColumns getFromCursor(Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return readFromCursor(cursor);
        } else {
            return null;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public Uri saveDefault(BppDeviceClassColumns btDeviceClass) {
        btDeviceClass.setIsDefault(1);
        return save(btDeviceClass);
    }

    public Uri save(BppDeviceClassColumns btDeviceClass) {
        ContentValues values = btDeviceClass.toContentValues();
        return context.getContentResolver().insert(DEVICE_CLASS_URI, values);
    }

    @SuppressWarnings("UnusedReturnValue")
    public int update(BppDeviceClassColumns btDeviceClass) {
        return update(btDeviceClass.getId(), btDeviceClass);
    }

    public int update(int id, BppDeviceClassColumns btDeviceClass) {
        return update(Uri.withAppendedPath(DEVICE_CLASS_URI, String.valueOf(id)), btDeviceClass);
    }

    public int update(Uri btDeviceClassUri, BppDeviceClassColumns btDeviceClass) {
        return context.getContentResolver().update(
                btDeviceClassUri,
                btDeviceClass.toContentValues(),
                null,
                null
        );
    }

    @SuppressWarnings("UnusedReturnValue")
    public int delete(int id) {
        return delete(Uri.withAppendedPath(DEVICE_CLASS_URI, Integer.toString(id)));
    }

    public int delete(Uri btDeviceClassUri) {
        return context.getContentResolver().delete(
                btDeviceClassUri,
                null,
                null
        );
    }
}
