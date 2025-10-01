SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_REPO = "/sources/nvidia-kernel-oot"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "wip-r38.2.x"
SRCREV = "85fe6a011e79695270cef9e3a1a4d10abb6d28f3"
PV = "38.2.1+git"
DEFAULT_PREFERENCE = "-1"

require nvidia-kernel-oot.inc
