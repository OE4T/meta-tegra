L4T_DEB_COPYRIGHT_MD5 = "9bc6d8162be2293e5c57f3798c73bb44"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "a6e2f703c68c2b3285e0214da34f7ed2fa38964df5b9970762c2c2b0dc6e589c"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvpvaintf.so \
    nvidia/libnvpvaumd_cuda.so \
    nvidia/libnvpvaumd_core.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
