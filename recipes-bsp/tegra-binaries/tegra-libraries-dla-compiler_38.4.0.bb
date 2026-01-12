SUMMARY = "NVIDIA DLA Compiler"
L4T_DEB_COPYRIGHT_MD5 = "0bcf16925f181c03665ce6ed3b238e77"

L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

COMPATIBLE_MACHINE = "(tegra234)"

MAINSUM = "d10524e9a3b5ecb6db7770d65d54b75df8a4e3fcd45446127ff4480ae343f862"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvdla_compiler.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "tegra-libraries-core"
