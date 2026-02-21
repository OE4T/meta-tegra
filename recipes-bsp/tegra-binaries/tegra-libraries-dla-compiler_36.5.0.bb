SUMMARY = "NVIDIA DLA Compiler"
L4T_DEB_COPYRIGHT_MD5 = "0bcf16925f181c03665ce6ed3b238e77"

L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

MAINSUM = "aaab864da8540410f9c69c8ca886cc79900e56a1e7c77a6d18f600fb4d3b08ea"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvdla_compiler.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "tegra-libraries-core"
