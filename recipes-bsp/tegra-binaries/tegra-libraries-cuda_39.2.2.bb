L4T_DEB_COPYRIGHT_MD5 = "93552430651503d037a4922ab34a8208"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, '3d-core')};subdir=${BP};name=core3d"
SRC_SOC_DEBS:append:tegra264 = " ${@l4t_deb_pkgname(d, 'cuda-openrm')};subdir=${BP};name=cuda"
SRC_SOC_DEBS:append:tegra234 = " ${@l4t_deb_pkgname(d, 'cuda-nvgpu')};subdir=${BP};name=cuda"
MAINSUM = "caf27c435cf6eb14977e707281b71e3b60e694024a4aee070fb974c955106182"
CORE3DSUM = "7bbf86938d923a7dca0da30d8b76ba7d1245a53ce3e888e09eee7c1bceb948ce"
CUDASUM:tegra264 = "c8dffa819a29ad8bb5bf9b7c637629dffd2ceb419b0343188cd5b29ce9e5668d"
CUDASUM:tegra234 = "b7f8538410e7b5845e7d17062cd2b59fc76737f86c008b0c4d305d932598ec6f"

SRC_URI[core3d.sha256sum] = "${CORE3DSUM}"
SRC_URI[cuda.sha256sum] = "${CUDASUM}"

CUDA_DRV_VARIANT = ""
CUDA_DRV_VARIANT:tegra234 = "nvgpu"
CUDA_DRV_VARIANT:tegra264 = "openrm"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvcudla.so \
    nvidia/libnvcuextend.so \
    nvidia/libnvidia-ptxjitcompiler.so.${L4T_LIB_VERSION} \
    nvidia/libnvidia-nvvm.so.${L4T_LIB_VERSION} \
"

do_install() {
    install_libraries
    install -m 0644 ${S}/opt/nvidia/l4t-gpu-libs/${CUDA_DRV_VARIANT}/libcuda_instrumentation.so ${D}${libdir}
    install -m 0644 ${S}/opt/nvidia/l4t-gpu-libs/${CUDA_DRV_VARIANT}/libcuda.so.1.1 ${D}${libdir}
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so.1
    ln -sf libcuda_instrumentation.so ${D}${libdir}/libcuda_instrumentation.so.1
    ln -sf libnvidia-ptxjitcompiler.so.${L4T_LIB_VERSION} ${D}${libdir}/libnvidia-ptxjitcompiler.so.1
    ln -sf libnvidia-nvvm.so.${L4T_LIB_VERSION} ${D}${libdir}/libnvidia-nvvm.so.4
    ln -sf libnvidia-nvvm.so.${L4T_LIB_VERSION} ${D}${libdir}/libnvidia-nvvm.so

    # This is done to fix docker passthroughs
    # libnvcucompat.so is part of base passthrough and will get mounted to /usr/lib/aarch64-linux-gnu
    # However, in nvidia stock containers this file is already populated with a symlink to nvidia/libnvcucompat.so
    # Hence, NVIDIA wants us to mount this file to `/usr/lib/aarch64-linux-gnu/nvidia/`
    # This fix is used for mounting the file at `/usr/lib/aarch64-linux-gnu` with different name
    # and then overriding the symlink for the new file name
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/nvidia/libnvcucompat.so ${D}${libdir}/libnvcucompat.so.${L4T_VERSION}
    ln -sf libnvcucompat.so.${L4T_VERSION} ${D}${libdir}/libnvcucompat.so
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RPROVIDES:${PN} += "libcuda.so()(64bit)"
RRECOMMENDS:${PN}:tegra234 = "nv-kernel-module-nvgpu"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
