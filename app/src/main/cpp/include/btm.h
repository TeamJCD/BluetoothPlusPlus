#ifndef BLUETOOTH_BTM_H
#define BLUETOOTH_BTM_H

#include <sys/types.h>

#define DEV_CLASS_LEN 3

typedef uint8_t DEV_CLASS[DEV_CLASS_LEN];

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

long call_BTM_ReadDeviceClass(pid_t pid, long so_handle, DEV_CLASS* dest);
long call_BTM_SetDeviceClass(pid_t pid, long so_handle, DEV_CLASS* dev_class);

#endif //BLUETOOTH_BTM_H
