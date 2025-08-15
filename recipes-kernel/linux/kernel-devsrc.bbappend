KERNEL_AWKSCRIPTDIRS ?= "scripts tools arch/*/tools"

do_install:append:tegra() {
    (
        cd $kerneldir/build
        for i in $(grep -srI "^#!/bin/awk" ${KERNEL_AWKSCRIPTDIRS} | cut -d":" -f1); do
            sed -i -e "s,^#!/bin/awk,#!${bindir}/env awk," $i
        done
    )
}
