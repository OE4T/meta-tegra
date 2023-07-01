L4T_DEB_COPYRIGHT_MD5 = "fb78a7baee7b16c65c5d8c04127e8bd7"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-3d-core_${PV}_arm64.deb;subdir=${BP};name=core3d"
MAINSUM = "afb2cf51e7ba73360b28411f641692a7007313b11dc53e315aa4173142b658e3"
MAINSUM_tegra210 = "431c2f58bdb5ec57ef83915130727aa72b7e8950a4e770c99d1c6862de3aa382"
CORE3DSUM = "d65d21276997377e5fb744e25439d19c264cf1172c5a223a94a45e0d9964af79"
CORE3DSUM_tegra210 = "6ff6d996a737f62230cc3b55f63a737f30ca1daf68dc52cecd1247d92341d95d"
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
RRECOMMENDS_${PN} = "kernel-module-nvgpu"
