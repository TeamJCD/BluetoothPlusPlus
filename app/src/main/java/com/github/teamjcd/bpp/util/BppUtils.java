package com.github.teamjcd.bpp.util;

public abstract class BppUtils {
    public static int parseDeviceClass(String deviceClassHex) throws NumberFormatException {
        return Integer.parseInt(deviceClassHex, 16);
    }

    public static String formatDeviceClass(int deviceClass) {
        String deviceClassHex = Integer.toHexString(deviceClass);
        return ("000000" + deviceClassHex).substring(deviceClassHex.length());
    }
}
