DESCRIPTION = "Boot partition layout configuration file for tegra210 targets"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "tegra-bootfiles tegra-helper-scripts-native"

PATH =. "${STAGING_BINDIR_NATIVE}/tegra210-flash:"

BOOTDEVNAME = "${@'spi' if d.getVar('TEGRA_SPIFLASH_BOOT') == '1' else 'sdmmc_boot'}"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

do_compile() {
    :
}

do_compile_tegra210() {
    nvflashxmlparse -t ${BOOTDEVNAME} ${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_TEMPLATE} > ${B}/layout.tmp
    sed -e 's,NXC,NVC,g' -e's,TXC,TBC,g' -e's,WX0,WB0,g' -e 's,BXF,BPF,g' ${B}/layout.tmp > ${B}/layout.txt
    cursize=0
    rm -f ${B}/layout.conf
    touch ${B}/layout.conf
    while read line; do
        eval "$line"
	if [ -n "$start_location" ]; then
            if [ $cursize -gt $start_location ]; then
                bberror "Partition $partname start location ($start_location) is less than current offset ($cursize)"
            fi
            cursize=$start_location
        fi
        partbytes=$(expr $partsize \* $blksize)
        echo "$partname:$cursize:$partbytes" >> ${B}/layout.conf
        cursize=$(expr $cursize \+ $partbytes)
    done < ${B}/layout.txt
}

do_install() {
    :
}
do_install_tegra210() {
    install -d ${D}${datadir}/tegra-boot-tools
    install -m 0644 ${B}/layout.conf ${D}${datadir}/tegra-boot-tools/boot-partitions.conf
}

FILES_${PN} = "${datadir}/tegra-boot-tools"
ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}_tegra210 = "0"
PACKAGE_ARCH = "${MACHINE_ARCH}"
