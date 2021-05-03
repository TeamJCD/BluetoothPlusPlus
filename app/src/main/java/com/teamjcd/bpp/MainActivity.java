package com.teamjcd.bpp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.internal.util.HexDump;
import com.teamjcd.bpp.db.BluetoothDeviceClassData;
import com.teamjcd.bpp.db.BluetoothDeviceClassStore;

import java.util.Objects;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private TextView deviceClassTextView;
    private ImageButton deviceClassButton;

    private BluetoothAdapter mAdapter;
    private BluetoothDeviceClassStore mStore;
    private BluetoothBroadcastReceiver mReceiver;
    private IntentFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceClassTextView = findViewById(R.id.device_class);
        deviceClassButton = findViewById(R.id.device_class_button);

        //Register bluetooth turn on receiver
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mReceiver = new BluetoothBroadcastReceiver();
        mStore = BluetoothDeviceClassStore.getBluetoothDeviceClassStore(this.getApplicationContext());

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!Objects.nonNull(mAdapter)) {
            showAdapterNotFoundToast();
        }

        deviceClassButton.setOnClickListener((v) -> {
            Intent intent = new Intent(this, BluetoothDeviceClassSettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mFilter);
        if (Objects.nonNull(mAdapter)) {
            if (mAdapter.isEnabled()) {
                fillDeviceClass();
                saveInitialValue();
            } else {
                mAdapter.enable();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private class BluetoothBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        fillDeviceClass();
                        saveInitialValue();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    }

    private void fillDeviceClass() {
        BluetoothClass bluetoothClass = getBluetoothDeviceClass();
        if (Objects.nonNull(bluetoothClass)) {
            deviceClassTextView.setText(HexDump.toHexString(bluetoothClass.getClassOfDevice()));
        }
    }

    private void saveInitialValue() {
        BluetoothDeviceClassData defaultClass = mStore.getDefault();
        Log.d(TAG, "saveInitialValue(): defaultClass - " + defaultClass);
        if (defaultClass == null) {
            BluetoothClass bluetoothClass = mAdapter.getBluetoothClass();
            Log.d(TAG, "saveInitialValue(): bluetoothClass - " + bluetoothClass);
            mStore.saveDefault(new BluetoothDeviceClassData(
                    "Default",
                    bluetoothClass.getClassOfDevice()
            ));
        }
    }

    private BluetoothClass getBluetoothDeviceClass() {
        return Optional.ofNullable(mAdapter.getBluetoothClass())
                .orElseGet(() -> new BluetoothClass(retrieveBluetoothClassConfig()));
    }

    private boolean setBluetoothDeviceClass(int deviceClass) {
        saveInitialValue();
        try {
            boolean result = mAdapter.setBluetoothClass(new BluetoothClass(deviceClass));
            if (!result) {
                result = storeBluetoothClassConfig(deviceClass);
            }
            if (result) {
                deviceClassTextView.setText(HexDump.toHexString(deviceClass));
            }
            return result;
        } catch (Exception exception) {
            //Could not set lets just return false
            return false;
        }
    }

    private int retrieveBluetoothClassConfig() {
        return Settings.Global.getInt(
                getContentResolver(), Settings.Global.BLUETOOTH_CLASS_OF_DEVICE, -1);
    }

    private boolean storeBluetoothClassConfig(int bluetoothClass) {
        boolean result = Settings.Global.putInt(
                getContentResolver(), Settings.Global.BLUETOOTH_CLASS_OF_DEVICE, bluetoothClass);

        if (!result) {
            Log.e(TAG, "Error storing BluetoothClass config - " + bluetoothClass);
        }

        return result;
    }

    private void showAdapterNotFoundToast() {
        Toast.makeText(this, R.string.bluetooth_adapter_not_found, Toast.LENGTH_SHORT).show();
    }

}