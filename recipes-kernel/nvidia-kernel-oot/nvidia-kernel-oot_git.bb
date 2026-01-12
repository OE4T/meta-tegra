SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "wip-r38.2.x"
SRCREV = "25c7d7e17bbd4bb43b87bd9aebfdecba2cf73413"
PV = "38.4.0+git"

require nvidia-kernel-oot.inc
