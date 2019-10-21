CUDA_REPO_EXTRA = "-10.0.166-410.62"

require cuda-binaries-native-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=987e5f80d382a9feae03e4afd8855dea"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-ubuntu1604-10-0-local-10.0.166-410.62_1.0-1_amd64.deb"
SRC_URI[md5sum] = "ae9eb96f288847ce993dbfc109fa4790"
SRC_URI[sha256sum] = "3bf80662894ae76acd93bef63de9d559d08f27c9c54262b14264c114f7cc115e"
do_unpack[depends] += "xz-native:do_populate_sysroot"
