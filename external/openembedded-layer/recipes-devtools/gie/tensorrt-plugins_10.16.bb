DESCRIPTION = "NVIDIA TensorRT Plugins for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Apache-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = " \
  file://LICENSE;md5=99db4b09478f3c2b0a11901785687034 \
  file://third_party/cub/LICENSE.TXT;md5=20d1414b801e2a130d7d546685105508 \
  file://parsers/onnx/third_party/onnx/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
  file://parsers/onnx/LICENSE;md5=aa3e92f9f2b6da1568c23ceaec468692 \
"

inherit cuda cmake pkgconfig

SRC_REPO = "github.com/NVIDIA/TensorRT.git;protocol=https"
SRCBRANCH = "release/10.16"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH} \
    file://0001-CMakeLists.txt-fix-cross-compilation-issues.patch \
"

# v10.16 tag
SRCREV = "52399f555c2f80cb690a4a558b604e1a5f227e7c"

DEPENDS += "zlib cuda-cudart cuda-nvrtc protobuf protobuf-native tensorrt-core"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGECONFIG ??= " \
    plugin \
    parsers \
"
PACKAGECONFIG[plugin] = "-DBUILD_PLUGINS=ON,-DBUILD_PLUGINS=OFF,"
PACKAGECONFIG[parsers] = "-DBUILD_PARSERS=ON,-DBUILD_PARSERS=OFF,"

EXTRA_OECMAKE = '-DBUILD_SAMPLES=OFF -DTRT_PLATFORM_ID="${TARGET_ARCH}" \
  -DGPU_ARCHS="${TEGRA_CUDA_ARCHITECTURE}" \
  -DCUDA_VERSION="${CUDA_VERSION}" \
  -DCUDA_INCLUDE_DIRS="${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include" \
  -DTENSORRT_PYTHON_INCLUDE_DIR="${S}/include/impl" \
  -DProtobuf_LIBRARY="${STAGING_LIBDIR}/libprotobuf.so" \
  -DProtobuf_PROTOC_EXECUTABLE="${STAGING_BINDIR_NATIVE}/protoc" \
  -DONNX_CUSTOM_PROTOC_EXECUTABLE="${STAGING_BINDIR_NATIVE}/protoc" \
  -DONNX_USE_PROTOBUF_SHARED_LIBS=ON \
  -DCMAKE_FIND_PACKAGE_PREFER_CONFIG=ON \
  -DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
'

CUDAFLAGS += "-Xcompiler -DENABLE_SM${TEGRA_CUDA_ARCHITECTURE}"
LDFLAGS += "-Wl,--no-undefined"

do_install:append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/include/NvInferPlugin.h ${D}${includedir}
    install -m 0644 ${S}/include/NvInferPluginUtils.h ${D}${includedir}
    install -m 0644 ${S}/include/NvOnnxConfig.h ${D}${includedir}
    install -m 0644 ${S}/parsers/onnx/NvOnnxParser.h ${D}${includedir}
}

RDEPENDS:${PN} += "cudnn libcublas"
