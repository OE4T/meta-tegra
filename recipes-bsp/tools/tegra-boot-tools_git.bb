require tegra-boot-tools.inc

SRC_REPO = "github.com/OE4T/tegra-boot-tools.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "${AUTOREV}"

PV = "3.0.99+git${SRCPV}"
S = "${WORKDIR}/git"

DEFAULT_PREFERENCE = "-1"
DEBUG_BUILD = "1"
