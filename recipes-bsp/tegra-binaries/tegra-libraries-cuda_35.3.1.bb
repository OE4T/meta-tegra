L4T_DEB_COPYRIGHT_MD5 = "fb78a7baee7b16c65c5d8c04127e8bd7"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, '3d-core')};subdir=${BP};name=core3d"
MAINSUM = "a44ebf2c7149293db3340756071a6723a5ea4078b5d0eb178c4ec88cdd1c3ecb"
CORE3DSUM = "ad895635ab22cbfc57c6a0fea7b1a75f6b5f26ec63c1b1848c49df83590a4bb1"
SRC_URI[core3d.sha256sum] = "${CORE3DSUM}"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libcuda.so.1.1 \
    tegra/libnvidia-ptxjitcompiler.so.${L4T_VERSION} \
    tegra/libnvidia-nvvm.so.${L4T_VERSION} \
"

do_install() {
    install_libraries
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so.1
    ln -sf libnvidia-ptxjitcompiler.so.${L4T_VERSION} ${D}${libdir}/libnvidia-ptxjitcompiler.so.1
    ln -sf libnvidia-nvvm.so.${L4T_VERSION} ${D}${libdir}/libnvidia-nvvm.so.4
    ln -sf libnvidia-nvvm.so.${L4T_VERSION} ${D}${libdir}/libnvidia-nvvm.so

    # This is done to fix docker passthroughs
    # libnvcucompat.so is part of base passthrough and will get mounted to /usr/lib/aarch64-linux-gnu
    # However, in nvidia stock containers this file is already populated with a symlink to tegra/libnvcucompat.so
    # Hence, NVIDIA wants us to mount this file to `/usr/lib/aarch64-linux-gnu/tegra/`
    # This fix is used for mounting the file at `/usr/lib/aarch64-linux-gnu` with different name
    # and then overriding the symlink for the new file name
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libnvcucompat.so ${D}${libdir}/libnvcucompat.so.${L4T_VERSION}
    ln -sf libnvcucompat.so.${L4T_VERSION} ${D}${libdir}/libnvcucompat.so
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RPROVIDES:${PN} += "libcuda.so()(64bit)"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
