L4T_DEB_COPYRIGHT_MD5 = "8dc9729e1dc38aac4adb4bd6f6e3b370"

require tegra-debian-libraries-common.inc

LICENSE += "& Apache-2.0"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.tegra_sensors;md5=e7fe81c64aaee40b3d9e5b11c3e0ea58"

MAINSUM = "7ea038eeb71cad7cdd63a21382e9d9e756792d7800e412d0a1ab0d544a9b7856"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvcolorutil.so \
    tegra/libnvdc.so \
    tegra/libnvddk_2d_v2.so \
    tegra/libnvddk_vic.so \
    tegra/libnvdla_compiler.so \
    tegra/libnvdla_runtime.so \
    tegra/libnvidia-tls.so.35.6.1 \
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
