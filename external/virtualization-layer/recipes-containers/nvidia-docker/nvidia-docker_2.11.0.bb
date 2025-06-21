SUMMARY = "nvidia-docker CLI wrapper"
DESCRIPTION = "Replaces nvidia-docker with a new implementation based on nvidia-container-runtime"
HOMEPAGE = "https://github.com/NVIDIA/nvidia-docker"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/NVIDIA/nvidia-docker.git;protocol=https;branch=master"
SRCREV = "178cec600ce52270f4b9d22326ba1fde218dba39"

REQUIRED_DISTRO_FEATURES = "virtualization"

inherit features_check

do_configure() {
    :
} 

do_compile() {
    :
}

do_install() {
    install -d ${D}/${bindir} ${D}/${sysconfdir}/docker
    install -m 755 ${S}/nvidia-docker ${D}/${bindir}
    install -m 644 ${S}/daemon.json ${D}/${sysconfdir}/docker
}

RDEPENDS:${PN} = "nvidia-container-toolkit bash"
