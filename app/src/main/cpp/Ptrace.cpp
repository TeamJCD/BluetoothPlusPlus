#include <cstdio>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include "Ptrace.h"
#include "Utils.h"

int Ptrace::attach(pid_t pid) {
    if (pid == -1) {
        return -1;
    }

    if (ptrace(PTRACE_ATTACH, pid, NULL, NULL) < 0) {
        perror(nullptr);
        return -1;
    }

    waitpid(pid, nullptr, WUNTRACED);

    ALOGD("Ptrace::attach - Attached to process %d", pid);
    return 0;
}

int Ptrace::detach(pid_t pid) {
    if (pid == -1) {
        return -1;
    }

    if (ptrace(PTRACE_DETACH, pid, NULL, NULL) < 0) {
        perror(nullptr);
        return -1;
    }

    ALOGD("Ptrace::detach - Detached from process %d", pid);
    return 0;
}
