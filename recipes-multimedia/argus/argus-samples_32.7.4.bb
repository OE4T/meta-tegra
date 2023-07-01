DESCRIPTION = "NVIDIA Argus API sample programs"
HOMEPAGE = "http://developer.nvidia.com"
LICENSE = "Proprietary & BSD"

require tegra-mmapi-${PV}.inc

SRC_URI += "\
    file://0001-argus-apps-camera-replace-xxd-invocation-with-shell-.patch \
    file://0002-Remove-references-to-non-existent-samples.patch \
"

DEPENDS = "tegra-mmapi virtual/egl virtual/libgles1 virtual/libgles2 jpeg expat gstreamer1.0 glib-2.0 coreutils-native"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2cc00be68c1227a7c42ff3620ef75d05 \
		    file://argus/LICENSE.TXT;md5=271791ce6ff6f928d44a848145021687"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "-DWITH_X11=ON,,virtual/libx11 gtk+3"

inherit cmake pkgconfig cuda

OECMAKE_SOURCEPATH = "${S}/argus"
EXTRA_OECMAKE = "-DMULTIPROCESS=ON \
                 -DCMAKE_INCLUDE_PATH=${S}/include/libjpeg-8b-tegra \
                 -DJPEG_NAMES=libnvjpeg.so"
