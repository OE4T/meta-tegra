# Simple Application Recipe Example
# This recipe demonstrates basic BitBake recipe structure for a simple C application
#
# Key concepts:
# - Recipe metadata (SUMMARY, DESCRIPTION, LICENSE)
# - Source fetching (SRC_URI)
# - Build configuration (S, do_compile, do_install)
# - Runtime dependencies (RDEPENDS)

SUMMARY = "Simple Hello World Application"
DESCRIPTION = "A basic C application demonstrating BitBake recipe structure \
               and proper installation into the target rootfs"
AUTHOR = "Meta-Tegra Learning System"
HOMEPAGE = "https://example.com/simple-app"
SECTION = "examples"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Version and revision tracking
PV = "1.0"
PR = "r0"

# Source URI - can be a git repo, tarball, or local files
# For this example, we use local files
SRC_URI = "file://simple-app.c \
           file://Makefile \
          "

# Build directory - defaults to ${WORKDIR}/${PN}-${PV}
# Since we're using local files without a versioned directory, set S to WORKDIR
S = "${WORKDIR}"

# Dependencies
# DEPENDS: Build-time dependencies (packages needed during compilation)
DEPENDS = ""

# RDEPENDS: Runtime dependencies (packages needed on target to run the application)
# ${PN} refers to Package Name (simple-app in this case)
RDEPENDS:${PN} = ""

# Compilation flags (optional - demonstrates how to set compiler flags)
EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"

# Build task - compiles the source code
# By default, BitBake looks for a Makefile and runs 'make'
# This task demonstrates explicit compilation
do_compile() {
    # Using oe_runmake for Makefile-based builds
    oe_runmake

    # Alternative: Direct compilation without Makefile
    # ${CC} ${CFLAGS} ${LDFLAGS} -o simple-app simple-app.c
}

# Install task - copies built files to the target rootfs staging area
# D = Destination directory (staging area for the package)
# bindir = Binary directory (usually /usr/bin)
# sysconfdir = System configuration directory (usually /etc)
do_install() {
    # Create the binary directory in the staging area
    install -d ${D}${bindir}

    # Install the compiled binary
    # install: -m 0755 = set permissions (rwxr-xr-x)
    install -m 0755 ${B}/simple-app ${D}${bindir}/simple-app

    # Example: Install configuration file (if needed)
    # install -d ${D}${sysconfdir}
    # install -m 0644 ${WORKDIR}/simple-app.conf ${D}${sysconfdir}/
}

# Optional: Package-specific configurations
# FILES: Explicitly specify which files belong to this package
FILES:${PN} = "${bindir}/simple-app"

# Optional: Create debug package
# INHIBIT_PACKAGE_DEBUG_SPLIT = "1"  # Uncomment to disable debug package creation

# Optional: Package architecture
# Set to "all" for architecture-independent packages (scripts, configs)
# PACKAGE_ARCH = "${MACHINE_ARCH}"  # Default: specific to machine architecture

# Optional: Add package to specific package groups
# PACKAGES = "${PN} ${PN}-dbg ${PN}-dev"

# Notes for developers:
# 1. To build this recipe: bitbake simple-app
# 2. To clean: bitbake simple-app -c cleansstate
# 3. To view tasks: bitbake simple-app -c listtasks
# 4. Source files should be in: meta-yourlayer/recipes-apps/simple-app/files/
