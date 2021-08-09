require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

inherit container-runtime-csv

CONTAINER_CSV_FILES = "${libdir}/*.so* ${libdir}/libv4l/plugins/* ${datadir}/glvnd/egl_vendor.d/* ${sysconfdir}/vulkan/icd.d/* \
                      /usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json"
CONTAINER_CSV_EXTRA = "lib, /usr/lib/aarch64-linux-gnu/tegra-egl/libEGL_nvidia.so.0"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/lib usr/sbin var/nvidia
}

do_compile[noexec] = "1"

DRVROOT = "${B}/usr/lib/aarch64-linux-gnu"

soname_fixup() {
    local libbasename="$1"
    local location="$2"
    local count=$(ls -1 "$location"/$libbasename.so.*.* | wc -l 2>/dev/null)

    if [ -z "$count" -o $count -eq 0 ]; then
        bberror "Cannot locate $libbasename for soname fixup"
        return 1
    fi
    if [ $count -gt 1 ]; then
        bberror "Cannot perform soname fixup for $libbasename: multiple shared objects with name"
        return 1
    fi
    local libpath=$(ls -1 "$location"/$libbasename.so.*.*)
    local libname=$(basename "$libpath")
    local soname=$(readelf -d "$libpath" | grep SONAME | sed -r -e 's,^.*\[(.+)\].*$,\1,')
    if [ -e "$location"/$soname ]; then
        bbnote "$(basename $soname) already exists, no need for soname fixup"
        return 0
    fi
    ln -s $libname "$location"/$soname
}

do_install() {
    install -d ${D}${localstatedir}
    cp -R ${B}/var/nvidia ${D}${localstatedir}/
    install -d ${D}${libdir}/libv4l/plugins/
    for f in ${DRVROOT}/tegra/libv4l2_nv*; do
        install -m 0644 $f ${D}${libdir}/libv4l/plugins/
    done
    install -d ${D}${libdir}
    for f in ${DRVROOT}/tegra/lib*; do
	[ -f $f ] || continue
	echo "$f" | grep -q "tegra/libv4l2_nv" || install -m 0644 $f ${D}${libdir}
    done
    for f in ${DRVROOT}/tegra-egl/lib*; do
        install -m 0644 $f ${D}${libdir}
    done
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so
    ln -sf libcuda.so.1.1 ${D}${libdir}/libcuda.so.1
    for libname in nvdsbufferpool nvbuf_fdmap nvbufsurface nvbufsurftransform nvbuf_utils; do
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so.1
	ln -sf lib$libname.so.1.0.0 ${D}${libdir}/lib$libname.so
    done
    ln -sf libnvid_mapper.so.1.0.0 ${D}${libdir}/libnvid_mapper.so.1
    ln -sf libnvid_mapper.so.1.0.0 ${D}${libdir}/libnvid_mapper.so
    rm -f ${D}${libdir}/libdrm* ${D}${libdir}/libnvphsd* ${D}${libdir}/libnvgov*
    rm -f ${D}${libdir}/libv4l2.so* ${D}${libdir}/libv4lconvert.so* ${D}${libdir}/libnvv4l2.so ${D}${libdir}/libnvv4lconvert.so
    rm -f ${D}${libdir}/libvulkan.so*
    soname_fixup libnvidia-fatbinaryloader ${D}${libdir}
    soname_fixup libnvidia-ptxjitcompiler ${D}${libdir}
    install -d ${D}${sbindir}
    install -m755 ${B}/usr/sbin/nvargus-daemon ${D}${sbindir}/
    install -d ${D}${datadir}/glvnd/egl_vendor.d
    install -m644 ${DRVROOT}/tegra-egl/nvidia.json ${D}${datadir}/glvnd/egl_vendor.d/10-nvidia.json
    install -d ${D}/usr/lib/aarch64-linux-gnu/tegra
    install -m644 ${DRVROOT}/tegra/nvidia_icd.json ${D}/usr/lib/aarch64-linux-gnu/tegra/
    install -d ${D}${sysconfdir}/vulkan/icd.d
    ln -sf /usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json ${D}${sysconfdir}/vulkan/icd.d/
    rm ${D}${libdir}/libnvidia-egl-wayland*
}

pkg_postinst_${PN}() {
    # argus and scf libraries hard-coded to use this path
    install -d $D/usr/lib/aarch64-linux-gnu/tegra-egl
    ln $D${libdir}/libEGL_nvidia.so.0 $D/usr/lib/aarch64-linux-gnu/tegra-egl/
}

PACKAGES = "${PN}-libv4l-plugins ${PN}-argus ${PN}-argus-daemon-base ${PN}-libnvosd ${PN}-dev ${PN}"

FILES_${PN}-libv4l-plugins = "${libdir}/libv4l"
FILES_${PN}-argus = "${libdir}/libnvargus*"
FILES_${PN}-argus-daemon-base = "${sbindir}/nvargus-daemon"
FILES_${PN}-libnvosd = "${libdir}/libnvosd*"
FILES_${PN} = "${libdir} ${sbindir} ${nonarch_libdir} ${localstatedir} ${sysconfdir} ${datadir} \
               /usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json"
FILES_${PN}-dev = "${libdir}/lib*GL*.so"
RDEPENDS_${PN} = "libasound"
RDEPENDS_${PN}-argus = "tegra-argus-daemon"
RDEPENDS_${PN}-argus-daemon-base = "${PN} libglvnd"
RDEPENDS_${PN}-libnvosd = "${PN} pango cairo glib-2.0"
RRECOMMENDS_${PN}-libnvosd = "liberation-fonts"
RRECOMMENDS_${PN} = "kernel-module-nvgpu"

INSANE_SKIP_${PN}-libv4l-plugins = "dev-so textrel ldflags build-deps"
INSANE_SKIP_${PN} = "dev-so textrel ldflags build-deps libdir"
INSANE_SKIP_${PN}-argus = "dev-so ldflags"
INSANE_SKIP_${PN}-argus-daemon-base = "ldflags"
INSANE_SKIP_${PN}-libnvosd = "dev-so ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
