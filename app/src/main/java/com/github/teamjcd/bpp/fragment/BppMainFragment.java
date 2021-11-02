package com.github.teamjcd.bpp.fragment;

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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import com.github.teamjcd.bpp.R;
import com.github.teamjcd.bpp.activity.BppDeviceClassEditorActivity;
import com.github.teamjcd.bpp.db.BppDeviceClassStore;
import com.github.teamjcd.bpp.preference.BppDeviceClassPreference;
import com.github.teamjcd.bpp.provider.BppDeviceClassColumns;
import com.github.teamjcd.bpp.util.BppUtils;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.github.teamjcd.bpp.content.BppDeviceClassContentProvider.DEVICE_CLASS_URI;
import static com.github.teamjcd.bpp.db.BppDeviceClassStore.getBluetoothDeviceClassStore;
import static com.github.teamjcd.bpp.fragment.BppDeviceClassEditorFragment.URI_EXTRA;

public class BppMainFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_EDIT = "com.github.teamjcd.bpp.fragment.BppMainFragment.ACTION_BLUETOOTH_DEVICE_CLASS_EDIT";
    public static final String ACTION_BLUETOOTH_DEVICE_CLASS_INSERT = "com.github.teamjcd.bpp.fragment.BppMainFragment.ACTION_BLUETOOTH_DEVICE_CLASS_INSERT";

    private static final String TAG = BppMainFragment.class.getName();

    private static final int FALLBACK_DEFAULT_BLUETOOTH_DEVICE_CLASS = BppUtils.parseDeviceClass("5a020c");

    private static final int MENU_DEVICE_CLASS_NEW = Menu.FIRST;
    private static final int MENU_MAC_ADDR_NEW = MENU_DEVICE_CLASS_NEW + 1;

    private IntentFilter mIntentFilter;
    private BluetoothAdapter mAdapter;
    private BppDeviceClassStore mStore;

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
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            saveInitialValue();
                            fillList();
                        }
                    }).launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        } else {
            saveInitialValue();
        }

        fillList();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_bpp_main);
    }


    @Override
    public void onResume() {
        super.onResume();

        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.registerReceiver(mReceiver, mIntentFilter);
        }

        fillList();
    }

    @Override
    public void onPause() {
        super.onPause();

        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(0, MENU_DEVICE_CLASS_NEW, 0,
                getResources().getString(R.string.menu_device_class_new))
                .setIcon(R.drawable.ic_add_24);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == MENU_DEVICE_CLASS_NEW) {
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
            BppDeviceClassColumns newDeviceClass = mStore.get(Integer.parseInt((String) newValue));
            if (newDeviceClass != null) {
                return mAdapter.setBluetoothClass(new BluetoothClass(newDeviceClass.getDeviceClass()));
            }
        }

        return true;
    }

    private void saveInitialValue() {
        BppDeviceClassColumns defaultClass = mStore.getDefault();
        Log.d(TAG, "saveInitialValue(): defaultClass - " + defaultClass);
        if (defaultClass == null) {
            BluetoothClass bluetoothClass = mAdapter.getBluetoothClass();
            Log.d(TAG, "saveInitialValue(): bluetoothClass - " + bluetoothClass);
            mStore.saveDefault(new BppDeviceClassColumns(
                    "Default",
                    bluetoothClass != null ?
                            bluetoothClass.getClassOfDevice() :
                            FALLBACK_DEFAULT_BLUETOOTH_DEVICE_CLASS
            ));
        }
    }

    private void fillList() {
        List<BppDeviceClassColumns> codDataList = mStore.getAll();
        Log.d(TAG, "fillList(): codDataList - " + codDataList);

        if (!codDataList.isEmpty()) {
            final PreferenceGroup codPrefList = findPreference("bluetooth_device_class_list");

            if (codPrefList != null) {
                codPrefList.removeAll();

                BluetoothClass bluetoothClass = mAdapter.getBluetoothClass();

                for (BppDeviceClassColumns codData : codDataList) {
                    final BppDeviceClassPreference pref = new BppDeviceClassPreference(getContext());

                    pref.setKey(Integer.toString(codData.getId()));
                    pref.setTitle(codData.getName());
                    pref.setPersistent(false);
                    pref.setSelectable(mAdapter.isEnabled());
                    pref.setOnPreferenceChangeListener(this);
                    pref.setIconSpaceReserved(false);

                    pref.setSummary(BppUtils.formatDeviceClass(codData.getDeviceClass()));

                    Log.d(TAG, "fillList(): codData.getDeviceClass - " + codData.getDeviceClass()
                            + " deviceClass - " + (bluetoothClass != null ? bluetoothClass.getClassOfDevice() : null));

                    if (bluetoothClass != null && codData.getDeviceClass() == bluetoothClass.getClassOfDevice()) {
                        pref.setChecked();
                    } else if (bluetoothClass == null && codData.getDeviceClass() == FALLBACK_DEFAULT_BLUETOOTH_DEVICE_CLASS) {
                        pref.setChecked();
                    }

                    codPrefList.addPreference(pref);
                }
            }
        }
    }

    private void addNewBluetoothDeviceClass() {
        Intent intent = new Intent(getContext(), BppDeviceClassEditorActivity.class);
        intent.setAction(ACTION_BLUETOOTH_DEVICE_CLASS_INSERT);
        intent.putExtra(URI_EXTRA, DEVICE_CLASS_URI);
        startActivity(intent);
    }
}
