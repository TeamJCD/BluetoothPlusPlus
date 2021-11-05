package com.github.teamjcd.bpp.util;

public abstract class BppUtils {
    public static int parseHex(String hex) throws NumberFormatException {
        return Integer.parseInt(hex, 16);
    }

    public static String formatDeviceClass(int deviceClass) {
        String deviceClassHex = Integer.toHexString(deviceClass);
        return ("000000" + deviceClassHex).substring(deviceClassHex.length());
    }

    public static String formatAddress(int address) {
        return ""; // TODO
    }
}
