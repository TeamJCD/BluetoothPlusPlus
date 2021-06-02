#ifndef BLUETOOTH_UTILS_H
#define BLUETOOTH_UTILS_H

#include <android/log.h>

#define LOG_TAG "bpp"
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

pid_t get_pid();
long get_module_base_addr(pid_t pid, const char* module_name);
long get_remote_function_addr(pid_t remote_pid, const char* module_name, long local_function_addr);

#endif //BLUETOOTH_UTILS_H
