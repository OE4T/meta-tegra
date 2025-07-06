SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "662e97f681c0af141d67b95f35b4ad961d7250d1"
PV = "36.4.4+git"

require nvidia-kernel-oot.inc
