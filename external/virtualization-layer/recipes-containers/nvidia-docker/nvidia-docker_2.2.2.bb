SUMMARY = "nvidia-docker CLI wrapper"
DESCRIPTION = "Replaces nvidia-docker with a new implementation based on nvidia-container-runtime"
HOMEPAGE = "https://github.com/NVIDIA/nvidia-docker"

LICENSE = "Apache-2.0 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://rpm/SOURCES/LICENSE;md5=ed1feacfb98290390086ae21adb37f38"

SRC_URI = "git://github.com/NVIDIA/nvidia-docker;protocol=https"
SRCREV = "0f10e4f8c53a14142557ca3ba3452120b2b53a60"

S = "${WORKDIR}/git"

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

RDEPENDS_${PN} = "nvidia-container-runtime bash"
