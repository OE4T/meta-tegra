DESCRIPTION = "NVIDIA Tegra Multimedia API headers and examples"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/007/linux-x64/Tegra_Multimedia_API_R${PV}_aarch64.tbz2 \
	   file://make-x11-conditional.patch"
SRC_URI[md5sum] = "d27734a6fbc0aecdddda82ebe605c0c6"
SRC_URI[sha256sum] = "2198ddd1d5217faf4634826aefd70ad80023fa94f0f2f26e89f5badd68295730"

COMPATIBLE_MACHINE = "(tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

DEPENDS = "tegra-libraries virtual/egl virtual/libgles1 virtual/libgles2 cuda-toolkit cuda-tools-native jpeg expat gstreamer1.0 glib-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "-DWITH_X11,,virtual/libx11 gtk+3"

inherit cmake pkgconfig

S = "${WORKDIR}/tegra_multimedia_api"
B = "${S}"

OECMAKE_SOURCEPATH = "${S}/argus"
EXTRA_OECMAKE = "-DARGUS_INCLUDE_DIR=${S}/argus/include \
                 -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_TARGET}/usr/local/cuda-8.0 \
                 -DMULTIPROCESS=ON"               

do_configure() {
    rm -rf ${S}/include/EGL ${S}/include/GL* ${S}/include/KHR* ${S}/include/libjpeg* ${S}/include/libv4l2.h
    cd ${OECMAKE_SOURCEPATH}
    cmake_do_configure
}

do_compile() {
    oe_runmake -C ${S}/argus
}

do_install() {
    oe_runmake -C ${S}/argus "DESTDIR=${D}" install
    install -d ${D}${includedir}
    cp -R --preserve=mode,timestamps ${S}/argus/include/Argus ${D}${includedir}
    cp --preserve=mode,timestamps ${S}/include/*.h ${D}${includedir}
}
