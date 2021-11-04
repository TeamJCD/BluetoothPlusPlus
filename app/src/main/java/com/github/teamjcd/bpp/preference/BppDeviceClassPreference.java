package com.github.teamjcd.bpp.preference;

import android.content.Context;
import android.net.Uri;
import com.github.teamjcd.bpp.activity.BppBaseActivity;
import com.github.teamjcd.bpp.activity.BppDeviceClassEditorActivity;

import static com.github.teamjcd.bpp.content.BppDeviceClassContentProvider.URI;
import static com.github.teamjcd.bpp.fragment.BppMainFragment.ACTION_DEVICE_CLASS_EDIT;

public class BppDeviceClassPreference extends BppBasePreference {
    public BppDeviceClassPreference(Context context) {
        super(context);
    }

    @Override
    protected Uri getContentUri() {
        return URI;
    }

    @Override
    protected Class<? extends BppBaseActivity> getIntentClass() {
        return BppDeviceClassEditorActivity.class;
    }

    @Override
    protected String getAction() {
        return ACTION_DEVICE_CLASS_EDIT;
    }
}
