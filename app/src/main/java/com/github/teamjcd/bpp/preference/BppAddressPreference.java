package com.github.teamjcd.bpp.preference;

import android.content.Context;
import android.net.Uri;
import com.github.teamjcd.bpp.activity.BppBaseActivity;
import com.github.teamjcd.bpp.activity.BppAddressEditorActivity;

import static com.github.teamjcd.bpp.content.BppAddressContentProvider.URI;
import static com.github.teamjcd.bpp.fragment.BppMainFragment.ACTION_ADDRESS_EDIT;

public class BppAddressPreference extends BppBasePreference {
    public BppAddressPreference(Context context) {
        super(context);
    }

    @Override
    protected Uri getContentUri() {
        return URI;
    }

    @Override
    protected Class<? extends BppBaseActivity> getIntentClass() {
        return BppAddressEditorActivity.class;
    }

    @Override
    protected String getAction() {
        return ACTION_ADDRESS_EDIT;
    }
}
