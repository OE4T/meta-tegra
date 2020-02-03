SUMMARY = "NVIDIA container runtime library"

DESCRIPTION = "NVIDIA container runtime library \
The nvidia-container library provides an interface to configure GNU/Linux \
containers leveraging NVIDIA hardware. The implementation relies on several \
kernel subsystems and is designed to be agnostic of the container runtime. \
"

HOMEPAGE = "https://github.com/NVIDIA/libnvidia-container"

DEPENDS = " \
    coreutils-native \
    pkgconfig-native \
    libcap \
    elfutils \
    libtirpc \
    libseccomp \
    ldconfig-native \
"
LICENSE = "GPLv3 & Apache-2.0"

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
    file://COPYING.LESSER;md5=3000208d539ec061b899bce1d9ce9404 \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

PR = "r1"

NVIDIA_MODPROBE_VERSION = "396.51"
ELF_TOOLCHAIN_VERSION = "0.7.1"
LIBTIRPC_VERSION = "1.1.4"

SRC_URI = " \
    git://github.com/NVIDIA/libnvidia-container.git;name=libnvidia \
    https://github.com/NVIDIA/nvidia-modprobe/archive/${NVIDIA_MODPROBE_VERSION}.tar.gz;name=modprobe \
    https://sourceforge.net/projects/elftoolchain/files/Sources/elftoolchain-${ELF_TOOLCHAIN_VERSION}/elftoolchain-${ELF_TOOLCHAIN_VERSION}.tar.bz2;name=elftoolchain \
    https://downloads.sourceforge.net/project/libtirpc/libtirpc/${LIBTIRPC_VERSION}/libtirpc-${LIBTIRPC_VERSION}.tar.bz2;name=libtirpc \
    file://0001-Makefile-Fix-RCP-flags-and-change-prefix.patch \
"

SRC_URI[elftoolchain.md5sum] = "47fe4cedded2edeaf8e429f1f842e23d"
SRC_URI[elftoolchain.sha256sum] = "44f14591fcf21294387215dd7562f3fb4bec2f42f476cf32420a6bbabb2bd2b5"
SRC_URI[modprobe.md5sum] = "f82b649e7a0f1d1279264f9494e7cf43"
SRC_URI[modprobe.sha256sum] = "25bc6437a384be670e9fd76ac2e5b9753517e23eb16e7fa891b18537b70c4b20"
SRC_URI[libtirpc.md5sum] = "f5d2a623e9dfbd818d2f3f3a4a878e3a"
SRC_URI[libtirpc.sha256sum] = "2ca529f02292e10c158562295a1ffd95d2ce8af97820e3534fe1b0e3aec7561d"


SRCREV = "b6aff41f09bb2c21ed7da3058c61a063726fa5d6"

S = "${WORKDIR}/git"

# We need to link with libelf, otherwise we need to
# include bmake-native which does not exist at the moment.
EXTRA_OEMAKE_append = " WITH_LIBELF=yes"

CFLAGS_prepend = " -I ${WORKDIR}/recipe-sysroot/usr/include/tirpc "

export OBJCPY="${OBJCOPY}"

# Fix me: Create an independent recipe for nvidia-modprobe
do_configure_append() {
    mkdir -p ${S}/deps/src
    # Mark Nvidia modprobe as downloaded
    cp -r ${WORKDIR}/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION} ${S}/deps/src
    touch ${S}/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/.download_stamp
    # Mark elftoolchain as downloaded
    cp -r ${WORKDIR}/elftoolchain-${ELF_TOOLCHAIN_VERSION} ${S}/deps/src
    touch ${S}/deps/src/elftoolchain-${ELF_TOOLCHAIN_VERSION}/.download_stamp
    # Mark libtirpc as downloaded
    cp -r ${WORKDIR}/libtirpc-${LIBTIRPC_VERSION} ${S}/deps/src
    touch ${S}/deps/src/libtirpc-${LIBTIRPC_VERSION}/.download_stamp

}

do_install () {
    oe_runmake install DESTDIR=${D} 
}

INSANE_SKIP_${PN}_append = "already-stripped"
