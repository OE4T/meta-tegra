UBOOT_BINARY ?= "u-boot-dtb.${UBOOT_SUFFIX}"

require recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra186 platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

PROVIDES += "u-boot"
DEPENDS += "dtc-native"
DEPENDS_append_tegra186 = " ${SOC_FAMILY}-flashtools-native"

UBOOT_TEGRA_REPO ?= "github.com/madisongh/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r28.1"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "0ce7ca286491e97a34d741bd92a57f2dcdc3033c"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

do_compile_append_tegra186() {
    rm -f ${B}/initrd
    touch ${B}/initrd
    if [ -n "${UBOOT_CONFIG}" ]; then
        unset i j k
	for config in ${UBOOT_MACHINE}; do
	    i=$(expr $i + 1)
	    for type in ${UBOOT_CONFIG}; do
	        j=$(expr $j + 1)
	        if [ $j -eq $i ]; then
	            for binary in ${UBOOT_BINARIES}; do
		        k=$(expr $k + 1)
		        if [ $k -eq $i ]; then
		            f="${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}"
		            rm -f $f
			    ${STAGING_BINDIR_NATIVE}/tegra186-flash/mkbootimg \
			        --kernel ${B}/${config}/${binary} --ramdisk ${B}/initrd --cmdline "" \
			        --board "/dev/mmcblk0p1" --output $f
			fi
		    done
		    unset k
		fi
	    done
	    unset j
	done
	unset i
    else
        mv ${UBOOT_BINARY} ${UBOOT_BINARY}.orig
	${STAGING_BINDIR_NATIVE}/tegra186-flash/mkbootimg \
	    --kernel ${UBOOT_BINARY}.orig --ramdisk ${B}/initrd --cmdline "" \
	    --board "/dev/mmbclk0p12" --output ${UBOOT_BINARY}
    fi
}
