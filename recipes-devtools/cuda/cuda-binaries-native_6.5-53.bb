require cuda-binaries-native-common.inc

CUDA_VERSION ?= "6.5"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-${CUDA_VERSION}/doc/EULA.txt;md5=035ceb64718e9de387e31cd635e782a9"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/005/linux-x64/cuda-repo-ubuntu1404-6-5-local_${PV}_amd64.deb"
SRC_URI[md5sum] = "0be04d4e6d93a1b564a4df23584c387e"
SRC_URI[sha256sum] = "41c34c4374ffa1967333bc9f75c00e5dfc0be8bc45866d5a2b0418c9fd1cef61"
