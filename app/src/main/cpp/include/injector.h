#ifndef BLUETOOTH_INJECTOR_H
#define BLUETOOTH_INJECTOR_H

#include <sys/types.h>

#if defined(__aarch64__)
    #define pt_regs  user_pt_regs
    #define uregs    regs
    #define ARM_r0   regs[0]
    #define ARM_lr   regs[30]
    #define ARM_sp   sp
    #define ARM_pc   pc
    #define ARM_cpsr pstate
#endif

#define CPSR_T_MASK (1u << 5)

namespace injector {
    int attach(pid_t pid);
    int detach(pid_t pid);
    void write(pid_t pid, uint8_t *addr, uint8_t *data, size_t size);
    long callRemoteFunction(pid_t pid, long remoteFunctionAddress, long* args, size_t argc);
    long callMmap(pid_t pid, size_t length);
    long callMunmap(pid_t pid, long address, size_t length);
}

#endif //BLUETOOTH_INJECTOR_H
