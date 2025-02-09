SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "b313e6f5646acffc6f044b98d3bd12bd1d84394b"
PV = "36.4.3+git"

S = "${WORKDIR}/git"

require nvidia-kernel-oot.inc
