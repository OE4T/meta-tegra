require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "Miscellaneous tools provided by L4T"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nv_tools.tbz2
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/home/ubuntu/tegrastats ${D}${sbindir}/
}

PACKAGES = "${PN}"
FILES_${PN} = "${sbindir}"
INSANE_SKIP_${PN} = "ldflags"
