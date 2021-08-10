CUDA_PKGNAMES = "libcublas10_10.2.3.300-1_${CUDA_DEB_PKGARCH}.deb libcublas-dev_10.2.3.300-1_${CUDA_DEB_PKGARCH}.deb"
CUDA_FULL_VERSION = "10.2.300-1"

require cuda-shared-binaries-${CUDA_FULL_VERSION}.inc

SRC_COMMON_DEBS = "libcublas10_10.2.3.300-1_${CUDA_DEB_PKGARCH}.deb;name=main;subdir=${BP} \
                   libcublas-dev_10.2.3.300-1_${CUDA_DEB_PKGARCH}.deb;name=dev;subdir=${BP} \
                   ${CUDA_LICENSE_PKG}"
MAINSUM = "a4cde28215c01bce78fa7c3d09909afaec487142e1144646bd7ff043a5c6c1df"
MAINSUM:x86-64 = "4e2c3e11c4c74511788b9a75c47895a8000e651754c27d99dc620361cfbed9be"
DEVSUM = "f2575fd6c86c9aa8eed0e782d68122d9ba3d1422ebd6e6c06c71bea096c169db"
DEVSUM:x86-64 = "ca5a21e8ec1a3137f39f9d460bb11ec6cefa88d89cf82099fcb8da7a385125ae"

BBCLASSEXTEND = "native nativesdk"
