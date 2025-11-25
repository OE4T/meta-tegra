SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "patches-rel-36"
SRCREV = "a94b44e97f04bd1c5171837dfe6fe3b53402e71c"
PV = "36.4.3+git"

S = "${WORKDIR}/git"

require nvidia-kernel-oot.inc
