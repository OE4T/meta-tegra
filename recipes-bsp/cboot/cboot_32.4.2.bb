DESCRIPTION = "cboot bootloader for Tegra194"
  
LICENSE = "MIT & BSD-2-Clause & BSD-3-Clause & Apache-2.0 & Zlib & Proprietary"
LIC_FILES_CHKSUM = " \
    file://bootloader/partner/t18x/cboot/target/init.c;endline=22;md5=59dc0a752bc93e442d4ec3de8b1d8614 \
    file://bootloader/partner/t18x/cboot/kernel/semaphore.c;endline=15;md5=ff935df987c4efa30f6c5462f708f1a4 \
    file://bootloader/partner/common/lib/external/mbedtls/bignum.c;endline=35;md5=84d5feb35adf77539b8a50b9497ea92a \
    file://bootloader/partner/common/lib/external/asn1/asn1_decoder.c;endline=23;md5=3f072eb94ef35fa574edc8b2dd5931c1 \
    file://bootloader/partner/common/lib/external/lz4/lz4.c;endline=33;md5=374dbaf7e07d3845e06cbab8589578e9 \
    file://bootloader/partner/common/lib/external/mincrypt/sha.c;endline=34;md5=4a0fce84ffc9e3901a9fb2e2a290e7b8 \
    file://bootloader/partner/common/lib/external/zlib/zlib.h;beginline=6;endline=23;md5=5377232268e952e9ef63bc555f7aa6c0 \
    file://LICENSE.cboot.txt;md5=972762a86d83ebd2df0b07be8d084935 \
"

inherit l4t_bsp

L4T_BSP_NAME = "${L4T_SRCS_NAME}"

SRC_URI = "\
    ${L4T_URI_BASE}/cboot_src_t19x.tbz2;downloadfilename=cboot_src_t19x-${PV}.tbz2;subdir=${BP} \
    file://0001-Convert-Python-scripts-to-Python3.patch \
"
SRC_URI[sha256sum] = "851a2c3b3dcfc7ed718050d55ce5941b2385e81bdc6d2d4cb6f324606dd5efe2"

require cboot-l4t.inc

COMPATIBLE_MACHINE = "(tegra194)"

S = "${WORKDIR}/${BP}"
