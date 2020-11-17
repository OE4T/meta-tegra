DESCRIPTION = "cboot bootloader for Tegra194"

SRC_URI = "${L4T_URI_BASE}/cboot_src_t19x.tbz2;downloadfilename=cboot_src_t19x-${PV}.tbz2;subdir=${BP} \
           file://0001-Convert-Python-scripts-to-Python3.patch \
"

SRC_URI[sha256sum] = "2e053ef1f0931ad670c3f5ae75aba60ce7cbde8436d1fd4c2be3cdd2b60f1b88"

TARGET_SOC = "t194"
COMPATIBLE_MACHINE = "(tegra194)"
PROVIDES += "virtual/bootloader"

require cboot-l4t.inc
