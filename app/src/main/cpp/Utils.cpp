#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <dirent.h>
#include <unistd.h>
#include "Utils.h"

pid_t Utils::getProcessId(const char *procName) {
    if (procName == NULL) {
        return -1;
    }

    DIR* dir = opendir("/proc");

    if (dir == nullptr) {
        ALOGE("Utils::getProcessId - /proc not found");
        return -1;
    }

    struct dirent* entry;
    while ((entry = readdir(dir)) != nullptr) {
        size_t pid = atoi(entry->d_name);

        if (pid != 0) {
            char fileName[30];
            char tempName[50];

            snprintf(fileName, 30, "/proc/%zu/cmdline", pid);

            FILE* fp = fopen(fileName, "r");
            if (fp != nullptr) {
                fgets(tempName, 50, fp);
                fclose(fp);

                if (strcmp(procName, tempName) == 0) {
                    ALOGD("Utils::getProcessId - Process found: %s (pid: %zu)", procName, pid);
                    return pid;
                }
            }
        }
    }

    ALOGE("Utils::getProcessId - Process not found: %s", procName);
    return -1;
}

long Utils::getModuleBaseAddress(pid_t pid, const char *moduleName) {
    if (pid == -1) {
        return 0;
    }

    long baseAddressLong = 0;

    char* fileName = (char*) calloc(50, sizeof(char));
    snprintf(fileName, 50, "/proc/%d/maps", pid);

    FILE* fp = fopen(fileName, "r");
    free(fileName);

    char line[512];
    if (fp != nullptr) {
        while(fgets(line, 512, fp) != nullptr) {
            if (strstr(line, moduleName) != nullptr) {
                char* baseAddress = strtok(line, "-");
                baseAddressLong = strtoul(baseAddress, nullptr, 16);
                break;
            }
        }

        fclose(fp);
    }

    return baseAddressLong;
}
