package com.teamjcd.bpp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.teamjcd.bpp.BluetoothDeviceClassEditor.URI_EXTRA;
import static com.teamjcd.bpp.BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_INSERT;
import static com.teamjcd.bpp.db.BluetoothDeviceClassContentProvider.DEVICE_CLASS_URI;

public class BluetoothDeviceClassSettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = BluetoothDeviceClassSettingsActivity.class.getName();
    private static final int MENU_NEW = Menu.FIRST;
    private static final int MENU_RESTORE = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Log.d(LOG_TAG, "Starting onCreate");
        setContentView(R.layout.activity_bluetooth_device_class_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_NEW, 0,
                getResources().getString(R.string.menu_new))
                .setIcon(R.drawable.ic_add_24)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_NEW:
                addNewBluetoothDeviceClass();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewBluetoothDeviceClass() {
        Intent intent = new Intent(getApplicationContext(), BluetoothDeviceClassEditorActivity.class);
        intent.setAction(ACTION_BLUETOOTH_DEVICE_CLASS_INSERT);
        intent.putExtra(URI_EXTRA, DEVICE_CLASS_URI);
        startActivity(intent);
    }
}
