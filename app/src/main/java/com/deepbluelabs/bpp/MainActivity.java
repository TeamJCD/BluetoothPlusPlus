package com.deepbluelabs.bpp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.internal.util.HexDump;

import java.util.Objects;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private TextView deviceClassTextView;
    private ImageButton deviceClassButton;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothBroadcastReceiver bluetoothBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceClassTextView = findViewById(R.id.device_class);
        deviceClassButton = findViewById(R.id.device_class_button);

        //Register bluetooth turn on receiver
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
        registerReceiver(bluetoothBroadcastReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Objects.nonNull(bluetoothAdapter)) {
            if (bluetoothAdapter.isEnabled()) {
                fillDeviceClass();
            } else {
                bluetoothAdapter.enable();
            }
        } else {
            showAdapterNotFoundToast();
        }

        deviceClassButton.setOnClickListener(this::showDeviceClassSelector);
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
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    }

    private void fillDeviceClass() {
        BluetoothClass bluetoothClass = Optional.ofNullable(bluetoothAdapter.getBluetoothClass())
                .orElseGet(() -> new BluetoothClass(retrieveBluetoothClassConfig()));
        if (Objects.nonNull(bluetoothClass)) {
            deviceClassTextView.setText(HexDump.toHexString(bluetoothClass.getClassOfDevice()));
        }
    }

    private void showDeviceClassSelector(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_baseline_settings_24);
        builderSingle.setTitle(R.string.select_device_class);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getString(R.string.gamepad));
        arrayAdapter.add(getString(R.string.phone));
        arrayAdapter.add(getString(R.string.custom));

        builderSingle.setNegativeButton(this.getText(android.R.string.cancel), (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            if (Objects.isNull(bluetoothAdapter)) {
                return; //No Bluetooth Adapter return
            }
            String selection = arrayAdapter.getItem(which);

            if (getString(R.string.custom).equals(selection)) {
                //Custom Device Class
                showCustomDeviceClassSelector();
                return;
            }

            BluetoothDeviceClassEnum.fromDeviceName(selection)
                    .map(selectionBtEnum -> setBluetoothDeviceClass(selectionBtEnum.getBtClass()))
                    .ifPresent(set -> {
                        if (set) {
                            showSuccessToast();
                        } else {
                            showFailToast();
                        }
                    });
        });
        builderSingle.show();
    }

    private void showCustomDeviceClassSelector() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.enter_device_class);

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("0x5A020C");
        alert.setView(input);

        alert.setPositiveButton(this.getText(android.R.string.ok), (dialog, whichButton) -> {
            if (Objects.isNull(bluetoothAdapter)) {
                return; //No Bluetooth Adapter return
            }
            Editable text = input.getText();
            if (Objects.nonNull(text)) {
                String deviceClassText = text.toString();
                try {
                    Integer decodedDeviceClass = Integer.decode(deviceClassText);
                    if (Objects.nonNull(decodedDeviceClass)
                            && setBluetoothDeviceClass(decodedDeviceClass)) {
                        showSuccessToast();
                        return;
                    }
                } catch (NumberFormatException exception) {
                    //Just fail message
                }
            }
            showFailToast();
        });

        alert.setNegativeButton(this.getText(android.R.string.cancel), (dialog, whichButton) -> {
            // Canceled.
        });
        alert.show();
    }

    private boolean setBluetoothDeviceClass(int deviceClass) {
        try {
            boolean result = bluetoothAdapter.setBluetoothClass(new BluetoothClass(deviceClass));
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
                getContentResolver(), Settings.Global.BLUETOOTH_CLASS_OF_DEVICE, 0);
    }

    private boolean storeBluetoothClassConfig(int bluetoothClass) {
        boolean result = Settings.Global.putInt(
                getContentResolver(), Settings.Global.BLUETOOTH_CLASS_OF_DEVICE, bluetoothClass);

        if (!result) {
            Log.e(TAG, "Error storing BluetoothClass config - " + bluetoothClass);
        }

        return result;
    }

    private void showSuccessToast() {
        Toast.makeText(this, R.string.device_class_set, Toast.LENGTH_SHORT).show();
    }

    private void showFailToast() {
        Toast.makeText(this, R.string.could_not_set_device_class, Toast.LENGTH_SHORT).show();
    }

    private void showAdapterNotFoundToast() {
        Toast.makeText(this, R.string.bluetooth_adapter_not_found, Toast.LENGTH_SHORT).show();
    }

}