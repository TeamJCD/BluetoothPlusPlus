#ifndef BLUETOOTH_PTRACE_H
#define BLUETOOTH_PTRACE_H

#include <sys/types.h>

class Ptrace {
public:
    static int attach(pid_t pid);
    static int detach(pid_t pid);
};

#endif //BLUETOOTH_PTRACE_H
