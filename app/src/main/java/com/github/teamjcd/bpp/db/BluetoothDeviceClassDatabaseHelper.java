package com.github.teamjcd.bpp.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;

import static android.provider.BaseColumns._ID;

public class BluetoothDeviceClassDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = BluetoothDeviceClassDatabaseHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "Bluetooth";
    public static final String TABLE_NAME = "DeviceClass";
    public static final int DATABASE_VERSION = 1;

    public static final String DEVICE_CLASS_NAME = "name";
    public static final String DEVICE_CLASS_VALUE = "class";
    public static final String DEVICE_CLASS_IS_DEFAULT = "is_default";

    public static final String[] PROJECTION = new String[]{
            _ID,
            DEVICE_CLASS_NAME,
            DEVICE_CLASS_VALUE,
            DEVICE_CLASS_IS_DEFAULT
    };

    public static final int ID_INDEX = 0;
    public static final int DEVICE_CLASS_NAME_INDEX = 1;
    public static final int DEVICE_CLASS_VALUE_INDEX = 2;
    public static final int DEVICE_CLASS_IS_DEFAULT_INDEX = 3;

    public BluetoothDeviceClassDatabaseHelper(Context context) {
        super(new DatabaseContext(context), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                DEVICE_CLASS_NAME + " TEXT NOT NULL," +
                DEVICE_CLASS_VALUE + " INTEGER NOT NULL," +
                DEVICE_CLASS_IS_DEFAULT + " INTEGER DEFAULT 0" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException();
    }

    public static class DatabaseContext extends ContextWrapper {
        public DatabaseContext(Context base) {
            super(base);
        }

        @Override
        public File getDatabasePath(String name) {
            File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(this, null);
            File externalStorageFile = (externalStorageFiles.length < 2) ? externalStorageFiles[0] : externalStorageFiles[1];
            File databaseFile = new File(externalStorageFile.getAbsolutePath() + File.separator + name);

            if (!databaseFile.getParentFile().exists()) {
                databaseFile.getParentFile().mkdirs();
            }

            Log.d(TAG, "getDatabasePath(): databaseFile - " + databaseFile);

            return databaseFile;
        }

        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            return openOrCreateDatabase(name,mode, factory);
        }

        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }
    }
}
