package com.github.teamjcd.bpp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.github.teamjcd.bpp.provider.BppBaseColumns;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static android.provider.BaseColumns._ID;
import static com.github.teamjcd.bpp.content.BppBaseContentProvider.DEFAULT_URI;
import static com.github.teamjcd.bpp.database.BppDatabaseHelper.*;

public abstract class BppBaseRepository<T extends BppBaseColumns> {
    private final Context context;
    private final Supplier<T> newInstance;

    public BppBaseRepository(Context context, Supplier<T> newInstance) {
        this.context = context;
        this.newInstance = newInstance;
    }

    public List<T> getAll() {
        Cursor cursor = context.getContentResolver().query(
                getUri(),
                PROJECTION,
                null, //selection
                null, //selectionArgs
                null //sortOrder
        );

        List<T> result = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                result.add(readFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public T get(int id) {
        return get(Uri.withAppendedPath(getUri(), String.valueOf(id)));
    }

    public T getDefault() {
        return get(Uri.withAppendedPath(getUri(), DEFAULT_URI));
    }

    public T get(Uri uri) {
        Cursor cursor = context.getContentResolver().query(
                uri,
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

    private T getFromCursor(Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return readFromCursor(cursor);
        } else {
            return null;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public Uri saveDefault(T columns) {
        columns.setIsDefault(1);
        return save(columns);
    }

    public Uri save(T columns) {
        ContentValues values = toContentValues(columns);
        return context.getContentResolver().insert(getUri(), values);
    }

    @SuppressWarnings("UnusedReturnValue")
    public int update(T columns) {
        return update(columns.getId(), columns);
    }

    public int update(int id, T columns) {
        return update(Uri.withAppendedPath(getUri(), String.valueOf(id)), columns);
    }

    public int update(Uri uri, T columns) {
        return context.getContentResolver().update(
                uri,
                toContentValues(columns),
                null,
                null
        );
    }

    @SuppressWarnings("UnusedReturnValue")
    public int delete(int id) {
        return delete(Uri.withAppendedPath(getUri(), Integer.toString(id)));
    }

    public int delete(Uri uri) {
        return context.getContentResolver().delete(
                uri,
                null,
                null
        );
    }

    private T readFromCursor(Cursor cursor) {
        T result = newInstance.get();
        result.setId(cursor.getInt(INDEX_ID));
        result.setName(cursor.getString(INDEX_NAME));
        result.setValue(cursor.getInt(INDEX_VALUE));
        result.setIsDefault(cursor.getInt(INDEX_IS_DEFAULT));
        return result;
    }

    private ContentValues toContentValues(T columns) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, columns.getName());
        values.put(COLUMN_VALUE, columns.getValue());
        values.put(COLUMN_IS_DEFAULT, columns.getIsDefault());
        return values;
    }

    protected abstract Uri getUri();
}
