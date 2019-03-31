SOC_FAMILY = "tegra210"

require tegra-binaries-${PV}.inc

SRC_MD5SUM = "${SRC_MD5SUM_tegra210}"
SRC_SHA256SUM = "${SRC_SHA256SUM_tegra210}"

WORKDIR = "${TMPDIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:tegra-binaries-native::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/L4T-native-${SOC_FAMILY}-${PV}-*"

S = "${WORKDIR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

COMPATIBLE_MACHINE = ""

inherit native

INHIBIT_DEFAULT_DEPS = "1"
do_compile[noexec] = "1"

BINDIR = "${bindir}/tegra210-flash"

addtask preconfigure after do_patch before do_configure

do_install() {
    install -d ${D}${BINDIR}
    install -m 0755 ${S}/elf-get-entry.py ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/gen-tboot-img.py ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/tegrabct ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegradevflash ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrahost ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegraparser ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrarcm ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/tegrasign ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/tegraflash.py ${D}${BINDIR}
    install -m 0644 ${S}/bootloader/tegraflash_internal.py ${D}${BINDIR}

    install -m 0755 ${S}/bootloader/mkgpt ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mksparse ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbootimg ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbctpart ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/chkbdinfo ${D}${BINDIR}
    install -m 0755 ${S}/tegra210-flash-helper.sh ${D}${BINDIR}
}
