DESCRIPTION = "Boot partition layout configuration file for tegra210 targets"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "tegra-bootfiles tegra-helper-scripts-native"

FLASHTOOLS_PATH = "${STAGING_BINDIR_NATIVE}/${SOC_FAMILY}-flash"
FLASHTOOLS_PATH:tegra194 = "${STAGING_BINDIR_NATIVE}/tegra186-flash"
PATH =. "${FLASHTOOLS_PATH}:"

BOOTDEVNAME = "${@'spi' if d.getVar('TEGRA_SPIFLASH_BOOT') == '1' else 'sdmmc_boot'}"
BOOTDEVNAME:xavier-nx = "spi"
USERDEVNAME = "${@'sdcard' if d.getVar('TEGRA_SPIFLASH_BOOT') == '1' else 'sdmmc_user'}"

inherit image_types_tegra

S = "${WORKDIR}"
B = "${WORKDIR}/build"

do_configure() {
    rm -f ${B}/flash.xml.in
    tegraflash_create_flash_config ${B} "dummy"
}

do_compile() {
    rm -f ${B}/layout.tmp
    nvflashxmlparse -t ${BOOTDEVNAME} ${B}/flash.xml.in > ${B}/layout.tmp
    cursize=0
    rm -f ${B}/layout.conf
    touch ${B}/layout.conf
    ALLPARTS=""
    while read line; do
        eval "$line"
	if [ -n "$start_location" ]; then
	    startbytes=$(expr $start_location \* $blksize || true)
            if [ $cursize -gt $startbytes ]; then
                bberror "Partition $partname start location ($startbytes) is less than current offset ($cursize)"
            fi
            cursize=$startbytes
        fi
        partbytes=$(expr $partsize \* $blksize)
        echo "$partname:$cursize:$partbytes" >> ${B}/layout.conf
        cursize=$(expr $cursize \+ $partbytes)
        [ -z "$ALLPARTS" ] || ALLPARTS="${ALLPARTS},"
        ALLPARTS="${ALLPARTS}$partname"
    done < ${B}/layout.tmp
    rm -f ${B}/userlayout.tmp
    nvflashxmlparse -t ${USERDEVNAME} ${B}/flash.xml.in > ${B}/userlayout.tmp
    while read line; do
        eval "$line"
        [ -z "$ALLPARTS" ] || ALLPARTS="${ALLPARTS},"
        ALLPARTS="${ALLPARTS}$partname"
    done < ${B}/userlayout.tmp
    echo "$ALLPARTS" > ${B}/all-partitions.conf
}

do_install() {
    install -d ${D}${datadir}/tegra-boot-tools
    install -m 0644 ${B}/layout.conf ${D}${datadir}/tegra-boot-tools/boot-partitions.conf
    install -m 0644 ${B}/all-partitions.conf ${D}${datadir}/tegra-boot-tools/
}

FILES:${PN} = "${datadir}/tegra-boot-tools"
PACKAGE_ARCH = "${MACHINE_ARCH}"
