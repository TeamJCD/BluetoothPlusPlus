#ifndef BLUETOOTH_BTM_H
#define BLUETOOTH_BTM_H

#include <sys/types.h>

#define DEV_CLASS_LEN 3

typedef uint8_t DEV_CLASS[DEV_CLASS_LEN];

long call_BTM_SetDeviceClass(pid_t pid, long so_handle, DEV_CLASS* dev_class);

#endif //BLUETOOTH_BTM_H
