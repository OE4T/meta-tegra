SUMMARY = "Python bindings for TensorRT"
HOMEPAGE = "http://developer.nvidia.com/tensorrt"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://python/packaging/LICENSE.txt;md5=c291e0a531e08d4914e269730ba2f70d"

DEPENDS = "python3-pybind11 tensorrt"

inherit setuptools3 cmake cuda

SRC_REPO = "github.com/NVIDIA/TensorRT.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
           file://0001-Do-not-overwrite-CMAKE_CXX_FLAGS.patch \
           file://0002-pyOnnx-fixups-for-TensorRT-7.1-API.patch \
           "
SRCREV = "ab20a8ac26da03df5e434b4f9d1da0156ca019b6"

S = "${WORKDIR}/git"

OECMAKE_SOURCEPATH = "${S}/python"
DISTUTILS_SETUP_PATH = "${B}"

EXTRA_OECMAKE = "-DONNX_INC_DIR=${STAGING_INCDIR} -DPYBIND11_DIR=${STAGING_DIR_TARGET} \
                 -DTARGET_ARCHITECTURE=${HOST_ARCH} -DCMAKE_BUILD_TYPE=Release \
                 -DPY_INCLUDE=${STAGING_INCDIR}/${PYTHON_DIR}"

CXXFLAGS += "${CUDA_CXXFLAGS}"

do_configure() {
    cmake_do_configure
    TRT_MAJOR=$(awk '/^\#define NV_TENSORRT_MAJOR/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_MINOR=$(awk '/^\#define NV_TENSORRT_MINOR/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_PATCH=$(awk '/^\#define NV_TENSORRT_PATCH/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_BUILD=$(awk '/^\#define NV_TENSORRT_BUILD/ {print $3}' ${STAGING_INCDIR}/NvInferVersion.h)
    TRT_VERSION=${TRT_MAJOR}.${TRT_MINOR}.${TRT_PATCH}.${TRT_BUILD}
    TRT_MAJMINPATCH=${TRT_MAJOR}.${TRT_MINOR}.${TRT_PATCH}
    varsubst() {
        sed -e "s|\#\#TENSORRT_VERSION\#\#|${TRT_VERSION}|g" \
	    -e "s|\#\#TENSORRT_MAJMINPATCH\#\#|${TRT_MAJMINPATCH}|g" $1 >$2
    }

    rm -rf ${B}/tensorrt
    mkdir ${B}/tensorrt
    varsubst ${S}/python/packaging/setup.cfg ${B}/setup.cfg
    varsubst ${S}/python/packaging/setup.py ${B}/setup.py
    varsubst ${S}/python/packaging/tensorrt/__init__.py ${B}/tensorrt/__init__.py
    cp ${S}/python/packaging/LICENSE.txt ${B}/
}

do_compile() {
    cmake_do_compile
    distutils3_do_compile
}

do_install() {
    distutils3_do_install
}

RDEPENDS_${PN} = "python3-ctypes python3-numpy"
