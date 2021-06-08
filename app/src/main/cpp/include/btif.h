#ifndef BLUETOOTH_BTIF_H
#define BLUETOOTH_BTIF_H

#include <sys/types.h>

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

long call_btif_dm_get_adapter_property(pid_t pid, long so_handle, bt_property_t* prop);

#endif //BLUETOOTH_BTIF_H
