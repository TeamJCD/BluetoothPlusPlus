package com.github.teamjcd.bpp.activity;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.ActionBar;

public abstract class BppBaseEditorActivity extends BppBaseActivity {
    private static final String TAG = BppBaseEditorActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Log.d(TAG, "Starting onCreate");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}
