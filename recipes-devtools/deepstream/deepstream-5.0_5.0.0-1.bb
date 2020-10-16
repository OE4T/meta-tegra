DESCRIPTION = "NVIDIA Deepstream SDK"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://usr/share/doc/deepstream-5.0/copyright;md5=f635f9f375e764ce281a2070599e2457 \
    file://opt/nvidia/deepstream/deepstream-5.0/LICENSE.txt;md5=c614b345088f989a9c05a7393419bf55 \
    file://opt/nvidia/deepstream/deepstream-5.0/doc/nvidia-tegra/LICENSE.iothub_client;md5=4f8c6347a759d246b5f96281726b8611 \
    file://opt/nvidia/deepstream/deepstream-5.0/doc/nvidia-tegra/LICENSE.nvds_amqp_protocol_adaptor;md5=8b4b651fa4090272b2e08e208140a658"
HOMEPAGE = "https://developer.nvidia.com/deepstream-sdk"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "${BPN}_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "a7a7015515883ac88c7587c7a2acfcf78510e539b84b702afd05f4f330faa55e"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

DEPENDS = "gstreamer1.0 gstreamer1.0-rtsp-server tensorrt cudnn libcublas cuda-cudart tegra-libraries"

S = "${WORKDIR}/${BPN}"
B = "${WORKDIR}/build"

DEEPSTREAM_PATH = "/opt/nvidia/deepstream/deepstream-5.0"
SYSROOT_DIRS += "${DEEPSTREAM_PATH}/lib/"

do_install() {
    install -d ${D}${bindir}/
    install -m 0755 ${S}${DEEPSTREAM_PATH}/bin/* ${D}${bindir}/

    install -d ${D}${DEEPSTREAM_PATH}/lib/
    for f in ${S}${DEEPSTREAM_PATH}/lib/*; do
        [ ! -d "$f" ] || continue
        install -m 0644 "$f" ${D}${DEEPSTREAM_PATH}/lib/
    done
    
    install -d ${D}${DEEPSTREAM_PATH}/lib/tensorflow
    cp --preserve=mode,timestamps,links --no-dereference ${S}${DEEPSTREAM_PATH}/lib/tensorflow/* ${D}${DEEPSTREAM_PATH}/lib/tensorflow

    install -d ${D}/${sysconfdir}/ld.so.conf.d/
    echo "${DEEPSTREAM_PATH}/lib" > ${D}/${sysconfdir}/ld.so.conf.d/deepstream.conf

    install -d ${D}${libdir}/gstreamer-1.0/deepstream
    install -m 0644 ${S}${DEEPSTREAM_PATH}/lib/gst-plugins/* ${D}${libdir}/gstreamer-1.0/deepstream/

    cp -R --preserve=mode,timestamps ${S}${DEEPSTREAM_PATH}/samples ${D}${DEEPSTREAM_PATH}/

    install -d ${D}${includedir}/deepstream
    cp -R --preserve=mode,timestamps ${S}${DEEPSTREAM_PATH}/sources/includes/* ${D}${includedir}/

    cp -R --preserve=mode,timestamps ${S}${DEEPSTREAM_PATH}/sources/ ${D}${DEEPSTREAM_PATH}/
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

RDEPENDS_${PN} = "glib-2.0 gstreamer1.0 libgstvideo-1.0 libgstrtspserver-1.0 libgstapp-1.0 json-glib \
	          libvisionworks"
RDEPENDS_${PN}-samples = "bash json-glib"
RDEPENDS_${PN}-sources = "bash"

PACKAGES += "${PN}-samples ${PN}-sources"

FILES_${PN} = "${sysconfdir}/ld.so.conf.d/  \
	       ${libdir}/gstreamer-1.0/deepstream \
	       ${DEEPSTREAM_PATH}/lib \
	      "

FILES_${PN}-dev = "${includedir}"

FILES_${PN}-samples = "${bindir}/* ${DEEPSTREAM_PATH}/samples \
		       ${DEEPSTREAM_PATH}/sources/apps/sample_apps/*/*.txt \
		       ${DEEPSTREAM_PATH}/sources/apps/sample_apps/deepstream-test5/configs/"

FILES_${PN}-sources = "${DEEPSTREAM_PATH}/sources"

INSANE_SKIP_${PN} = "dev-so ldflags"
INSANE_SKIP_${PN}-samples = "ldflags"
