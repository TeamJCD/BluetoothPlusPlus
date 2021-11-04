package com.github.teamjcd.bpp.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.github.teamjcd.bpp.database.BppDatabaseHelper;

import java.util.Objects;

import static android.provider.BaseColumns._ID;
import static com.github.teamjcd.bpp.database.BppDatabaseHelper.COLUMN_IS_DEFAULT;
import static com.github.teamjcd.bpp.database.BppDatabaseHelper.COLUMN_VALUE;

public abstract class BppBaseContentProvider extends ContentProvider {
    public static final String AUTHORITY = Objects.requireNonNull(BppBaseContentProvider.class.getPackage()).getName();
    public static final String DEFAULT_URI = "default";

    protected static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private static final int ROOT = 0;
    private static final int ID = 1;
    private static final int DEFAULT = 2;

    private final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private SQLiteDatabase database;

    public BppBaseContentProvider() {
        URI_MATCHER.addURI(AUTHORITY, getTable(), ROOT);
        URI_MATCHER.addURI(AUTHORITY, getTable() + "/#", ID);
        URI_MATCHER.addURI(AUTHORITY, getTable() + "/" + DEFAULT_URI, DEFAULT);
    }

    @Override
    public boolean onCreate() {
        BppDatabaseHelper dbHelper = new BppDatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = URI_MATCHER.match(uri);

        String where = null;
        String[] whereArgs = null;
        if (match < 0) {
            return null;
        } else if (match == ID) {
            String lastPathSegment = uri.getLastPathSegment();
            where = _ID + " = ?";
            whereArgs = new String[]{ lastPathSegment };
        } else if (match == DEFAULT) {
            where = COLUMN_IS_DEFAULT + " = 1";
        }

        return database.query(getTable(),
                projection,
                where,
                whereArgs,
                null,
                null,
                null);
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ROOT:
                return "vnd.android.cursor.dir/vnd.com.github.teamjcd.bpp.content.BppBaseContentProvider.dir";
            case ID:
            case DEFAULT:
                return "vnd.android.cursor.item/vnd.com.github.teamjcd.bpp.content.BppBaseContentProvider.item";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = URI_MATCHER.match(uri);

        if (match != ROOT) {
            return null;
        }

        return Uri.withAppendedPath(
                uri, String.valueOf(database.insert(getTable(), null, values)));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);

        if (match != ID) {
            return 0;
        }

        String lastPathSegment = uri.getLastPathSegment();
        return database.delete(getTable(), _ID + " = ?", new String[]{ lastPathSegment });
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);

        if (match != ID) {
            values.remove(COLUMN_VALUE);
        }

        String lastPathSegment = uri.getLastPathSegment();
        return database.update(getTable(), values, _ID + " = ?", new String[]{ lastPathSegment });
    }

    protected abstract String getTable();
}
