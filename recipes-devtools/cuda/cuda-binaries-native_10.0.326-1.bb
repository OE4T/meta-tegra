CUDA_REPO_EXTRA = "-10.0.326-410.108"

require cuda-binaries-native-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=049f8f2a31b8c3cc436cc63f0a72c9c9"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-ubuntu1804-10-0-local-10.0.326-410.108_1.0-1_amd64.deb"
SRC_URI[md5sum] = "0396239108d46ab682fb90468ea937b6"
SRC_URI[sha256sum] = "94ae0add742224198eaa059353fc218ba3e90fe008bafa01e00f547600c3afdf"
do_unpack[depends] += "xz-native:do_populate_sysroot"
