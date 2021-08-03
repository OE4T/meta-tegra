DESCRIPTION = "Deploy a custom boot screen for L4T bootloader"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ceb3372c9595f0a8067e55da801e4a1"

COMPATIBLE_MACHINE = "(tegra)"

SOC_FAMILY ??= "tegra186"
DEPENDS = "${SOC_FAMILY}-flashtools-native lz4-native"

inherit deploy nopackages

PROVIDES += "virtual/bootlogo"

SRC_URI = "https://github.com/OE4T/bootlogo-oe4t/releases/download/v${PV}/bootlogo-oe4t-${PV}.tar.gz"
SRC_URI[sha256sum] = "f53ded9fc40c4146ddaf3aa6118cde0c7ed57452795e80fb9df436c13a0248da"

S = "${WORKDIR}/bootlogo-oe4t-${PV}"

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

FLASHTOOLS_DIR = "${SOC_FAMILY}-flash"
FLASHTOOLS_DIR:tegra194 = "tegra186-flash"

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

    OUT=${B} ${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}/BMP_generator_L4T.py -t bmp -e "${LIST}" -v 0

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
