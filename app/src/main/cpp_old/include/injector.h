#ifndef BLUETOOTH_INJECTOR_H
#define BLUETOOTH_INJECTOR_H

#include <sys/types.h>

#if defined(__aarch64__)
#define LIBC_PATH_OLD		"/system/lib64/libc.so"
#define LIBC_PATH_NEW		"/apex/com.android.runtime/lib64/bionic/libc.so"
#define LIBRARY_PATH        "/system/lib64/libbluetooth_qti.so"
#define LINKER_PATH_OLD		"/system/lib64/libdl.so"
#define LINKER_PATH_NEW		"/apex/com.android.runtime/lib64/bionic/libdl.so"
#define VNDK_LIB_PATH		"/system/lib64/libRS.so"
#else
#define LIBC_PATH_OLD		"/system/lib/libc.so"
#define LIBC_PATH_NEW		"/apex/com.android.runtime/lib/bionic/libc.so"
#define LIBRARY_PATH        "/system/lib/libbluetooth.so" // TODO change to libbluetooth_qti.so after testing
#define LINKER_PATH_OLD		"/system/lib/libdl.so"
#define LINKER_PATH_NEW		"/apex/com.android.runtime/lib/bionic/libdl.so"
#define VNDK_LIB_PATH		"/system/lib/libRS.so"
#endif

const char* get_linker_path();

long call_mmap(pid_t pid, size_t length);
long call_munmap(pid_t pid, long addr, size_t length);

long call_dlopen(pid_t pid);
long call_dlclose(pid_t pid, long so_handle);

#endif //BLUETOOTH_INJECTOR_H
