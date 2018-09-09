FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://egl-gles2-nv-extensions.patch"

DEPENDS += "tegra-mmapi-glheaders"

DEPENDS_append_tegra = " tegra-libraries"
EXTRA_OECONF_append_tegra = " --without-dri-drivers --disable-dri3"

python () {
    overrides = d.getVar("OVERRIDES").split(":")
    if "tegra210" not in overrides and "tegra124" not in overrides and "tegra186" not in overrides:
        return

    x11flag = d.getVarFlag("PACKAGECONFIG", "x11", False)
    d.setVarFlag("PACKAGECONFIG", "x11", x11flag.replace("--enable-glx-tls", "--enable-glx"))
}

move_libraries() {
    install -d ${D}${libdir}/mesa
    if [ -e ${D}${libdir}/libGL.so ]; then
       mv ${D}${libdir}/libGL.* ${D}${libdir}/mesa/
    fi
    if [ -e ${D}${libdir}/libGLESv2.so ]; then
        mv ${D}${libdir}/libGLES* ${D}${libdir}/mesa/
    fi
    if [ -e ${D}${libdir}/libEGL.so ]; then
        mv ${D}${libdir}/libEGL.* ${D}${libdir}/mesa/
    fi
}
do_install_append_tegra() {
    move_libraries
}

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"

PACKAGES =+ "${PN}-stubs-dev"
FILES_${PN}-stubs-dev = "${libdir}/mesa"
ALLOW_EMPTY_${PN}-stubs-dev = "1"
PRIVATE_LIBS_${PN}-stubs-dev = "\
    libEGL.so.1 \
    libGLESv1_CM.so.1 \
    libGLESv2.so.2 \
    libGL.so.1 \
"
