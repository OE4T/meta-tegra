CUDA_REPO_EXTRA = "-10.0.117-410.38"

require cuda-binaries-native-common.inc

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=b6b0dd0f286af264e7cd31befef7d738"

PR = "r0"

SRC_URI = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t_prerel/4.0/m39xww/beta40/JetPackL4T_40_b190/16.04/cuda-repo-ubuntu1604-10-0-local-10.0.117-410.38_1.0-1_amd64.deb"
SRC_URI[md5sum] = "563a8ab56c5618b990e1b72d18e14c6f"
SRC_URI[sha256sum] = "89d8a72957aa1737ad15ce2a4e803063972eca26b0e326366d0f755153d66db3"
do_unpack[depends] += "xz-native:do_populate_sysroot"
