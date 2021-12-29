L4T_DEB_COPYRIGHT_MD5 = "fab0c15b4bbf7f8d5ac2bd6673d4211c"

require tegra-debian-libraries-common.inc

LICENSE += "& Apache-2.0"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.tegra_sensors;md5=e7fe81c64aaee40b3d9e5b11c3e0ea58"

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-libraries-core_32.7.2.bb
MAINSUM = "41845dade9d3e1cd67be7875e0634167de414678f088d4c6342ccd696894e63e"
MAINSUM:tegra210 = "553a56f565e0ac9659a6633c3fe07afc3e68fad4451bcec9b651a929c0e986c5"
========
MAINSUM = "da997f39c1e66d5d8ceeca6e4ee33a3e9951237c1238ea51df25c7d8e10c65a7"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-libraries-core_34.1.0.bb

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvcolorutil.so \
    tegra/libnvdc.so \
    tegra/libnvddk_2d_v2.so \
    tegra/libnvddk_vic.so \
    tegra/libnvdla_compiler.so \
    tegra/libnvdla_runtime.so \
    tegra/libnvidia-tls.so.${L4T_VERSION} \
    tegra/libnvimp.so \
    tegra/libnvll.so \
    tegra/libnvos.so \
    tegra/libnvphs.so \
    tegra/libnvrm_chip.so \
    tegra/libnvrm_gpu.so \
    tegra/libnvrm_host1x.so \
    tegra/libnvrm_mem.so \
    tegra/libnvrm_stream.so \
    tegra/libnvrm_surface.so \
    tegra/libnvrm_sync.so \
    tegra/libnvsciipc.so \
    tegra/libnvsocsys.so \
    tegra/libnvvic.so \
    tegra/libsensors.hal-client.nvs.so \
    tegra/libsensors.l4t.no_fusion.nvs.so \
    tegra/libsensors_hal.nvs.so \
    tegra/libnvisp_utils.so \
    tegra/libnvpva.so \
    tegra/libnvpvaumd.so \
    tegra/libnvpva_algorithms.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
