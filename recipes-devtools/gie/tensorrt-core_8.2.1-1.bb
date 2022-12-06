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
LIBSHA256SUM = "13bc5c1e28a537972a24f7573b7196d399f51f2badf069afd2ed670b41868414"
DEVSHA256SUM = "e2314b9eaad7324fc5fe08bb42160026f6f4332781248658ae261d65a8cb94df"
NVPSHA256SUM = "a1d9074b39fd589097c8fa95a0354baabc97a5395e0e1aa6d07ada0e0c3a0fcd"
NVPDEVSHA256SUM = "bac686afc13fcf41bb0f157c18f2c7ad5e447a8df771c2303c1f3232520a721c"

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
