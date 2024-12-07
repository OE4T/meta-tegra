SUMMARY = "NVIDIA DLA Compiler"
L4T_DEB_COPYRIGHT_MD5 = "45d9a257d98fb290f71e4f5f87f805b4"

L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

MAINSUM = "31cd17e859590332c17e26ed0b6d3ca4dce604a366b5b724cad89a08fd1b368a"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvdla_compiler.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "tegra-libraries-core"
