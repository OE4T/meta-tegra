CUDA_REPO_EXTRA = "-10.0.117"

require cuda-binaries-common.inc

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=b6b0dd0f286af264e7cd31befef7d738"

SRC_URI = "https://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t_prerel/4.0/m39xww/beta40/JetPackL4T_40_b190/cuda-repo-l4t-10-0-local-10.0.117_1.0-1_arm64.deb"
SRC_URI[md5sum] = "82616939c933a7aa33a073bbaf029f42"
SRC_URI[sha256sum] = "0368c933ad9dbfa8f4376390bfb0b5a2183d980dbca43f31807219ff5a6b6d62"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"

PR = "r0"

