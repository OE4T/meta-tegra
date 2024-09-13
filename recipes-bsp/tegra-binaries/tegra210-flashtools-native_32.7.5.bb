SOC_FAMILY = "tegra210"
L4T_BSP_NAME = "T210"
L4T_BSP_PREFIX = "Jetson-210"

require tegra-binaries-${PV}.inc

L4T_MD5SUM = "${L4T_MD5SUM:tegra210}"
L4T_SHA256SUM = "${L4T_SHA256SUM:tegra210}"
SB_MD5SUM = "${SB_MD5SUM:tegra210}"
SB_SHA256SUM = "${SB_SHA256SUM:tegra210}"

WORKDIR = "${TMPDIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:tegra-binaries-native::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-*"

SRC_URI += "\
           file://0003-Convert-BUP_generator.py-to-Python3.patch \
           file://0008-Skip-qspi-sd-specific-entries-for-other-t210-BUP-pay.patch \
           file://0012-BMP_generator_L4T.py-to-Python3.patch \
           file://0013-Fix-location-of-bsp_version-file-in-l4t_bup_gen.func.patch \
           file://0014-odmsign.func-Fix-cp-error-on-missing-recovery-kernel.patch \
           file://0015-Use-raw-strings-for-regex-patterns.patch \
           "

S = "${WORKDIR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

COMPATIBLE_MACHINE = ""

inherit native

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "tegra-helper-scripts-native"

do_compile[noexec] = "1"

BINDIR = "${bindir}/tegra210-flash"

addtask preconfigure after do_patch before do_configure

do_install() {
    install -d ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/tegrabct ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegradevflash ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrahost ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegraparser ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrarcm ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign_v3*py ${D}${BINDIR}
    sed -i -e'1s,^#!.*,#!/usr/bin/env python3,' ${D}${BINDIR}/tegrasign_v3.py
    install -m 0755 ${S}/bootloader/tegraopenssl ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/tegraflash.py ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/tegraflash_internal.py ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/BUP_generator.py ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/l4t_bup_gen.func ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/odmsign.func ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/mkgpt ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mksparse ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbootimg ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbctpart ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/chkbdinfo ${D}${BINDIR}
    install -m 0755 ${S}/pkc/mkpkc ${D}${BINDIR}

    install -m 0755 ${S}/tools/bmp-splash/BMP_generator_L4T.py ${D}${BINDIR}

}

INHIBIT_SYSROOT_STRIP = "1"
