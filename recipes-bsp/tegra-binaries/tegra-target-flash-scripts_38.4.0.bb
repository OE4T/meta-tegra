require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
DEPENDS = "abootimg-native"

B = "${WORKDIR}/build"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"

do_compile() {
    abootimg -x ${S}/unified_flash/tools/flashtools/flashing_kernel/initramfs/t264/boot_flashing.img
    abootimg-unpack-initrd initrd.img
    # In all scripts we rewrite hard-coded /bin/ references to NV
    # scripts to use bindir, since we can't assume usrmerge

    # delete sections in init script that break things,
    sed -i -e'/create reboot/,/overlayfs_check/d' -e'/^cd .usr.sbin/,/^ln -s/d' \
	-e's,/bin/nv_\(enable_remote\|fuse_read\|recovery\)\.sh,${bindir}/nv_\1.sh,g' \
	 ${B}/ramdisk/init
    # t264: nvdd expects /dev/810c5b0000.spi but kernel uses /dev/mtd0.
    # Create symlink, gated on tegra264 compatible. Poll for MTD probe.
    sed -i -e'/source.*nv_recovery/i\
# Create QSPI device symlink for nvdd (t264 SPI5 MMIO: 0x810c5b0000)\
if [ -e /proc/device-tree/compatible ] && grep -q "tegra264" /proc/device-tree/compatible 2>/dev/null; then\
    qspi_wait=0\
    while [ ! -e /dev/mtd0 ] && [ "$qspi_wait" -lt 15 ]; do\
        sleep 1\
        qspi_wait=$((qspi_wait + 1))\
    done\
    if [ -e /dev/mtd0 ]; then\
        ln -sf /dev/mtd0 /dev/810c5b0000.spi\
        echo "Flash-init: created QSPI symlink /dev/810c5b0000.spi -> /dev/mtd0 (waited ${qspi_wait}s)" > /dev/kmsg\
    else\
        echo "Flash-init: WARNING - /dev/mtd0 not found after 15s, QSPI flash may fail" > /dev/kmsg\
    fi\
fi' \
	${B}/ramdisk/init
    # our busybox has no 'bc' built in, so convert to normal bc
    sed -i -e's,busybox bc,bc,g' \
	-e's,/bin/nv_\(enable_remote\|fuse_read\|recovery\)\.sh,${bindir}/nv_\1.sh,g' \
	${B}/ramdisk/usr/bin/nv_enable_remote.sh
    # Replace hardcoded UDC device names with dynamic /sys/class/udc/*
    # enumeration, consistent with init-flash.sh.
    sed -i -e'/^[[:space:]]*known_udc_dev[0-9]*=/d' \
	${B}/ramdisk/usr/bin/nv_enable_remote.sh
    sed -i -e'/echo "Finding UDC"/a\
\t\t# Dynamically find any available UDC device\
\t\tfor udc_candidate in /sys/class/udc/*; do\
\t\t\tif [ -e "${udc_candidate}" ]; then\
\t\t\t\tudc_dev=$(basename "${udc_candidate}")\
\t\t\t\tbreak 2\
\t\t\tfi\
\t\tdone' \
	${B}/ramdisk/usr/bin/nv_enable_remote.sh
    sed -i -e'/if \[ -e "\/sys\/class\/udc\/\${known_udc_dev[0-9]*}" \]/,+3d' \
	${B}/ramdisk/usr/bin/nv_enable_remote.sh
    sed -i -e's,if \[ "${udc_dev}" == "" \],if [ -z "${udc_dev}" ],' \
	${B}/ramdisk/usr/bin/nv_enable_remote.sh
    # sigh
    sed -i -e's,^me=.*,me=root,' \
	-e's,/bin/nv_\(enable_remote\|fuse_read\|recovery\)\.sh,${bindir}/nv_\1.sh,g' \
	${B}/ramdisk/usr/bin/nv_fuse_read.sh
    sed -i -e's,/bin/nv_\(enable_remote\|fuse_read\|recovery\)\.sh,${bindir}/nv_\1.sh,g' \
	${B}/ramdisk/usr/bin/nv_recovery.sh
    cat >${B}/ramdisk/initrd_flash.cfg <<EOF
adb=1
EOF
}
do_compile[cleandirs] = "${B}"

do_install() {
	install -m 0755 -D ${B}/ramdisk/init ${D}/init
	install -m 0644 ${B}/ramdisk/initrd_flash.cfg ${D}/initrd_flash.cfg
	install -m 0755 -D -t ${D}${bindir} ${B}/ramdisk/usr/bin/nv_recovery.sh ${B}/ramdisk/usr/bin/nv_enable_remote.sh ${B}/ramdisk/usr/bin/nv_fuse_read.sh
}

FILES:${PN} += "/init /initrd_flash.cfg"
RDEPENDS:${PN} = "bash bc adb-prebuilt util-linux-blkdiscard util-linux-blockdev util-linux-losetup e2fsprogs-resize2fs e2fsprogs-e2fsck kmod vim-xxd coreutils parted"
RRECOMMENDS:${PN} = "kernel-module-loop \
                     kernel-module-libcomposite \
                     kernel-module-usb-f-fs \
                     kernel-module-tegra-xudc \
		     kernel-module-mods \
"
