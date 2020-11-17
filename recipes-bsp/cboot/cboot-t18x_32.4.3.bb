DESCRIPTION = "cboot bootloader for Tegra186"

SRC_URI = "${L4T_URI_BASE}/cboot_src_t18x.tbz2;downloadfilename=cboot_src_t18x-${PV}.tbz2;subdir=${BP} \
           file://0001-Convert-Python-scripts-to-Python3.patch \
"
SRC_URI[sha256sum] = "8391b7c5c7d43d5e3af47aa486dbb9b108333a2914bbded24bedaa1cb070408c"

TARGET_SOC = "t186"
COMPATIBLE_MACHINE = "(tegra186)"
PROVIDES_append = "${@' virtual/bootloader' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"

require cboot-l4t.inc
