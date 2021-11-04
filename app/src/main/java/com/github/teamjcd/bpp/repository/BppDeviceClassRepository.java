package com.github.teamjcd.bpp.repository;

import android.content.Context;
import android.net.Uri;
import com.github.teamjcd.bpp.provider.BppDeviceClassColumns;

import static com.github.teamjcd.bpp.content.BppDeviceClassContentProvider.URI;

public class BppDeviceClassRepository extends BppBaseRepository<BppDeviceClassColumns> {
    public BppDeviceClassRepository(Context context) {
        super(context, BppDeviceClassColumns::new);
    }

    @Override
    protected Uri getUri() {
        return URI;
    }
}
