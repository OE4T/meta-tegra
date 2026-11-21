L4T_DEB_COPYRIGHT_MD5 = "d9a8361f8068fc4e3b934118d1de9f3f"

require tegra-debian-libraries-common.inc

MAINSUM = "d10d5e95a4970e7667c08fd987e4d37ac6efa1268b6ee07ecf769a537bfa43c3"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvcolorutil.so \
    nvidia/libnvdc.so \
    nvidia/libnvddk_2d_v2.so \
    nvidia/libnvddk_vic.so \
    nvidia/libnvdla_runtime.so \
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
