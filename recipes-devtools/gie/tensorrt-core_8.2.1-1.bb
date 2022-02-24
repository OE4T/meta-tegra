DESCRIPTION = "NVIDIA TensorRT Core (GPU Inference Engine) for deep learning"
LICENSE = "Proprietary"

inherit l4t_deb_pkgfeed container-runtime-csv features_check

HOMEPAGE = "http://developer.nvidia.com/tensorrt"

L4T_DEB_GROUP = "tensorrt"

SRC_COMMON_DEBS = "\
    libnvinfer8_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer8_${PV}+cuda10.2_arm64.deb;name=lib;subdir=tensorrt \
    libnvinfer-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvinfer-dev_${PV}+cuda10.2_arm64.deb;name=dev;subdir=tensorrt \
    libnvparsers8_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvparsers8_${PV}+cuda10.2_arm64.deb;name=nvp;subdir=tensorrt \
    libnvparsers-dev_${PV}+cuda10.2_arm64.deb;downloadfilename=libnvparsers-dev_${PV}+cuda10.2_arm64.deb;name=nvpdev;subdir=tensorrt \
"
LIBSHA256SUM = "af79b941988fff6daec89a663fd5bc12541df8c568e4e0c66c4e1991ec4c923c"
DEVSHA256SUM = "54c79348f364731d90aa857597185d73067864e208fb24a8fb7675405661c6d6"
NVPSHA256SUM = "f2ab966c85b6e8a36098a6bc05e0ac478bd1cc9d966373223c0ec56dc73d302b"
NVPDEVSHA256SUM = "816c0f9daa031be52e873907662c26d8d58e29cc2603a5d7ba1e60a0d76d00fd"

SRC_URI[lib.sha256sum] = "${LIBSHA256SUM}"
SRC_URI[dev.sha256sum] = "${DEVSHA256SUM}"
SRC_URI[nvp.sha256sum] = "${NVPSHA256SUM}"
SRC_URI[nvpdev.sha256sum] = "${NVPDEVSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=48;md5=3d6981c1227c404d42d710f96a875a1b"

REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "tegra-libraries-multimedia"

S = "${WORKDIR}/tensorrt"

CONTAINER_CSV_FILES = "${libdir}/*.so*"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/usr/include/aarch64-linux-gnu/*.h ${D}${includedir}
    install -d ${D}${libdir}
    cp --preserve=mode,timestamps,links --no-dereference ${S}/usr/lib/aarch64-linux-gnu/*.so* ${D}${libdir}
}

INSANE_SKIP:${PN} = "already-stripped"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
