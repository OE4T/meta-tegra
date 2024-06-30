L4T_DEB_COPYRIGHT_MD5 = "ef1b882a6a8ed90f38e4b0288fcd1525"

require tegra-debian-libraries-common.inc

LICENSE += "& Apache-2.0"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-tegra/LICENSE.tegra_sensors;md5=e7fe81c64aaee40b3d9e5b11c3e0ea58"

MAINSUM = "0afb73f595113bf02d2f66924dd351bb665941a569507fd730c66167a4823d94"
MAINSUM:tegra210 = "c2550014e670366c339662203cbc0c5d2afd92b5a6557923844a42526e3aa6ac"

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
    tegra/libnvrm.so \
    tegra/libnvrm_gpu.so \
    tegra/libnvrm_graphics.so \
    tegra/libsensors.hal-client.nvs.so \
    tegra/libsensors.l4t.no_fusion.nvs.so \
    tegra/libsensors_hal.nvs.so \
    ${SOC_SPECIFIC_LIBS} \
"

SOC_SPECIFIC_LIBS = "\
    tegra/libnvisp_utils.so \
    tegra/libnvpva.so \
"
SOC_SPECIFIC_LIBS:tegra210 = ""

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RRECOMMENDS:${PN} = "kernel-module-nvgpu"
