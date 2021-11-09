package com.github.teamjcd.bpp.content;

import android.net.Uri;

import static com.github.teamjcd.bpp.database.BppDatabaseHelper.TABLE_ADDRESS;

public class BppAddressContentProvider extends BppBaseContentProvider {
    public static final Uri URI = Uri.withAppendedPath(Uri.parse("content://" + BppAddressContentProvider.class.getName()), TABLE_ADDRESS);

    @Override
    protected String getTable() {
        return TABLE_ADDRESS;
    }
}
