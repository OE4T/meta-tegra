DESCRIPTION = "NVIDIA TensorRT Plugins for deep learning"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Apache-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = " \
  file://LICENSE;md5=5feff12211c5116f88277308f4b88a64 \
  file://third_party/cub/LICENSE.TXT;md5=20d1414b801e2a130d7d546685105508 \
  file://parsers/onnx/third_party/onnx/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
  file://parsers/onnx/LICENSE;md5=aa3e92f9f2b6da1568c23ceaec468692 \
"

inherit cuda cmake pkgconfig

SRC_REPO = "github.com/NVIDIA/TensorRT.git;protocol=https"
SRCBRANCH = "release/10.3"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH} \
    file://0001-CMakeLists.txt-fix-cross-compilation-issues.patch \
"

# v10.3.0 tag
SRCREV = "c5b9de37f7ef9034e2efc621c664145c7c12436e"

DEPENDS += "zlib cuda-cudart cuda-nvrtc protobuf protobuf-native tensorrt-core"

COMPATIBLE_MACHINE = "(tegra)"

PACKAGECONFIG ??= " \
    plugin \
    parsers \
"
PACKAGECONFIG[plugin] = "-DBUILD_PLUGINS=ON,-DBUILD_PLUGINS=OFF,"
PACKAGECONFIG[parsers] = "-DBUILD_PARSERS=ON,-DBUILD_PARSERS=OFF,"

EXTRA_OECMAKE = '-DBUILD_SAMPLES=OFF -DSKIP_GPU_ARCHS=ON -DTRT_PLATFORM_ID="${TARGET_ARCH}" \
  -DCUDA_VERSION="${CUDA_VERSION}" \
  -DCUDA_INCLUDE_DIRS="${STAGING_DIR_HOST}/usr/local/cuda-${CUDA_VERSION}/include" \
  -DENABLED_SMS="-DENABLE_SM${TEGRA_CUDA_ARCHITECTURE}" \
  -DSTABLE_DIFFUSION_GENCODES="-gencode arch=compute_${TEGRA_CUDA_ARCHITECTURE},code=compute_${TEGRA_CUDA_ARCHITECTURE}" \
  -DProtobuf_LIBRARY="${STAGING_LIBDIR}/libprotobuf.so" \
  -DProtobuf_PROTOC_EXECUTABLE="${STAGING_BINDIR_NATIVE}/protoc" \
  -DONNX_CUSTOM_PROTOC_EXECUTABLE="${STAGING_BINDIR_NATIVE}/protoc" \
  -DONNX_USE_PROTOBUF_SHARED_LIBS=ON \
  -DCMAKE_FIND_PACKAGE_PREFER_CONFIG=ON \
  -DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
'

def cudify_flags(varname, d):
    return ' '.join(['-Xcompiler {}'.format(flag) for flag in (d.getVar(varname) or '').split()])

CUDAFLAGS += "-Xcompiler -DENABLE_SM${TEGRA_CUDA_ARCHITECTURE} ${@cudify_flags('DEBUG_PREFIX_MAP', d)}"
LDFLAGS += "-Wl,--no-undefined"

do_install:append() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/include/NvInferPlugin.h ${D}${includedir}
    install -m 0644 ${S}/include/NvInferPluginUtils.h ${D}${includedir}
    install -m 0644 ${S}/include/NvOnnxConfig.h ${D}${includedir}
    install -m 0644 ${S}/parsers/onnx/NvOnnxParser.h ${D}${includedir}
}

RDEPENDS:${PN} += "cudnn libcublas"
