#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <exception>
#include "BluetoothPlusPlus.h"

using namespace std;

int main(int argc, char const* argv[]) {
    if (argc < 2 || argc > 3 ||
        (argc == 2 && strcmp(argv[1], "get") != 0) ||
        (argc == 3 && strcmp(argv[1], "set") != 0)) {
        printf("Usage: %s [get|set] [device class]\n", argv[0]);
        return -1;
    }

    try {
        BluetoothPlusPlus bpp;
        if (strcmp(argv[1], "get") == 0) {
            dev_class_t deviceClass;

            int ret;
            if ((ret = bpp.readDeviceClass(deviceClass)) == 0) {
                printf("%02x%02x%02x\n", deviceClass[0], deviceClass[1], deviceClass[2]);
            }

            return ret;
        } else if (strcmp(argv[1], "set") == 0) {
            dev_class_t deviceClass;
            memcpy(deviceClass, argv[2], DEV_CLASS_LEN);
            return bpp.setDeviceClass(deviceClass);
        }
    } catch(const exception &e) {
        printf("%s\n", e.what());
    }

    return -1;
}
