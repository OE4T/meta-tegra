SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "53a09db8532fc589cbafed6c7136c0ae7ba65b36"
PV = "36.5.0+git"

require nvidia-kernel-oot.inc

DEFAULT_PREFERENCE = "-1"
