SUMMARY = "NVIDIA container runtime"
DESCRIPTION = "NVIDIA container runtime hook \
Provides a modified version of runc allowing users to run GPU enabled \
containers. \
"
HOMEPAGE = "https://github.com/NVIDIA/nvidia-container-runtime"

LICENSE = "BSD-3-Clause & Apache-2.0 & MIT & ISC"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=06cff45c51018e430083a716510821b7 \
                    file://src/${GO_IMPORT}/runtime/rpm/SOURCES/LICENSE;md5=8b6d801605bfda3202cd9376f389e96b \
		    file://src/${GO_INSTALL}/vendor/github.com/pmezard/go-difflib/LICENSE;md5=e9a2ebb8de779a07500ddecca806145e \
		    file://src/${GO_INSTALL}/vendor/github.com/pelletier/go-toml/LICENSE;md5=dc9ea87a81f62b8871b2a4158edbfde6 \
		    file://src/${GO_INSTALL}/vendor/github.com/opencontainers/runtime-spec/LICENSE;md5=b355a61a394a504dacde901c958f662c \
		    file://src/${GO_INSTALL}/vendor/github.com/davecgh/go-spew/LICENSE;md5=c06795ed54b2a35ebeeb543cd3a73e56 \
		    file://src/${GO_INSTALL}/vendor/github.com/stretchr/testify/LICENSE;md5=d4c9e9b2abd3afaebed1524a9a77b937"

SRC_URI = " \
    git://github.com/NVIDIA/nvidia-container-runtime;protocol=https \
    file://l4t.csv \
"
SRCREV = "027d6c876ad8c163f53e75cbd5a6a54776985e5c"

GO_IMPORT = "github.com/NVIDIA/${BPN}"
GO_INSTALL = "${GO_IMPORT}/runtime/src"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "virtualization"

inherit go features_check

do_compile() {
    cd ${B}/src/${GO_INSTALL}
    mkdir -p ${B}/${GO_BUILD_BINDIR}
    ${GO} build ${GOBUILDFLAGS} -o ${B}/${GO_BUILD_BINDIR}/nvidia-container-runtime
    cd ${B}
}

do_install_append_tegra() {
    install -d -m 755 ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
    install -m 0644 ${WORKDIR}/l4t.csv ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d/l4t.csv
}

RDEPENDS_${PN} = "\
    nvidia-container-toolkit \
    docker-ce \
"
RDEPENDS_${PN}-dev += "bash make"
