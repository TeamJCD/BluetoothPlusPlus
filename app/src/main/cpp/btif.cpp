#include <dlfcn.h>
#include <cstring>
#include "btif.h"
#include "injector.h"
#include "ptrace.h"
#include "utils.h"

long call_btif_dm_get_adapter_property(pid_t pid, long so_handle, bt_property_t* prop) {
    const char* symbol = "btif_dm_get_adapter_property";

    long function_addr = get_remote_function_addr(pid, get_linker_path(), ((long) (void*) dlsym));
    ALOGD("call_dlsym - function_addr: %lx, pid: %d, so_handle: %lx, symbol: %s", function_addr, pid, so_handle, symbol);

    long mmap_ret_symbol = call_mmap(pid, 0x400);
    long mmap_ret_prop = call_mmap(pid, 0x400);

    ptrace_write(pid, (uint8_t*) mmap_ret_symbol, (uint8_t*) symbol, strlen(symbol) + 1);
    ptrace_write(pid, (uint8_t*) mmap_ret_prop, (uint8_t*) prop, sizeof(bt_property_t));

    long params[3];
    params[0] = so_handle;
    params[1] = mmap_ret_symbol;
    params[2] = mmap_ret_prop;

    long ret = call_remote_function(pid, function_addr, params, 3);

    ptrace_read(pid, (long*) &prop, (uint8_t*) mmap_ret_prop, sizeof(bt_property_t) / sizeof(long));

    call_munmap(pid, mmap_ret_symbol, 0x400);
    call_munmap(pid, mmap_ret_prop, 0x400);

    return ret;
}
