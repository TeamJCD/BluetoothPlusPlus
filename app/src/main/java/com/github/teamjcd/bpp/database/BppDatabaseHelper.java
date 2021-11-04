package com.github.teamjcd.bpp.database;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;

import static android.provider.BaseColumns._ID;

public class BppDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = BppDatabaseHelper.class.getName();

    public static final String DATABASE = "BluetoothPlusPlus";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_DEVICE_CLASS = "DeviceClass";
    public static final String TABLE_ADDRESS = "Address";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_IS_DEFAULT = "is_default";

    public static final String[] PROJECTION = new String[]{
            _ID,
            COLUMN_NAME,
            COLUMN_VALUE,
            COLUMN_IS_DEFAULT
    };

    public static final int INDEX_ID = 0;
    public static final int INDEX_NAME = 1;
    public static final int INDEX_VALUE = 2;
    public static final int INDEX_IS_DEFAULT = 3;

    public BppDatabaseHelper(Context context) {
        super(new DatabaseContext(context), DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_DEVICE_CLASS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                COLUMN_VALUE + " INTEGER NOT NULL," +
                COLUMN_IS_DEFAULT + " INTEGER DEFAULT 0" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_ADDRESS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                COLUMN_VALUE + " INTEGER NOT NULL," +
                COLUMN_IS_DEFAULT + " INTEGER DEFAULT 0" +
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

            if (databaseFile.getParentFile() != null && !databaseFile.getParentFile().exists()) {
                if (!databaseFile.getParentFile().mkdirs()) {
                    Log.e(TAG, "getDatabasePath(): Unable to create directory "
                            + databaseFile.getParentFile());

                    return super.getDatabasePath(name);
                }
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
            return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        }
    }
}
