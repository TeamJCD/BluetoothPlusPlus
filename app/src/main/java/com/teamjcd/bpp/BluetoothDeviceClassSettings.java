package com.teamjcd.bpp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

import com.teamjcd.bpp.db.BluetoothDeviceClassData;
import com.teamjcd.bpp.db.BluetoothDeviceClassStore;

import java.util.List;

import static com.teamjcd.bpp.db.BluetoothDeviceClassContentProvider.DEVICE_CLASS_URI;
import static com.teamjcd.bpp.db.BluetoothDeviceClassStore.getBluetoothDeviceClassStore;


public class BluetoothDeviceClassSettings extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_EDIT = "com.github.teamjcd.android.settings.bluetooth.BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_EDIT";
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_INSERT = "com.github.teamjcd.android.settings.bluetooth.BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_INSERT";
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_SETTINGS = "com.github.teamjcd.android.settings.bluetooth.BluetoothDeviceClassSettings.BLUETOOTH_DEVICE_CLASS_SETTINGS";

    public static final String BLUETOOTH_DEVICE_CLASS_ID = "bluetooth_device_class_id";

    private static final String TAG = "BluetoothDeviceClassSettings";

    private IntentFilter mIntentFilter;
    private BluetoothAdapter mAdapter;
    private BluetoothDeviceClassStore mStore;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                fillList();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mStore = getBluetoothDeviceClassStore(getContext());

        // TODO do not enable bluetooth automatically, instead display info text
        // something like "please enable bluetooth in order to manage device classes
        if (!mAdapter.isEnabled()) {
            mAdapter.enable();
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.bluetooth_device_class_settings);
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
        fillList();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange(): Preference - " + preference
                + ", newValue - " + newValue + ", newValue type - "
                + newValue.getClass());

        if (newValue instanceof String) {
            BluetoothDeviceClassData newDeviceClass = mStore.get(Integer.parseInt((String) newValue));
            if (newDeviceClass != null) {
                mAdapter.setBluetoothClass(new BluetoothClass(newDeviceClass.getDeviceClass()));
            }
        }

        return true;
    }

    private void fillList() {
        List<BluetoothDeviceClassData> codDataList = mStore.getAll();
        Log.d(TAG, "fillList(): codDataList - " + codDataList);

        if (!codDataList.isEmpty()) {
            final PreferenceGroup codPrefList = (PreferenceGroup) findPreference("bluetooth_device_class_list");
            codPrefList.removeAll();

            BluetoothClass bluetoothClass = mAdapter.getBluetoothClass();
            int deviceClass = bluetoothClass.getClassOfDevice();

            for (BluetoothDeviceClassData codData : codDataList) {
                final BluetoothDeviceClassPreference pref = new BluetoothDeviceClassPreference(getContext());

                pref.setKey(Integer.toString(codData.getId()));
                pref.setTitle(codData.getName());
                pref.setPersistent(false);
                pref.setOnPreferenceChangeListener(this);

                String summary = Integer.toHexString(codData.getDeviceClass());
                pref.setSummary(("000000" + summary)
                        .substring(summary.length()));

                Log.d(TAG, "fillList(): codData.getDeviceClass - " +
                        codData.getDeviceClass() + " deviceClass - " + deviceClass);

                if (codData.getDeviceClass() == deviceClass) {
                    pref.setChecked();
                }

                codPrefList.addPreference(pref);
            }
        }
    }
}
