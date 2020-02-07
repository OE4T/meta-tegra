SUMMARY = "nvidia-docker CLI wrapper"
DESCRIPTION = "Replaces nvidia-docker with a new implementation based on nvidia-container-runtime"
HOMEPAGE = "https://github.com/NVIDIA/nvidia-docker"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
REVISION = "r1"

RDEPENDS_${PN} = "nvidia-container-runtime bash"

S = "${WORKDIR}/git"
SRC_URI = " \
git://github.com/NVIDIA/nvidia-docker;protocol=https \
"

SRCREV = "0f10e4f8c53a14142557ca3ba3452120b2b53a60"
   
do_configure(){
} 

do_compile(){
}

do_install_append(){
install -d -m 755 ${D}/${bindir} ${D}/${sysconfdir}/docker
install -m 755 ${S}/nvidia-docker ${D}/${bindir}
install -m 644 ${S}/daemon.json ${D}/${sysconfdir}/docker
}

