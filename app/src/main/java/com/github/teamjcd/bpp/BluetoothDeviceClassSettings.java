package com.github.teamjcd.bpp;

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

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

import com.github.teamjcd.bpp.db.BluetoothDeviceClassData;
import com.github.teamjcd.bpp.db.BluetoothDeviceClassStore;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.github.teamjcd.bpp.BluetoothDeviceClassEditor.URI_EXTRA;
import static com.github.teamjcd.bpp.db.BluetoothDeviceClassContentProvider.DEVICE_CLASS_URI;
import static com.github.teamjcd.bpp.db.BluetoothDeviceClassStore.getBluetoothDeviceClassStore;


public class BluetoothDeviceClassSettings extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_EDIT = "com.github.teamjcd.bpp.BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_EDIT";
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_INSERT = "com.github.teamjcd.bpp.BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_INSERT";

    private static final String TAG = "BluetoothDeviceClassSettings";

    private static final int REQUEST_ENABLE_BT = 1;

    private static final int MENU_NEW = Menu.FIRST;

    private IntentFilter mIntentFilter;
    private BluetoothAdapter mAdapter;
    private BluetoothDeviceClassStore mStore;

    private boolean mUnavailable;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                mUnavailable = state != BluetoothAdapter.STATE_ON;

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        saveInitialValue();
                    case BluetoothAdapter.STATE_OFF:
                        fillList();
                        break;
                    default:
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mStore = getBluetoothDeviceClassStore(getContext());

        mUnavailable = mAdapter == null || !mAdapter.isEnabled();

        if (mUnavailable) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            saveInitialValue();
        }

        fillList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            saveInitialValue();
            fillList();
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

        if (!mUnavailable) {
            fillList();
        }
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

    private void fillList() {
        List<BluetoothDeviceClassData> codDataList = mStore.getAll();
        Log.d(TAG, "fillList(): codDataList - " + codDataList);

        if (!codDataList.isEmpty()) {
            final PreferenceGroup codPrefList = findPreference("bluetooth_device_class_list");
            codPrefList.removeAll();

            BluetoothClass bluetoothClass = mAdapter.getBluetoothClass();

            for (BluetoothDeviceClassData codData : codDataList) {
                final BluetoothDeviceClassPreference pref = new BluetoothDeviceClassPreference(getContext());

                pref.setKey(Integer.toString(codData.getId()));
                pref.setTitle(codData.getName());
                pref.setPersistent(false);
                pref.setSelectable(mAdapter.isEnabled());
                pref.setOnPreferenceChangeListener(this);

                String summary = Integer.toHexString(codData.getDeviceClass());
                pref.setSummary(("000000" + summary)
                        .substring(summary.length()));

                Log.d(TAG, "fillList(): codData.getDeviceClass - " + codData.getDeviceClass()
                        + " deviceClass - " + (bluetoothClass != null ? bluetoothClass.getClassOfDevice() : null));

                if (bluetoothClass != null && codData.getDeviceClass() == bluetoothClass.getClassOfDevice()) {
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
