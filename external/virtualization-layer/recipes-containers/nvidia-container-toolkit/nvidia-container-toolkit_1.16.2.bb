SUMMARY = "NVIDIA container runtime"
DESCRIPTION = "NVIDIA container runtime hook \
Provides a modified version of runc allowing users to run GPU enabled \
containers. \
"
HOMEPAGE = "https://github.com/NVIDIA/nvidia-container-runtime"

COMPATIBLE_MACHINE = "(tegra)"

LICENSE = "Apache-2.0 & MIT & ISC & MPL-2.0 & (Apache-2.0 | MIT) & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://src/${GO_IMPORT}/vendor/tags.cncf.io/container-device-interface/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://src/${GO_IMPORT}/vendor/github.com/davecgh/go-spew/LICENSE;md5=c06795ed54b2a35ebeeb543cd3a73e56 \
                    file://src/${GO_IMPORT}/vendor/github.com/fsnotify/fsnotify/LICENSE;md5=8bae8b116e2cfd723492b02d9a212fe2 \
                    file://src/${GO_IMPORT}/vendor/github.com/NVIDIA/go-nvml/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/runtime-spec/LICENSE;md5=b355a61a394a504dacde901c958f662c \
                    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/runtime-tools/LICENSE;md5=b355a61a394a504dacde901c958f662c \
                    file://src/${GO_IMPORT}/vendor/github.com/pelletier/go-toml/LICENSE;md5=e49b63d868761700c5df76e7946d0bd7 \
                    file://src/${GO_IMPORT}/vendor/github.com/pmezard/go-difflib/LICENSE;md5=e9a2ebb8de779a07500ddecca806145e \
                    file://src/${GO_IMPORT}/vendor/github.com/sirupsen/logrus/LICENSE;md5=8dadfef729c08ec4e631c4f6fc5d43a0 \
                    file://src/${GO_IMPORT}/vendor/github.com/stretchr/testify/LICENSE;md5=188f01994659f3c0d310612333d2a26f \
                    file://src/${GO_IMPORT}/vendor/github.com/syndtr/gocapability/LICENSE;md5=a7304f5073e7be4ba7bffabbf9f2bbca \
                    file://src/${GO_IMPORT}/vendor/github.com/urfave/cli/v2/LICENSE;md5=51992c80b05795f59c22028d39f9b74c \
                    file://src/${GO_IMPORT}/vendor/github.com/NVIDIA/go-nvlib/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://src/${GO_IMPORT}/vendor/golang.org/x/mod/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a \
                    file://src/${GO_IMPORT}/vendor/golang.org/x/sys/LICENSE;md5=7998cb338f82d15c0eff93b7004d272a \
                    file://src/${GO_IMPORT}/vendor/gopkg.in/yaml.v2/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://src/${GO_IMPORT}/vendor/gopkg.in/yaml.v3/LICENSE;md5=3c91c17266710e16afdbb2b6d15c761c \
                    file://src/${GO_IMPORT}/vendor/sigs.k8s.io/yaml/LICENSE;md5=0ceb9ff3b27d3a8cf451ca3785d73c71 \
"

SRC_URI = "git://github.com/NVIDIA/nvidia-container-toolkit.git;protocol=https;branch=release-1.16;destsuffix=${GO_SRCURI_DESTSUFFIX}"
SRCREV = "a5a5833c14a15fd9c86bcece85d5ec6621b65652"

SRC_URI += "\
    file://0001-Add-support-for-alternate-roots-for-tegra-CSV-handli.patch;patchdir=src/${GO_IMPORT} \
    file://generate-config.sh.in \
    file://nvidia-container-setup.service.in \
"

GO_IMPORT = "github.com/NVIDIA/nvidia-container-toolkit"
GO_INSTALL = "${GO_IMPORT}/cmd/..."
# The go-nvml symbol lookup functions *require* lazy dynamic symbol resolution
SECURITY_LDFLAGS = ""
LDFLAGS += "-Wl,-z,lazy"
GO_LINKSHARED = ""

GO_EXTRA_LDFLAGS:append = "\
    -X github.com/NVIDIA/nvidia-container-toolkit/internal/info.version=${GITPKGVTAG} \
    -X github.com/NVIDIA/nvidia-container-toolkit/internal/info.gitCommit=${GITPKGV} \
"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "virtualization"

inherit go-mod gitpkgv features_check systemd

do_compile() {
    go_do_compile
    sed -e's,@DATADIR@,${datadir},g' ${UNPACKDIR}/generate-config.sh.in > ${B}/generate-config.sh
    sed -e's,@LIBEXECDIR@,${libexecdir},g' ${UNPACKDIR}/nvidia-container-setup.service.in > ${B}/nvidia-container-setup.service
}

do_install(){
    go_do_install
    ln -sf nvidia-container-runtime-hook ${D}${bindir}/nvidia-container-toolkit
    install -d ${D}${sysconfdir}/nvidia-container-runtime
    install -D -m0755 ${B}/generate-config.sh ${D}${libexecdir}/nvidia-container-runtime/generate-config
    install -D -m0644 ${B}/nvidia-container-setup.service ${D}${systemd_system_unitdir}/nvidia-container-setup.service
    ln -sf /run/nvidia-container-runtime/config.toml ${D}${sysconfdir}/nvidia-container-runtime/config.toml
}

SYSTEMD_SERVICE:${PN} = "nvidia-container-setup.service"
RDEPENDS:${PN} = "\
    libnvidia-container-tools \
    docker \
    nv-tegra-release \
    tegra-configs-container-csv \
    tegra-libraries-nvml \
    tegra-container-passthrough \
"
RDEPENDS:${PN}-dev += "bash make"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
