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

import static com.teamjcd.bpp.BluetoothDeviceClassEditor.URI_EXTRA;
import static com.teamjcd.bpp.db.BluetoothDeviceClassContentProvider.DEVICE_CLASS_URI;
import static com.teamjcd.bpp.db.BluetoothDeviceClassStore.getBluetoothDeviceClassStore;


public class BluetoothDeviceClassSettings extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_EDIT = "com.teamjcd.android.settings.bluetooth.BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_EDIT";
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_INSERT = "com.teamjcd.android.settings.bluetooth.BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_INSERT";

    private static final String TAG = "BluetoothDeviceClassSettings";

    private static final int MENU_NEW = Menu.FIRST;
    private static final int MENU_RESTORE = Menu.FIRST + 1;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, MENU_NEW, 0,
                getResources().getString(R.string.menu_new))
                .setIcon(R.drawable.ic_add_24)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        super.onCreateOptionsMenu(menu, inflater);
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

    private void addNewBluetoothDeviceClass() {
        Intent intent = new Intent(getContext(), BluetoothDeviceClassEditorActivity.class);
        intent.setAction(ACTION_BLUETOOTH_DEVICE_CLASS_INSERT);
        intent.putExtra(URI_EXTRA, DEVICE_CLASS_URI);
        startActivity(intent);
    }
}
