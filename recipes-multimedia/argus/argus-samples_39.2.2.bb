DESCRIPTION = "NVIDIA Argus API sample programs"
HOMEPAGE = "http://developer.nvidia.com"

require tegra-mmapi-${PV}.inc

SRC_URI += "file://0001-argus-apps-camera-replace-xxd-invocation-with-shell-.patch \
            file://0005-argus-allow-an-explicit-CUDA-driver-library-path.patch"

DEPENDS = "tegra-mmapi tegra-libraries-cuda virtual/egl virtual/libgles1 virtual/libgles2 jpeg expat gstreamer1.0 glib-2.0 coreutils-native"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "-DWITH_X11=ON,,virtual/libx11 gtk+3"

inherit cmake pkgconfig cuda

OECMAKE_SOURCEPATH = "${S}/argus"
EXTRA_OECMAKE = "-DMULTIPROCESS=ON \
                 -DARGUS_CUDA_DRV_LIB_PATH:PATH=${STAGING_LIBDIR} \
                 -DCMAKE_INCLUDE_PATH=${S}/include/libjpeg-8b-tegra \
                 -DJPEG_NAMES=libnvjpeg.so \
                 -DCMAKE_POLICY_VERSION_MINIMUM=3.5"
