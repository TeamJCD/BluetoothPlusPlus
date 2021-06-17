#ifndef BLUETOOTH_UTILS_H
#define BLUETOOTH_UTILS_H

#include <android/log.h>
#include <sys/types.h>

#define LOG_TAG "bpp"
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

class Utils {
public:
    static pid_t getProcessId(const char* procName);
    static long getModuleBaseAddress(pid_t pid, const char* moduleName);
};

#endif //BLUETOOTH_UTILS_H
