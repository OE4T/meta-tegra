L4T_DEB_COPYRIGHT_MD5 = "9bc6d8162be2293e5c57f3798c73bb44"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "5b3238d351b262d74ac8f709b75b151659bc3d51a3ce28c53948543408af060f"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvpvaintf.so \
    nvidia/libnvpvaumd_cuda.so \
    nvidia/libnvpvaumd_core.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
