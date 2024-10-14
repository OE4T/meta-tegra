L4T_DEB_COPYRIGHT_MD5 = "2dff92eb07c96bdcd80b098adca7826a"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, '3d-core')};subdir=${BP};name=core3d"
MAINSUM = "6d3b1c90dda7f313785d5ca919a87278307be6d3596a5cdb4e249b8ea6e8af58"
CORE3DSUM = "d2606154c4b74d37ce0eb0d81dbcdad6ce420d0874032a05e00f054a05f8cb50"
SRC_URI[core3d.sha256sum] = "${CORE3DSUM}"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libcuda.so.1.1 \
    nvidia/libnvcudla.so \
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
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
