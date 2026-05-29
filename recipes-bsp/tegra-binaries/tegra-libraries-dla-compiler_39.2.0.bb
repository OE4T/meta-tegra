SUMMARY = "NVIDIA DLA Compiler"
L4T_DEB_COPYRIGHT_MD5 = "0bcf16925f181c03665ce6ed3b238e77"

L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

COMPATIBLE_MACHINE = "(tegra234)"

MAINSUM = "20edfd946ce3c91c497f24905729bfd9a9245c326d98d261a11e5fafe3e10bf7"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvdla_compiler.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
RDEPENDS:${PN} = "tegra-libraries-core"
