CUDA_PKGNAMES = "libcublas-11-4_11.6.5.24-1_${CUDA_DEB_PKGARCH}.deb libcublas-dev-11-4_11.6.5.24-1_${CUDA_DEB_PKGARCH}.deb"

require cuda-shared-binaries.inc

SRC_COMMON_DEBS = "libcublas-11-4_11.6.5.24-1_${CUDA_DEB_PKGARCH}.deb;name=main;subdir=${BP} \
                   libcublas-dev-11-4_11.6.5.24-1_${CUDA_DEB_PKGARCH}.deb;name=dev;subdir=${BP} \
                   ${CUDA_LICENSE_PKG}"

MAINSUM = "7c8b627011842dedafff52d205169c9cb16099a2c29c6b219671332ac8481300"
MAINSUM:x86-64 = "efd6989af7f46a517451429a91453a85881bf0b7fed2348e5efd67df50ee2b0f"
DEVSUM = "eb555f81d063dccae3065665673a4f479beedb28a1fed17e6bde7b68ede2b439"
DEVSUM:x86-64 = "3836e570295b6448ceea91d26688ebd1e7300f29474a89caf7f48334589c78fa"

BBCLASSEXTEND = "native nativesdk"
