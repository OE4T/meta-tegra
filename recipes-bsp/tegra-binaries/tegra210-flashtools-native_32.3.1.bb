SOC_FAMILY = "tegra210"
L4T_BSP_NAME = "t210ref_release_aarch64"
L4T_BSP_PREFIX = "Tegra210"

require tegra-binaries-${PV}.inc

L4T_MD5SUM = "${L4T_MD5SUM_tegra210}"
L4T_SHA256SUM = "${L4T_SHA256SUM_tegra210}"
SB_MD5SUM = "${SB_MD5SUM_tegra210}"
SB_SHA256SUM = "${SB_SHA256SUM_tegra210}"

WORKDIR = "${TMPDIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:tegra-binaries-native::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-*"

SRC_URI += "\
           file://0003-Convert-BUP_generator.py-to-Python3.patch \
           file://0009-Update-tegraflash_internal.py-for-Python-3.9.patch \
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
}

INHIBIT_SYSROOT_STRIP = "1"
