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
    # delete sections in init script that break things
    sed -i -e'/create reboot/,/overlayfs_check/d' -e'/^cd .usr.sbin/,/^ln -s/d' \
	 ${B}/ramdisk/init
    # our busybox has no 'bc' built in, so convert to normal bc
    sed -i -e's,busybox bc,bc,g' ${B}/ramdisk/usr/bin/nv_enable_remote.sh
    # sigh
    sed -i -e's,^me=.*,me=root,' ${B}/ramdisk/usr/bin/nv_fuse_read.sh
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
