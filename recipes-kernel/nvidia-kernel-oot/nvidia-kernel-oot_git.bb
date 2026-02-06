SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "${AUTOREV}"
PV = "36.5.0+git"

require nvidia-kernel-oot.inc
