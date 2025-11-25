SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "84d62bcc040f3e55b5d0cc7288daf6870367db6a"
PV = "36.4.4+git"

require nvidia-kernel-oot.inc
