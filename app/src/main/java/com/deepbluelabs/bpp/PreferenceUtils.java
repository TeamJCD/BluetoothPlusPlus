package com.deepbluelabs.bpp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private static final String PREFERENCE_NAME = "BLUETOOTH_PLUS_PLUS";
    private static final String BLUETOOTH_DEVICE_CLASS_KEY = "BLUETOOTH_DEVICE_CLASS_KEY";

    public static int getDefaultBluetoothDeviceClass(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(BLUETOOTH_DEVICE_CLASS_KEY, 0);
    }

    public static void setDefaultBluetoothDeviceClass(Context context, int deviceClass) {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putInt(BLUETOOTH_DEVICE_CLASS_KEY, deviceClass);
        edit.commit();
    }
}
