package com.github.teamjcd.bpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    public static int getBluetoothClassNative() throws InterruptedException, IOException {
        Process process = Runtime.getRuntime().exec("su -c ./bin/bpp get");
        process.waitFor();

        String result = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .readLine();

        return parse(result);
    }

    public static boolean setBluetoothClassNative(int bluetoothClass) throws InterruptedException,
            IOException {
        Process process = Runtime.getRuntime().exec("su -c ./bin/bpp set " + bluetoothClass);
        process.waitFor();

        return process.exitValue() == 0;
    }
}
