SUMMARY = "NVIDIA container runtime hook"
DESCRIPTION = "NVIDIA container runtime hook \
Provides an OCI hook to enable GPU support in containers. \
"
#Home page for now
HOMEPAGE = "https://github.com/NVIDIA/nvidia-container-runtime"

LICENSE = "Apache-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://src/${GO_IMPORT}/pkg/rpm/SOURCES/LICENSE;md5=06cff45c51018e430083a716510821b7 \
                    file://src/${GO_INSTALL}/vendor/github.com/BurntSushi/toml/COPYING;md5=9e24c0e2a784c1d1fcabb279f4f107e0"
SRC_URI = "git://github.com/NVIDIA/nvidia-container-toolkit.git;protocol=https;branch=main"
SRCREV = "60f165ad6901f85b0c3acbf7ce2c66cd759c4fb8"

GO_IMPORT = "github.com/NVIDIA/${BPN}"
GO_INSTALL = "${GO_IMPORT}/${BPN}"

export GO111MODULE = "off"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "virtualization"

inherit go features_check

do_install:append(){
    install -d ${D}${sysconfdir}/nvidia-container-runtime ${D}${libexecdir}/oci/hooks.d ${D}/${datadir}/oci/hooks.d
    install ${S}/src/${GO_IMPORT}/config/config.toml.centos ${D}${sysconfdir}/nvidia-container-runtime/config.toml
    install ${S}/src/${GO_IMPORT}/oci-nvidia-hook ${D}${libexecdir}/oci/hooks.d
    install ${S}/src/${GO_IMPORT}/oci-nvidia-hook.json ${D}/${datadir}/oci/hooks.d
    ln -sf nvidia-container-toolkit ${D}${bindir}/nvidia-container-runtime-hook
}

FILES:${PN} += "${datadir}/oci"

RDEPENDS:${PN} = "\
    libnvidia-container-tools \
    docker \
"
RDEPENDS:${PN}-dev += "bash make"
