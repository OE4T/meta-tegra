SOC_FAMILY ?= "tegra210"

require tegra-binaries-${PV}.inc

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

# addtask preconfigure after do_patch before do_configure

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/elf-get-entry.py ${D}${bindir}
    install -m 0755 ${S}/bootloader/gen-tboot-img.py ${D}${bindir}

    install -m 0755 ${S}/bootloader/tegrabct ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegradevflash ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegrahost ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegraparser ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegrarcm ${D}${bindir}
    install -m 0755 ${S}/bootloader/tegrasign ${D}${bindir}

    install -m 0755 ${S}/bootloader/tegraflash.py ${D}${bindir}
    install -m 0644 ${S}/bootloader/tegraflash_internal.py ${D}${bindir}

    install -m 0755 ${S}/bootloader/mkgpt ${D}${bindir}
    install -m 0755 ${S}/bootloader/mksparse ${D}${bindir}
    install -m 0755 ${S}/bootloader/mkbctpart ${D}${bindir}
}
