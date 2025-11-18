# Yocto & BitBake Quick Reference

## BitBake Syntax Fundamentals

### Variable Assignment

```bitbake
# Simple assignment (immediate expansion)
VAR = "value"

# Immediate expansion (expanded when parsed)
VAR := "value-${OTHER_VAR}"

# Weak assignment (only if not already set)
VAR ?= "default-value"

# Weak immediate expansion
VAR ??= "default-${OTHER}"

# Append with space
VAR += "additional"

# Prepend with space
VAR =+ "prefix"

# Append without space
VAR .= "suffix"

# Prepend without space
VAR =. "prefix"

# Append (override-safe)
VAR:append = " appended"

# Prepend (override-safe)
VAR:prepend = "prepended "

# Remove from variable
VAR:remove = "unwanted"
```

### Variable Expansion

```bitbake
# Basic expansion
MESSAGE = "Hello ${USER}"

# Default value if variable is unset
VALUE = "${VAR:-default}"

# Python expansion
PYVAR = "${@'python' + ' expression'}"

# Inline Python
COMPUTED = "${@d.getVar('VAR') or 'fallback'}"
```

### Conditional Assignment

```bitbake
# Architecture-specific
PACKAGE_ARCH:aarch64 = "arm64-specific"

# Machine-specific
KERNEL_DEVICETREE:jetson-xavier-nx = "nvidia/tegra194-p3668-0001.dtb"

# Conditional on feature
DEPENDS:append:class-target = " dependency"
DEPENDS:append:class-native = " native-dependency"
```

## Essential Variables Reference

### Package Dependencies

```bitbake
# Build-time dependencies (for build host)
DEPENDS = "recipe1 recipe2"

# Runtime dependencies (on target)
RDEPENDS:${PN} = "package1 package2"

# Recommended packages (weak runtime dependency)
RRECOMMENDS:${PN} = "optional-package"

# Runtime suggestions
RSUGGESTS:${PN} = "nice-to-have"

# Runtime conflicts
RCONFLICTS:${PN} = "conflicting-package"

# Runtime replacement
RREPLACES:${PN} = "old-package"

# Provider preference
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra"

# Version preference
PREFERRED_VERSION_linux-tegra = "5.10%"
```

### Package Information

```bitbake
# Package name (defaults to recipe name without version)
PN = "package-name"

# Package version
PV = "1.0.0"

# Package release
PR = "r0"

# Package epoch (for versioning precedence)
PE = "1"

# Package architecture
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Summary (short description)
SUMMARY = "Brief package description"

# Description (detailed)
DESCRIPTION = "Detailed package description for documentation"

# Homepage
HOMEPAGE = "https://example.com"

# License
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=<hash>"

# Section/category
SECTION = "kernel/modules"
```

### Source Handling

```bitbake
# Source URI
SRC_URI = "https://example.com/source-${PV}.tar.gz \
           file://0001-custom-patch.patch \
           file://config-file.cfg \
          "

# Source checksum (SHA256)
SRC_URI[sha256sum] = "abc123..."

# Git source
SRC_URI = "git://github.com/user/repo.git;protocol=https;branch=main"
SRCREV = "1234567890abcdef"

# Multiple git repos
SRC_URI = "git://github.com/user/repo1.git;name=repo1;branch=main \
           git://github.com/user/repo2.git;name=repo2;destsuffix=repo2;branch=dev"
SRCREV_repo1 = "abc123..."
SRCREV_repo2 = "def456..."

# Local files
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://local-file.conf"

# Work directory
S = "${WORKDIR}/custom-source-dir"

# Build directory
B = "${WORKDIR}/build"
```

### Build Configuration

```bitbake
# Inherit classes
inherit autotools pkgconfig systemd

# Parallel make
PARALLEL_MAKE = "-j 8"

# Extra configure flags
EXTRA_OECONF = "--enable-feature --disable-other"

# Extra compile flags
EXTRA_OEMAKE = "CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"

# CMake configuration
EXTRA_OECMAKE = "-DENABLE_FEATURE=ON"

# Installation prefix
prefix = "/usr"
bindir = "${prefix}/bin"
libdir = "${prefix}/lib"
includedir = "${prefix}/include"
sysconfdir = "/etc"
```

