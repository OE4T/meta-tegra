# GPIO Library Recipe with Python Bindings
# This recipe demonstrates building a C library with Python bindings
#
# Key concepts:
# - Building shared libraries
# - Installing headers and pkg-config files
# - Python bindings using SWIG or ctypes
# - Multiple package outputs (lib, dev, python)

SUMMARY = "GPIO Control Library with Python Bindings"
DESCRIPTION = "A C library for GPIO control on Jetson platforms with Python bindings. \
               Demonstrates library packaging, header installation, and language bindings."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=placeholder_md5"

SECTION = "libs"
HOMEPAGE = "https://example.com/gpio-lib"

PV = "1.0"
PR = "r0"

# Source files
SRC_URI = "file://gpio_lib.c \
           file://gpio_lib.h \
           file://gpio_lib_python.c \
           file://setup.py \
           file://LICENSE \
           file://Makefile \
           file://gpio-lib.pc.in \
          "

S = "${WORKDIR}"

# Build dependencies
DEPENDS = "python3 python3-setuptools-native"

# Inherit classes for additional functionality
# - setuptools3: For Python package building
# - pkgconfig: For pkg-config file generation
inherit setuptools3 pkgconfig

# Runtime dependencies for different packages
RDEPENDS:${PN} = ""
RDEPENDS:${PN}-python = "${PN} python3-core"

# Shared library version (for soname)
SOVERSION = "1.0.0"

# Compiler flags
EXTRA_OEMAKE = "\
    CC='${CC}' \
    CFLAGS='${CFLAGS} -fPIC' \
    LDFLAGS='${LDFLAGS}' \
    SOVERSION='${SOVERSION}' \
    PREFIX='${prefix}' \
"

# Build the C library and Python bindings
do_compile() {
    # Build the shared library
    oe_runmake lib

    # Generate pkg-config file
    sed -e 's|@PREFIX@|${prefix}|g' \
        -e 's|@VERSION@|${PV}|g' \
        ${S}/gpio-lib.pc.in > ${B}/gpio-lib.pc

    # Build Python bindings
    # Note: This assumes setup.py uses distutils/setuptools
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py build
}

# Install library, headers, and bindings
do_install() {
    # Install shared library
    install -d ${D}${libdir}
    install -m 0755 ${B}/libgpio.so.${SOVERSION} ${D}${libdir}/
    ln -sf libgpio.so.${SOVERSION} ${D}${libdir}/libgpio.so.1
    ln -sf libgpio.so.1 ${D}${libdir}/libgpio.so

    # Install headers
    install -d ${D}${includedir}
    install -m 0644 ${S}/gpio_lib.h ${D}${includedir}/

    # Install pkg-config file
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${B}/gpio-lib.pc ${D}${libdir}/pkgconfig/

    # Install Python bindings
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py install \
        --root=${D} \
        --prefix=${prefix} \
        --install-lib=${PYTHON_SITEPACKAGES_DIR} \
        --install-data=${datadir}
}

# Package splitting
# This creates multiple packages from one recipe:
# - gpio-lib: Runtime library
# - gpio-lib-dev: Development files (headers, .so symlink, pkg-config)
# - gpio-lib-python: Python bindings

PACKAGES = "${PN}-python ${PN}-dev ${PN}"

# Define files for each package
FILES:${PN} = "\
    ${libdir}/libgpio.so.* \
"

FILES:${PN}-dev = "\
    ${includedir}/gpio_lib.h \
    ${libdir}/libgpio.so \
    ${libdir}/pkgconfig/gpio-lib.pc \
"

FILES:${PN}-python = "\
    ${PYTHON_SITEPACKAGES_DIR}/* \
"

# Prevent debug symbol stripping for development
INHIBIT_PACKAGE_STRIP = "0"

# Allow empty packages (in case some packages have no files)
ALLOW_EMPTY:${PN}-python = "1"

# Security flags
# SECURITY_CFLAGS: Enable security hardening
# SECURITY_LDFLAGS: Enable security hardening for linking
SECURITY_CFLAGS = "-fstack-protector-strong -D_FORTIFY_SOURCE=2"
SECURITY_LDFLAGS = "-Wl,-z,relro,-z,now"

# Usage notes:
# 1. To build: bitbake gpio-lib
# 2. To build only C library: bitbake gpio-lib -c compile
# 3. To test Python bindings: bitbake gpio-lib-python
# 4. Install in image: IMAGE_INSTALL:append = " gpio-lib gpio-lib-python"
#
# Development workflow:
# 1. Source should be in: meta-yourlayer/recipes-libs/gpio-lib/files/
# 2. Test compilation: bitbake gpio-lib -c compile
# 3. Check installed files: oe-pkgdata-util list-pkg-files gpio-lib
# 4. Inspect package: bitbake gpio-lib -c devshell
