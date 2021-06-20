#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <dirent.h>
#include <errno.h>
#include <sys/uio.h>
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

long Utils::getRemoteBaseAddress(pid_t pid, const char *moduleName) {
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

int Utils::readRemoteMemory(pid_t pid, long remoteBaseAddress, char *buf, size_t bufferLength) {
    if (pid == -1) {
        return -1;
    }

    struct iovec local[1];
    local[0].iov_base = calloc(bufferLength, sizeof(char));
    local[0].iov_len = bufferLength;

    struct iovec remote[1];
    remote[0].iov_base = (void*) remoteBaseAddress;
    remote[0].iov_len = bufferLength;

    ssize_t result = process_vm_readv(pid, local, 1, remote, 1, 0);
    if (result < 0) {
        switch (errno) {
            case EINVAL:
                ALOGE("Utils::readRemoteMemory - Error: Invalid arguments");
                break;
            case EFAULT:
                ALOGE("Utils::readRemoteMemory - Error: Unable to access target memory address");
                break;
            case ENOMEM:
                ALOGE("Utils::readRemoteMemory - Error: Unable to allocate memory");
                break;
            case EPERM:
                ALOGE("Utils::readRemoteMemory - Error: Insufficient privileges to target process");
                break;
            case ESRCH:
                ALOGE("Utils::readRemoteMemory - Error: Process does not exist");
                break;
            default:
                ALOGE("Utils::readRemoteMemory - Error: Unknown error occurred");
        }

        return -1;
    }

    buf = (char*) local[0].iov_base;
    return 0;
}
