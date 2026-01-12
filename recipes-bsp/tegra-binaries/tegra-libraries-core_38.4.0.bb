L4T_DEB_COPYRIGHT_MD5 = "d9a8361f8068fc4e3b934118d1de9f3f"

require tegra-debian-libraries-common.inc

LICENSE += "& Apache-2.0"
LIC_FILES_CHKSUM += "file://usr/share/doc/nvidia-l4t-core/LICENSE.libnvidia-rmapi-tegra;md5=5c7c5200a29e873064f17b5bbf4d3c56"

MAINSUM = "5fe480d1937754498299b138cf86b5e5b78d50f4b52c0b3b7f900c50c6de6ad4"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvcolorutil.so \
    nvidia/libnvdc.so \
    nvidia/libnvddk_2d_v2.so \
    nvidia/libnvddk_vic.so \
    nvidia/libnvdla_runtime.so \
    nvidia/libnvidia-rmapi-tegra.so.${L4T_LIB_VERSION} \
    nvidia/libnvidia-tls.so.${L4T_LIB_VERSION} \
    nvidia/libnvimp.so \
    nvidia/libnvisp_utils.so \
    nvidia/libnvos.so \
    nvidia/libnvpva.so \
    nvidia/libnvpva_algorithms.so \
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
    nvidia/libnvplayfair.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
