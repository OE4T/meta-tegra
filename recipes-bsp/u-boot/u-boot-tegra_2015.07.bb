UBOOT_BINARY ?= "u-boot-dtb.${UBOOT_SUFFIX}"

require recipes-bsp/u-boot/u-boot.inc

DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra210|tegra124)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=0507cd7da8e7ad6d6701926ec9b84c95"

PROVIDES += "u-boot"
DEPENDS += "tegra-flashtools-native"

UBOOT_TEGRA_REPO ?= "github.com/madisongh/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r24.2"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "1d3dd062ca2ade517d7feae9134031060d70dac9"
PV .= "+git${SRCPV}"

# Add the TBOOT header so it can be flashed
do_compile_append() {
    if [ -n "${UBOOT_CONFIG}" ]; then
       unset i
       for config in ${UBOOT_MACHINE}; do
           uboot_entry=`elf-get-entry.py "${B}/${config}/u-boot"`
           i=`expr $i + 1`
           unset j
           for type in ${UBOOT_CONFIG}; do
               j=`expr $j + 1`
               if [ $j -eq $i ]; then
                   mv "${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}" "${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}.tmp"
                   gen-tboot-img.py "${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}.tmp" ${uboot_entry} "${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}"
                   rm "${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}.tmp"
               fi
           done
           unset j
       done
       unset i
    else
        uboot_entry=`elf-get-entry.py "${B}/u-boot"`
        mv "${B}/${UBOOT_BINARY}" "${B}/${UBOOT_BINARY}.tmp"
        gen-tboot-img.py "${B}/${UBOOT_BINARY}.tmp" ${uboot_entry} "${B}/${UBOOT_BINARY}"
        rm "${B}/${UBOOT_BINARY}.tmp"
    fi
}
