/* SPDX-License-Identifier: MIT */
#include <linux/reboot.h>
#include <sys/reboot.h>
#include <sys/syscall.h>
#include <unistd.h>

static inline int sys_reboot(const void *arg) {
    return (int) syscall(SYS_reboot, LINUX_REBOOT_MAGIC1, LINUX_REBOOT_MAGIC2, LINUX_REBOOT_CMD_RESTART2, arg);
}

int
main (int argc, char *argv[])
{
    if (argc > 1)
        return sys_reboot(argv[1]);
    return sys_reboot("forced-recovery");
}
