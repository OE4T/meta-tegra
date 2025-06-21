SUMMARY = "Wrapper for libv4l2_nvvidconv to work around STREAMOFF segfault issue"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d58123d89b8fdb1ac2cb445de95dbb79"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "libv4l"

SRC_REPO = "github.com/OE4T/nvvidconv-plugin-wrapper.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
# Corresponds to v1.0.1 tag
SRCREV = "0b2f038a6c715e73e0c38ae8ec1680f4406d655d"
PV = "1.0.1"

inherit cmake pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

FILES:${PN} = "${libdir}/libv4l/plugins/*${SOLIBSDEV}"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
