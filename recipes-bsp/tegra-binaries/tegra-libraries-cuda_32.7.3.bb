L4T_DEB_COPYRIGHT_MD5 = "fb78a7baee7b16c65c5d8c04127e8bd7"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-3d-core_${PV}_arm64.deb;subdir=${BP};name=core3d"
MAINSUM = "b1a22a8272950752b70c255449d1b0b9b6edf8f461bdc33abc308f4e685d005f"
MAINSUM:tegra210 = "6c4358bfbdbecd69a352aca7dcb6ff5c16442277227b3550d628f7342c9b6007"
CORE3DSUM = "18aa04998115a2d4e3285ddd3f864cf5a5e5e6cd07c9b82f49bf63c347506fcb"
CORE3DSUM:tegra210 = "6928ebe44d8097622ef24e1c505fe45c69415ee2db8614ecffb11a35f5d0a343"
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
