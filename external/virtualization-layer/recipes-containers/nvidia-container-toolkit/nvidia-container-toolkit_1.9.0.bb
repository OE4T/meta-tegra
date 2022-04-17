SUMMARY = "NVIDIA container runtime"
DESCRIPTION = "NVIDIA container runtime hook \
Provides a modified version of runc allowing users to run GPU enabled \
containers. \
"
HOMEPAGE = "https://github.com/NVIDIA/nvidia-container-runtime"

COMPATIBLE_MACHINE = "(tegra)"

LICENSE = "Apache-2.0 & MIT & ISC & BSD-2-Clause & BSD-3-Clause & LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://src/${GO_IMPORT}/vendor/github.com/containers/podman/v2/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://src/${GO_IMPORT}/vendor/github.com/davecgh/go-spew/LICENSE;md5=c06795ed54b2a35ebeeb543cd3a73e56 \
                    file://src/${GO_IMPORT}/vendor/github.com/opencontainers/runtime-spec/LICENSE;md5=b355a61a394a504dacde901c958f662c \
                    file://src/${GO_IMPORT}/vendor/github.com/pelletier/go-toml/LICENSE;md5=e49b63d868761700c5df76e7946d0bd7 \
                    file://src/${GO_IMPORT}/vendor/github.com/pkg/errors/LICENSE;md5=6fe682a02df52c6653f33bd0f7126b5a \
                    file://src/${GO_IMPORT}/vendor/github.com/pmezard/go-difflib/LICENSE;md5=e9a2ebb8de779a07500ddecca806145e \
                    file://src/${GO_IMPORT}/vendor/github.com/shurcooL/sanitized_anchor_name/LICENSE;md5=c670c44b8d826e9b7b99077e5c7ba283 \
                    file://src/${GO_IMPORT}/vendor/github.com/sirupsen/logrus/LICENSE;md5=8dadfef729c08ec4e631c4f6fc5d43a0 \
                    file://src/${GO_IMPORT}/vendor/github.com/stretchr/testify/LICENSE;md5=188f01994659f3c0d310612333d2a26f \
                    file://src/${GO_IMPORT}/vendor/github.com/tsaikd/KDGoLib/LICENSE;md5=c8156b3403995b12675506b785491cd2 \
                    file://src/${GO_IMPORT}/vendor/github.com/urfave/cli/v2/LICENSE;md5=c542707ca9fc0b7802407ba62310bd8f \
                    file://src/${GO_IMPORT}/vendor/golang.org/x/mod/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
                    file://src/${GO_IMPORT}/vendor/golang.org/x/sys/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
                    file://src/${GO_IMPORT}/vendor/gopkg.in/yaml.v3/LICENSE;md5=3c91c17266710e16afdbb2b6d15c761c \
"

SRC_URI = "git://github.com/NVIDIA/nvidia-container-toolkit.git;protocol=https;branch=master"
SRCREV = "56ad97b8e51245795c7610e275116ae5417a7c34"

GO_IMPORT = "github.com/NVIDIA/nvidia-container-toolkit"
GO_INSTALL = "${GO_IMPORT}/cmd/..."

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "virtualization"

inherit go-mod features_check

do_install(){
    go_do_install
    install -d ${D}${sysconfdir}/nvidia-container-runtime
    install -m 0644 ${S}/src/${GO_IMPORT}/config/config.toml.ubuntu+jetpack ${D}${sysconfdir}/nvidia-container-runtime/config.toml
    sed -i -e's,ldconfig\.real,ldconfig,' ${D}${sysconfdir}/nvidia-container-runtime/config.toml
    ln -sf nvidia-container-toolkit ${D}${bindir}/nvidia-container-runtime-hook
}

RDEPENDS:${PN} = "\
    libnvidia-container-tools \
    docker \
    tegra-configs-container-csv \
"
RDEPENDS:${PN}-dev += "bash make"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
