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
    ldconfig-native \
"
LICENSE = "Apache-2.0 & MIT"

# Both source repositories include GPL COPYING (and for
# libnvidia-container, COPYING.LESSER) files. However:
# * For libnvidia-container, those files might only apply if elfutils
#   sources were included (the makefile has commands to download and
#   build libelf from elfutils sources). We configure the build to
#   use libelf provided externally.
# * For nvidia-modprobe, only the nvidia-modprobe-utils library is
#   built and used.  All sources for that library are MIT-licensed.

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/nvidia-modprobe-utils.c;endline=22;md5=8f11a22ea12c5aecde3340212f7fc9a1 \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/pci-enum.h;endline=29;md5=ca948b6fabc48e616fccbf17247feebf \
    file://deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/modprobe-utils/pci-sysfs.c;endline=25;md5=0449248350efd54938e7f8d25af965cb \
"

NVIDIA_MODPROBE_VERSION = "495.44"
ELF_TOOLCHAIN_VERSION = "0.7.1"
LIBTIRPC_VERSION = "1.3.2"

SRC_URI = "git://github.com/NVIDIA/libnvidia-container.git;protocol=https;name=libnvidia;branch=main \
           git://github.com/NVIDIA/nvidia-modprobe.git;protocol=https;branch=main;name=modprobe;destsuffix=git/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION} \
           file://0001-OE-cross-build-fixups.patch \
           file://0002-Expose-device-file-attrs.patch \
"

# tag: v1.14.2
SRCREV_libnvidia = "1eb5a30a6ad0415550a9df632ac8832bf7e2bbba"
# Nvidia modprobe version 495.44
SRCREV_modprobe = "292409904a5d18163fc7d1fbc11f98627324b82a"
SRCREV_FORMAT = "libnvidia_modprobe"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[seccomp] = "WITH_SECCOMP=yes,WITH_SECCOMP=no,libseccomp"

# We need to link with libelf, otherwise we need to
# include bmake-native which does not exist at the moment.
EXTRA_OEMAKE = 'EXCLUDE_BUILD_FLAGS=1 PLATFORM=${HOST_ARCH} WITH_NVCGO=no WITH_LIBELF=yes COMPILER=${@d.getVar('CC').split()[0]} REVISION=${SRCREV_libnvidia} ${PACKAGECONFIG_CONFARGS} \
                NVIDIA_MODPROBE_EXTRA_CFLAGS="${NVIDIA_MODPROBE_EXTRA_CFLAGS}"'
NVIDIA_MODPROBE_EXTRA_CFLAGS ?= "${DEBUG_PREFIX_MAP}"

export OBJCPY="${OBJCOPY}"

# Fix me: Create an independent recipe for nvidia-modprobe
do_configure:append() {
    # Mark Nvidia modprobe as downloaded
    touch ${S}/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/.download_stamp
}

do_install () {
    oe_runmake install DESTDIR=${D}
    # See note about licensing above
    find ${D}${datadir}/doc -type f -name 'COPYING*' -delete
}

PACKAGES =+ "${PN}-tools"
FILES:${PN}-tools = "${bindir}"
RDEPENDS:${PN}:append:tegra = " ldconfig tegra-libraries-cuda"
