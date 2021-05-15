package com.github.teamjcd.bpp;

public final class BluetoothDeviceClassUtils {
    private BluetoothDeviceClassUtils() {
    }

    public static int parse(String deviceClassHex) throws NumberFormatException {
        return Integer.parseInt(deviceClassHex, 16);
    }

    public static String format(int deviceClass) {
        String deviceClassHex = Integer.toHexString(deviceClass);
        return ("000000" + deviceClassHex).substring(deviceClassHex.length());
    }
}
