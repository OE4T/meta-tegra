OPENGL_PKGCONFIGS_tegra210 = "glx xinerama"
OPENGL_PKGCONFIGS_tegra186 = "glx xinerama"
OPENGL_PKGCONFIGS_tegra194 = "glx xinerama"
PACKAGECONFIG_remove_tegra210 = "dri2"
PACKAGECONFIG_remove_tegra186 = "dri2"
PACKAGECONFIG_remove_tegra194 = "dri2"
DEPENDS_remove_tegra210 = "libdrm"
DEPENDS_remove_tegra186 = "libdrm"
DEPENDS_remove_tegra194 = "libdrm"
EXTRA_OECONF_append_tegra210 = " --disable-libdrm --disable-config-udev-kms"
EXTRA_OECONF_append_tegra186 = " --disable-libdrm --disable-config-udev-kms"
EXTRA_OECONF_append_tegra194 = " --disable-libdrm --disable-config-udev-kms"
TARGET_CFLAGS_append_tegra210 = " -I=${includedir}/drm -DGL_GLEXT_PROTOTYPES"
TARGET_CFLAGS_append_tegra186 = " -I=${includedir}/drm -DGL_GLEXT_PROTOTYPES"
TARGET_CFLAGS_append_tegra194 = " -I=${includedir}/drm -DGL_GLEXT_PROTOTYPES"

do_install_append_tegra210() {
    rm -rf ${D}${libdir}/xorg/modules/extensions
}
do_install_append_tegra186() {
    rm -rf ${D}${libdir}/xorg/modules/extensions
}
do_install_append_tegra194() {
    rm -rf ${D}${libdir}/xorg/modules/extensions
}

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
