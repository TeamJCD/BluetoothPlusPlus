#include <cerrno>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <dirent.h>
#include <sys/system_properties.h>
#include <sys/uio.h>
#include <unistd.h>
#include "BluetoothPlusPlus.h"
#include "utils.h"

int getOsVersion() {
    if (androidOsVersion != -1) {
        return androidOsVersion;
    }

    char osVersion[PROP_VALUE_MAX + 1];
    __system_property_get("ro.build.version.release", osVersion);
    androidOsVersion = atoi(osVersion);

    return androidOsVersion;
}

const char* utils::getLibcPath() {
    if (getOsVersion() >= 10) {
        return LIBC_PATH_NEW;
    } else {
        return LIBC_PATH_OLD;
    }
}

pid_t utils::getProcessId(const char *processName) {
    if (processName == NULL) {
        return -1;
    }

    DIR *dir = opendir("/proc");
    if (dir == nullptr) {
        ALOGE("utils::getProcessId - /proc not found");
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

                if (strcmp(processName, tempName) == 0) {
                    ALOGD("utils::getProcessId - Process found: %s (pid: %zu)", processName, pid);
                    return pid;
                }
            }
        }
    }

    ALOGE("utils::getProcessId - Process not found: %s", processName);
    return -1;
}

long utils::getRemoteBaseAddress(pid_t pid, const char *libraryPath) {
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
            if (strstr(line, libraryPath) != nullptr) {
                char* baseAddress = strtok(line, "-");
                baseAddressLong = strtoul(baseAddress, nullptr, 16);
                break;
            }
        }

        fclose(fp);
    }

    return baseAddressLong;
}

int utils::readRemoteMemory(pid_t pid, long address, void *buffer, size_t bufferLength) {
    if (pid == -1) {
        return -1;
    }

    struct iovec local[1];
    local[0].iov_base = buffer;
    local[0].iov_len = bufferLength;

    struct iovec remote[1];
    remote[0].iov_base = (void*) address;
    remote[0].iov_len = bufferLength;

    ssize_t result = process_vm_readv(pid, local, 1, remote, 1, 0);
    if (result < 0) {
        switch (errno) {
            case EINVAL:
                ALOGE("utils::readRemoteMemory - Error: Invalid arguments");
                break;
            case EFAULT:
                ALOGE("utils::readRemoteMemory - Error: Unable to access target memory address");
                break;
            case ENOMEM:
                ALOGE("utils::readRemoteMemory - Error: Unable to allocate memory");
                break;
            case EPERM:
                ALOGE("utils::readRemoteMemory - Error: Insufficient privileges to target process");
                break;
            case ESRCH:
                ALOGE("utils::readRemoteMemory - Error: Process does not exist");
                break;
            default:
                ALOGE("utils::readRemoteMemory - Error: Unknown error occurred");
        }

        return -1;
    }

    return 0;
}

long utils::getRemoteFunctionAddress(long scanSize, const char *signature, const char* memory, long remoteBaseAddress) {
    long remoteFunctionAddress = -1;
    bool foundFlag = false;

    for (long i = 0; i < scanSize; ++i) {
        for (int j = 0; j < (long unsigned int) strlen(signature); ++j) {
            foundFlag = signature[j] == memory[i + j] || signature[j] == '?';

            if (!foundFlag) {
                break;
            }
        }

        if (foundFlag) {
            remoteFunctionAddress = remoteBaseAddress + i;
        }
    }

    if (remoteFunctionAddress != -1) {
        ALOGD("utils::getRemoteFunctionAddress - Found remote function address: %lx", remoteFunctionAddress);
    }

    return remoteFunctionAddress;
}

long utils::getRemoteFunctionAddress(pid_t pid, const char* libraryPath, long localFunctionAddress) {
    long remoteBaseAddress = getRemoteBaseAddress(pid, libraryPath);
    long localBaseAddress = getRemoteBaseAddress(getpid(), libraryPath);

    if (remoteBaseAddress == 0 || localBaseAddress == 0) {
        return 0;
    }

    return localFunctionAddress + (remoteBaseAddress - localBaseAddress);
}
