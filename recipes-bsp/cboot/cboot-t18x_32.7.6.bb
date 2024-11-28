DESCRIPTION = "cboot bootloader for Tegra186"

SRC_URI = "${L4T_URI_BASE}/cboot_src_t18x.tbz2;downloadfilename=cboot_src_t18x-${PV}.tbz2;subdir=${BP} \
    file://0001-Drop-mistaken-global-variable-definition-in-sdmmc_de.patch \
    file://0002-Convert-Python-scripts-to-Python3.patch \
    file://0003-macros.mk-fix-GNU-make-4.3-compatibility.patch \
    file://0004-Restore-version-number-to-L4T-builds.patch \
    file://0005-Fix-spurious-console-none-warning.patch \
    file://0006-Add-bootinfo-module-definition-to-tegrabl_error.patch \
    file://0007-Add-bootinfo-module.patch \
    file://0008-t186-l4t.mk-make-some-build-options-configurable.patch \
    file://0009-t186-add-bootinfo-to-build.patch \
    file://0010-Add-machine-ID-to-kernel-command-line.patch \
    file://0011-bmp-support-A-B-slots.patch \
    file://0012-Fix-ext4-sparse-file-handling.patch \
    file://0013-Fix-ext4-multi-block-linear-directory-traversal.patch \
    file://0014-ext2-fix-symlink-support-in-ext2_dir_lookup.patch \
"
SRC_URI[sha256sum] = "126ad9539c6108097782cbac346394d01bf14175c4258c3f17e8fb68a1476d3c"

PACKAGECONFIG ??= "display recovery"
PACKAGECONFIG[display] = "CONFIG_ENABLE_DISPLAY=1,,"
PACKAGECONFIG[recovery] = "CONFIG_ENABLE_L4T_RECOVERY=1,,"
PACKAGECONFIG[machine-id] = "CONFIG_ENABLE_MACHINE_ID=1,,"

TARGET_SOC = "t186"
COMPATIBLE_MACHINE = "(tegra186)"
PROVIDES:append = "${@' virtual/bootloader' if (d.getVar('PREFERRED_PROVIDER_virtual/bootloader') or '').startswith('cboot') else ''}"

require cboot-l4t.inc