### Image Variables

```bitbake
# Image features
IMAGE_FEATURES = "ssh-server-openssh package-management"

# Extra packages
IMAGE_INSTALL:append = " custom-package"

# Image types
IMAGE_FSTYPES = "tar.gz ext4 wic"

# Rootfs size
IMAGE_ROOTFS_SIZE = "8388608"
IMAGE_ROOTFS_EXTRA_SPACE = "524288"

# Image overhead factor
IMAGE_OVERHEAD_FACTOR = "1.3"

# Image name
IMAGE_BASENAME = "custom-image"
```

## Task Execution Order

### Standard Task Flow

```
do_fetch          → Download sources
  ↓
do_unpack         → Extract sources
  ↓
do_patch          → Apply patches
  ↓
do_configure      → Configure (./configure, cmake, etc.)
  ↓
do_compile        → Build the software
  ↓
do_install        → Install to ${D}
  ↓
do_populate_sysroot → Copy to sysroot for other recipes
  ↓
do_package        → Split into packages
  ↓
do_package_write_* → Create package files (rpm, deb, ipk)
  ↓
do_build          → Meta-task marking completion
```

### Task Dependencies

```bitbake
# Add dependency between tasks
do_compile[depends] += "other-recipe:do_populate_sysroot"

# Runtime dependency check
do_install[rdepends] += "runtime-package"

# Recursive dependency
do_build[recrdepends] += "recipe:task"

# Order-only dependency (no hard dependency)
do_task[deptask] = "do_other_task"

# No stamp (always run)
do_custom_task[nostamp] = "1"

# Network access allowed
do_fetch[network] = "1"
```

### Custom Tasks

```bitbake
# Define custom task
do_custom_task() {
    # Shell script
    echo "Running custom task"
    install -d ${D}/custom/path
}

# Python task
python do_python_task() {
    bb.note("Python task executing")
    d.setVar('VAR', 'value')
}

# Add to task chain
addtask custom_task after do_configure before do_compile
addtask python_task after do_install before do_package
```

## Layer Configuration

### layer.conf Structure

```bitbake
# Layer identification
BBPATH .= ":${LAYERDIR}"

# Layer collections
BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
            ${LAYERDIR}/recipes-*/*/*.bbappend"

# Layer collection name
BBFILE_COLLECTIONS += "meta-custom"

# Pattern for layer
BBFILE_PATTERN_meta-custom = "^${LAYERDIR}/"

# Priority (higher = more important)
BBFILE_PRIORITY_meta-custom = "10"

# Layer dependencies
LAYERDEPENDS_meta-custom = "core"

# Version dependencies
LAYERSERIES_COMPAT_meta-custom = "kirkstone langdale"

# Additional license paths
LICENSE_PATH += "${LAYERDIR}/licenses"
```

### local.conf Common Settings

```bitbake
# Machine selection
MACHINE = "jetson-xavier-nx-devkit"

# Distribution
DISTRO = "poky"

# Download directory
DL_DIR = "${TOPDIR}/downloads"

# Shared state cache
SSTATE_DIR = "${TOPDIR}/sstate-cache"

# Parallel execution
BB_NUMBER_THREADS = "8"
PARALLEL_MAKE = "-j 8"

# Package format
PACKAGE_CLASSES = "package_rpm"

# Additional features
EXTRA_IMAGE_FEATURES = "debug-tweaks tools-debug tools-sdk"

# SDK settings
SDKMACHINE = "x86_64"

# Remove packages
PACKAGE_EXCLUDE = "unwanted-package"

# Distro features
DISTRO_FEATURES:append = " systemd"
DISTRO_FEATURES:remove = "sysvinit"

# Init manager
INIT_MANAGER = "systemd"
```

## Class Inheritance Patterns

### Common Classes

