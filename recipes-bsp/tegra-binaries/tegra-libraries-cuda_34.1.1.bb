L4T_DEB_COPYRIGHT_MD5 = "fb78a7baee7b16c65c5d8c04127e8bd7"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-3d-core_${PV}_arm64.deb;subdir=${BP};name=core3d"
MAINSUM = "bd68de7fc3ee1dcbc55115bebee148aa73cd5b5c665d761912b42ec3ddd3c523"
CORE3DSUM = "0aaa907a7102ecfb8797d9ba7ddbd18640930377279b0d43509c850bbdcb3a5a"
SRC_URI[core3d.sha256sum] = "${CORE3DSUM}"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libcuda.so.1.1 \
    tegra/libnvcucompat.so \
    tegra/libnvidia-ptxjitcompiler.so.${L4T_VERSION} \
"

do_install() {
    install_libraries
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so.1
    ln -sf libnvidia-ptxjitcompiler.so.${L4T_VERSION} ${D}${libdir}/libnvidia-ptxjitcompiler.so.1
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
