DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Apache-2.0"

require tensorrt.inc

PROVIDES += "tensorrt-core"

SRC_COMMON_DEBS += "\
    libnvinfer11_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer11_${PV}+cuda13.4_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda13.4_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda13.4_arm64.deb;name=dev;subdir=tensorrt \
"

SRC_URI[lib.sha256sum] = "9c278096e89d5ef9bd670e3be3933037e49faa5c483c5224007c45feda57c5b2"
SRC_URI[dev.sha256sum] = "08fef71b28c45173cce297e49df2cf1f5a53d8e59d9bab084fef46a3fc3ec608"

do_install() {
    install -d ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer.so.${BASEVER} ${D}${libdir}
    install -m 0644 ${S}/usr/lib/aarch64-linux-gnu/libnvinfer_builder_resource_*.so.${BASEVER} ${D}${libdir}

    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so.${MAJVER}
    ln -s libnvinfer.so.${BASEVER} ${D}${libdir}/libnvinfer.so
}

DEPENDS += "tensorrt-headers"

# NVIDIA's deb bundles one builder resource per GPU arch (sm75..sm120 +
# PTX, ~2GB). Split each into its own package and pull in only the target SoC's
# arch via RDEPENDS, instead of installing all of them.
PACKAGES_DYNAMIC += "^${PN}-builder-resource-.*"

python split_builder_resources() {
    do_split_packages(d, d.expand('${libdir}'),
                      r'^libnvinfer_builder_resource_(.*)\.so\..*$',
                      d.expand('${PN}-builder-resource-%s'),
                      'TensorRT builder resource for %s',
                      allow_dirs=False, prepend=True)
}
PACKAGESPLITFUNCS =+ "split_builder_resources"

# The resource the builder loads is not simply sm<compute-capability>: the SBSA
# deb ships no sm_87, and on orin, tensorrt apparently loads sm_86.
# Select per SoC.
TENSORRT_BUILDER_RESOURCE_ARCH ?= "${TEGRA_CUDA_ARCHITECTURE}"
TENSORRT_BUILDER_RESOURCE_ARCH:tegra234 = "86"
RDEPENDS:${PN} += "${PN}-builder-resource-sm${TENSORRT_BUILDER_RESOURCE_ARCH}"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
