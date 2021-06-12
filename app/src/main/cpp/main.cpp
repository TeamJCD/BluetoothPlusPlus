#include <sys/types.h>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include "btm.h"
#include "injector.h"
#include "ptrace.h"
#include "utils.h"

int main(int argc, char const* argv[]) {
    if (argc < 2 || argc > 3 ||
            (argc == 2 && strcmp(argv[1], "get") != 0) ||
            (argc == 3 && strcmp(argv[1], "set") != 0)) {
        printf("Usage: %s [get|set] [device class]\n", argv[0]);
        return -1;
    }

    const bool qti = true; // TODO check whether qti bt stack is used or not

    if (!qti) {
        ALOGD("No QTI Bluetooth Stack found, nothing to do.");
        return 0;
    }

    pid_t pid = get_pid();

    ptrace_attach(pid);

    long so_handle = call_dlopen(pid);

    if (!so_handle) {
        ALOGE("Injection failed");
        return -1;
    }

    long result = -1;

    if (strcmp(argv[1], "get") == 0) {
        ALOGD("Getting Device Class");

        DEV_CLASS dev_class;

        result = call_BTM_ReadDeviceClass(pid, so_handle, dev_class);

        if (result == 0) {
            ALOGD("Retrieved Device Class: 0x%02x%02x%02x", dev_class[0], dev_class[1],
                  dev_class[2]);

            printf("%02x%02x%02x", dev_class[0], dev_class[1], dev_class[2]);
        }
    } else if (strcmp(argv[1], "set") == 0) {
        DEV_CLASS* dev_class = nullptr;
        memcpy(dev_class, argv[2], DEV_CLASS_LEN);

        ALOGD("Setting Device Class: 0x%2s%2s%2s", dev_class[0], dev_class[1], dev_class[2]);

        result = call_BTM_SetDeviceClass(pid, so_handle, dev_class);
    }

    call_dlclose(pid, so_handle);

    ptrace_detach(pid);

    return result == 0 ? 0 : -1;
}
