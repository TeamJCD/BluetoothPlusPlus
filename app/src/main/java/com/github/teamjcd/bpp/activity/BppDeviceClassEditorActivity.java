package com.github.teamjcd.bpp.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import com.github.teamjcd.bpp.R;

public class BppDeviceClassEditorActivity extends BppBaseActivity {
    private static final String LOG_TAG = BppDeviceClassEditorActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Log.d(LOG_TAG, "Starting onCreate");

        setContentView(R.layout.activity_bpp_device_class_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.screen_title_edit);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setFragment(getSupportFragmentManager().findFragmentById(R.id.fragment_bpp_device_class_editor));
    }
}
