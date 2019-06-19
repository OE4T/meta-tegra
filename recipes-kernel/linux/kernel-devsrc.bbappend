do_install_append() {
    # This fixes the rpm dependency failure on install of kernel-devsrc
    cd ${D} || true
    for i in $(grep -srI "!/bin/awk" | cut -d":" -f1); do
        sed -i -e "s#!/bin/awk#!${bindir}/env awk#g" $i
    done
}
