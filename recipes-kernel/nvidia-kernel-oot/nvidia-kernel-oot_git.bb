SRC_REPO = "github.com/OE4T/nvidia-kernel-oot;protocol=https"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH = "patches-r36.4.4"
SRCREV = "df648bcc3e40a824197468e7cc943947f7d83bb4"
PV = "36.4.4+git"

require nvidia-kernel-oot.inc
