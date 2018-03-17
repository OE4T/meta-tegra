require cuda-binaries-common.inc

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-9.0/LICENSE;md5=d87e5ceb8a41dd716b3205c4e00aa729 \
	            file://usr/local/cuda-9.0/doc/EULA.txt;md5=1d9340fbe1f77282520c3ef05235c26a"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2GA/m892ki/JetPackL4T_32_b196/cuda-repo-l4t-9-0-local_${PV}_arm64.deb"
SRC_URI[md5sum] = "9385806be429ad92d49f0750f748e2b2"
SRC_URI[sha256sum] = "02cced9392175cd0983c34dc774bd296c75f7d556ce8eae35787f041e70b6e94"
do_unpack[depends] += "xz-native:do_populate_sysroot"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"

PR = "r0"

