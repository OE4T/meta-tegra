require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 lib/firmware
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}
    cp -R -f ${B}/lib/firmware ${D}${nonarch_base_libdir}/
    ln -snf tegra12x ${D}${nonarch_base_libdir}/firmware/gk20a
}

PACKAGES = "${PN}"
FILES_${PN} = "${nonarch_base_libdir}"
