package com.github.teamjcd.bpp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;

public class BluetoothDeviceClassEditorActivity extends BluetoothDeviceClassActivity {
    private static final String LOG_TAG = BluetoothDeviceClassEditorActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Log.d(LOG_TAG, "Starting onCreate");

        setContentView(R.layout.activity_bluetooth_device_class_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.screen_title_edit);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setFragment(getSupportFragmentManager().findFragmentById(R.id.bluetooth_device_class_editor));
    }
}
