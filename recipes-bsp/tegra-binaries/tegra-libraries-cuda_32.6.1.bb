DEPENDS = "tegra-libraries-core"

require tegra-libraries-common.inc

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libcuda.so.1.1 \
    tegra/libnvidia-fatbinaryloader.so.440.18 \
    tegra/libnvidia-ptxjitcompiler.so.440.18 \
"

do_install() {
    install_libraries
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so.1
    ln -sf libnvidia-fatbinaryloader.so.440.18 ${D}${libdir}/libnvidia-fatbinaryloader.so.1
    ln -sf libnvidia-ptxjitcompiler.so.440.18 ${D}${libdir}/libnvidia-ptxjitcompiler.so.1
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
