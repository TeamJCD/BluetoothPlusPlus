package com.github.teamjcd.bpp.activity;

import com.github.teamjcd.bpp.R;

public class BppMainActivity extends BppBaseActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_bpp_main;
    }

    @Override
    protected int getFragmentResId() {
        return R.id.fragment_bpp_main;
    }
}
