require cuda-binaries-common.inc

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-9.0/LICENSE;md5=d87e5ceb8a41dd716b3205c4e00aa729 \
	            file://usr/local/cuda-9.0/doc/EULA.txt;md5=1d9340fbe1f77282520c3ef05235c26a"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2/pwv346/JetPackL4T_32_b157/cuda-repo-l4t-9-0-local_${PV}_arm64.deb"
SRC_URI[md5sum] = "23454f5c652f429195428c0b30265859"
SRC_URI[sha256sum] = "00a4e695aa73e2f93364a6a4f6cf4ee13afdab3f56056a9a666c4b761c111442"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_MACHINE = "(tegra186)"

PR = "rc0"

