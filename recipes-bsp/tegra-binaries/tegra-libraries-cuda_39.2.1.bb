L4T_DEB_COPYRIGHT_MD5 = "93552430651503d037a4922ab34a8208"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, '3d-core')};subdir=${BP};name=core3d"
SRC_SOC_DEBS:append:tegra264 = " ${@l4t_deb_pkgname(d, 'cuda-openrm')};subdir=${BP};name=cuda"
SRC_SOC_DEBS:append:tegra234 = " ${@l4t_deb_pkgname(d, 'cuda-nvgpu')};subdir=${BP};name=cuda"
MAINSUM = "a8f65b17c3dc9cae34d77e3a3481b809a49443d83241d85d5fea36525af6ab51"
CORE3DSUM = "77fcb6c2c11ed67403f8e7697aab9d6ef11a2acbde86dd38a062a8945d5d8631"
CUDASUM:tegra264 = "ac4d4b377d9154cb92fb59ddb8292937fcd9ff44b53bd807459c54a03fc29787"
CUDASUM:tegra234 = "a70b1a731eaaf8a528428f1f8fe882ba5b06d670d8ffe28c338342fd195eb302"

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
