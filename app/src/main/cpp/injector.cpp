#include <jni.h>
#include <sys/mman.h>
#include <sys/system_properties.h>
#include <sys/types.h>
#include <dlfcn.h>
#include <cstring>
#include "bpp_jni.h"
#include "injector.h"
#include "ptrace.h"
#include "utils.h"

static int android_os_version = -1;

int get_os_version() {
    if (android_os_version != -1) {
        return android_os_version;
    }

    char os_version[PROP_VALUE_MAX + 1];
    __system_property_get("ro.build.version.release", os_version);
    android_os_version = atoi(os_version);

    return android_os_version;
}

const char* get_linker_path() {
    if (get_os_version() >= 10) {
        return LINKER_PATH_NEW;
    } else {
        return LINKER_PATH_OLD;
    }
}

const char* get_libc_path() {
    if (get_os_version() >= 10) {
        return LIBC_PATH_NEW;
    } else {
        return LIBC_PATH_OLD;
    }
}

long call_mmap(pid_t pid, size_t length) {
    long params[6];
    params[0] = 0;
    params[1] = length;
    params[2] = PROT_READ | PROT_WRITE;
    params[3] = MAP_PRIVATE | MAP_ANONYMOUS;
    params[4] = 0;
    params[5] = 0;

    long function_addr = get_remote_function_addr(pid, get_libc_path(), ((long) (void*) mmap));
    return call_remote_function(pid, function_addr, params, 6);
}

long call_munmap(pid_t pid, long addr, size_t length) {
    long params[2];
    params[0] = addr;
    params[1] = length;

    long function_addr = get_remote_function_addr(pid, get_libc_path(), ((long) (void*) munmap));
    return call_remote_function(pid, function_addr, params, 2);
}

long call_dlopen(pid_t pid) {
    long function_addr = get_remote_function_addr(pid, get_linker_path(), ((long) (void*) dlopen));
    ALOGD("call_dlopen - function_addr: %lx", function_addr);

    long mmap_ret = call_mmap(pid, 0x400);

    ptrace_write(pid, (uint8_t*) mmap_ret, (uint8_t*) LIBRARY_PATH, strlen(LIBRARY_PATH) + 1);

    long vndk_return_addr = get_module_base_addr(pid, VNDK_LIB_PATH);

    long params[2];
    params[0] = mmap_ret;
    params[1] = RTLD_NOW | RTLD_LOCAL;

    long ret = call_remote_function_from_namespace(pid, function_addr, vndk_return_addr, params, 2);

    call_munmap(pid, mmap_ret, 0x400);

    return ret;
}

long call_dlclose(pid_t pid, long so_handle) {
    long function_addr = get_remote_function_addr(pid, get_linker_path(), ((long) (void*) dlclose));
    ALOGD("call_dlclose - function_addr: %lx, pid: %d, so_handle: %lx", function_addr, pid, so_handle);

    long params[1];
    params[0] = so_handle;

    return call_remote_function(pid, function_addr, params, 1);
}
