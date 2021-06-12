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

    mkdir -p $MODPATH/system/priv-app/BluetoothPlusPlus/bin
    cp $MODPATH/common/$ARCH_DIR/bpp $MODPATH/system/priv-app/BluetoothPlusPlus/bin
    set_perm $MODPATH/system/priv-app/BluetoothPlusPlus/bin/bpp root root 750
}

install_binary
