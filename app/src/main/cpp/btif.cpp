#include <cstring>
#include "btif.h"
#include "utils.h"

void btif_get_device_class(bt_property_t prop) {
    btif_dm_get_adapter_property(&prop); // FIXME linker error
}

void btif_set_device_class(DEV_CLASS dev_class) {
    if (BTM_SetDeviceClass(dev_class) != 0) { // FIXME linker error
        ALOGE("btif_set_device_class - Unable to set dev_class: 0x%2hhu%2hhu%2hhu",
              dev_class[0], dev_class[1], dev_class[2]);
    }
}
