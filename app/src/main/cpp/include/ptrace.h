#ifndef BLUETOOTH_PTRACE_H
#define BLUETOOTH_PTRACE_H

#include <sys/types.h>

#define CPSR_T_MASK (1u << 5)

int ptrace_attach(pid_t pid);
int ptrace_detach(pid_t pid);
void ptrace_write(pid_t pid, uint8_t* addr, uint8_t* data, size_t size);
void ptrace_read(pid_t pid, uint8_t* dest, uint8_t* addr, size_t size);

long call_remote_function(pid_t pid, long function_addr, long* args, size_t argc);
long call_remote_function_from_namespace(pid_t pid, long function_addr, long namespace_addr, long* args, size_t argc);

#endif //BLUETOOTH_PTRACE_H
