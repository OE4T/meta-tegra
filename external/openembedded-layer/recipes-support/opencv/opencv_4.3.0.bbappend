FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

inherit cuda

EXTRA_OECMAKE_append_tegra210 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="5.3" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE_append_tegra186 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="6.2" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE_append_tegra194 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="7.2" -DCUDA_ARCH_PTX=""'

EXTRA_OECMAKE_append = " -DOPENCV_GENERATE_PKGCONFIG=ON"

OPTICALFLOW_MD5 = "ca5acedee6cb45d0ec610a6732de5c15"
OPTICALFLOW_HASH = "79c6cee80a2df9a196f20afd6b598a9810964c32"

SRC_URI += "https://github.com/NVIDIA/NVIDIAOpticalFlowSDK/archive/${OPTICALFLOW_HASH}.zip;name=opticalflow;unpack=false;subdir=${OPENCV_DLDIR}/nvidia_optical_flow;downloadfilename=${OPTICALFLOW_MD5}-${OPTICALFLOW_HASH}.zip"

SRC_URI[opticalflow.md5sum] = "${OPTICALFLOW_MD5}"
SRC_URI[opticalflow.sha256sum] = "c6ce0a9bc628b354b0b59a9677edc45c9ee2f640f3abb7353a94fe28b2689ed4"

# No stable URI is available for NVIDIAOpticalFlowSDK
INSANE_SKIP_append = " src-uri-bad"

DEPENDS_append_cuda = "${@' cudnn' if 'dnn' in d.getVar('PACKAGECONFIG') else ''}"
SRC_URI += "file://0001-Fix-search-paths-in-FindCUDNN.cmake.patch"
