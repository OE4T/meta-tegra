FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-3.3:"

SRC_URI += "file://0001-Merge-pull-request-9418-from-borisfom-cuda9.patch \
            file://0001-Addressing-CUDA9-shfl-deprecation.patch"

CXXFLAGS += "-std=c++11"
CUDA_NVCC_EXTRA_FLAGS = "-std=c++11 --expt-relaxed-constexpr"

inherit cuda

EXTRA_OECMAKE_append_tegra210 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="5.3" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE_append_tegra186 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="6.1" -DCUDA_ARCH_PTX=""'

# See here (https://docs.opencv.org/trunk/d6/d15/tutorial_building_tegra_cuda.html) 
# for the recommended flags.
# Standard opencv_3.3 recipe from meta-oe does not include OpenMP, so I'm adding it here.
EXTRA_OECMAKE_append_tegra124 = '       \
    -DWITH_CUDA=ON                      \
    -DCUDA_ARCH_BIN="3.2"               \
    -DCUDA_ARCH_PTX=""                  \
    -DENABLE_NEON=ON                    \
    -DWITH_OPENMP=ON                    \
  '