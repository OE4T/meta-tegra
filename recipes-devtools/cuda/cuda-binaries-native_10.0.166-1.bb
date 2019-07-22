CUDA_REPO_EXTRA = "-10.0.166-410.62"

require cuda-binaries-native-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=987e5f80d382a9feae03e4afd8855dea"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-ubuntu1604-10-0-local-10.0.166-410.62_1.0-1_amd64.deb"
SRC_URI[md5sum] = "dd2cfc03503ea3ed45d1d2bdf51c3d16"
SRC_URI[sha256sum] = "ec0cd98f99836b9be816a4842ecd03ededbe38943b0cafd8b143357f7b007b40"
do_unpack[depends] += "xz-native:do_populate_sysroot"
