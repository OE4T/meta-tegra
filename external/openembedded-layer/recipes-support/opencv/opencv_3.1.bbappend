FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-3.1:"

SRC_URI += "file://disable-graphcut-for-cuda-8.0.patch"

inherit cuda

EXTRA_OECMAKE_append_tegra210 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="5.3" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE_append_tegra124 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="3.2" -DCUDA_ARCH_PTX=""'
