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
    file://bootloader/partner/common/drivers/display/tegrabl_display.c;endline=9;md5=c8ca1ecaf97ac64ea801dd20a81d463a \
"

SRC_URI = "${NVIDIA_DEVNET_MIRROR}/L4T-${PV}/cboot_src_t194.tbz2;subdir=${BP};downloadfilename=${BP}.tbz2"
SRC_URI[md5sum] = "f1e23010e2ed635ae1f26f1aea08e76e"
SRC_URI[sha256sum] = "b895489382b16032afc1e2c79cb0a38440e111378cf91ccf42c8df013f6b7390"

inherit nvidia_devnet_downloads

require cboot-l4t.inc
