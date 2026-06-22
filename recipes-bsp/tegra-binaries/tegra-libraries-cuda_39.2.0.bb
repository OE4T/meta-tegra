L4T_DEB_COPYRIGHT_MD5 = "93552430651503d037a4922ab34a8208"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, '3d-core')};subdir=${BP};name=core3d"
SRC_SOC_DEBS:append:tegra264 = " ${@l4t_deb_pkgname(d, 'cuda-openrm')};subdir=${BP};name=cuda"
SRC_SOC_DEBS:append:tegra234 = " ${@l4t_deb_pkgname(d, 'cuda-nvgpu')};subdir=${BP};name=cuda"
MAINSUM = "7a614c3d6fc73af28869634cac47489c3008fc4e05cd5cfb53847bb0da9a9078"
CORE3DSUM = "59d87095d3469239c4d1f867a50b9fad857787697a22170041e639525fc87565"
CUDASUM:tegra264 = "838296baadd348d9373994a96901d864dc4e2a3aa7aa3eff4b12df1a8aed9376"
CUDASUM:tegra234 = "e15b308522a4dcf89cbe640dd74a138430943846b4b141d548b847a26b3c9b71"

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
