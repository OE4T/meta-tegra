FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:tegra = " file://0001-Fix-casts-to-void-4275.patch"

