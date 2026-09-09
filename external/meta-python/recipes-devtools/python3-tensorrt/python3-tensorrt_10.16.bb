SUMMARY = "Python bindings for TensorRT"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://python/packaging/bindings_wheel/LICENSE.txt;md5=0f58ca2991dd21e8f5c268a18ac2535b"

DEPENDS = "python3-pybind11 tensorrt-core tensorrt-plugins"

COMPATIBLE_MACHINE = "(tegra)"

inherit cmake cuda python3-dir python3targetconfig

SRC_REPO = "github.com/NVIDIA/TensorRT.git;protocol=https"
SRCBRANCH = "release/${PV}"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH} \
           file://0001-Fixups-for-cross-building-in-OE.patch \
    "

# v${PV} tag
SRCREV = "52399f555c2f80cb690a4a558b604e1a5f227e7c"

OECMAKE_SOURCEPATH = "${S}/python"

EXTRA_OECMAKE = "-DTENSORRT_ROOT=${S} -DTENSORRT_LIBPATH=${STAGING_LIBDIR} -DTENSORRT_MODULE=tensorrt \
                 -DCUDA_INCLUDE_DIRS=${CUDA_PATH}/include \
                 -DTARGET=${HOST_ARCH} -DCMAKE_BUILD_TYPE=Release \
                 -DPY_INCLUDE=${STAGING_INCDIR}/${PYTHON_DIR} -DEXT_PATH=${STAGING_INCDIR} \
                 -DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
                 -DTRT_NVINFER_NAME=nvinfer -DTRT_ONNXPARSER_NAME=nvonnxparser "

CXXFLAGS += "${CUDA_CXXFLAGS}"

do_configure() {
    cmake_do_configure
    TRT_MAJOR=$(awk '/^#define TRT_MAJOR_ENTERPRISE/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_MINOR=$(awk '/^#define TRT_MINOR_ENTERPRISE/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_PATCH=$(awk '/^#define TRT_PATCH_ENTERPRISE/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_MAJMINPATCH=${TRT_MAJOR}.${TRT_MINOR}.${TRT_PATCH}
    varsubst() {
        sed -e "s|\#\#TENSORRT_PYTHON_VERSION\#\#|${TRT_MAJMINPATCH}|g" \
	    -e "s|\#\#TENSORRT_PLUGIN_DISABLED\#\#|False|g" \
	    -e "s|\#\#TENSORRT_NVINFER_NAME\#\#|nvinfer|g" \
	    -e "s|\#\#TENSORRT_ONNXPARSER_NAME\#\#|nvonnxparser|g" \
	    -e "s|\#\#TENSORRT_MAJOR\#\#|${TRT_MAJOR}|g" \
	    -e "s|\#\#TENSORRT_MINOR\#\#|${TRT_MINOR}|g" \
	    -e "s|\#\#TENSORRT_MODULE\#\#|tensorrt|g" $1 >$2
    }

    rm -rf ${B}/tensorrt
    mkdir ${B}/tensorrt
    varsubst ${S}/python/packaging/bindings_wheel/tensorrt/__init__.py ${B}/tensorrt/__init__.py
    cp -R --preserve=mode,timestamps ${S}/python/packaging/bindings_wheel/tensorrt/plugin ${B}/tensorrt/
}

do_install() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/tensorrt
    install -m 0644 ${B}/tensorrt/__init__.py ${D}${PYTHON_SITEPACKAGES_DIR}/tensorrt/
    install -m 0755 ${B}/tensorrt/tensorrt.so ${D}${PYTHON_SITEPACKAGES_DIR}/tensorrt/
    cp -R --preserve=mode,timestamps ${B}/tensorrt/plugin ${D}${PYTHON_SITEPACKAGES_DIR}/tensorrt/
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}"

RDEPENDS:${PN} = "python3-core python3-ctypes python3-numpy tensorrt-plugins"
