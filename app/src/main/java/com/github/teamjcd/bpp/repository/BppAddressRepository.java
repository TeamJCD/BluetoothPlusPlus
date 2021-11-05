package com.github.teamjcd.bpp.repository;

import android.content.Context;
import android.net.Uri;
import com.github.teamjcd.bpp.provider.BppAddressColumns;

import static com.github.teamjcd.bpp.content.BppAddressContentProvider.URI;

public class BppAddressRepository extends BppBaseRepository<BppAddressColumns> {
    public BppAddressRepository(Context context) {
        super(context, BppAddressColumns::new);
    }

    @Override
    protected Uri getUri() {
        return URI;
    }
}
