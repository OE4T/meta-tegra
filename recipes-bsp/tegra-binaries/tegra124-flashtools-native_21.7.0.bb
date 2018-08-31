require tegra-binaries-${PV}.inc

WORKDIR = "${TMPDIR}/work-shared/L4T-native-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:tegra-binaries-native::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/L4T-native--${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/L4T-native-${PV}-*"

S = "${WORKDIR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

COMPATIBLE_MACHINE = ""

inherit native

INHIBIT_DEFAULT_DEPS = "1"
do_compile[noexec] = "1"

BINDIR = "${bindir}/tegra124-flash"

do_install() {
    install -d ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/nvflash ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkgpt ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mksparse ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbootimg ${D}${BINDIR}
    install -m 0755 ${S}/bootloader/mkbctpart ${D}${BINDIR}
}
