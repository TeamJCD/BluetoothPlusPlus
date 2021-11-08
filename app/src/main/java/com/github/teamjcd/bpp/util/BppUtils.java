package com.github.teamjcd.bpp.util;

import java.util.Optional;

public abstract class BppUtils {
    public static Long parseHex(String hex) throws NumberFormatException {
        return Optional.ofNullable(hex)
                .map(s -> s.replaceAll("[^\\dA-F]", ""))
                .map(s -> Long.parseLong(s, 16))
                .orElse(null);
    }

    public static String formatDeviceClass(long deviceClass) {
        String deviceClassHex = Long.toHexString(deviceClass);
        return ("000000" + deviceClassHex).substring(deviceClassHex.length());
    }

    public static String formatAddress(long address) {
        return ""; // TODO
    }
}
