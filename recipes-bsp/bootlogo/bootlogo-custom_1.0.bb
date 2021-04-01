DESCRIPTION = "Deploy a custom boot screen for L4T bootloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "tegra186-flashtools-native lz4-native"

inherit deploy nopackages

PROVIDES += "virtual/bootlogo"

SRC_URI = "git://github.com/OE4T/bootlogo-oe4t;protocol=https"
SRCREV = "a8bb17ae9932645c5f562f8922e82565b964a7c4"

S = "${WORKDIR}/git"

# One of 'raw' or 'cooked'
PACKAGECONFIG ?= "cooked"

PACKAGECONFIG[raw] = ",,imagemagick-native,,,cooked"
PACKAGECONFIG[cooked] = ",,,,,raw"

# Input input graphic to be scaled
RAW_GRAPHIC_FILE ?= "input.png"

# Config file definting pregenerated bitmap files
COOKED_CONFIG_FILE ?= "config_file"

BMP_BLOB = "bootlogo-${MACHINE}-${PV}-${PR}.blob"
BMP_SYMLINK = "bootlogo-${MACHINE}.blob"

include bootlogo.inc

do_compile() {

    LIST=""

    if [ "${@bb.utils.contains("PACKAGECONFIG", "raw", "1", "0", d)}" = "1" ]; then
        if [ ! -s "${S}/${RAW_GRAPHIC_FILE}" ]; then
            bbfatal "RAW_GRAPHIC_FILE must exist"
        fi

        cook_bitmaps "${S}/${RAW_GRAPHIC_FILE}"
    else
        if [ ! -s "${S}/${COOKED_CONFIG_FILE}" ]; then
            bbfatal "COOKED_CONFIG_FILE must exist"
        fi

        while read _line
        do
            _line="${S}/${_line}"
            LIST="${LIST}${_line};"
        done < ${S}/${COOKED_CONFIG_FILE}
    fi

    # NB that '%?' gets rid of the trailing ';' because BMP_generator blows up
    LIST="${LIST%?}"

    rm -f ${B}/bmp.blob
    rm -f ${B}/bmp-compressed.blob

    OUT=${B} ${STAGING_BINDIR_NATIVE}/tegra186-flash/BMP_generator_L4T.py -t bmp -e "${LIST}" -v 0

    if [ ! -s ${B}/bmp.blob ]; then
        bbfatal "BMP_generator_L4T.py failed to create bmp.blob"
    fi

    compress_blob "${B}/bmp.blob" "${B}/bmp-compressed.blob"

    if [ ! -s ${B}/bmp-compressed.blob ]; then
        bbfatal "failed to create compressed bmp-compressed.blob"
    fi
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/bmp-compressed.blob ${DEPLOYDIR}/${BMP_BLOB}
    ln -sf ${BMP_BLOB} ${DEPLOYDIR}/${BMP_SYMLINK}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
do_install[noexec] = "1"

addtask deploy before do_build after do_install
