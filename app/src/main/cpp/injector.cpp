#include <cstdio>
#include <cstring>
#include <linux/elf.h>
#include <sys/mman.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include "injector.h"
#include "utils.h"

void getRegisters(pid_t pid, struct pt_regs *regs) {
    #if defined(__aarch64__)
        struct {
            void* ufb;
            size_t len;
        } regsvec = { regs, sizeof(struct pt_regs) };

        ptrace(PTRACE_GETREGSET, pid, NT_PRSTATUS, &regsvec);
    #else
        ptrace(PTRACE_GETREGS, pid, NULL, regs);
    #endif
}

void setRegisters(pid_t pid, struct pt_regs *regs) {
    #if defined(__aarch64__)
        struct {
            void* ufb;
            size_t len;
        } regsvec = { regs, sizeof(struct pt_regs) };

        ptrace(PTRACE_SETREGSET, pid, NT_PRSTATUS, &regsvec);
    #else
        ptrace(PTRACE_SETREGS, pid, NULL, regs);
    #endif
}

void restart(pid_t pid) {
    ptrace(PTRACE_CONT, pid, NULL, NULL);
}

int injector::attach(pid_t pid) {
    if (pid == -1) {
        return -1;
    }

    if (ptrace(PTRACE_ATTACH, pid, NULL, NULL) < 0) {
        perror(nullptr);
        return -1;
    }

    waitpid(pid, nullptr, WUNTRACED);

    ALOGD("injector::attach - Attached to process %d", pid);
    return 0;
}

int injector::detach(pid_t pid) {
    if (pid == -1) {
        return -1;
    }

    if (ptrace(PTRACE_DETACH, pid, NULL, NULL) < 0) {
        perror(nullptr);
        return -1;
    }

    ALOGD("injector::detach - Detached from process %d", pid);
    return 0;
}

void injector::write(pid_t pid, uint8_t *addr, uint8_t *data, size_t size) {
    const size_t wordSize = sizeof(long);
    int mod = size % wordSize;
    int loopCount = size / wordSize;

    uint8_t* tempAddress = addr;
    uint8_t* tempData = data;

    for (int i = 0; i < loopCount; ++i) {
        ptrace(PTRACE_POKEDATA, pid, tempAddress, *((long*) tempData));
        tempAddress += wordSize;
        tempData += wordSize;
    }

    if (mod > 0) {
        long val = ptrace(PTRACE_PEEKDATA, pid, tempAddress, NULL);
        auto* p = (uint8_t*) &val;

        for(int i = 0; i < mod; ++i) {
            *p = *(tempData);
            ++p;
            ++tempData;
        }

        ptrace(PTRACE_POKEDATA, pid, tempAddress, val);
    }

    ALOGD("injector::write - Wrote %zu bytes to %p process %d", size, (void*) addr, pid);
}

long injector::callRemoteFunction(pid_t pid, long remoteFunctionAddress, long *args, size_t argc) {
    #if defined(__aarch64__)
        #define REGS_ARG_NUM 6
    #else
        #define REGS_ARG_NUM 4
    #endif

    struct pt_regs registers{};
    struct pt_regs backupRegisters{};

    getRegisters(pid, &registers);
    memcpy(&backupRegisters, &registers, sizeof(struct pt_regs));

    for (int i = 0; i < argc && i < REGS_ARG_NUM; ++i) {
        registers.uregs[i] = args[i];
    }

    if (argc > REGS_ARG_NUM) {
        registers.ARM_sp -= (argc - REGS_ARG_NUM) * sizeof(long);
        long* data = args + REGS_ARG_NUM;
        write(pid, (uint8_t*) registers.ARM_sp, (uint8_t*) data, (argc - REGS_ARG_NUM) * sizeof(long));
    }

    registers.ARM_lr = 0;
    registers.ARM_pc = remoteFunctionAddress;

    #if !defined(__aarch64__)
        if (registers.ARM_pc & 1) {
            registers.ARM_pc &= (~1u);
            registers.ARM_cpsr |= CPSR_T_MASK;
        } else {
            registers.ARM_cpsr &= ~CPSR_T_MASK;
        }
    #endif

    setRegisters(pid, &registers);
    restart(pid);

    waitpid(pid, nullptr, WUNTRACED);

    getRegisters(pid, &registers);
    setRegisters(pid, &backupRegisters);

    ALOGD("Ptrace::callRemoteFunction - Call remote function %lx with %zu arguments, return value is %llx",
          remoteFunctionAddress, argc, (long long) registers.ARM_r0);

    return registers.ARM_r0;
}

long injector::callMmap(pid_t pid, size_t length) {
    long params[6];
    params[0] = 0;
    params[1] = length;
    params[2] = PROT_READ | PROT_WRITE;
    params[3] = MAP_PRIVATE | MAP_ANONYMOUS;
    params[4] = 0;
    params[5] = 0;

    long remoteFunctionAddress = utils::getRemoteFunctionAddress(pid, utils::getLibcPath(), ((long) (void*) mmap));
    return callRemoteFunction(pid, remoteFunctionAddress, params, 6);
}

long injector::callMunmap(pid_t pid, long address, size_t length) {
    long params[2];
    params[0] = address;
    params[1] = length;

    long remoteFunctionAddress = utils::getRemoteFunctionAddress(pid, utils::getLibcPath(), ((long) (void*) munmap));
    return callRemoteFunction(pid, remoteFunctionAddress, params, 2);
}
