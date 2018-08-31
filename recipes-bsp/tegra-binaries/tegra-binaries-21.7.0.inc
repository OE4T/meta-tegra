LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f"

SRC_URI = "https://developer.download.nvidia.com/embedded/L4T/r21_Release_v7.0/Tegra124_Linux_R${PV}_armhf.tbz2"
SRC_URI[md5sum] = "2139c0c7ecff94da68aef8a6ad0df20b"
SRC_URI[sha256sum] = "676add1e8e6b2fcf76d97f22f38c9d0cbbe8a92342039a85c8a4c87e8c1ce824"

SRC_URI_append_tegra124 = "\
        file://nvstartup.service       \
        file://nvstartup.init          \
        file://nvstartup.sh            \
"


S = "${TMPDIR}/work-shared/tegra-binaries-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra124)"
