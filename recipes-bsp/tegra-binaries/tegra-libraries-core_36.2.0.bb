L4T_DEB_COPYRIGHT_MD5 = "4316d40a8ea9e946e76430d2d02b5848"

require tegra-debian-libraries-common.inc

LICENSE += "& Apache-2.0"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.tegra_sensors;md5=e7fe81c64aaee40b3d9e5b11c3e0ea58"

MAINSUM = "4630328fe8baa73352bbd5e7d6acc0e8cb7d7026f172cb68d13bd7d54063feb0"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvcolorutil.so \
    nvidia/libnvdc.so \
    nvidia/libnvddk_2d_v2.so \
    nvidia/libnvddk_vic.so \
    nvidia/libnvdla_runtime.so \
    nvidia/libnvidia-rmapi-tegra.so.${L4T_LIB_VERSION} \
    nvidia/libnvidia-tls.so.${L4T_LIB_VERSION} \
    nvidia/libnvimp.so \
    nvidia/libnvll.so \
    nvidia/libnvos.so \
    nvidia/libnvphs.so \
    nvidia/libnvrm_chip.so \
    nvidia/libnvrm_gpu.so \
    nvidia/libnvrm_host1x.so \
    nvidia/libnvrm_mem.so \
    nvidia/libnvrm_stream.so \
    nvidia/libnvrm_surface.so \
    nvidia/libnvrm_sync.so \
    nvidia/libnvsciipc.so \
    nvidia/libnvsocsys.so \
    nvidia/libnvtegrahv.so \
    nvidia/libnvvic.so \
    nvidia/libsensors.hal-client.nvs.so \
    nvidia/libsensors.l4t.no_fusion.nvs.so \
    nvidia/libsensors_hal.nvs.so \
    nvidia/libnvisp_utils.so \
    nvidia/libnvpva.so \
    nvidia/libnvpvaumd.so \
    nvidia/libnvpva_algorithms.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
