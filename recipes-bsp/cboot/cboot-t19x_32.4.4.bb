DESCRIPTION = "cboot bootloader for Tegra194"

SRC_URI = "${L4T_URI_BASE}/cboot_src_t19x.tbz2;downloadfilename=cboot_src_t19x-${PV}.tbz2;subdir=${BP} \
           file://0001-Convert-Python-scripts-to-Python3.patch \
"

SRC_URI[sha256sum] = "64acfe1f18d1541e2eb63cb0d38a73c7b85272740e2b073d02ff2100305b5659"

TARGET_SOC = "t194"
COMPATIBLE_MACHINE = "(tegra194)"
PROVIDES += "virtual/bootloader"

require cboot-l4t.inc
