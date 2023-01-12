#!/usr/bin/env python3

import os
import sys
import shlex
import argparse
import logging
import string

def recode_lot0(reg):
    """
    Converts the 5-digit (6 bits per digit) base-36 lot code to binary.
    """
    reg <<= 2;
    lot = 0
    for i in range(0, 5):
        digit = (reg & 0xfc000000) >> 26
        lot *= 36
        lot += digit
        reg <<= 6
    return lot

def br_cid_to_linux_uid(brcid):
    """
    Converts the chip ID reported by the boot ROM into the chip ID
    exported by the fuse driver via sysfs on Linux (NVIDIA downstream).
    On 4.9 kernel:  /sys/module/tegra_fuse/parameters/tegra_chip_uid
    On 5.10 kernel: /sys/devices/platform/efuse-burn/ecid
    """
    if brcid.startswith("0x"):
        brcid = brcid[2:]
    if len(brcid) != 32 or not all(c in string.hexdigits for c in brcid):
        raise ValueError("BR_CID must be 32 hex digits")
    brcid_chip = brcid[1:3]
    brcid_chip2 = brcid[4:6]
    # See Linux driver for this mapping
    if brcid_chip == "21":
        linux_chip = 5
    elif brcid_chip == "18":
        linux_chip = 6
    elif brcid_chip == "80" and brcid_chip2 == "19":
        linux_chip = 7
    elif brcid_chip2 == "23":
        linux_chip = 8
    else:
        raise RuntimeError("Unrecognized chip ID 0x{}".format(brcid_chip))
    serialnum = [0, 0, 0, 0]
    serialnum[3] = int(brcid[0:8], 16) & 0xF
    serialnum[2] = int(brcid[8:16], 16)
    serialnum[1] = int(brcid[16:24], 16)
    serialnum[0] = int(brcid[24:32], 16)

    fuse_y = (serialnum[0] >> 6) & 0x1ff
    fuse_x = (serialnum[0] >> 15) & 0x1ff
    fuse_wafer = (serialnum[0] >> 24) & 0x3f
    fuse_lot1 = serialnum[1] & 0x3ffffff
    fuse_lot1 <<= 2
    fuse_lot1 |= (serialnum[0] >> 30) & 0x3
    fuse_lot0 = serialnum[2] & 0x3ffffff
    fuse_lot0 <<= 6
    fuse_lot0 |= (serialnum[1] >> 26) & 0x3f
    fuse_fab = (serialnum[2] >> 26) & 0x3f
    fuse_vendor = serialnum[3] & 0xf

    logging.info("Vendor: 0x%x" % fuse_vendor)
    logging.info("Fab:    0x%x" % fuse_fab)
    logging.info("Lot0:   0x%x" % fuse_lot0)
    logging.info("Lot1:   0x%x" % fuse_lot1)
    logging.info("Wafer:  0x%x" % fuse_wafer)
    logging.info("X:      0x%x" % fuse_x)
    logging.info("Y:      0x%x" % fuse_y)

    # N.B. The Linux driver (in both 4.9 and 5.10) has a
    # bug that reuses the fuse_y value instead of the
    # fuse_wafer value at bit offset 18 here (a typo in the
    # FUSE_OPT_WAFER_ID definition using register 0x118
    # instead of the correct 0x110), which is why there
    # are two fuse_y references below.
    #
    # Also note that the Linux driver only uses the lot0
    # field, and ignores lot1.
    linux_uid = (
        (linux_chip << 60) |
        ((fuse_vendor & 0xf) << 56) |
        ((fuse_fab & 0x3f) << 50) |
        ((recode_lot0(fuse_lot0) & 0x3ffffff) << 24) |
        ((fuse_y & 0x3f) << 18) |
        ((fuse_x & 0x1ff) << 9) |
        (fuse_y & 0x1ff))

    return linux_uid


def main():
    parser = argparse.ArgumentParser(
        description="""
Converts the BR_CID reported by a Jetson module into the corresponding Linux tegra_chip_uid
""")
    parser.add_argument('--decimal', help='output decimal instead of hex value', action='store_true')
    parser.add_argument('-o', '--output', help='file to write output to', action='store')
    parser.add_argument('-v', '--verbose', help='verbose logging', action='store_true')
    parser.add_argument('brcid', help='BR_CID reported by boot ROM', action='store')

    args = parser.parse_args()
    logging.basicConfig(format='%(message)s', level=logging.INFO if args.verbose else logging.WARNING)

    uid = br_cid_to_linux_uid(args.brcid)
    if args.output:
        outf = open(args.output, "w")
    else:
        outf = sys.stdout

    if args.decimal:
        print("{}".format(uid), file=outf)
    else:
        print("{:x}".format(uid), file=outf)

    if outf != sys.stdout:
        outf.close()
    return 0

if __name__ == '__main__':
    try:
        ret = main()
    except Exception:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
