#ifndef BLUETOOTH_BLUETOOTHPLUSPLUS_H
#define BLUETOOTH_BLUETOOTHPLUSPLUS_H

#include <cstdint>
#include <sys/types.h>

#if defined(__aarch64__)
    #define LIBC_PATH_OLD           "/system/lib64/libc.so"
    #define LIBC_PATH_NEW           "/apex/com.android.runtime/lib64/bionic/libc.so"
    #define LIBRARY_PATH            "/system/lib64/bluetooth_qti.so"
    #define SIGNATURE_GET_DEV_CLASS ""
    #define SIGNATURE_SET_DEV_CLASS ""
#else
    #define LIBC_PATH_OLD           "/system/lib/libc.so"
    #define LIBC_PATH_NEW           "/apex/com.android.runtime/lib/bionic/libc.so"
    #define LIBRARY_PATH            "/system/lib/libbluetooth.so"
    #define SIGNATURE_GET_DEV_CLASS "\x48\x41\xf2\x38\x51\x78\x44\x00\x68\x08\x44\x70\x47\x00\xbf\x7a\xa7\x1d\x00\x80\xb5\x3b\xf7\x0b\xfe\xc1"
    #define SIGNATURE_SET_DEV_CLASS "\xb5\x04\x46\x0e\x48\x41\xf2\x38\x56\x21\x46\x78\x44\x03\x22\x05\x68\xa8\x19\xce\xf1\x52\xea\x68\xb1\x20\x88\x41\xf2\x3a\x52\xa1\x78\xa9\x54\xa8\x53\x3b\xf7\x25\xfe\x00\x68\x80\x47\x20\xb1\x20\x46\x25\xf0\xba\xf8\x00\x20\x70\xbd\x0c\x20\x70\xbd\x00\xbf\xb8\xa7\x1d\x00\x03\x48\x41"
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
    int setDeviceClass(void *deviceClass);
};

#endif //BLUETOOTH_BLUETOOTHPLUSPLUS_H
