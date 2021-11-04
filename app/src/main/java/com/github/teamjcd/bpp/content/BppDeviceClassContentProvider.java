package com.github.teamjcd.bpp.content;

import android.net.Uri;

import static com.github.teamjcd.bpp.database.BppDatabaseHelper.TABLE_DEVICE_CLASS;

public class BppDeviceClassContentProvider extends BppBaseContentProvider {
    public static final Uri URI = Uri.withAppendedPath(BASE_URI, TABLE_DEVICE_CLASS);

    @Override
    protected String getTable() {
        return TABLE_DEVICE_CLASS;
    }
}
