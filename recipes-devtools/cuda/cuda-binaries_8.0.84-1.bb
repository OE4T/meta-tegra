require cuda-binaries-common.inc

CUDA_VERSION ?= "8.0"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-${CUDA_VERSION}/doc/EULA.txt;md5=731999c10c8433615a1e9a2b631051f1"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/013/linux-x64/cuda-repo-l4t-8-0-local_${PV}_arm64.deb"
SRC_URI[md5sum] = "0d1eb183b3569f8d7f7aa895ae2f1111"
SRC_URI[sha256sum] = "01effce5a9d03dc2eac6dfa9fdf2357d2cd2227ff7bbf785584de976d0815e8d"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
