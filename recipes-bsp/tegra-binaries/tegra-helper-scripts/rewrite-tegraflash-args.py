#!/usr/bin/env python3

import os
import sys
import shlex
import argparse
import getopt
import logging

def main():
    # see main program in tegraflash.py
    tf_options = ["skipuid", "bct=", "bct_cold_boot=", "cfg=", "bl=", "hostbin=", "cmd=", "key=", "encrypt_key=","instance=",
                  "out=", "chip=", "dtb=", "bldtb=", "kerneldtb=", "bpfdtb=", "nct=", "applet=", "fb=", "odmdata=", "overlay_dtb=",
                  "lnx=", "tos=", "eks=", "boardconfig=", "securedev", "keyindex=", "wb=", "keep", "secureboot",
                  "bl-load=", "bins=", "dev_params=", "sdram_config=", "ramcode=", "misc_config=", "misc_cold_boot_config=",
                  "mb1_bct=", "mb2_bct=", "mb2_cold_boot_bct=", "mb2bct_cfg=", "ecid=",
                  "pinmux_config=", "scr_config=", "scr_cold_boot_config=",
                  "pmc_config=", "pmic_config=", "gpioint_config=", "uphy_config=", "br_cmd_config=",
                  "prod_config=", "device_config=", "applet-cpu=", "bpf=", "skipsanitize",
                  "encrypt_key=", "enable_user_kdk", "nv_key=", "nvencrypt_key=", "cl=", "soft_fuses=", "cust_info=", "fuse_info=",
                  "deviceprod_config=", "rcm_bct=","mem_bct=", "mem_bct_cold_boot=", "mb1_cold_boot_bct=", "wb0sdram_config=",
                  "minratchet_config=", "blversion=", "output_dir=", "nv_nvratchet=", "nv_oemratchet=", "image_dirs=",
                  "trim_bpmp_dtb", "cpubl=", "cpubl_rcm=", "concat_cpubl_bldtb", "external_device", "sparseupdate", "ratchet_blob=",
                  "applet_softfuse=", "boot_chain=", "bct_backup",
                  "mb1_bin=", "psc_bl1_bin=", "rcmboot_pt_layout=", "coldboot_pt_layout=", "rcmboot_bct_cfg=", "coldboot_bct_cfg=",
                  "duk=", "dce_base_dtb=", "dce_overlay_dtb=", "dry_run", "enable_mods", "X", "disable_random_iv", "no_flash",
                  "compress="]
    parser = argparse.ArgumentParser(
        description="""
Extracts/manipulates partition information in an NVIDIA flash layout XML file
""")
    parser.add_argument('--bins', help='add or rewrite --bins entries', action='store')
    parser.add_argument('--cmd', help='rewrite --cmd', action='store')
    parser.add_argument('--add', help='option(s) to add', action='store')
    parser.add_argument('--remove', help='option(s) to remove', action='store')
    parser.add_argument('-o', '--output', help='file to write output to', action='store')
    parser.add_argument('-v', '--verbose', help='verbose logging', action='store_true')
    parser.add_argument('filename', help='file containing tegraflash.py command', action='store')

    args = parser.parse_args()
    logging.basicConfig(format='%(message)s', level=logging.INFO if args.verbose else logging.WARNING)
    with open(args.filename, "r") as f:
        orig_cmd = shlex.split(f.readline().rstrip())
    tf_opts, tf_args = getopt.getopt(orig_cmd[1:], 'h', tf_options)

    if args.remove:
        to_remove = set(args.remove.split(','))
        logging.info("Removing: {}".format(', '.join(to_remove)))
        new_opts = [opt_tuple for opt_tuple in tf_opts if opt_tuple[0] not in to_remove]
        tf_opts = new_opts

    if args.add:
        for newopt in args.add.split(','):
            newpair = newopt.split('=')
            if len(newpair) == 2:
                tf_opts.append(newpair)
            elif len(newpair) == 1:
                tf_opts.append((newpair[0], ''))
            else:
                raise RuntimeError("Invalid add argument: {}".format(newopt))
    if args.cmd:
        cmd_index = None
        for i, opt_tuple in enumerate(tf_opts):
            if opt_tuple[0] == "--cmd":
                cmd_index = i
                break
        if cmd_index:
            logging.info('Replacing cmd "{}" with "{}"'.format(tf_opts[cmd_index][1], args.cmd))
            tf_opts[cmd_index] = ("--cmd", args.cmd)
        else:
            logging.info('Adding cmd "{}"'.format(args.cmd))
            tf_opts.append(("--cmd", args.cmd))
    if args.bins:
        newbins = [binpair.split('=') for binpair in args.bins.split(',')]
        if newbins:
            bins_index = None
            for i, opt_tuple in enumerate(tf_opts):
                if opt_tuple[0] == "--bins":
                    bins_index = i
                    break
            if bins_index:
                binlist = [tuple(binpair.split()) for binpair in tf_opts[bins_index][1].split(';')]
            else:
                binlist = []
            changed = False
            for newpair in newbins:
                binpair_index = None
                for i, binpair in enumerate(binlist):
                    if binpair[0] == newpair[0]:
                        binpair_index = i
                        break
                if binpair_index:
                    if not newpair[1]:
                        logging.info("Removing bin {}".format(binlist[binpair_index][0]))
                        del binlist[binpair_index]
                        changed = True
                    else:
                        logging.info("Replacing {} with {} for bin {}".format(binlist[binpair_index][1], newpair[1], binlist[binpair_index][0]))
                        binlist[binpair_index] = (binlist[binpair_index][0], newpair[1])
                        changed = True
                else:
                    logging.info("Adding bin {} {}".format(newpair[0], newpair[1]))
                    binlist.append(newpair)
                    changed = True
            if changed:
                binlist_str = "; ".join(["{} {}".format(binpair[0], binpair[1]) for binpair in binlist])
                if bins_index:
                    tf_opts[bins_index] = (tf_opts[bins_index][0], binlist_str)
                else:
                    tf_opts.append(("--bins", binlist_str))
    if args.output:
        outf = open(args.output, "w")
    else:
        outf = sys.stdout

    outcmd = [shlex.quote(orig_cmd[0])]
    for opt_tuple in tf_opts:
        if opt_tuple[0][2:] in tf_options:
            outcmd.append(shlex.quote(opt_tuple[0]))
        elif "{}=".format(opt_tuple[0][2:]) in tf_options:
            outcmd.append(shlex.quote(opt_tuple[0]))
            outcmd.append(shlex.quote(opt_tuple[1]))
        else:
            raise RuntimeError("Unknown option: {}".format(opt_tuple[0]))
    print("{}".format(' '.join(outcmd)), file=outf)
    if outf != sys.stdout:
        outf.close()

if __name__ == '__main__':
    try:
        ret = main()
    except Exception:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
