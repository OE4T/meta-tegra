SUMMARY = "Python bindings for TensorRT"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://python/packaging/bindings_wheel/LICENSE.txt;md5=0f58ca2991dd21e8f5c268a18ac2535b"

DEPENDS = "python3-pybind11 tensorrt-core tensorrt-plugins"

COMPATIBLE_MACHINE = "(tegra)"

inherit setuptools3 cmake cuda

SRC_REPO = "github.com/NVIDIA/TensorRT.git;protocol=https"
SRCBRANCH = "release/10.3"
SRC_URI = "gitsm://${SRC_REPO};branch=${SRCBRANCH} \
           file://0001-Fixups-for-cross-building-in-OE.patch \
           "
# v10.3.0 tag
SRCREV = "c5b9de37f7ef9034e2efc621c664145c7c12436e"

S = "${WORKDIR}/git"

OECMAKE_SOURCEPATH = "${S}/python"
SETUPTOOLS_SETUP_PATH = "${B}"

EXTRA_OECMAKE = "-DTENSORRT_ROOT=${S} -DTENSORRT_LIBPATH=${STAGING_LIBDIR} -DTENSORRT_MODULE=tensorrt \
                 -DCUDA_INCLUDE_DIRS=${CUDA_PATH}/include \
                 -DTARGET=${HOST_ARCH} -DCMAKE_BUILD_TYPE=Release \
                 -DPY_INCLUDE=${STAGING_INCDIR}/${PYTHON_DIR} -DEXT_PATH=${STAGING_INCDIR}"

CXXFLAGS += "${CUDA_CXXFLAGS}"

do_configure() {
    cmake_do_configure
    TRT_MAJOR=$(awk '/^#define NV_TENSORRT_MAJOR/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_MINOR=$(awk '/^#define NV_TENSORRT_MINOR/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_PATCH=$(awk '/^#define NV_TENSORRT_PATCH/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_BUILD=$(awk '/^#define NV_TENSORRT_BUILD/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_VERSION=${TRT_MAJOR}.${TRT_MINOR}.${TRT_PATCH}.${TRT_BUILD}
    TRT_MAJMINPATCH=${TRT_MAJOR}.${TRT_MINOR}.${TRT_PATCH}
    varsubst() {
        sed -e "s|\#\#TENSORRT_VERSION\#\#|${TRT_VERSION}|g" \
	    -e "s|\#\#TENSORRT_MAJMINPATCH\#\#|${TRT_MAJMINPATCH}|g" \
	    -e "s|\#\#TENSORRT_PYTHON_VERSION\#\#|${TRT_MAJMINPATCH}|g" \
	    -e "s|\#\#TENSORRT_MODULE\#\#|tensorrt|g" $1 >$2
    }

    rm -rf ${B}/tensorrt
    mkdir ${B}/tensorrt
    varsubst ${S}/python/packaging/bindings_wheel/setup.cfg ${B}/setup.cfg
    varsubst ${S}/python/packaging/bindings_wheel/setup.py ${B}/setup.py
    varsubst ${S}/python/packaging/bindings_wheel/tensorrt/__init__.py ${B}/tensorrt/__init__.py
    cp ${S}/python/packaging/bindings_wheel/LICENSE.txt ${B}/
}

do_compile() {
    cmake_do_compile
    setuptools3_do_compile
}

do_install() {
    setuptools3_do_install
}

RDEPENDS:${PN} = "python3-ctypes python3-numpy"
