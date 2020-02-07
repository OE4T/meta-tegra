SUMMARY = "NVIDIA container runtime"
DESCRIPTION = "NVIDIA container runtime hook \
Provides a modified version of runc allowing users to run GPU enabled \
containers. \
"
HOMEPAGE = "https://github.com/NVIDIA/nvidia-container-runtime"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=06cff45c51018e430083a716510821b7"
REVISION = "r1"

RDEPENDS_${PN} = "\
    cuda-driver \
    cuda-toolkit \
    nvidia-container-toolkit \
    docker-ce \
"
SRCREV_NVIDIA_RUNTIME = "027d6c876ad8c163f53e75cbd5a6a54776985e5c"

GO_IMPORT = "github.com/NVIDIA/${BPN}"

inherit go

SRC_URI = " \
    git://github.com/NVIDIA/nvidia-container-runtime;protocol=https;destsuffix=${S}/src/${GO_IMPORT}/;subpath=runtime/src;name=go \
    git://github.com/NVIDIA/nvidia-container-runtime;protocol=https;destsuffix=git;name=runtime \
    file://l4t.csv \
"

SRCREV_runtime = "${SRCREV_NVIDIA_RUNTIME}"
SRCREV_go = "${SRCREV_NVIDIA_RUNTIME}"
    

DEPENDS += " \
    gettext-native \
"

do_install_append(){
    install -d -m 755 ${D}/${datadir}/licenses/${BPN}-${PV}
    install ${WORKDIR}/git/LICENSE ${D}/${datadir}/licenses/${BPN}-${PV}
    # Remove sh files that will cause a QA dependency error
    rm ${D}/${libdir}/go/src/${GO_IMPORT}/vendor/github.com/stretchr/testify/.travis.*.sh
    rm ${D}/${libdir}/go/src/${GO_IMPORT}/vendor/github.com/pelletier/go-toml/benchmark.sh
    
    # L4T CSV
    install -d -m 755 ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
    envsubst < ${WORKDIR}/l4t.csv > ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d/l4t.csv
}

FILES_${PN} += " \
    ${datadir}/licenses \
    ${datadir}/licenses/${BPN}-${PV} \
    ${sysconfdir}/nvidia-container-runtime \
    ${sysconfdir}/nvidia-container-runtime/host-files-for-container.d \
"

FILES_${PN}-lic = " \
    ${datadir}/licenses \
    ${datadir}/licenses/${BPN}-${PV} \
"

