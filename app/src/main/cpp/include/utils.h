#ifndef BLUETOOTH_UTILS_H
#define BLUETOOTH_UTILS_H

#include <android/log.h>
#include <sys/types.h>

#define LOG_TAG "bpp"
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static int androidOsVersion = -1;

namespace utils {
    const char* getLibcPath();
    pid_t getProcessId(const char *processName);
    long getRemoteBaseAddress(pid_t pid, const char *libraryPath);
    int readRemoteMemory(pid_t pid, long address, void *buffer, size_t bufferLength);
    long getRemoteFunctionAddress(long scanSize, char* signature, const char* memory, long remoteBaseAddress);
    long getRemoteFunctionAddress(pid_t pid, const char* libraryPath, long localFunctionAddress);
}

#endif //BLUETOOTH_UTILS_H
