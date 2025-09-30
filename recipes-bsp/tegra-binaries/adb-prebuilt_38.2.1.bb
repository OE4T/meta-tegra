require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${base_bindir}
    install -m 0755 ${S}/tools/kernel_flash/bin/aarch64/adbd64 ${D}${bindir}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
