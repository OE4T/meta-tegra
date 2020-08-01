DESCRIPTION = "GPU stress test"
HOMEPAGE = ""
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=87e91a92b2cc50fa43dba1a1a4ffca96"
SRC_REPO ?= "github.com/madisongh/gpu-burn;protocol=https"
SRCBRANCH ?= "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV ?= "8b4d5cfc3f01829dccf282b92e4dae03cbfb0fa1"
PV = "0.9+git${SRCPV}"

COMPATIBLE_MACHINE = "(cuda)"

S = "${WORKDIR}/git"

inherit cuda cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_INSTALL_PREFIX=/opt/cudatests"

FILES_${PN} = "/opt/cudatests"
