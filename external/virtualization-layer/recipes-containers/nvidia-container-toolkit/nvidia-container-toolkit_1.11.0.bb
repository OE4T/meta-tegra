SUMMARY = "NVIDIA container runtime"
DESCRIPTION = "NVIDIA container runtime hook \
Provides a modified version of runc allowing users to run GPU enabled \
containers. \
"
HOMEPAGE = "https://github.com/NVIDIA/nvidia-container-runtime"

COMPATIBLE_MACHINE = "(tegra)"

LICENSE = "Apache-2.0 & MIT & ISC & MPL-2.0 & (Apache-2.0 | MIT)"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://src/${GO_IMPORT}/vendor/github.com/blang/semver/LICENSE;md5=5a3ade42a900439691ebc22013660cae \
                    file://src/${GO_IMPORT}/vendor/github.com/container-orchestrated-devices/container-device-interface/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://src/${GO_IMPORT}/vendor/github.com/davecgh/go-spew/LICENSE;md5=c06795ed54b2a35ebeeb543cd3a73e56 \
                    file://src/${GO_IMPORT}/vendor/github.com/fsnotify/fsnotify/LICENSE;md5=68f2948d3c4943313d07e084a362486c \
                    file://src/${GO_IMPORT}/vendor/github.com/hashicorp/errwrap/LICENSE;md5=b278a92d2c1509760384428817710378 \
                    file://src/${GO_IMPORT}/vendor/github.com/hashicorp/go-multierror/LICENSE;md5=d44fdeb607e2d2614db9464dbedd4094 \
                    file://src/${GO_IMPORT}/vendor/github.com/NVIDIA/go-nvml/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/runc/LICENSE;md5=435b266b3899aa8a959f17d41c56def8 \
                    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/runtime-spec/LICENSE;md5=b355a61a394a504dacde901c958f662c \
                    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/runtime-tools/LICENSE;md5=b355a61a394a504dacde901c958f662c \
                    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/selinux/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://src/${GO_IMPORT}/vendor/github.com/pelletier/go-toml/LICENSE;md5=e49b63d868761700c5df76e7946d0bd7 \
                    file://src/${GO_IMPORT}/vendor/github.com/pkg/errors/LICENSE;md5=6fe682a02df52c6653f33bd0f7126b5a \
                    file://src/${GO_IMPORT}/vendor/github.com/pmezard/go-difflib/LICENSE;md5=e9a2ebb8de779a07500ddecca806145e \
                    file://src/${GO_IMPORT}/vendor/github.com/sirupsen/logrus/LICENSE;md5=8dadfef729c08ec4e631c4f6fc5d43a0 \
                    file://src/${GO_IMPORT}/vendor/github.com/stretchr/testify/LICENSE;md5=188f01994659f3c0d310612333d2a26f \
                    file://src/${GO_IMPORT}/vendor/github.com/syndtr/gocapability/LICENSE;md5=a7304f5073e7be4ba7bffabbf9f2bbca \
                    file://src/${GO_IMPORT}/vendor/github.com/urfave/cli/v2/LICENSE;md5=c542707ca9fc0b7802407ba62310bd8f \
                    file://src/${GO_IMPORT}/vendor/gitlab.com/nvidia/cloud-native/go-nvlib/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://src/${GO_IMPORT}/vendor/golang.org/x/mod/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
                    file://src/${GO_IMPORT}/vendor/golang.org/x/sys/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
                    file://src/${GO_IMPORT}/vendor/gopkg.in/yaml.v2/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://src/${GO_IMPORT}/vendor/gopkg.in/yaml.v3/LICENSE;md5=3c91c17266710e16afdbb2b6d15c761c \
                    file://src/${GO_IMPORT}/vendor/sigs.k8s.io/yaml/LICENSE;md5=0ceb9ff3b27d3a8cf451ca3785d73c71 \
"

SRC_URI = "git://github.com/NVIDIA/nvidia-container-toolkit.git;protocol=https;branch=main"
SRCREV = "d9de4a09b8fd51a46207398199ecfeb3998ad49d"

SRC_URI += "file://0001-Fix-cgo-LDFLAGS-for-go-1.21-and-later.patch;patchdir=src/${GO_IMPORT}"

GO_IMPORT = "github.com/NVIDIA/nvidia-container-toolkit"
GO_INSTALL = "${GO_IMPORT}/cmd/..."
# The go-nvml symbol lookup functions *require* lazy dynamic symbol resolution
SECURITY_LDFLAGS = ""
LDFLAGS += "-Wl,-z,lazy"
GO_LINKSHARED = ""

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "virtualization"

inherit go-mod features_check

do_install(){
    go_do_install
    install -d ${D}${sysconfdir}/nvidia-container-runtime
    install -m 0644 ${S}/src/${GO_IMPORT}/config/config.toml.ubuntu ${D}${sysconfdir}/nvidia-container-runtime/config.toml
    sed -i -e's,ldconfig\.real,ldconfig,' ${D}${sysconfdir}/nvidia-container-runtime/config.toml
    sed -i -e's,mode = "auto",mode = "legacy",' ${D}${sysconfdir}/nvidia-container-runtime/config.toml
    ln -sf nvidia-container-runtime-hook ${D}${bindir}/nvidia-container-toolkit
}

RDEPENDS:${PN} = "\
    libnvidia-container-tools \
    docker \
    tegra-configs-container-csv \
"
RDEPENDS:${PN}-dev += "bash make"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
