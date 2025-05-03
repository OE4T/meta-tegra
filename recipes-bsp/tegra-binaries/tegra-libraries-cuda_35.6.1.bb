L4T_DEB_COPYRIGHT_MD5 = "2dff92eb07c96bdcd80b098adca7826a"
DEPENDS = "tegra-libraries-core"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, '3d-core')};subdir=${BP};name=core3d"
MAINSUM = "051eb4597821d898f68beca756802722d64b3803f7644bd904a70a7e9a874747"
CORE3DSUM = "e4448e39255b6bac877217f404330e83a6797771c817b5e11a6d48921024b152"
SRC_URI[core3d.sha256sum] = "${CORE3DSUM}"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libcuda.so.1.1 \
    tegra/libnvidia-ptxjitcompiler.so.35.6.1 \
    tegra/libnvidia-nvvm.so.35.6.1 \
"

do_install() {
    install_libraries
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so.1
    ln -sf libnvidia-ptxjitcompiler.so.35.6.1 ${D}${libdir}/libnvidia-ptxjitcompiler.so.1
    ln -sf libnvidia-nvvm.so.35.6.1 ${D}${libdir}/libnvidia-nvvm.so.4
    ln -sf libnvidia-nvvm.so.35.6.1 ${D}${libdir}/libnvidia-nvvm.so

    # This is done to fix docker passthroughs
    # libnvcucompat.so is part of base passthrough and will get mounted to /usr/lib/aarch64-linux-gnu
    # However, in nvidia stock containers this file is already populated with a symlink to tegra/libnvcucompat.so
    # Hence, NVIDIA wants us to mount this file to `/usr/lib/aarch64-linux-gnu/tegra/`
    # This fix is used for mounting the file at `/usr/lib/aarch64-linux-gnu` with different name
    # and then overriding the symlink for the new file name
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/tegra/libnvcucompat.so ${D}${libdir}/libnvcucompat.so.35.6.1
    ln -sf libnvcucompat.so.35.6.1 ${D}${libdir}/libnvcucompat.so
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RPROVIDES:${PN} += "libcuda.so()(64bit)"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
