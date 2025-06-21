SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "92c7803bcf2e6ce7f12a69ca5570c36095518d94"
PV = "36.4.3+git"

require nvidia-kernel-oot.inc
