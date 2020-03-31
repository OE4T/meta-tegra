CUDA_REPO_EXTRA = "-10.0.326-410.108"

require cuda-binaries-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=049f8f2a31b8c3cc436cc63f0a72c9c9"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-ubuntu1604-10-0-local-10.0.326-410.108_1.0-1_amd64.deb"
SRC_URI[md5sum] = "07ff157a7980aed07d0eafeebe86d64a"
SRC_URI[sha256sum] = "b476fa6a5b702ac55977a9d9cad4d1761a850fc3fd76bd7caf04eb4f15b288a3"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_HOST = "(x86_64)"
BBCLASSEXTEND = "native nativesdk"
