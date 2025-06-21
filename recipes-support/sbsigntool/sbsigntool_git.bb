DESCRIPTION = "Utility for signing and verifying files for UEFI Secure Boot"
LICENSE = "GPL-3.0-only & LGPL-2.1-only & LGPL-3.0-only & MIT"

# sbsigntool statically links to libccan.a which is built with modules
# passed to "create-ccan-tree" (and their dependencies). Therefore,
# we also keep track of all the ccan module licenses.
LIC_FILES_CHKSUM = "file://LICENSE.GPLv3;md5=9eef91148a9b14ec7f9df333daebc746 \
                    file://COPYING;md5=a7710ac18adec371b84a9594ed04fd20 \
                    file://lib/ccan.git/ccan/endian/LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://lib/ccan.git/ccan/htable/LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://lib/ccan.git/ccan/list/LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://lib/ccan.git/ccan/read_write_all/LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://lib/ccan.git/ccan/talloc/LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://lib/ccan.git/ccan/typesafe_cb/LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://lib/ccan.git/ccan/failtest/LICENSE;md5=6a6a8e020838b23406c81b19c1d46df6 \
                    file://lib/ccan.git/ccan/tlist/LICENSE;md5=6a6a8e020838b23406c81b19c1d46df6 \
                    file://lib/ccan.git/ccan/time/LICENSE;md5=838c366f69b72c5df05c96dff79b35f2 \
"

# The original upstream is git://kernel.ubuntu.com/jk/sbsigntool but it has
# not been maintained and many patches have been backported in this repo.
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/jejb/sbsigntools.git;protocol=https;name=sbsigntools;branch=master \
           git://github.com/rustyrussell/ccan.git;protocol=https;destsuffix=git/lib/ccan.git;name=ccan;branch=master \
           file://0001-Updates-for-OE-cross-builds.patch \
           file://0002-ccan-simplify-SCOREDIR.patch;patchdir=lib/ccan.git \
           "

SRCREV_sbsigntools  ?= "9cfca9fe7aa7a8e29b92fe33ce8433e212c9a8ba"
SRCREV_ccan         ?= "b1f28e17227f2320d07fe052a8a48942fe17caa5"
SRCREV_FORMAT       =  "sbsigntools_ccan"

DEPENDS = "binutils-native gnu-efi help2man-native openssl util-linux"

PV = "0.9.5-git"

inherit autotools pkgconfig

SBSIGN_SEARCH_BASE = "${RECIPE_SYSROOT}"
SBSIGN_SEARCH_BASE:class-native = "${RECIPE_SYSROOT_NATIVE}"

EXTRA_OECONF = "EFI_ARCH=${@efi_arch(d)}"

do_configure() {
    rm -rf ${S}/lib/ccan
    oldwd="$PWD"
    cd ${S}
    ./lib/ccan.git/tools/create-ccan-tree \
    		--build-type=automake ${S}/lib/ccan \
		talloc read_write_all build_assert array_size endian
    cd "$oldwd"
    autotools_do_configure
}

def efi_arch(d):
    import re
    harch = d.getVar("HOST_ARCH")
    if re.match("i[3456789]86", harch):
        return "ia32"
    return harch

BBCLASSEXTEND = "native"
