#ifndef BLUETOOTH_BTIF_H
#define BLUETOOTH_BTIF_H

#include <cstdint>

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

uint8_t btif_dm_get_adapter_property(bt_property_t* prop);
uint8_t BTM_SetDeviceClass(DEV_CLASS dev_class);

#endif //BLUETOOTH_BTIF_H