```bitbake
# Autotools (./configure && make)
inherit autotools

# CMake
inherit cmake

# Kernel module
inherit module

# Systemd service
inherit systemd
SYSTEMD_SERVICE:${PN} = "myservice.service"
SYSTEMD_AUTO_ENABLE = "enable"

# Python
inherit python3native
inherit setuptools3

# Pkgconfig
inherit pkgconfig

# Update alternatives
inherit update-alternatives
ALTERNATIVE:${PN} = "binary-name"
ALTERNATIVE_LINK_NAME[binary-name] = "${bindir}/binary-name"
ALTERNATIVE_TARGET[binary-name] = "${bindir}/binary-name.custom"
ALTERNATIVE_PRIORITY[binary-name] = "100"

# Binconfig
inherit binconfig

# Kernel
inherit kernel
```

### Custom Classes

```bitbake
# In meta-layer/classes/custom.bbclass
CUSTOM_VAR ?= "default"

custom_do_something() {
    bbwarn "Custom function called"
}

# Use in recipe
inherit custom
```

## BitBake Commands

### Building

```bash
# Build a recipe
bitbake recipe-name

# Build an image
bitbake core-image-minimal

# Clean a recipe
bitbake -c clean recipe-name

# Clean sstate
bitbake -c cleansstate recipe-name

# Clean all (including downloads)
bitbake -c cleanall recipe-name

# Fetch only
bitbake -c fetch recipe-name

# Build SDK
bitbake -c populate_sdk image-name

# Build specific task
bitbake -c compile recipe-name
```

### Information & Debugging

```bash
# Show recipe information
bitbake-layers show-recipes

# Show layers
bitbake-layers show-layers

# Show appends
bitbake-layers show-appends

# Show overlayed files
bitbake-layers show-overlayed

# Search for recipe
bitbake-layers find-recipe recipe-name

# Show environment
bitbake -e recipe-name | less

# Show dependencies
bitbake -g recipe-name

# Dry run
bitbake -n recipe-name

# Continue after errors
bitbake -k image-name

# Force rebuild
bitbake -f -c compile recipe-name
```

## Advanced Patterns

### Override Syntax

```bitbake
# Override order (right has higher precedence)
OVERRIDES = "arch:machine:distro:feature:class"

# Using overrides
VAR = "default"
VAR:x86 = "x86-specific"
VAR:jetson = "jetson-specific"
VAR:jetson:production = "jetson-production"

# Checking overrides
${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-enabled', '', d)}
```

### Anonymous Python Functions

```bitbake
python __anonymous() {
    # Runs during parsing
    machine = d.getVar('MACHINE')
    if 'jetson' in machine:
        d.appendVar('DEPENDS', ' tegra-specific-dep')
}
```

### Multi-lib Support

```bitbake
# In local.conf
require conf/multilib.conf
MULTILIBS = "multilib:lib32"
DEFAULTTUNE:virtclass-multilib-lib32 = "armv7a"

# In recipe
BBCLASSEXTEND = "native nativesdk"
```

### Virtual Providers

```bitbake
# Provide virtual package
PROVIDES = "virtual/kernel"

# Prefer specific provider
PREFERRED_PROVIDER_virtual/kernel = "linux-tegra"
PREFERRED_VERSION_linux-tegra = "5.10%"
```

## Common Patterns

### Recipe Template

```bitbake
SUMMARY = "Brief description"
DESCRIPTION = "Detailed description"
HOMEPAGE = "https://example.com"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=abc123"

SRC_URI = "https://example.com/${PN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "abc123..."

S = "${WORKDIR}/${PN}-${PV}"

inherit autotools pkgconfig

DEPENDS = "dependency1 dependency2"
RDEPENDS:${PN} = "runtime-dep1 runtime-dep2"

EXTRA_OECONF = "--enable-feature"

do_install:append() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${S}/config ${D}${sysconfdir}/
}

FILES:${PN} += "${sysconfdir}/config"
```

---

*Last Updated: 2025-11-18*
*Maintained by: Documentation Researcher Agent*
