package com.github.teamjcd.bpp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;

public class BluetoothDeviceClassSettingsActivity extends BluetoothDeviceClassActivity {
    private static final String TAG = BluetoothDeviceClassSettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Log.d(TAG, "Starting onCreate");

        setContentView(R.layout.activity_bluetooth_device_class_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        setFragment(getSupportFragmentManager().findFragmentById(R.id.bluetooth_device_class_settings));
    }
}
