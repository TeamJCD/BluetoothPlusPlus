#include <dirent.h>
#include <cstring>
#include <cstdio>
#include <unistd.h>
#include <cstdlib>
#include "utils.h"

pid_t get_pid() {
    DIR* dir = opendir("/proc");

    if (dir == nullptr) {
        ALOGE("get_pid - /proc not found");
        return -1;
    }

    struct dirent* entry;
    while ((entry = readdir(dir)) != nullptr) {
        size_t pid = atoi(entry->d_name);

        if (pid != 0) {
            char file_name[30];
            char temp_name[50];

            snprintf(file_name, 30, "/proc/%zu/cmdline", pid);

            FILE* fp = fopen(file_name, "r");
            if (fp != nullptr) {
                fgets(temp_name, 50, fp);
                fclose(fp);

                if (strcmp("com.android.bluetooth", temp_name) == 0) {
                    ALOGD("get_pid - Bluetooth process found, pid: %zu", pid);
                    return pid;
                }
            }
        }
    }

    ALOGE("get_pid - Bluetooth process not found");
    return -1;
}

long get_module_base_addr(pid_t pid, const char* module_name) {
    if (pid == -1) {
        return 0;
    }

    long base_addr_long = 0;

    char* file_name = (char*) calloc(50, sizeof(char));
    snprintf(file_name, 50, "/proc/%d/maps", pid);

    FILE* fp = fopen(file_name, "r");
    free(file_name);

    char line[512];
    if (fp != nullptr) {
        while(fgets(line, 512, fp) != nullptr) {
            if (strstr(line, module_name) != nullptr) {
                char* base_addr = strtok(line, "-");
                base_addr_long = strtoul(base_addr, nullptr, 16);
                break;
            }
        }

        fclose(fp);
    }

    return base_addr_long;
}

long get_remote_function_addr(pid_t remote_pid, const char* module_name, long local_function_addr) {
    long remote_base_addr = get_module_base_addr(remote_pid, module_name);
    long local_base_addr = get_module_base_addr(getpid(), module_name);

    if (remote_base_addr == 0 || local_base_addr == 0) {
        return 0;
    }

    return local_function_addr + (remote_base_addr - local_base_addr);
}
