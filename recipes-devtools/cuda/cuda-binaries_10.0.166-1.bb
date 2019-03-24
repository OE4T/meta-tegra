CUDA_REPO_EXTRA = "-10.0.166"

require cuda-binaries-common.inc

inherit nvidia_devnet_downloads

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-10.0/LICENSE;md5=dfb2d23fe5070ac47b201fbf1e497891 \
	            file://usr/local/cuda-10.0/doc/EULA.txt;md5=987e5f80d382a9feae03e4afd8855dea"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/cuda-repo-l4t-10-0-local-10.0.166_1.0-1_arm64.deb"
SRC_URI[md5sum] = "5e3eedc3707305f9022d41754d6becde"
SRC_URI[sha256sum] = "e47e37672df5cccf0fafd5f82508e94e6bb2d5b1aafc57584b0b88b14e0a3319"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

