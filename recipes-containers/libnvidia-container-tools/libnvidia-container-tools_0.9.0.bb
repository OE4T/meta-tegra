SUMMARY = "NVIDIA container runtime library"

DESCRIPTION = "NVIDIA container runtime library \
The nvidia-container library provides an interface to configure GNU/Linux \
containers leveraging NVIDIA hardware. The implementation relies on several \
kernel subsystems and is designed to be agnostic of the container runtime. \
"
HOMEPAGE = "https://github.com/NVIDIA/libnvidia-container"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = " \
    coreutils-native \
    pkgconfig-native \
    libcap \
    elfutils \
    libtirpc \
    ldconfig-native \
"
LICENSE = "GPLv3 & Proprietary"

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=06cff45c51018e430083a716510821b7 \
    file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
    file://COPYING.LESSER;md5=3000208d539ec061b899bce1d9ce9404 \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

PR = "r1"

NVIDIA_MODPROBE_VERSION = "396.51"
ELF_TOOLCHAIN_VERSION = "0.7.1"
LIBTIRPC_VERSION = "1.1.4"

SRC_URI = "git://github.com/NVIDIA/libnvidia-container.git;name=libnvidia;branch=jetson \
           git://github.com/NVIDIA/nvidia-modprobe.git;name=modprobe;destsuffix=git/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION} \
           file://0001-Makefile-Fix-RCP-flags-and-change-path-definitions-s.patch \
           file://0002-common.mk-Set-JETSON-variable-if-not-set-before.patch \
           file://0003-Fix-mapping-of-library-paths-for-jetson-mounts.patch \
           "

SRC_URI[modprobe.md5sum] = "f82b649e7a0f1d1279264f9494e7cf43"
SRC_URI[modprobe.sha256sum] = "25bc6437a384be670e9fd76ac2e5b9753517e23eb16e7fa891b18537b70c4b20"


SRCREV_libnvidia = "09df17a2424704c1e4af310d3ce51fe475ba292a"
# Nvidia modprobe version 396.51
SRCREV_modprobe = "d97c08af5061f1516fb2e3a26508936f69d6d71d"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[seccomp] = " WITH_SECCOMP=yes , WITH_SECCOMP=no ,libseccomp"

# We need to link with libelf, otherwise we need to
# include bmake-native which does not exist at the moment.
EXTRA_OEMAKE_append = " WITH_LIBELF=yes JETSON=TRUE ${PACKAGECONFIG_CONFARGS}"

CFLAGS_prepend = " -I=/usr/include/tirpc "

export OBJCPY="${OBJCOPY}"

# Fix me: Create an independent recipe for nvidia-modprobe
do_configure_append() {
    # Mark Nvidia modprobe as downloaded
    touch ${S}/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/.download_stamp
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

INSANE_SKIP_${PN}_append = "already-stripped"
