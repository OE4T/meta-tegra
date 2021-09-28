DESCRIPTION = "NVIDIA TensorRT Plugins for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Apache-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = " \
  file://LICENSE;md5=b43e4a60a0643023327e7caf2dbf8663 \
  file://third_party/cub/LICENSE.TXT;md5=20d1414b801e2a130d7d546685105508 \
  file://parsers/onnx/third_party/onnx/LICENSE;md5=efff5c5110f124a1e2163814067b16e7 \
  file://parsers/onnx/LICENSE;md5=73b35773827cb985bfc6c085ed8d2394 \
"

inherit cuda cmake container-runtime-csv

SRC_REPO = "github.com/NVIDIA/TensorRT.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH} \
    file://0001-CMakeLists.txt-fix-cross-compilation-issues.patch \
    file://0002-Remove-constexpr.patch \
"
SRCREV = "9a9cae75e7155b2114454f37ccc49eca9d3352dc"

S = "${WORKDIR}/git"

DEPENDS += "zlib libcublas cudnn cuda-cudart cuda-nvrtc protobuf protobuf-native tensorrt-core"

COMPATIBLE_MACHINE = "(tegra)"

CONTAINER_CSV_FILES = "${libdir}/*.so*"

PACKAGECONFIG ??= " \
    plugin \
    parsers \
"
PACKAGECONFIG[plugin] = "-DBUILD_PLUGINS=ON,-DBUILD_PLUGINS=OFF,"
PACKAGECONFIG[parsers] = "-DBUILD_PARSERS=ON,-DBUILD_PARSERS=OFF,"

EXTRA_OECMAKE = "-DBUILD_SAMPLES=OFF -DSKIP_GPU_ARCHS=ON -DTRT_PLATFORM_ID="${TARGET_ARCH}" \
  -DCUDA_VERSION="${CUDA_VERSION}" -DCUDA_NVCC_FLAGS="${CUDA_NVCC_FLAGS}" \
  -DCUDA_INCLUDE_DIRS="${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include" \
"

LDFLAGS += "-Wl,--no-undefined"

do_install_append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/include/NvInferPlugin.h ${D}${includedir}
    install -m 0644 ${S}/include/NvInferPluginUtils.h ${D}${includedir}
    install -m 0644 ${S}/include/NvOnnxConfig.h ${D}${includedir}  
    install -m 0644 ${S}/parsers/onnx/NvOnnxParser.h ${D}${includedir}
}

RDEPENDS_${PN} += "tegra-libraries"
