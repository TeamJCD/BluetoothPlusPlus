package com.deepbluelabs.bpp;

import java.util.Arrays;
import java.util.Optional;

public enum BluetoothDeviceClassEnum {
    PHONE("Phone", 0x5A020C),
    GAMEPAD("Gamepad", 0x002508);

    private final String name;
    private final int btClass;

    BluetoothDeviceClassEnum(String name, int btClass) {
        this.name = name;
        this.btClass = btClass;
    }

    public String getName() {
        return name;
    }

    public int getBtClass() {
        return btClass;
    }

    public static Optional<BluetoothDeviceClassEnum> fromDeviceName(String name) {
        return Arrays.stream(BluetoothDeviceClassEnum.values())
                .filter(btClassEnum -> btClassEnum.getName().equals(name))
                .findAny();
    }
}
