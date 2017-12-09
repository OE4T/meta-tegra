require cuda-binaries-native-common.inc

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/local/cuda-9.0/LICENSE;md5=d87e5ceb8a41dd716b3205c4e00aa729 \
	            file://usr/local/cuda-9.0/doc/EULA.txt;md5=1d9340fbe1f77282520c3ef05235c26a"

PR = "rc0"

SRC_URI = "http://developer.download.nvidia.com/devzone/devcenter/mobile/jetpack_l4t/3.2/pwv346/JetPackL4T_32_b157/cuda-repo-ubuntu1604-9-0-local_${PV}_amd64.deb"
SRC_URI[md5sum] = "d30e035f20d83b377147dbf8d795e5b7"
SRC_URI[sha256sum] = "afd9f4cad61d2021bcd23e79b3c89f8e4ca98689441c09ebe20f7099e890f150"
do_unpack[depends] += "xz-native:do_populate_sysroot"
