DESCRIPTION = "NVIDIA TensorRT (GPU Inference Engine) for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"

inherit nvidia_devnet_downloads

SUBDIR = "NoDLA/"
SUBDIR_tegra194 = "DLA/"
PREFIX = "${@d.getVar('SUBDIR').replace('/', '-')}"

SRC_URI = "\
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvinfer6_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvinfer6_${PV}+cuda10.0_arm64.deb;name=lib;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvinfer-dev_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvinfer-dev_${PV}+cuda10.0_arm64.deb;name=dev;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvinfer-samples_${PV}+cuda10.0_all.deb;downloadfilename=${PREFIX}libnvinfer-samples_${PV}+cuda10.0_all.deb;name=samples;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvparsers6_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvparsers6_${PV}+cuda10.0_arm64.deb;name=nvp;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvparsers-dev_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvparsers-dev_${PV}+cuda10.0_arm64.deb;name=nvpdev;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvonnxparsers6_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvonnxparsers6_${PV}+cuda10.0_arm64.deb;name=onnx;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvonnxparsers-dev_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvonnxparsers-dev_${PV}+cuda10.0_arm64.deb;name=onnxdev;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvinfer-plugin6_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvinfer-plugin6_${PV}+cuda10.0_arm64.deb;name=plugin;subdir=tensorrt \
    ${NVIDIA_DEVNET_MIRROR}/${SUBDIR}libnvinfer-plugin-dev_${PV}+cuda10.0_arm64.deb;downloadfilename=${PREFIX}libnvinfer-plugin-dev_${PV}+cuda10.0_arm64.deb;name=plugindev;subdir=tensorrt \
"
LIBMD5SUM = "1835f0a51a9b3f7321116f9413c74cdf"
LIBSHA256SUM = "9c5112a137e07a7a46f4bb4000cb44fee22c1893fbf40b29fe5b1cbe1f4687cb"
DEVMD5SUM = "028c2ba7bb1df5fb849083a03f60f3b3"
DEVSHA256SUM = "222c4a8b940e7327c9967d768611c826d30cdb61bcd75a7d44ec2ce460b6840f"
SAMPMD5SUM = "d03fa95628d7d82a5f96705b0bf7f376"
SAMPSHA256SUM = "dfdf40fffa2906b015f1cdc34c9d8925949d5f971968bc6d669454778239d2ed"
NVPMD5SUM = "904515e33e978af82ca70dd7a70b589c"
NVPSHA256SUM = "d98c2ee6b8c3f5fdfb92972febb91045cd5123217225d9763c0a45186575b30a"
NVPDEVMD5SUM = "1b1f184f0162f7fece9008082a922623"
NVPDEVSHA256SUM = "2cf0bb7d92ea474bc0f93d6d3d30ccfc9691cb848ab00157fb0ceb1bb9967338"
ONNXMD5SUM = "53ce9029e0543d28d9695ab62f8e6244"
ONNXSHA256SUM = "7eb42b20ebade59221bb700c6885c5adc8ab3147e12d8c831cbb4d13c8240861"
ONNXDEVMD5SUM = "2715ed6071e2653244af3fee13109821"
ONNXDEVSHA256SUM = "eda6d5dea52f42fe702ff6274a6a333946d4bed1e9a82e59da57d691141ec6f2"
PLUGINMD5SUM = "f8a3fa53f4ff1ca996f2e32832065924"
PLUGINSHA256SUM = "7b55e597c6b197bcd2f9f0a706c5aa225f5880b8ec45f9bd0937bfe752494474"
PLUGINDEVMD5SUM = "a306531a14e3d0a7a19f2cde5bec98ed"
PLUGINDEVSHA256SUM = "3122db9dacb4fa59ea1229bf5ac9095c9fa760f7992cdad02f0911567c12710a"

