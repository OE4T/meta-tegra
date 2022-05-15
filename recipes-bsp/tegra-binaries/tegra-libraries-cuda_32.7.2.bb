L4T_DEB_COPYRIGHT_MD5 = "fb78a7baee7b16c65c5d8c04127e8bd7"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-3d-core_${PV}_arm64.deb;subdir=${BP};name=core3d"
MAINSUM = "643e6cd3be8fa6e84e782d371365764e0b558f87c9f77ace0be1baaa0af4c972"
MAINSUM:tegra210 = "29a1cd28bf3924d2ac76ed1c5e1cb24020eab63bb057678f97c4ff5c18852c13"
CORE3DSUM = "97fd11faa79ab16f5525492628108a68a58a5c1c5127ba58509784d58b90729c"
CORE3DSUM:tegra210 = "29fa00ca54f7776503a608989ae7baa2459cc1a1d52e075819a0a07761192c25"
SRC_URI[core3d.sha256sum] = "${CORE3DSUM}"

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
