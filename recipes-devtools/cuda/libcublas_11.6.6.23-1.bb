CUDA_PKGNAMES = "libcublas-11-4_11.6.6.23-1_${CUDA_DEB_PKGARCH}.deb libcublas-dev-11-4_11.6.6.23-1_${CUDA_DEB_PKGARCH}.deb"

require cuda-shared-binaries.inc

SRC_COMMON_DEBS = "libcublas-11-4_11.6.6.23-1_${CUDA_DEB_PKGARCH}.deb;name=main;subdir=${BP} \
                   libcublas-dev-11-4_11.6.6.23-1_${CUDA_DEB_PKGARCH}.deb;name=dev;subdir=${BP} \
                   ${CUDA_LICENSE_PKG}"

COMPATIBLE_HOST:x86-64 = "(-)"
MAINSUM = "4e9499122c3921073869970580f78c31beaa65b58f8345600ad2db99297f27e5"
DEVSUM = "268491c19854a47d1fc8e7530301da318c159b50ed94b3d1579792d8a4bbbd90"

BBCLASSEXTEND = "native nativesdk"
