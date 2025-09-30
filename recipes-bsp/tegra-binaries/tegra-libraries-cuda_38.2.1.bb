L4T_DEB_COPYRIGHT_MD5 = "93552430651503d037a4922ab34a8208"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, '3d-core')};subdir=${BP};name=core3d"
MAINSUM = "5baefb9a8894af6913313cd6c35702980365e3b0c2fb7a39ac2fb1dbc0781ba7"
CORE3DSUM = "49d5c4d659293d3850f77bae7f5a8888d40ced71abaec585ba40e88ea29fa293"
SRC_URI[core3d.sha256sum] = "${CORE3DSUM}"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libcuda.so.1.1 \
    nvidia/libnvcudla.so \
    nvidia/libnvcuextend.so \
    nvidia/libnvidia-ptxjitcompiler.so.${L4T_LIB_VERSION} \
    nvidia/libnvidia-nvvm.so.${L4T_LIB_VERSION} \
"

do_install() {
    install_libraries
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so.1
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
