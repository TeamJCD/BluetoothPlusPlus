package com.teamjcd.bpp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class BluetoothDeviceClassEditorActivity extends AppCompatActivity {
    private static final String LOG_TAG = BluetoothDeviceClassEditorActivity.class.getName();

    Fragment mEditorFrag;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Log.d(LOG_TAG, "Starting onCreate");
        setContentView(R.layout.activity_bluetooth_device_class_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mEditorFrag = getSupportFragmentManager().findFragmentById(R.id.bluetooth_device_class_editor);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mEditorFrag.onCreateOptionsMenu(menu, getMenuInflater());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mEditorFrag.onOptionsItemSelected(item);
    }
}
