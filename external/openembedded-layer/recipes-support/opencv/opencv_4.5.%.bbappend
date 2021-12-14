FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

inherit cuda

EXTRA_OECMAKE:append:tegra210 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="5.3" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE:append:tegra186 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="6.2" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE:append:tegra194 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="7.2" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE:append:cuda = ' -DOPENCV_CUDA_DETECTION_NVCC_FLAGS="-ccbin ${CUDAHOSTCXX}"'

EXTRA_OECMAKE:append = " -DOPENCV_GENERATE_PKGCONFIG=ON"

OPTICALFLOW_MD5 = "a73cd48b18dcc0cc8933b30796074191"
OPTICALFLOW_HASH = "edb50da3cf849840d680249aa6dbef248ebce2ca"

SRC_URI += "https://github.com/NVIDIA/NVIDIAOpticalFlowSDK/archive/${OPTICALFLOW_HASH}.zip;name=opticalflow;unpack=false;subdir=${OPENCV_DLDIR}/nvidia_optical_flow;downloadfilename=${OPTICALFLOW_MD5}-${OPTICALFLOW_HASH}.zip"

SRC_URI[opticalflow.md5sum] = "${OPTICALFLOW_MD5}"
SRC_URI[opticalflow.sha256sum] = "e300c02e4900741700b2b857965d2589f803390849e1e2022732e02f4ae9be44"

# No stable URI is available for NVIDIAOpticalFlowSDK
INSANE_SKIP:append = " src-uri-bad"

DEPENDS:append:cuda = "${@' cudnn' if 'dnn' in d.getVar('PACKAGECONFIG') else ''}"
SRC_URI += "file://0001-Fix-search-paths-in-FindCUDNN.cmake.patch"
