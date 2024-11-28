L4T_DEB_COPYRIGHT_MD5 = "4e4696e7549f9ee192223e3d7eb71341"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-3d-core_${PV}_arm64.deb;subdir=${BP};name=core3d"
MAINSUM = "7113b0fad83a3b2d3b3437bb62f01b97d6fdec2a20ffc7cf9dac2650f61d0d82"
MAINSUM:tegra210 = "0042650294496d632f5d7695a23632411ec3c1d3b2fdd664181338031bb45f86"
CORE3DSUM = "1fa3a9d3af25bdb0c9c000419b19eb74996c768b15d11c8fff9a6165c8632b24"
CORE3DSUM:tegra210 = "44df4b465417916ac1bdb1c801892c7af948114cadf5a3a1eafb780984600a94"
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
