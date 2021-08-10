DESCRIPTION = "NVIDIA Deepstream SDK"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://usr/share/doc/deepstream-5.1/copyright;md5=51be1eeb26038570c51ffe07b0097891 \
    file://opt/nvidia/deepstream/deepstream-5.1/LICENSE.txt;md5=433007b08df9ec24f46deff7572d671c \
    file://opt/nvidia/deepstream/deepstream-5.1/doc/nvidia-tegra/LICENSE.iothub_client;md5=4f8c6347a759d246b5f96281726b8611 \
    file://opt/nvidia/deepstream/deepstream-5.1/doc/nvidia-tegra/LICENSE.nvds_amqp_protocol_adaptor;md5=8b4b651fa4090272b2e08e208140a658"
HOMEPAGE = "https://developer.nvidia.com/deepstream-sdk"

inherit l4t_deb_pkgfeed

SRC_COMMON_DEBS = "${BPN}_${PV}_arm64.deb;subdir=${BPN}"
SRC_URI[sha256sum] = "68b5ddff8b8682ed657fe8554cfb492f36621274a7593d9660f335ce8ba926a5"

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[amqp] = ",,rabbitmq-c"
PACKAGECONFIG[kafka] = ",,librdkafka"
# NB: requires hiredis 1.0.0+
PACKAGECONFIG[redis] = ",,hiredis"

DEPENDS = "glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-rtsp-server tensorrt libnvvpi1 libvisionworks json-glib tegra-libraries"

S = "${WORKDIR}/${BPN}"
B = "${WORKDIR}/build"

DEEPSTREAM_PATH = "/opt/nvidia/deepstream/deepstream-5.1"
SYSROOT_DIRS += "${DEEPSTREAM_PATH}/lib/"

do_configure() {
    for feature in amqp kafka redis; do
	if ! echo "${PACKAGECONFIG}" | grep -q "$feature"; then
	    rm -f ${S}${DEEPSTREAM_PATH}/lib/libnvds_${feature}*
	fi
    done
}

do_install() {
    install -d ${D}${bindir}/
    install -m 0755 ${S}${DEEPSTREAM_PATH}/bin/* ${D}${bindir}/

    install -d ${D}${DEEPSTREAM_PATH}/lib/
    for f in ${S}${DEEPSTREAM_PATH}/lib/*; do
        [ ! -d "$f" ] || continue
        install -m 0644 "$f" ${D}${DEEPSTREAM_PATH}/lib/
    done
    ln -sf libnvds_msgconv.so.1.0.0 ${D}${DEEPSTREAM_PATH}/lib/libnvds_msgconv.so
    # Requires Python 3.6
    rm -f ${D}${DEEPSTREAM_PATH}/lib/pyds.so

    for i in 1 2; do
        path="${DEEPSTREAM_PATH}/lib/triton_backends/tensorflow$i"
	install -d ${D}$path
	cp --preserve=mode,timestamps,links --no-dereference ${S}$path/* ${D}$path/
    done

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
INSANE_SKIP = "dev-so ldflags"

def pkgconf_packages(d):
    pkgconf = bb.utils.filter('PACKAGECONFIG', 'amqp kafka redis', d).split()
    pn = d.getVar('PN')
    return ' '.join(['{}-{}'.format(pn, p) for p in pkgconf])

PKGCONF_PACKAGES = "${@pkgconf_packages(d)}"

PACKAGES =+ "${PN}-samples-data ${PN}-samples ${PN}-sources ${PN}-iothub-client ${PN}-azure-edge ${PN}-triton-plugins ${PKGCONF_PACKAGES}"

FILES:${PN} = "${sysconfdir}/ld.so.conf.d/  \
	       ${libdir}/gstreamer-1.0/deepstream \
	       ${DEEPSTREAM_PATH}/lib \
	      "

FILES:${PN}-dev = "${includedir}"

FILES:${PN}-samples = "${bindir}/*"
FILES:${PN}-samples-data = "\
	${DEEPSTREAM_PATH}/samples \
	${DEEPSTREAM_PATH}/sources/apps/sample_apps/*/*.txt \
	${DEEPSTREAM_PATH}/sources/apps/sample_apps/*/README \
	${DEEPSTREAM_PATH}/sources/apps/sample_apps/*/configs/ \
	${DEEPSTREAM_PATH}/sources/apps/sample_apps/*/inferserver/ \
	${DEEPSTREAM_PATH}/sources/apps/sample_apps/*/csv_files/ \
"

FILES:${PN}-sources = "${DEEPSTREAM_PATH}/sources"

FILES:${PN}-iothub-client = "${DEEPSTREAM_PATH}/lib/libiothub_client.so"
FILES:${PN}-azure-edge = "${DEEPSTREAM_PATH}/lib/libazure*"
FILES:${PN}-triton-plugins = "${DEEPSTREAM_PATH}/lib/triton_backends"
FILES:${PN}-amqp = "${DEEPSTREAM_PATH}/lib/libnvds_amqp*"
FILES:${PN}-kafka = "${DEEPSTREAM_PATH}/lib/libnvds_kafka*"
FILES:${PN}-redis = "${DEEPSTREAM_PATH}/lib/libnvds_redis*"

RDEPENDS:${PN} = "libcufft libvisionworks-devso-symlink"
RDEPENDS:${PN}-samples = "${PN}-samples-data"
RDEPENDS:${PN}-samples-data = "bash"
RDEPENDS:${PN}-sources = "bash ${PN}-samples-data ${PN}"
RRECOMMENDS:${PN} = "liberation-fonts"
