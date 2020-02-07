SUMMARY = "NVIDIA container runtime hook"
DESCRIPTION = "NVIDIA container runtime hook \
Provides a OCI hook to enable GPU support in containers. \
"
#Home page for now
HOMEPAGE = "https://github.com/NVIDIA/nvidia-container-runtime"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
REVISION = "r1"

RDEPENDS_${PN} = "\
cuda-driver \
cuda-toolkit \
libnvidia-container-tools \
docker-ce \
"
SRCREV_NVIDIA_TOOLKIT = "60f165ad6901f85b0c3acbf7ce2c66cd759c4fb8"

GO_IMPORT = "github.com/NVIDIA/${BPN}"

inherit go

SRC_URI = " \
git://github.com/NVIDIA/container-toolkit;protocol=https;subpath=${BPN};name=go \
git://github.com/NVIDIA/container-toolkit;protocol=https;destsuffix=git;name=toolkit \
"

SRCREV_toolkit = "${SRCREV_NVIDIA_TOOLKIT}"
SRCREV_go = "${SRCREV_NVIDIA_TOOLKIT}"
    

DEPENDS += "\
"

do_install_append(){
install -d -m 755 ${D}${sysconfdir}/nvidia-container-runtime ${D}${libexecdir}/oci/hooks.d ${D}/${datadir}/oci/hooks.d ${D}/${datadir}/licenses/${BPN}-${PV}
install ${WORKDIR}/git/config/config.toml.centos ${D}${sysconfdir}/nvidia-container-runtime/config.toml
install ${WORKDIR}/git/oci-nvidia-hook ${D}${libexecdir}/oci/hooks.d
install ${WORKDIR}/git/oci-nvidia-hook.json ${D}/${datadir}/oci/hooks.d
install ${WORKDIR}/git/LICENSE ${D}/${datadir}/licenses/${BPN}-${PV}
ln -sf  ${bindir}/nvidia-container-toolkit ${D}${bindir}/nvidia-container-runtime-hook
}
FILES_${PN} += " \
${datadir} \
${datadir}/oci/ \
${datadir}/oci/hooks.d \
"
FILES_${PN}-lic += "${datadir}/licenses/${BPN}-${PV}"
