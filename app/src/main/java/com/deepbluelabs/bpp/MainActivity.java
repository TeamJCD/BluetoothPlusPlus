package com.deepbluelabs.bpp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.widget.TextView;

import com.android.internal.util.HexDump;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView deviceClass = findViewById(R.id.device_class);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Objects.nonNull(defaultAdapter)) {
            BluetoothClass bluetoothClass = defaultAdapter.getBluetoothClass();
            deviceClass.setText(HexDump.toHexString(bluetoothClass.getClassOfDevice()));
            defaultAdapter.setBluetoothClass(new BluetoothClass(0x002508));
        }
    }
}