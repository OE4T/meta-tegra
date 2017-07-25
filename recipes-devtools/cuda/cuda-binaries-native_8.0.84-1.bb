require cuda-binaries-native-common.inc

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-8.0/doc/EULA.txt;md5=731999c10c8433615a1e9a2b631051f1"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/013/linux-x64/cuda-repo-ubuntu1404-8-0-local_${PV}_amd64.deb"
SRC_URI[md5sum] = "c2e41f9d686e6a93ab562509ca566408"
SRC_URI[sha256sum] = "56c3e0688880458e9005b200ec8e8e3a72c3038c9efb88dfcc7f5b6707860b72"
