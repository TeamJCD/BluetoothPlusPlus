package com.github.teamjcd.bpp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class BluetoothDeviceClassEditorActivity extends BluetoothDeviceClassActivity {
    private static final String TAG = BluetoothDeviceClassEditorActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Log.d(TAG, "Starting onCreate");

        setContentView(R.layout.activity_bluetooth_device_class_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.screen_title_edit);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setFragment(getSupportFragmentManager().findFragmentById(R.id.bluetooth_device_class_editor));
    }
}
