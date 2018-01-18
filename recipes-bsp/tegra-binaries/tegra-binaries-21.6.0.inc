LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r21_Release_v6.0/Tegra124_Linux_R${PV}_armhf.tbz2"
SRC_URI[md5sum] = "e82193b117171243ea21c908fbd347ed"
SRC_URI[sha256sum] = "b9dfbdd0d21d6454e5e9668673a3494e7567698764673938d4c1ef83f42ee72d"

SRC_URI += "\
        file://tegra124/nvstartup.service       \
        file://tegra124/nvstartup.sh            \
"


S = "${TMPDIR}/work-shared/tegra-binaries-${PV}-${PR}/Linux_for_Tegra"
B = "${WORKDIR}/build"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

COMPATIBLE_MACHINE = "(tegra124)"
