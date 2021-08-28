SUMMARY = "Wrapper for libv4l2_nvvidconv to work around STREAMOFF segfault issue"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d58123d89b8fdb1ac2cb445de95dbb79"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "libv4l"

SRC_REPO = "github.com/OE4T/nvvidconv-plugin-wrapper.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
# Corresponds to v1.0.0 tag
SRCREV = "2eea65282ae3c385cf007dc61cd6ef6cf7b5d13a"
PV = "1.0.0"

S = "${WORKDIR}/git"

inherit cmake container-runtime-csv

CONTAINER_CSV_FILES = "${libdir}/libv4l/plugins/*.so*"

FILES_${PN} = "${libdir}/libv4l/plugins/*${SOLIBSDEV}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