LIBMD5SUM_tegra194 = "6368d3cee6df8ad49ce6f3a7237e7eb9"
LIBSHA256SUM_tegra194 = "c3b6010edfdf920dc58376936f99ab8c790cb89420b020fb80c766f1a032c68d"
DEVMD5SUM_tegra194 = "dad7cdacb1ab2e03a799f2385e291fed"
DEVSHA256SUM_tegra194 = "288bb16ce45a0d8b6edd76025cdedc29a9443581dced60d916851104d272b376"
SAMPMD5SUM_tegra194 = "22a406a00a6aab62550eafb2381b2954"
SAMPSHA256SUM_tegra194 = "2dcb2d9bf02e8db91dcb97b2ac102840697cab757ccaa51b9918eb4091b4997c"
NVPMD5SUM_tegra194 = "2355f433cb94d4ec63d0ed709ab301ce"
NVPSHA256SUM_tegra194 = "3dfc96c8b32989cd292c049f149b651c7c1971d4f26dcd57d9194caac414932e"
NVPDEVMD5SUM_tegra194 = "ab4797e1efac6db1864c3cc03ed61a60"
NVPDEVSHA256SUM_tegra194 = "4a50910db8edfe7783dde2551cb54388268e6d2b1cab3fd36eee23b02d00c5b7"
ONNXMD5SUM_tegra194 = "454533e75d44be33ed9807e4b000fd16"
ONNXSHA256SUM_tegra194 = "3fb860415f1bba37ad09abddf1f86c945fd34336a2d7ab1f617d5a3eaeb78a55"
ONNXDEVMD5SUM_tegra194 = "a381f4b506c05c39701314c1efd8d18d"
ONNXDEVSHA256SUM_tegra194 = "a2c8d585a791f2c9bd09531c73925146cfaa3cfb74fa582b0d935eec63eba180"
PLUGINMD5SUM_tegra194 = "54edbf9f4d023d7d26a400dc334f8d29"
PLUGINSHA256SUM_tegra194 = "23f034c07026ce503c4081e1fcd208696de76618552c01500010fad6b1e7e353"
PLUGINDEVMD5SUM_tegra194 = "1d28118c0e053381a5f94ab26421a603"
PLUGINDEVSHA256SUM_tegra194 = "546b0197df519815533b0bd0e0d6613f5c45686c904b93a2a6a00b44fd296f9e"

SRC_URI[lib.md5sum] = "${LIBMD5SUM}"
SRC_URI[lib.sha256sum] = "${LIBSHA256SUM}"
SRC_URI[dev.md5sum] = "${DEVMD5SUM}"
SRC_URI[dev.sha256sum] = "${DEVSHA256SUM}"
SRC_URI[samples.md5sum] = "${SAMPMD5SUM}"
SRC_URI[samples.sha256sum] = "${SAMPSHA256SUM}"
SRC_URI[nvp.md5sum] = "${NVPMD5SUM}"
SRC_URI[nvp.sha256sum] = "${NVPSHA256SUM}"
SRC_URI[nvpdev.md5sum] = "${NVPDEVMD5SUM}"
SRC_URI[nvpdev.sha256sum] = "${NVPDEVSHA256SUM}"
SRC_URI[onnx.md5sum] = "${ONNXMD5SUM}"
SRC_URI[onnx.sha256sum] = "${ONNXSHA256SUM}"
SRC_URI[onnxdev.md5sum] = "${ONNXDEVMD5SUM}"
SRC_URI[onnxdev.sha256sum] = "${ONNXDEVSHA256SUM}"
SRC_URI[plugin.md5sum] = "${PLUGINMD5SUM}"
SRC_URI[plugin.sha256sum] = "${PLUGINSHA256SUM}"
SRC_URI[plugindev.md5sum] = "${PLUGINDEVMD5SUM}"
SRC_URI[plugindev.sha256sum] = "${PLUGINDEVSHA256SUM}"

COMPATIBLE_MACHINE = "(tegra)"

LIC_FILES_CHKSUM = "file://usr/include/aarch64-linux-gnu/NvInfer.h;endline=48;md5=1755df325a6e1ac8515b1e469efe07a7"

CUDAPATH ?= "/usr/local/cuda-${CUDA_VERSION}"
BASEVER = "${@d.getVar('PV').split('-')[0]}"

S = "${WORKDIR}/tensorrt"

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
    tar -C ${S}/usr/lib/aarch64-linux-gnu -cf- . | tar -C ${D}${libdir}/ --no-same-owner -xf-
    install -d ${D}${prefix}/src
    cp --preserve=mode,timestamps --recursive ${S}/usr/src/tensorrt ${D}${prefix}/src/
}
PACKAGES =+ "${PN}-samples"
FILES_${PN}-samples = "${prefix}/src"

RDEPENDS_${PN} += "libstdc++ cudnn cuda-cublas cuda-cudart cuda-command-line-tools-libnvtoolsext tegra-libraries libglvnd"
RDEPENDS_${PN}-samples += "tegra-libraries bash python libglvnd cudnn cuda-cudart cuda-cublas"
RPROVIDES_${PN}-samples = "${PN}-examples"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
INSANE_SKIP_${PN} = "textrel ldflags"
INSANE_SKIP_${PN}-samples = "ldflags"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
