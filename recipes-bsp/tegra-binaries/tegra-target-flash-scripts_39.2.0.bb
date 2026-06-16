require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra264)"
DEPENDS = "abootimg-native"

B = "${WORKDIR}/build"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"

do_compile() {
    abootimg -x ${S}/unified_flash/tools/flashtools/flashing_kernel/initramfs/t264/boot_flashing.img
    abootimg-unpack-initrd initrd.img

    # --- paths ---
    rd="${B}/ramdisk"
    init="${rd}/init"
    nv_remote="${rd}/usr/bin/nv_enable_remote.sh"
    nv_fuse="${rd}/usr/bin/nv_fuse_read.sh"
    nv_recovery="${rd}/usr/bin/nv_recovery.sh"
    snip_mount="${B}/snippet-nv-remote-mount.sh"
    snip_modalias="${B}/snippet-nv-remote-modalias.sh"

    # Snippets spliced into nv_enable_remote.sh (see below).
    cat >"${snip_mount}" <<'EOF'

mount -t proc proc -o nosuid,nodev,noexec /proc || true
mount -t devtmpfs none -o nosuid /dev || true
mount -t sysfs sysfs -o nosuid,nodev,noexec /sys || true

EOF
    cat >"${snip_modalias}" <<'EOF'

find /sys -name modalias | while read m; do
    modalias=$(cat "$m")
    modprobe -v "$modalias" 2> /dev/null
done

EOF

    # --- ramdisk/init: drop sections that break our setup; use ${bindir} for nv_* scripts ---
    sed -i \
        -e'/create reboot/,/overlayfs_check/d' \
        -e'/^cd .usr.sbin/,/^ln -s/d' \
        -e's,/bin/nv_\(enable_remote\|fuse_read\|recovery\)\.sh,${bindir}/nv_\1.sh,g' \
        "${init}"

    # --- nv_enable_remote.sh: busybox bc -> bc; /bin paths -> ${bindir} ---
    sed -i \
        -e's,busybox bc,bc,g' \
        -e's,/bin/nv_\(enable_remote\|fuse_read\|recovery\)\.sh,${bindir}/nv_\1.sh,g' \
        "${nv_remote}"

    # After shebang: mount proc/dev/sys when the script is not started from full init.
    head -n1 "${nv_remote}" >"${nv_remote}.new"
    cat "${snip_mount}" >>"${nv_remote}.new"
    tail -n +2 "${nv_remote}" >>"${nv_remote}.new"
    mv "${nv_remote}.new" "${nv_remote}"
    chmod 0755 "${nv_remote}"

    # Before eMMC boot RO + modprobe block: load modules advertised in sysfs.
    line=$(grep -nF 'Enable editing mmcblk0bootx' "${nv_remote}" 2>/dev/null | head -1 | cut -d: -f1)
    if [ -n "${line}" ] && [ "${line}" -gt 0 ] 2>/dev/null; then
        line_prev=$(expr "${line}" - 1)
        head -n "${line_prev}" "${nv_remote}" >"${nv_remote}.new"
        cat "${snip_modalias}" >>"${nv_remote}.new"
        tail -n +${line} "${nv_remote}" >>"${nv_remote}.new"
        mv "${nv_remote}.new" "${nv_remote}"
        chmod 0755 "${nv_remote}"
    fi

    # --- other NVIDIA helpers ---
    sed -i \
        -e's,^me=.*,me=root,' \
        -e's,/bin/nv_\(enable_remote\|fuse_read\|recovery\)\.sh,${bindir}/nv_\1.sh,g' \
        "${nv_fuse}"
    sed -i \
        -e's,/bin/nv_\(enable_remote\|fuse_read\|recovery\)\.sh,${bindir}/nv_\1.sh,g' \
        "${nv_recovery}"

    cat >"${B}/ramdisk/initrd_flash.cfg" <<'EOF'
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
                     kernel-module-ipv6 \
                     kernel-module-dwmac-tegra \
"
