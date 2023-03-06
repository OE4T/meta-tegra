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
    libtirpc126 \
    ldconfig-native \
"
LICENSE = "BSD-3-Clause & MIT & Proprietary"

# Both source repositories include GPL COPYING (and for
# libnvidia-container, COPYING.LESSER) files. However:
# * For libnvidia-container, those files might only apply if elfutils
#   sources were included (the makefile has commands to download and
#   build libelf from elfutils sources). We configure the build to
#   use libelf provided externally.
# * For nvidia-modprobe, only the nvidia-modprobe-utils library is
#   built and used.  All sources for that library are MIT-licensed.

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=06cff45c51018e430083a716510821b7 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/nvidia-modprobe-utils.c;endline=22;md5=8f11a22ea12c5aecde3340212f7fc9a1 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/pci-enum.h;endline=29;md5=b2c0e63b1fa594dcb4f4093247d74a29 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/pci-sysfs.c;endline=25;md5=a5eee0d4ba40238ac5823a33ead29b6d \
    file://src/cuda.h;endline=48;md5=d212e7eced2562852ccf8267e4811e6f \
"

PR = "r1"

NVIDIA_MODPROBE_VERSION = "396.51"
ELF_TOOLCHAIN_VERSION = "0.7.1"
LIBTIRPC_VERSION = "1.1.4"

SRC_URI = "git://github.com/NVIDIA/libnvidia-container.git;protocol=https;name=libnvidia;branch=jetson \
           git://github.com/NVIDIA/nvidia-modprobe.git;protocol=https;branch=main;name=modprobe;destsuffix=git/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION} \
           file://0001-Makefile-Fix-RCP-flags-and-change-path-definitions-s.patch \
           file://0002-mk-common.mk-set-JETSON-variable-if-not-set-before.patch \
           file://0003-Fix-mapping-of-library-paths-for-jetson-mounts.patch \
           file://0004-Fix-build.h-generation-for-cross-builds.patch \
           file://0005-Update-makefile-for-statically-linking-external-libt.patch \
           file://0006-Add-support-for-separate-pass-through-tree.patch \
           "

SRC_URI[modprobe.md5sum] = "f82b649e7a0f1d1279264f9494e7cf43"
SRC_URI[modprobe.sha256sum] = "25bc6437a384be670e9fd76ac2e5b9753517e23eb16e7fa891b18537b70c4b20"


SRCREV_libnvidia = "61f57bcdf7aa6e73d9a348a7e36ec9fd94128fb2"
# Nvidia modprobe version 396.51
SRCREV_modprobe = "d97c08af5061f1516fb2e3a26508936f69d6d71d"
SRCREV_FORMAT = "libnvidia_modprobe"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[seccomp] = "WITH_SECCOMP=yes,WITH_SECCOMP=no,libseccomp"

def build_date(d):
    import datetime
    epoch = d.getVar('SOURCE_DATE_EPOCH')
    if epoch:
        dt = datetime.datetime.fromtimestamp(int(epoch), tz=datetime.timezone.utc)
        return 'DATE=' + dt.isoformat(timespec='minutes')
    return ''

# This must match the setting in tegra-container-passthrough recipe
PASSTHRU_ROOT = "${datadir}/nvidia-container-passthrough"

EXTRA_OEMAKE = "EXCLUDE_BUILD_FLAGS=1 PASSTHRU_ROOT=${PASSTHRU_ROOT} PLATFORM=${HOST_ARCH} JETSON=TRUE WITH_LIBELF=yes COMPILER=${@d.getVar('CC').split()[0]} REVISION=${SRCREV_libnvidia} ${@build_date(d)} ${PACKAGECONFIG_CONFARGS}"

CFLAGS:prepend = " -I=/usr/include/tirpc-1.2.6 "

export OBJCPY="${OBJCOPY}"

# Fix me: Create an independent recipe for nvidia-modprobe
do_configure:append() {
    # Mark Nvidia modprobe as downloaded
    touch ${S}/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/.download_stamp
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

INSANE_SKIP:${PN} = "already-stripped"
RDEPENDS:${PN} = "tegra-container-passthrough"
