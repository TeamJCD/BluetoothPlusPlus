package com.github.teamjcd.bpp;


public class BluetoothDeviceClassPreferenceController {
//        extends BasePreferenceController implements
//        LifecycleObserver, OnStart, OnStop {
//    private BluetoothAdapter mAdapter;
//    private BluetoothDeviceClassStore mStore;
//
//    private static final String TAG = "BluetoothDeviceClassPrefCtrl";
//
//    private Preference mPreference;
//
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                saveInitialValue();
//                updateState(mPreference);
//            }
//        }
//    };
//
//    public BluetoothDeviceClassPreferenceController(Context context, String preferenceKey) {
//        super(context, preferenceKey);
//
//        mAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mAdapter == null) {
//            Log.e(TAG, "Bluetooth is not supported on this device");
//            return;
//        }
//
//        mStore = BluetoothDeviceClassStore.getBluetoothDeviceClassStore(context);
//    }
//
//    @Override
//    public void displayPreference(PreferenceScreen screen) {
//        super.displayPreference(screen);
//        mPreference = screen.findPreference(getPreferenceKey());
//    }
//
//    @Override
//    public void onStart() {
//        final IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        mContext.registerReceiver(mReceiver, intentFilter);
//        saveInitialValue();
//    }
//
//    @Override
//    public void onStop() {
//        mContext.unregisterReceiver(mReceiver);
//    }
//
//    @Override
//    public void updateState(final Preference preference) {
//        preference.setSummary(getSummary());
//        preference.setVisible(mAdapter != null && mAdapter.isEnabled());
//    }
//
//    @Override
//    public CharSequence getSummary() {
//        if (mStore == null) {
//            return null;
//        }
//
//        BluetoothClass bluetoothClass = mAdapter.getBluetoothClass();
//        int deviceClass = bluetoothClass.getClassOfDevice();
//
//        for (BluetoothDeviceClassData codData : mStore.getAll()) {
//            if (codData.getDeviceClass() == deviceClass) {
//                return codData.getName();
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public int getAvailabilityStatus() {
//        return mAdapter != null ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
//    }
//
//    @Override
//    public boolean handlePreferenceTreeClick(final Preference preference) {
//        if (getPreferenceKey().equals(preference.getKey())) {
//            final Intent intent = new Intent(BluetoothDeviceClassSettings.ACTION_BLUETOOTH_DEVICE_CLASS_SETTINGS);
//            intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_AS_SUBSETTING, true);
//            mContext.startActivity(intent);
//            return true;
//        }
//
//        return false;
//    }
//
//    @SuppressLint("NewApi")
//    private void saveInitialValue() {
//        BluetoothDeviceClassData defaultClass = mStore.getDefault();
//        Log.d(TAG, "saveInitialValue(): defaultClass - " + defaultClass);
//        if (defaultClass == null) {
//            BluetoothClass bluetoothClass = mAdapter.getBluetoothClass();
//            Log.d(TAG, "saveInitialValue(): bluetoothClass - " + bluetoothClass);
//            mStore.saveDefault(new BluetoothDeviceClassData(
//                    "Default",
//                    bluetoothClass.getClassOfDevice()
//            ));
//        }
//    }
}
