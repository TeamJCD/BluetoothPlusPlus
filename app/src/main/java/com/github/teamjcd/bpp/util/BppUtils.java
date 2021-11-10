package com.github.teamjcd.bpp.util;

import java.util.Optional;

@SuppressWarnings("squid:S1610")
public abstract class BppUtils {
    private BppUtils() {
    }

    public static Long parseHex(String hex) throws NumberFormatException {
        return Optional.ofNullable(hex)
                .map(s -> s.replaceAll("[^\\dA-F]", ""))
                .map(s -> Long.parseLong(s, 16))
                .orElse(null);
    }

    public static String formatDeviceClass(long deviceClass) {
        return String.format("%06x", deviceClass);
    }

    public static String formatAddress(long address) {
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                (address >> 40) & 0xff,
                (address >> 32) & 0xff,
                (address >> 24) & 0xff,
                (address >> 16) & 0xff,
                (address >> 8) & 0xff,
                address & 0xff);
    }
}
