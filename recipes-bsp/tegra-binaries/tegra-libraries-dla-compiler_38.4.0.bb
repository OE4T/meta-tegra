SUMMARY = "NVIDIA DLA Compiler"
L4T_DEB_COPYRIGHT_MD5 = "0bcf16925f181c03665ce6ed3b238e77"

L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

COMPATIBLE_MACHINE = "(tegra234)"

MAINSUM = "8dfdfff5df6197dccca8c3232ea6e646098b5f5533c18b8a92cd73a94a104ebb"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvdla_compiler.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "tegra-libraries-core"
