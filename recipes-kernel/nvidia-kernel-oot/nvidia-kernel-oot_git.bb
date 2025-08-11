SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "0fd1438b4e001b658d733d6a1780286e43738ae4"
PV = "36.4.4+git"

require nvidia-kernel-oot.inc
