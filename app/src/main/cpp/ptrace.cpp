#include <linux/elf.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <unistd.h>
#include <cstdio>
#include <utils.h>
#include <cstring>
#include "ptrace.h"

#if defined(__aarch64__)
#define pt_regs  user_pt_regs
#define uregs    regs
#define ARM_r0   regs[0]
#define ARM_lr   regs[30]
#define ARM_sp   sp
#define ARM_pc   pc
#define ARM_cpsr pstate
#endif

static void ptrace_get_regs(pid_t pid, struct pt_regs *regs);
static void ptrace_set_regs(pid_t pid, struct pt_regs *regs);
static void ptrace_cont(pid_t pid);

int ptrace_attach(pid_t pid) {
    if (pid == -1) {
        return -1;
    }

    if (ptrace(PTRACE_ATTACH, pid, NULL, NULL) < 0) {
        perror(nullptr);
        return -1;
    }

    waitpid(pid, nullptr, WUNTRACED);

    ALOGD("attached to process %d", pid);
    return 0;
}

int ptrace_detach(pid_t pid) {
    if (pid == -1) {
        return -1;
    }

    if (ptrace(PTRACE_DETACH, pid, NULL, NULL) < 0) {
        perror(nullptr);
        return -1;
    }

    ALOGD("detached from process %d", pid);
    return 0;
}

void ptrace_write(pid_t pid, uint8_t* addr, uint8_t* data, size_t size) {
    const size_t WORD_SIZE = sizeof(long);
    int mod = size % WORD_SIZE;
    int loop_count = size / WORD_SIZE;

    uint8_t* tmp_addr = addr;
    uint8_t* tmp_data = data;

    for (int i = 0; i < loop_count; ++i) {
        ptrace(PTRACE_POKEDATA, pid, tmp_addr, *((long*) tmp_data));
        tmp_addr += WORD_SIZE;
        tmp_data += WORD_SIZE;
    }

    if (mod > 0) {
        long val = ptrace(PTRACE_PEEKDATA, pid, tmp_addr, NULL);
        auto* p = (uint8_t*) &val;

        for(int i = 0; i < mod; ++i) {
            *p = *(tmp_data);
            p++;
            tmp_data++;
        }

        ptrace(PTRACE_POKEDATA, pid, tmp_addr, val);
    }

    ALOGD("write %zu bytes to %p process %d", size, (void*) addr, pid);
}

void ptrace_read(pid_t pid, uint8_t* dest, uint8_t* addr, size_t size) {
    for (int i = 0; i < size; ++i) {
        dest[i] = ptrace(PTRACE_PEEKDATA, pid, addr, NULL);
    }
}

static void ptrace_get_regs(pid_t pid, struct pt_regs *regs) {
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

static void ptrace_set_regs(pid_t pid, struct pt_regs *regs) {
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

static void ptrace_cont(pid_t pid) {
    ptrace(PTRACE_CONT, pid, NULL, NULL);
}

long call_remote_function(pid_t pid, long function_addr, long* args, size_t argc) {
    return call_remote_function_from_namespace(pid, function_addr, 0, args, argc);
}

long call_remote_function_from_namespace(pid_t pid, long function_addr, long return_addr, long* args, size_t argc) {
    #if defined(__aarch64__)
        #define REGS_ARG_NUM    6
    #else
        #define REGS_ARG_NUM    4
    #endif

    struct pt_regs regs{};
    struct pt_regs backup_regs{};

    ptrace_get_regs(pid, &regs);
    memcpy(&backup_regs, &regs, sizeof(struct pt_regs));

    for (int i = 0; i < argc && i < REGS_ARG_NUM; ++i) {
        regs.uregs[i] = args[i];
    }

    if (argc > REGS_ARG_NUM) {
        regs.ARM_sp -= (argc - REGS_ARG_NUM) * sizeof(long);
        long* data = args + REGS_ARG_NUM;
        ptrace_write(pid, (uint8_t*) regs.ARM_sp, (uint8_t*) data, (argc - REGS_ARG_NUM) * sizeof(long));
    }

    regs.ARM_lr = return_addr;
    regs.ARM_pc = function_addr;

    #if !defined(__aarch64__)
        if (regs.ARM_pc & 1) {
            regs.ARM_pc &= (~1u);
            regs.ARM_cpsr |= CPSR_T_MASK;
        } else {
            regs.ARM_cpsr &= ~CPSR_T_MASK;
        }
    #endif

    ptrace_set_regs(pid, &regs);
    ptrace_cont(pid);

    waitpid(pid, nullptr, WUNTRACED);

    ptrace_get_regs(pid, &regs);
    ptrace_set_regs(pid, &backup_regs);

    ALOGD("call remote function %lx with %zu arguments, return value is %llx",
          function_addr, argc, (long long) regs.ARM_r0);

    return regs.ARM_r0;
}
