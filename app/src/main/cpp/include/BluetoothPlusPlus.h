#ifndef BLUETOOTH_BLUETOOTHPLUSPLUS_H
#define BLUETOOTH_BLUETOOTHPLUSPLUS_H

#include <cstdint>
#include <sys/types.h>

#if defined(__aarch64__)
    #define LIBRARY_PATH            "/system/lib64/bluetooth_qti.so"
    #define SIGNATURE_GET_DEV_CLASS ""
#else
    #define LIBRARY_PATH            "/system/lib/libbluetooth.so"
    #define SIGNATURE_GET_DEV_CLASS "\x48\x41\xf2\x38\x51\x78\x44\x00\x68\x08\x44\x70\x47\x00\xbf\x7a\xa7\x1d\x00\x80\xb5\x3b\xf7\x0b\xfe\xc1"
#endif

#define DEV_CLASS_LEN 3

typedef uint8_t dev_class_t[DEV_CLASS_LEN];

class BluetoothPlusPlus {
private:
    pid_t pid;
    long remoteBaseAddress;
    long scanSize;
    char* memory;

public:
    BluetoothPlusPlus();
    ~BluetoothPlusPlus();
    int readDeviceClass(void *deviceClass);
};

#endif //BLUETOOTH_BLUETOOTHPLUSPLUS_H
