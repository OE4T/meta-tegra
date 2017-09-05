require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DEPENDS = "patchelf-native"

inherit update-rc.d systemd update-alternatives

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib usr/sbin usr/share/egl var/nvidia
}

do_compile[noexec] = "1"

DRVROOT = "${B}/usr/lib/aarch64-linux-gnu"

do_install() {
    install -d ${D}${localstatedir}
    cp -R ${B}/var/nvidia ${D}${localstatedir}/
    install -d ${D}${libdir}/libv4l/plugins
    for f in ${DRVROOT}/libv4l/plugins/lib*; do
        install -m 0644 $f ${D}${libdir}/libv4l/plugins/
    done
    install -d ${D}${libdir}
    for f in ${DRVROOT}/tegra/lib*; do
        install -m 0644 $f ${D}${libdir}
    done
    for f in ${DRVROOT}/tegra-egl/lib*; do
        install -m 0644 $f ${D}${libdir}
    done
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so.1
    ln -sf libGL.so.1 ${D}${libdir}/libGL.so
    ln -sf libEGL.so.1 ${D}${libdir}/libEGL.so
    ln -sf libGLESv1_CM.so.1 ${D}${libdir}/libGLESv1_CM.so
    ln -sf libGLESv2.so.2 ${D}${libdir}/libGLESv2.so
    ln -sf libnvbuf_utils.so.1.0.0 ${D}${libdir}/libnvbuf_utils.so.1
    ln -sf libnvbuf_utils.so.1.0.0 ${D}${libdir}/libnvbuf_utils.so
    mv ${D}${libdir}/libdrm.so.2 ${D}${libdir}/libdrm-tegra.so.2
    patchelf --set-soname libdrm-tegra.so.2 ${D}${libdir}/libdrm-tegra.so.2
    ln -sf libdrm-tegra.so.2 ${D}${libdir}/libdrm-tegra.so
    # nvcamera_daemon only looks in this special subdir, so symlink it
    install -d ${D}${nonarch_libdir}/aarch64-linux-gnu
    ln -snf ${libdir} ${D}${nonarch_libdir}/aarch64-linux-gnu/tegra-egl
    install -d ${D}${sbindir}
    install -m755 ${B}/usr/sbin/nvcamera-daemon ${D}${sbindir}/
    install -m755 ${B}/usr/sbin/argus_daemon ${D}${sbindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m755 ${S}/nvcamera-daemon.init ${D}${sysconfdir}/init.d/nvcamera-daemon
    install -m755 ${S}/argus-daemon.init ${D}${sysconfdir}/init.d/argus-daemon
    install -d ${D}${systemd_system_unitdir}
    install -m644 ${S}/nvcamera-daemon.service ${D}${systemd_system_unitdir}
    install -m644 ${S}/argus-daemon.service ${D}${systemd_system_unitdir}
    install -d ${D}${datadir}/egl/egl_external_platform.d
    install -m644 ${B}/usr/share/egl/egl_external_platform.d/* ${D}${datadir}/egl/egl_external_platform.d/
}

# Allow switching between libdrm from freedesktop and libdrm from NVIDIA.
# freedesktop is the default provider.
ALTERNATIVE_libdrm-tegra = "libdrm.so.2 libdrm.so.2.4.0"
ALTERNATIVE_LINK_NAME[libdrm.so.2] = "${libdir}/libdrm.so.2"
ALTERNATIVE_LINK_NAME[libdrm.so.2.4.0] = "${libdir}/libdrm.so.2.4.0"
ALTERNATIVE_TARGET = "${libdir}/libdrm-tegra.so.2"
ALTERNATIVE_PRIORITY = "10"

PACKAGES = "${PN}-libv4l-plugins ${PN}-argus libdrm-tegra libdrm-tegra-dev ${PN}"

FILES_${PN}-libv4l-plugins = "${libdir}/libv4l"
FILES_${PN}-argus = "${libdir}/libargus* ${sbindir}/argus_daemon"
FILES_libdrm-tegra = "${libdir}/libdrm-tegra${SOLIBS}"
FILES_libdrm-tegra-dev = "${libdir}/libdrm-tegra${SOLIBSDEV}"
FILES_${PN} = "${libdir} ${sbindir} ${nonarch_libdir} ${localstatedir} ${sysconfdir} ${datadir}/egl"
RDEPENDS_${PN} = "libasound"
RDEPENDS_${PN}-argus = "${PN}"

INITSCRIPT_PACKAGES = "${PN} ${PN}-argus"
INITSCRIPT_NAME_${PN} = "nvcamera-daemon"
INITSCRIPT_NAME_${PN}-argus = "argus-daemon"
INITSCRIPT_PARAMS_${PN} = "defaults"
SYSTEMD_PACKAGES = "${PN} ${PN}-argus"
SYSTEMD_SERVICE_${PN} = "nvcamera-daemon.service"
SYSTEMD_SERVICE_${PN}-argus = "argus-daemon.service"

INSANE_SKIP_${PN}-libv4l-plugins = "dev-so textrel ldflags build-deps"
INSANE_SKIP_${PN} = "dev-so textrel ldflags build-deps"
INSANE_SKIP_${PN}-argus = "dev-so ldflags"
INSANE_SKIP_libdrm-tegra = "ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
