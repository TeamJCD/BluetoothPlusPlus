#ifndef BLUETOOTH_BPP_JNI_H
#define BLUETOOTH_BPP_JNI_H

#define DEV_CLASS_LEN 3

typedef enum {
    /**
     * Description - Bluetooth Class of Device as found in Assigned Numbers
     * Access mode - Only GET.
     * Data type   - uint32_t.
     */
    BT_PROPERTY_CLASS_OF_DEVICE = 0x4
} bt_property_type_t;

typedef struct {
    bt_property_type_t type;
    int len;
    void* val;
} bt_property_t;

typedef uint8_t DEV_CLASS[DEV_CLASS_LEN];

static int pid = -1;
static bool qti = false;

#endif //BLUETOOTH_BPP_JNI_H
