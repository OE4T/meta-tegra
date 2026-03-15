FILESEXTRAPATHS:prepend:tegra := "${THISDIR}/files:"

# This backport applies to the 2.13.12 release tarball only.
SRC_URI:append:tegra = "${@'' if d.getVar('BBEXTENDCURR') == 'devupstream' else ' file://0001-Fix-build-with-Linux-5.15.171-and-later.patch'}"
