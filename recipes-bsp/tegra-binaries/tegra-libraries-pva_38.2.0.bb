L4T_DEB_COPYRIGHT_MD5 = "9bc6d8162be2293e5c57f3798c73bb44"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "a721d6a39897803f17b946ecb3e071a7d275221e43e4564acc93ccd7e4711fcb"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvpvaintf.so \
    nvidia/libnvpvaumd_cuda.so \
    nvidia/libnvpvaumd_core.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
