FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'kvm', 'file://kvm.cfg', '', d)}"
