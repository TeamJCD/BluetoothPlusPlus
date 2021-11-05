package com.github.teamjcd.bpp.activity;

import com.github.teamjcd.bpp.R;

public class BppAddressEditorActivity extends BppBaseEditorActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_bpp_address_editor;
    }

    @Override
    protected int getFragmentResId() {
        return R.id.fragment_bpp_address_editor;
    }
}
