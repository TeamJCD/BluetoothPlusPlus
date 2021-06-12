#include <dlfcn.h>
#include <cstring>
#include "btm.h"
#include "injector.h"
#include "ptrace.h"
#include "utils.h"

long call_BTM_ReadDeviceClass(pid_t pid, long so_handle, DEV_CLASS* dest) {
    const char* symbol = "BTM_ReadDeviceClass";

    long function_addr = get_remote_function_addr(pid, get_linker_path(), ((long) (void*) dlsym));
    ALOGD("call_BTM_ReadDeviceClass - function_addr: %lx, pid: %d, so_handle: %lx, symbol: %s", function_addr, pid, so_handle, symbol);

    long mmap_ret_symbol = call_mmap(pid, 0x400);

    ptrace_write(pid, (uint8_t*) mmap_ret_symbol, (uint8_t*) symbol, strlen(symbol) + 1);

    long params[2];
    params[0] = so_handle;
    params[1] = mmap_ret_symbol;

    long ret = call_remote_function(pid, function_addr, params, 2); // TODO write return value of BTM_ReadDeviceClass in dest
    ALOGD("ret: %ld", ret);

    call_munmap(pid, mmap_ret_symbol, 0x400);

    return ret;
}

long call_BTM_SetDeviceClass(pid_t pid, long so_handle, DEV_CLASS* dev_class) {
    const char* symbol = "BTM_SetDeviceClass";

    long function_addr = get_remote_function_addr(pid, get_linker_path(), ((long) (void*) dlsym));
    ALOGD("call_BTM_SetDeviceClass - function_addr: %lx, pid: %d, so_handle: %lx, symbol: %s", function_addr, pid, so_handle, symbol);

    long mmap_ret = call_mmap(pid, 0x400);

    ptrace_write(pid, (uint8_t*) mmap_ret, (uint8_t*) symbol, strlen(symbol) + 1);
    //ptrace_write(pid, (uint8_t*) mmap_ret, (uint8_t*) dev_class, DEV_CLASS_LEN);

    long params[2];
    params[0] = so_handle;
    params[1] = mmap_ret;

    long ret = call_remote_function(pid, function_addr, params, 2);

    call_munmap(pid, mmap_ret, 0x400);

    return ret;
}
