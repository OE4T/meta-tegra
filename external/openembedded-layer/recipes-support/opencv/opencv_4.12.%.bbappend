FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

inherit cuda

PACKAGECONFIG[cuda] = "-DWITH_CUDA=ON -DENABLE_CUDA_FIRST_CLASS_LANGUAGE=ON,-DWITH_CUDA=OFF,${CUDA_DEPENDS} cudnn"

OPENCV_CUDA_SUPPORT ?= "${@'cuda dnn' if d.getVar('OECMAKE_CUDA_ARCHITECTURES') != 'OFF' else ''}"
PACKAGECONFIG:append:cuda = " ${OPENCV_CUDA_SUPPORT}"

SRC_URI:append:cuda = " \
    file://0001-Fix-search-paths-in-FindCUDNN.cmake.patch \
    file://0002-Fix-broken-override-of-CUDA_TOOLKIT_TARGET_DIR-setti.patch \
    file://0003-Merge-pull-request-27636-from-jmackay2-cuda_13.patch \
    file://0004-cuda-update-videostab-for-cuda-13.0.patch;patchdir=contrib \
"

OPTICALFLOW_MD5 = "a73cd48b18dcc0cc8933b30796074191"
OPTICALFLOW_HASH = "edb50da3cf849840d680249aa6dbef248ebce2ca"

SRC_URI:append:cuda = " https://github.com/NVIDIA/NVIDIAOpticalFlowSDK/archive/${OPTICALFLOW_HASH}.zip;name=opticalflow;unpack=false;downloadfilename=${OPTICALFLOW_MD5}-${OPTICALFLOW_HASH}.zip"

do_unpack_extra:append:cuda() {
    mkdir -p ${OPENCV_DLDIR}/nvidia_optical_flow
    ln -sf ${UNPACKDIR}/${OPTICALFLOW_MD5}-${OPTICALFLOW_HASH}.zip ${OPENCV_DLDIR}/nvidia_optical_flow/
}

do_install:append:cuda() {
    sed -i -e's,-L${STAGING_DIR_HOST}${exec_prefix},-L\$\{exec_prefix\},' ${D}${libdir}/pkgconfig/opencv4.pc
}

SRC_URI[opticalflow.md5sum] = "${OPTICALFLOW_MD5}"
SRC_URI[opticalflow.sha256sum] = "e300c02e4900741700b2b857965d2589f803390849e1e2022732e02f4ae9be44"

# No stable URI is available for NVIDIAOpticalFlowSDK
INSANE_SKIP:append:cuda = " src-uri-bad"
