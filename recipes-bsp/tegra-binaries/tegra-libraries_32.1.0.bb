require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

inherit update-rc.d systemd

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib usr/sbin var/nvidia
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
    ln -sf libnvbuf_utils.so.1.0.0 ${D}${libdir}/libnvbuf_utils.so.1
    ln -sf libnvbuf_utils.so.1.0.0 ${D}${libdir}/libnvbuf_utils.so
    ln -sf libnvid_mapper.so.1.0.0 ${D}${libdir}/libnvid_mapper.so.1
    ln -sf libnvid_mapper.so.1.0.0 ${D}${libdir}/libnvid_mapper.so
    rm -f ${D}${libdir}/libdrm* ${D}${libdir}/libnvphsd* ${D}${libdir}/libnvgov*
    # argus and scf libraries hard-coded to use this path
    install -d ${D}/usr/lib/aarch64-linux-gnu/tegra-egl
    ln -sf ${libdir}/libEGL_nvidia.so.0 ${D}/usr/lib/aarch64-linux-gnu/tegra-egl/libEGL_nvidia.so.0
    install -d ${D}${sbindir}
    install -m755 ${B}/usr/sbin/nvargus-daemon ${D}${sbindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m755 ${S}/nvargus-daemon.init ${D}${sysconfdir}/init.d/nvargus-daemon
    install -d ${D}${systemd_system_unitdir}
    install -m644 ${S}/nvargus-daemon.service ${D}${systemd_system_unitdir}
    install -d ${D}${datadir}/glvnd/egl_vendor.d
    install -m644 ${DRVROOT}/tegra-egl/nvidia.json ${D}${datadir}/glvnd/egl_vendor.d/10-nvidia.json
    install -d ${D}${sysconfdir}/vulkan/icd.d
    install -m644 ${DRVROOT}/tegra/nvidia_icd.json ${D}${sysconfdir}/vulkan/icd.d/
    rm ${D}${libdir}/libnvidia-egl-wayland*
}

PACKAGES = "${PN}-libv4l-plugins ${PN}-argus ${PN}-libnvosd ${PN}-dev ${PN}"

FILES_${PN}-libv4l-plugins = "${libdir}/libv4l"
FILES_${PN}-argus = "${libdir}/libnvargus* ${sbindir}/nvargus_daemon ${sysconfdir}/init.d/nvargus-daemon"
FILES_${PN}-libnvosd = "${libdir}/libnvosd*"
FILES_${PN} = "${libdir} ${sbindir} ${nonarch_libdir} ${localstatedir} ${sysconfdir} ${datadir}"
FILES_${PN}-dev = "${libdir}/lib*GL*.so"
RDEPENDS_${PN} = "libasound"
RDEPENDS_${PN}-argus = "${PN}"
RDEPENDS_${PN}-libnvosd = "${PN} pango cairo glib-2.0"
RDEPENDS_${PN}-dev = "libegl-mesa-dev libgles1-mesa-dev libgles2-mesa-dev libgl-mesa-dev ${PN}"

INITSCRIPT_PACKAGES = "${PN}-argus"
INITSCRIPT_NAME_${PN}-argus = "nvargus-daemon"
INITSCRIPT_PARAMS_${PN}-argus = "defaults"
SYSTEMD_PACKAGES = "${PN}-argus"
SYSTEMD_SERVICE_${PN}-argus = "nvargus-daemon.service"

INSANE_SKIP_${PN}-libv4l-plugins = "dev-so textrel ldflags build-deps"
INSANE_SKIP_${PN} = "dev-so textrel ldflags build-deps"
INSANE_SKIP_${PN}-argus = "dev-so ldflags"
INSANE_SKIP_${PN}-libnvosd = "dev-so ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
