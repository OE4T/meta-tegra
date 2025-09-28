SUMMARY = "NVIDIA DLA Compiler"
L4T_DEB_COPYRIGHT_MD5 = "45d9a257d98fb290f71e4f5f87f805b4"

L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

MAINSUM = "01f0453a154e077a468c9bfb9b19c817cb130d207cd82d1d57487ffffe7c65b9"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvdla_compiler.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "tegra-libraries-core"
