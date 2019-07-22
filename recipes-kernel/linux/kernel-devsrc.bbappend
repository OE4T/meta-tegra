KERNEL_AWKSCRIPTDIRS ?= "scripts tools arch/*/tools"

do_install_prepend_tegra124() {
    touch ${S}/Build
    if [ ! -e ${S}/arch/arm/kernel/module.lds ]; then
        touch ${S}/arch/arm/kernel/module.lds
	remove_module_lds=yes
    fi
    if [ ! -d ${S}/arch/x86/entry ]; then
        mkdir -p ${S}/arch/x86/entry/syscalls
	touch ${S}/arch/x86/entry/syscalls/syscall_32.tbl
	remove_arch_x86_entry=yes
    fi
}

do_install_append() {
    (
	cd $kerneldir/build
	for i in $(grep -srI "^#!/bin/awk" ${KERNEL_AWKSCRIPTDIRS} | cut -d":" -f1); do
	    sed -i -e "s,^#!/bin/awk,#!${bindir}/env awk," $i
	done
    )
}

do_install_append_tegra124() {
    rm -f ${S}/Build $kerneldir/build/Build
    if [ "$remove_module_lds" = "yes" ]; then
        rm -f ${S}/arch/arm/kernel/module.lds $kerneldir/build/arch/arm/kernel/module.lds
    fi
    if [ "$remove_arch_x86_entry" = "yes" ]; then
        rm -rf ${S}/arch/x86/entry kerneldir/build/arch/x86/entry
    fi
}
