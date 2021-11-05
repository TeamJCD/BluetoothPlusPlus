#include <android/log.h>

#ifndef BPP_H
#define BPP_H

#define LOG_TAG "BluetoothPlusPlusJni"
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define ALOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ALOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)

typedef enum {
  BT_PROPERTY_BDADDR = 0x2
} bt_property_type_t;

typedef struct {
  int (*get_adapter_property)(bt_property_type_t type);
} bt_interface_t;

const char kLibbluetooth[] = "libbluetooth.so";
const char kBluetoothInterfaceSym[] = "bluetoothInterface";

static const bt_interface_t* interface;

int hal_util_load_bt_library(const bt_interface_t** interface);

#endif //BPP_H
