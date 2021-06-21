install_binary() {
    ui_print "- Installing binary"

    if [[ $ARCH == "arm" ]]; then
        ARCH_DIR="armeabi-v7a";
    elif [[ $ARCH == "arm64" ]]; then
        ARCH_DIR="arm64-v8a";
    else
        ui_print "- Architecture $ARCH not supported"
        abort
    fi

    mkdir -p "$MODPATH/system/priv-app/BluetoothPlusPlus/bin"
    cp "$MODPATH/bin/$ARCH_DIR/bpp_qti" "$MODPATH/system/priv-app/BluetoothPlusPlus/bin"
    set_perm "$MODPATH/system/priv-app/BluetoothPlusPlus/bin/bpp_qti" root root 750
}

if [[ -f "/system/system_ext/lib/libbluetooth_qti.so" || -f "/system/system_ext/lib64/libbluetooth_qti.so" ]]; then
    install_binary
fi
