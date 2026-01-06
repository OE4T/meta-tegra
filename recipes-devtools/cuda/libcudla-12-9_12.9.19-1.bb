CUDA_PKG = "libcudla libcudla-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "b45b97605b9c9862c82b2d18ac88e1d5202511370f5af5052d8169705a70ad95"
DEVSUM = "6f90e9fcaa3738d942627fae26156f2c4cebad2f1b6a54c92cb3137e52b56450"

RDEPENDS:${PN} = "tegra-libraries-core tegra-libraries-cuda"
