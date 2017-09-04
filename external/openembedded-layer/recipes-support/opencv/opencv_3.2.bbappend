SRC_URI_remove = "file://0002-Revert-check-FP16-build-condition-correctly.patch"

inherit cuda

EXTRA_OECMAKE_append_tegra210 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="5.3" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE_append_tegra186 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="6.1" -DCUDA_ARCH_PTX=""'
EXTRA_OECMAKE_append_tegra124 = ' -DWITH_CUDA=ON -DCUDA_ARCH_BIN="3.2" -DCUDA_ARCH_PTX=""'
