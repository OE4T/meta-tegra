#!/bin/bash
# -*- mode: shell-script; indent-tabs-mode: nil; sh-basic-offset: 4; -*-
bup_blob=0
bup_type=
rcm_boot=0
keyfile=
sbk_keyfile=
spi_only=
external_device=0
sdcard=
no_flash=0
to_sign=0
flash_cmd=
imgfile=
dataimg=
inst_args=""
extdevargs=
sparseargs=
erase_spi=
hsm_arg=
blocksize=4096

# These functions are used in odmsign.func but do not
# need to do anything when run from this script, as we
# have already copied needed files to the current working
# directory.
partition_exists_in_PT_table() {
    local partname="$1"
    local layoutfile="$2"
    "$here/nvflashxmlparse" --get-filename "$partname" "$layoutfile" >/dev/null 2>&1
}

get_value_from_PT_table() {
    local partname="$1"
    local field="$2"
    local layoutfile="$3"
    local varname="$4"
    if [ "$field" != "filename" ]; then
        echo "ERR: unsupported flash layout field: $field" >&2
        return 1
    fi
    local value=$("$here/nvflashxmlparse" --get-filename "$partname" "$layoutfile" 2>/dev/null)
    eval "$varname=\"$value\""
}

ARGS=$(getopt -n $(basename "$0") -l "bup,bup-type:,hsm,no-flash,sign,sdcard,spi-only,boot-only,external-device,rcm-boot,datafile:,usb-instance:,uefi-enc:,erase-spi" -o "u:v:s:b:B:yc:" -- "$@")
if [ $? -ne 0 ]; then
    echo "Error parsing options" >&2
    exit 1
fi
eval set -- "$ARGS"
unset ARGS

while true; do
    case "$1" in
    --bup)
        bup_blob=1
        no_flash=1
        shift
        ;;
    --bup-type)
        bup_type="$2"
        shift 2
        ;;
    --hsm)
        hsm_arg=" --hsm"
        shift
        ;;
    --no-flash)
        no_flash=1
        shift
        ;;
    --sign)
        to_sign=1
        shift
        ;;
    --sdcard)
        sdcard=yes
        shift
        ;;
    --spi-only|--boot-only)
        spi_only=yes
        shift
        ;;
    --rcm-boot)
        rcm_boot=1
        shift
        ;;
    --erase-spi)
        erase_spi=yes
        shift
        ;;
    --external-device)
        external_device=1
        extdevargs="--external_device"
        shift
        ;;
    --datafile)
        dataimg="$2"
        shift 2
        ;;
    --usb-instance)
        usb_instance="$2"
        inst_args="--instance ${usb_instance}"
        shift 2
        ;;
    -u)
        keyfile="$2"
        shift 2
        ;;
    -v)
        sbk_keyfile="$2"
        shift 2
        ;;
    -s)
        make_sdcard_args="$make_sdcard_args -s $2"
        shift 2
        ;;
    -b)
        make_sdcard_args="$make_sdcard_args -b $2"
        shift 2
        ;;
    -B)
        blocksize="$2"
        shift 2
        ;;
    -y)
        make_sdcard_args="$make_sdcard_args -y"
        shift
        ;;
    -c)
        flash_cmd="$2"
        shift 2
        ;;
    --)
        shift
        break
        ;;
    *)
        echo "Error processing options" >&2
        exit 1
        ;;
    esac
done

flash_in="$1"
dtb_file="$2"
sdramcfg_files="$3"
odmdata="$4"
kernfile="$5"
imgfile="$6"
shift 6

here=$(readlink -f $(dirname "$0"))
flashappname="tegraflash.py"
custinfo_out="custinfo_out.bin"

# tegraflash.py depends on the dtc command
if ! command -v dtc >/dev/null 2>&1; then
    echo "ERR: 'dtc' command not found. Please install the 'device-tree-compiler' package." >&2
    exit 1
fi

if [ ! -e ./flashvars ]; then
    echo "ERR: missing flash variables file" >&2
    exit 1
fi

. ./flashvars

if [ -z "$CHIPID" ]; then
    echo "ERR: CHIPID variable not set" >&2
    exit 1
fi

[ -n "$RCMBOOT_KERNEL" ] || RCMBOOT_KERNEL="initrd-flash.img"

if [ $external_device -eq 0 -a "$CHPID" != "0x26" ]; then
    also_sign_rcmboot=1
else
    also_sign_rcmboot=0
fi

rcm_bootcontrol_overlay="L4TConfiguration-rcmboot.dtbo"

non_bootcontrol_overlays="$PLUGIN_MANAGER_OVERLAYS"
if [ -z "$non_bootcontrol_overlays" ]; then
    non_bootcontrol_overlays="$OVERLAY_DTB_FILE"
elif [ -n "$OVERLAY_DTB_FILE" ]; then
    non_bootcontrol_overlays="$non_bootcontrol_overlays,$OVERLAY_DTB_FILE"
fi
[ -z "$non_bootcontrol_overlays" ] || non_bootcontrol_overlays=",$non_bootcontrol_overlays"

if [ $rcm_boot -ne 0 -a $to_sign -eq 0 ]; then
    overlay_dtb_files="$rcm_bootcontrol_overlay$non_bootcontrol_overlays"
    also_sign_rcmboot=0
else
    overlay_dtb_files="$BOOTCONTROL_OVERLAYS$non_bootcontrol_overlays"
fi

overlay_dtb_arg=
rcm_overlay_dtb_arg=
if [ -n "$overlay_dtb_files" ]; then
    overlay_dtb_arg="--overlay_dtb $overlay_dtb_files"
    rcm_overlay_dtb_arg="--overlay_dtb $rcm_bootcontrol_overlay$non_bootcontrol_overlays"
fi
if [ -n "$DCE_OVERLAY" ]; then
    overlay_dtb_arg="$overlay_dtb_arg --dce_overlay_dtb $DCE_OVERLAY"
    rcm_overlay_dtb_arg="$rcm_overlay_dtb_arg --dce_overlay_dtb $DCE_OVERLAY"
fi

fuselevel="fuselevel_production"

# Temp file for storing cvm.bin in, if we need to query the board for its
# attributes
cvm_bin=$(mktemp cvm.bin.XXXXX)

skipuid=""
bootauth=""
BR_CID=
if [ -z "$CHIPREV" ]; then
    chipidargs="--new_session --chip $CHIPID"
    BR_CID=$($here/tegrarcm_v2 ${chipidargs} ${inst_args} --uid | grep BR_CID | cut -d' ' -f2)
    chipid="$BR_CID"
    if [ -z "$chipid" -a "$chipd" != "0x00000" ]; then
        echo "ERR: could not retrieve chip ID" >&2
        exit 1
    fi
    if [ "$CHIPID" != "0x${chipid:6:2}" ]; then
        echo "ERR: CHIPID ($CHIPID) does not match actual chip ID (0x${chipid:6:2})" >&2
        exit 1
    fi
    if [ "${chipid:6:2}" = "26" ]; then
        flval="0x${chipid:2:1}"
        flval=$(printf "%x" "$((flval & 0xe))")
        tmp_1="0x${chipid:3:2}"
        tmp_1=$(printf "%2.2x" "$((tmp_1 & 0xf0))")
        flval="${flval}${tmp_1}"
        case "${flval}" in
            000)
                # The public L4T kit includes only production binaries
                echo "ERR: non-production chip found" >&2
                exit 1
                ;;
            800)
                bootauth="NS"
                ;;
            e00)
                bootauth="PKC"
                ;;
            e80)
                bootauth="SBKPKC"
                ;;
            *)
                echo "ERR: unrecognized fused configuration 0x$flval" >&2
                exit 1
        esac
        CHIPREV="${chipid:5:1}"
    elif [ "${chipid:6:2}" = "23" ]; then
        flval="0x${chipid:2:1}"
        flval=$(printf "%x" "$((flval & 0x8))")
        tmp_1="0x${chipid:3:2}"
        tmp_1=$(printf "%2.2x" "$((tmp_1 & 0xf0))")
        flval="${flval}${tmp_1}"
        case "${flval}" in
            000)
                # The public L4T kit includes only production binaries
                echo "ERR: non-production chip found" >&2
                exit 1
                ;;
            800)
                bootauth="NS"
                # non-secured
                ;;
            810|820|830|840|850)
                bootauth="PKC"
                # RSA/ECDSA P-256/ECDSA P-512/ED25519/XMSS
                ;;
            890|8a0|8b0|8c0|8d0)
                bootauth="SBKPKC"
                # SBK + RSA/ECDSA P-256/ECDSA P-512/ED25519/XMSS
                ;;
            *)
                echo "ERR: unrecognized fused configuration 0x$flval" >&2
                exit 1
        esac
        skipuid="--skipuid"
        CHIPREV="${chipid:5:1}"
    else
        echo "ERR: unrecognized chip ID: 0x${chipid:6:2}" >&2
        exit 1
    fi
    case $bootauth in
        PKC)
            if [ -z "$keyfile" ]; then
                echo "ERR: Target is configured for secure boot ($bootauth); use -u option to specify key file" >&2
                exit 1
            fi
            ;;
        SBKPKC)
            if [ -z "$keyfile" -o -z "$sbk_keyfile" ]; then
                echo "ERR: Target is configured for secure boot ($bootauth); use -u and -v options to specify key files" >&2
                exit 1
            fi
            ;;
        NS)
            if [ -n "$keyfile" -o -n "$sbk_keyfile" ]; then
                echo "WARN: Target is not secured; ignoring key files" >&2
                keyfile=
                sbk_keyfile=
            fi
            ;;
    esac
    BOOTSEC_MODE="$bootauth"
elif [ "$CHIPID" = "0x23" ]; then
    skipuid="--skipuid"
fi

if [ -n "$hsm_arg" -a -z "$keyfile" ]; then
    echo "ERR: using --hsm requires -u <keyfile> option" >&2
    exit 1
fi

have_boardinfo=
keyargs=
[ -z "$keyfile" ] || keyargs="$keyargs $hsm_arg --key $keyfile"
[ -z "$sbk_keyfile" ] || keyargs="$keyargs --encrypt_key $sbk_keyfile"
if [ -z "$FAB" -o -z "$BOARDID" ]; then
    rm -f rcm_state
    if [ -n "$EMC_FUSE_DEV_PARAMS" ]; then
        sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "$EMC_FUSE_DEV_PARAMS"
    fi
    if [ "$CHIPID" = "0x23" ]; then
        if ! python3 $flashappname ${inst_args} --chip 0x23 $skipuid $keyargs \
             --applet mb1_t234_prod.bin \
             --dev_params $EMC_FUSE_DEV_PARAMS \
             --cfg readinfo_t234_min_prod.xml \
             --device_config $DEVICE_CONFIG --misc_config $MISC_CONFIG --bins "mb2_applet applet_t234.bin" \
             --cmd "readfuses fuse_t234.bin fuse_t234.xml; dump eeprom cvm ${cvm_bin}; dump try_custinfo ${custinfo_out}; reboot recovery"; then
            echo "ERR: could not retrieve EEPROM board information" >&2
            exit 1
        fi
    elif [ "$CHIPID" = "0x26" ]; then
        rm -f diag_bct_cfg.xml
        cp flash_l4t_t264_bct_cfg.xml diag_bct_cfg.xml
        if ! "$here/nvbct-config" diag_bct_cfg.xml \
                             brbct_cfg/bpmp_mem_cfg=$BPMP_MEM_CONFIG \
                             brbct_cfg/brcommand=$BOOTROM_CONFIG \
                             brbct_cfg/wb0sdram=$WB0SDRAM_BCT \
                             brbct_cfg/deviceprod=$DEVICEPROD_CONFIG \
                             brbct_cfg/prod=$PROD_CONFIG \
                             brbct_cfg/scr=$SCR_CONFIG \
                             brbct_cfg/mb2bctcfg=$MB2BCT_CFG \
                             brbct_cfg/uphy=$UPHY_CONFIG \
                             brbct_cfg/device=$DEVICE_CONFIG \
                             brbct_cfg/misc=$MISC_CONFIG \
                             brbct_cfg/pinmux=$PINMUX_CONFIG \
                             brbct_cfg/gpioint=$GPIOINT_CONFIG \
                             brbct_cfg/pmic=$PMIC_CONFIG \
                             brbct_cfg/pmc=$PMC_CONFIG \
                             brbct_cfg/dev_param=$EMC_FUSE_DEV_PARAMS \
                             brbct_cfg/sdram=$sdramcfg_files ; then
            echo "ERR: could not update BCT configuration" >&2
            exit 1
        fi

        if ! python3 $flashappname ${inst_args} --chip 0x26 $skipuid $keyargs \
              --applet applet_t264.bin \
              --rcmboot_bct_cfg diag_bct_cfg.xml \
              --rcmboot_pt_layout readinfo_t264_min_prod.xml \
             --cmd "readfuses fuse_t264.bin fuse_t264.xml; dump eeprom cvm ${cvm_bin}; dump try_custinfo ${custinfo_out}; reboot recovery"; then
            echo "ERR: could not retrieve EEPROM board information" >&2
            exit 1
         fi
    fi

    # The chip_info.bin_bak file is created as a side effect of the above tegraflash.py invocation
    if [ ! -e chip_info.bin_bak ]; then
        echo "ERR: chip_info.bin_bak missing after dumping boardinfo" >&2
        exit 1
    fi
    CHIP_SKU=$($here/chkbdinfo -C chip_info.bin_bak | tr -d '[:space:]')
    board_ramcode=$($here/chkbdinfo -R chip_info.bin_bak)
    if [ -z "$board_ramcode" ]; then
        echo "ERR: ramcode could not be extracted from chip info" >&2
        exit 1
    fi
    board_ramcode="$(echo "$board_ramcode" | cut -d: -f4)"
    board_ramcode=$((16#$board_ramcode % 16))
    RAMCODE="$board_ramcode"
    # XXX- these don't appear to be used
    # chip_minor_revision=$($here/chkbdinfo -M chip_info.bin_bak)
    # bootrom_revision=$($here/chkbdinfo -O chip_info.bin_bak)
    # -XXX
    skipuid=""
    have_boardinfo="yes"
fi

if [ -n "$BOARDID" ]; then
    boardid="$BOARDID"
else
    boardid=$($here/chkbdinfo -i ${cvm_bin} | tr -d '[:space:]')
    BOARDID="$boardid"
    if [ -n "$CHECK_BOARDID" -a "$BOARDID" -ne "$CHECK_BOARDID" ]; then
        echo "ERR: actual board ID $BOARDID does not match expected board ID $CHECK_BOARDID" >&2
        exit 1;
    fi
fi

if [ "$CHIPID" = "0x23" -o "$CHIPID" = "0x26" ] && [ -z "$CHIP_SKU" ]; then
    echo "ERR: no default chip SKU set" >&2
    exit 1
fi

if [ -n "$FAB" ]; then
    board_version="$FAB"
else
    board_version=$($here/chkbdinfo -f ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z])
    FAB="$board_version"
fi
if [ -n "$BOARDSKU" ]; then
    board_sku="$BOARDSKU"
elif [ -n "$have_boardinfo" ]; then
    board_sku=$($here/chkbdinfo -k ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z])
    BOARDSKU="$board_sku"
    if [ -n "$CHECK_BOARDSKU" -a "$BOARDSKU" -ne "$CHECK_BOARDSKU" ]; then
        echo "ERR: actual board SKU $BOARDSKU does not match expected board SKU $CHECK_BOARDSKU" >&2
        exit 1;
    fi
fi
if [ -n "$BOARDREV" ]; then
    board_revision="$BOARDREV"
elif [ -n "$have_boardinfo" ]; then
    board_revision=$($here/chkbdinfo -r ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z])
    BOARDREV="$board_revision"
fi
if [ -z "$serial_number" -a -n "$have_boardinfo" ]; then
    serial_number=$($here/chkbdinfo -a ${cvm_bin} | tr -d '[:space:]')
fi

[ -f ${cvm_bin} ] && rm -f ${cvm_bin}

if [ -z "$RAMCODE" -a "$BOARDID" = "3701" -a "$FAB" = "301" ]; then
    RAMCODE=0
fi

rm -f boardvars.sh
cat >boardvars.sh <<EOF
BOARDID="$BOARDID"
FAB="$FAB"
BOARDSKU="$BOARDSKU"
BOARDREV="$BOARDREV"
CHIPREV="$CHIPREV"
CHIP_SKU="$CHIP_SKU"
EOF
if [ -n "$RAMCODE" ]; then
    echo "RAMCODE=$RAMCODE" >>boardvars.sh
fi
if [ -n "$BOOTSEC_MODE" ]; then
    echo "BOOTSEC_MODE=$BOOTSEC_MODE" >>boardvars.sh
fi
if [ -n "$serial_number" ]; then
    echo "serial_number=$serial_number" >>boardvars.sh
fi
if [ -n "$usb_instance" ]; then
    echo "usb_instance=$usb_instance" >>boardvars.sh
fi
if [ -n "$BR_CID" ]; then
    echo "BR_CID=\"$BR_CID\"" >>boardvars.sh
fi
if [ -n "$CHIP_SKU" ]; then
    echo "CHIP_SKU=\"$CHIP_SKU\"" >>boardvars.sh
fi

if echo "$CHIP_SKU" | grep -q ":" 2>/dev/null; then
    chip_sku=$(echo "$CHIP_SKU" | cut -d: -f4)
else
    chip_sku=$CHIP_SKU
fi

ramcodeargs=
if [ "$CHIPID" = "0x23" ]; then
    if [ "$BOARDID" = "3701" ]; then
        case $chip_sku in
            00)
            ;;
            90)
                BPF_FILE=$(echo "$BPF_FILE" | sed -e"s,T.*-A1,TE992M-A1,")
                ;;
            97|9E)
                BPF_FILE=$(echo "$BPF_FILE" | sed -e"s,T.*-A1,TA990SA-A1,")
                ;;
            D0|D2)
                BPF_FILE=$(echo "$BPF_FILE" | sed -e"s,T.*-A1,TE990M-A1,")
                ;;
            *)
                echo "ERR: unrecognized chip SKU: $chip_sku" >&2
                exit 1
                ;;
        esac
        if [ "$BOARDSKU" = "0004" -o "$BOARDSKU" = "0005" ]; then
            PMICBOARDSKU="0005"
        elif [ "$BOARDSKU" = "0000" -a "$FAB" != "QS1" ]; then
            PMICBOARDSKU="0005"
        else
            PMICBOARDSKU="0000"
        fi
        if [ "$BOARDSKU" != "0005" ]; then
            if [ "$chip_sku" = "00" -o "$chip_sku" = "D0" ] && echo "$FAB" | egrep -q '^(TS[123]|EB[123]|[012]00)$'; then
                PINMUX_CONFIG="tegra234-mb1-bct-pinmux-p3701-0000.dtsi"
                PMC_CONFIG="tegra234-mb1-bct-padvoltage-p3701-0000.dtsi"
            fi
        fi
        if ! [ "$BOARDSKU" = "0000" -o "$BOARDSKU" = "0001" -o "$BOARDSKU" = "0002" ]; then
            BPFDTB_FILE=$(echo "$BPFDTB_FILE" | sed -e"s,3701-0000,3701-$BOARDSKU,")
            if [ "$BOARDSKU" = "0005" -o "$BOARDSKU" = "0008" ]; then
                EMC_BCT=$(echo "$EMC_BCT" | sed -e"s,3701-0000,3701-$BOARDSKU,")
                WB0SDRAM_BCT=$(echo "$WB0SDRAM_BCT" | sed -e"s,3701-0000,3701-$BOARDSKU,")
            else
                dtb_file=$(echo "$dtb_file" | sed -e"s,p3701-0000,p3701-$BOARDSKU,")
            fi
        fi
        if [ "$BOARDSKU" = "0002" -o "$BOARDSKU" = "0008" ]; then
            fsifw_binsarg="fsi_fw fsi-lk.bin;"
        else
            fsifw_binsarg=
        fi
        PMIC_CONFIG=$(echo "$PMIC_CONFIG" | sed -e"s,@PMICBOARDSKU@,$PMICBOARDSKU,")
    elif [ "$BOARDID" = "3767" ]; then
        case $chip_sku in
            00)
                ;;
            D3|D4)
                BPF_FILE=$(echo "$BPF_FILE" | sed -e"s,T.*-A1,TE980M-A1,")
                ;;
            D5|D6)
                BPF_FILE=$(echo "$BPF_FILE" | sed -e"s,T.*-A1,TE950M-A1,")
                ;;
            *)
                echo "ERR: unrecognized chip SKU: $chip_sku" >&2
                exit 1
                ;;
        esac
        PINMUXREV="a03"
        PMCREV="a03"
        PMICREV="a02"
        if [ "$BOARDSKU" = "0000" -o "$BOARDSKU" = "0002" ]; then
            if [ "$FAB" = "TS1" -o "$FAB" = "EB1" ]; then
                PINMUXREV="a01"
                BPFDTB_FILE="tegra234-bpmp-3767-0000-a00-3509-a02.dtb"
                PMCREV="a01"
                PMICREV="a00"
            fi
        fi
        if [ "$BOARDSKU" = "0001" -o "$BOARDSKU" = "0003" -o "$BOARDSKU" = "0005" ]; then
            EMC_BCT="tegra234-p3767-0001-sdram-l4t.dts"
            WB0SDRAM_BCT="tegra234-p3767-0001-wb0sdram-l4t.dts"
        elif [ "$BOARDSKU" = "0004" ]; then
            EMC_BCT="tegra234-p3767-0004-sdram-l4t.dts"
            WB0SDRAM_BCT="tegra234-p3767-0004-wb0sdram-l4t.dts"
        fi
        PINMUX_CONFIG=$(echo "$PINMUX_CONFIG" | sed -e"s,@PINMUXREV@,$PINMUXREV,")
        PMC_CONFIG=$(echo "$PMC_CONFIG" | sed -e"s,@PMCREV@,$PMCREV,")
        PMIC_CONFIG=$(echo "$PMIC_CONFIG" | sed -e"s,@PMICREV@,$PMICREV,")
    fi

    if [ -n "$RAMCODE" ]; then
        ramcodeargs="--ramcode $RAMCODE"
    fi

    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${DEV_PARAMS}";
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${DEV_PARAMS_B}";
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${EMC_FUSE_DEV_PARAMS}";
elif [ "$CHIPID" = "0x26" ]; then
    case $chip_sku in
        00)
        ;;
        A3)
            BPF_FILE=$(echo "$BPF_FILE" | sed -e"s,T.*-A1,TA1080SA-A1,")
            ;;
        A0)
            BPF_FILE=$(echo "$BPF_FILE" | sed -e"s,T.*-A1,TA1090SA-A1,")
            ;;
        E0)
            BPF_FILE=$(echo "$BPF_FILE" | sed -e"s,T.*-A1,TE1090M-A1,")
            ;;
        *)
            echo "ERR: unrecognized chip SKU: $chip_sku" >&2
            exit 1
            ;;
    esac
    if [ -n "$RAMCODE" ]; then
        ramcodeargs="--ramcode $RAMCODE"
    fi
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${DEV_PARAMS}";
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${EMC_FUSE_DEV_PARAMS}";
fi
echo "Board ID($BOARDID) version($FAB) sku($BOARDSKU) revision($BOARDREV) Chip SKU($chip_sku) ramcode($RAMCODE)"

rm -f ${MACHINE}_bootblob_ver.txt
echo "NV4" >${MACHINE}_bootblob_ver.txt
. bsp_version
echo "# R$BSP_BRANCH , REVISION: $BSP_MAJOR.$BSP_MINOR" >>${MACHINE}_bootblob_ver.txt
echo "BOARDID=$BOARDID BOARDSKU=$BOARDSKU FAB=$FAB" >>${MACHINE}_bootblob_ver.txt
date "+%Y%m%d%H%M%S" >>${MACHINE}_bootblob_ver.txt
printf "0x%x\n" $(( (BSP_BRANCH<<16) | (BSP_MAJOR<<8) | BSP_MINOR )) >>${MACHINE}_bootblob_ver.txt
bytes=$(wc -c ${MACHINE}_bootblob_ver.txt | cut -d' ' -f1)
cksum=$(python3 -c "import zlib; print(\"%X\" % (zlib.crc32(open(\"${MACHINE}_bootblob_ver.txt\", \"rb\").read()) & 0xFFFFFFFF))")
echo "BYTES:$bytes CRC32:$cksum" >>${MACHINE}_bootblob_ver.txt
if [ -z "$sdcard" -a $external_device -eq 0 ]; then
    appfile=$(basename "$imgfile").img
    if [ -n "$dataimg" ]; then
    datafile=$(basename "$dataimg").img
    fi
else
    appfile="$imgfile"
    datafile="$dataimg"
fi
appfile_sed=
if [ $bup_blob -ne 0 -o $rcm_boot -ne 0 ]; then
    kernfile="${kernfile:-boot.img}"
    appfile_sed="-e/APPFILE/d -e/DATAFILE/d"
elif [ $no_flash -eq 0 -a -z "$sdcard" -a $external_device -eq 0 ]; then
    appfile_sed="-es,APPFILE_b,$appfile, -es,APPFILE,$appfile, -es,DATAFILE,$datafile,"
elif [ $no_flash -ne 0 ]; then
    touch APPFILE APPFILE_b DATAFILE
else
    pre_sdcard_sed="-es,APPFILE_b,$appfile, -es,APPFILE,$appfile,"
    if [ -n "$datafile" ]; then
        pre_sdcard_sed="$pre_sdcard_sed -es,DATAFILE,$datafile,"
        touch DATAFILE
    fi
    touch APPFILE APPFILE_b
fi

if [ "$TBCDTB_FILE" = "@DTBFILE@" ]; then
    TBCDTB_FILE="$dtb_file"
fi

dtb_file_basename=$(basename "$dtb_file")
kernel_dtbfile="kernel_$dtb_file_basename"
rm -f "$kernel_dtbfile"
if [ -e "$dtb_file.signed" ]; then
    cp "$dtb_file.signed" "$kernel_dtbfile"
else
    cp "$dtb_file" "$kernel_dtbfile"
fi

if [ "$spi_only" = "yes" -o $external_device -eq 1 ]; then
    if [ ! -e "$here/nvflashxmlparse" ]; then
        echo "ERR: missing nvflashxmlparse script" >&2
        exit 1
    fi
fi
if [ "$spi_only" = "yes" ] || [ $bup_blob -ne 0 -a "$bup_type" = "bl" ]; then
    "$here/nvflashxmlparse" --extract -t boot -o flash.xml.tmp "$flash_in" || exit 1
else
    cp "$flash_in" flash.xml.tmp
fi
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,BPFDTB_FILE,$BPFDTB_FILE," \
    -e"s, DTB_FILE,$kernel_dtbfile," -e"s,BPFFILE,$BPF_FILE," \
    $appfile_sed flash.xml.tmp > flash.xml
rm flash.xml.tmp

custinfo_args=
if [ -f "$custinfo_out" ]; then
    custinfo_args="--cust_info $custinfo_out"
fi

binsargs_params=
if [ "$CHIPID" = "0x26" ]; then
    rm -f coldboot_bct_cfg.xml rcmboot_bct_cfg.xml
    cp flash_l4t_t264_bct_cfg.xml coldboot_bct_cfg.xml
    if ! "$here/nvbct-config" coldboot_bct_cfg.xml \
         brbct_cfg/uphy=$UPHY_CONFIG \
         brbct_cfg/device=$DEVICE_CONFIG \
         brbct_cfg/misc=$MISC_CONFIG \
         brbct_cfg/pinmux=$PINMUX_CONFIG \
         brbct_cfg/gpioint=$GPIOINT_CONFIG \
         brbct_cfg/pmic=$PMIC_CONFIG \
         brbct_cfg/pmc=$PMC_CONFIG \
         brbct_cfg/deviceprod=$DEVICEPROD_CONFIG \
         brbct_cfg/prod=$PROD_CONFIG \
         brbct_cfg/scr=$SCR_CONFIG \
         brbct_cfg/wb0sdram=$WB0SDRAM_BCT \
         brbct_cfg/brcommand=$BOOTROM_CONFIG \
         brbct_cfg/bpmp_mem_cfg=$BPMP_MEM_CONFIG \
         brbct_cfg/dev_param=$DEV_PARAMS \
         hpct_cfg/dev_param=$DEV_PARAMS \
         sbct_cfg/dev_param=$DEV_PARAMS \
         brbct_cfg/mb2bctcfg=$MB2BCT_CFG \
         brbct_cfg/sdram=$sdramcfg_files \
         hpct_cfg/sdram=$sdramcfg_files \
         sbct_cfg/sdram=$sdramcfg_files \
         hpct_cfg/bl=hpse_bl1_t264_prod.bin \
         hpct_cfg/fw=hpsefw_t264_prod.bin \
         hpct_cfg/raw_bin=hpseraw_t264_prod.bin \
         sbct_cfg/bl=sb_bl1_t264_prod.bin \
         sbct_cfg/fw=sbfw_t264_prod.bin \
         sbct_cfg/raw_bin=sbraw_t264_prod.bin; then
        echo "ERR: could not update BCT configuration" >&2
        exit 1
    fi
    # Same values are set in both coldboot and rcmboot layouts
    cp coldboot_bct_cfg.xml rcmboot_bct_cfg.xml
    bctargs="--bct_flags_file platform_config_profile.yaml \
--coldboot_bct_cfg coldboot_bct_cfg.xml \
--rcmboot_bct_cfg rcmboot_bct_cfg.xml \
--bldtb $TBCDTB_FILE \
--concat_cpubl_bldtb \
--cpubl uefi_t26x_general.bin \
--cpubl_rcm uefi_t26x_general.bin \
"
    binsargs_params="mb2_bootloader mb2_t264.bin; \
xusb_fw xusb_t264_prod.bin; \
pva_fw nvpva_030.fw; \
dce_fw display-t264-dce.bin; \
bpmp_fw $BPF_FILE; \
bpmp_fw_dtb $BPFDTB_FILE; \
rce_fw camera-rtcpu-t264-rce.img; \
eks eks.img"

elif [ "$CHIPID" = "0x23" ]; then
    binsargs_params="psc_fw pscfw_t234_prod.bin; \
mts_mce mce_flash_o10_cr_prod.bin; \
mb2_applet applet_t234.bin; \
mb2_bootloader mb2_t234.bin; \
xusb_fw xusb_t234_prod.bin; \
pva_fw nvpva_020.fw; \
dce_fw display-t234-dce.bin; \
nvdec nvdec_t234_prod.fw; \
bpmp_fw $BPF_FILE; \
bpmp_fw_dtb $BPFDTB_FILE; \
rce_fw camera-rtcpu-t234-rce.img; \
ape_fw adsp-fw.bin; \
spe_fw spe_t234.bin; $fsifw_binsarg \
tsec_fw tsec_t234.bin; \
tos tos-optee_t234.img; \
eks eks.img"
    bctargs="$UPHY_CONFIG $MINRATCHET_CONFIG \
         --device_config $DEVICE_CONFIG \
         --misc_config $MISC_CONFIG \
         --scr_config $SCR_CONFIG \
         --pinmux_config $PINMUX_CONFIG \
         --gpioint_config $GPIOINT_CONFIG \
         --pmic_config $PMIC_CONFIG \
         --pmc_config $PMC_CONFIG \
         --prod_config $PROD_CONFIG \
         --br_cmd_config $BR_CMD_CONFIG \
         --dev_params $DEV_PARAMS,$DEV_PARAMS_B \
         --deviceprod_config $DEVICEPROD_CONFIG \
         --wb0sdram_config $WB0SDRAM_BCT \
         --mb2bct_cfg $MB2BCT_CFG \
         --bldtb $TBCDTB_FILE \
         --concat_cpubl_bldtb \
         --cpubl uefi_t23x_general.bin \
         --cpubl_rcm uefi_t23x_general.bin"
fi

if [ $rcm_boot -ne 0 -a $to_sign -eq 0 ]; then
    binsargs_params="$binsargs_params; kernel $kernfile; kernel_dtb $kernel_dtbfile; bootloader_dtb $TBCDTB_FILE"
fi

if [ $bup_blob -ne 0 -o $to_sign -ne 0 -o "$sdcard" = "yes" -o $external_device -eq 1 ]; then
    tfcmd=sign
    skipuid="--skipuid"
elif [ $rcm_boot -ne 0 ]; then
    tfcmd=rcmboot
else
    if [ -z "$sdcard" -a $external_device -eq 0 -a $no_flash -eq 0 -a "$spi_only" != "yes" ]; then
        rm -f "$appfile"
        echo "Creating sparseimage ${appfile}..."
        $here/mksparse -b ${blocksize} --fillpattern=0 "$imgfile" "$appfile" || exit 1
        if [ -n "$datafile" ]; then
            rm -f "$datafile"
            echo "Creating sparseimage ${datafile}..."
            $here/mksparse -b ${blocksize} --fillpattern=0 "$dataimg" "$datafile" || exit 1
        fi
    fi
    tfcmd=${flash_cmd:-"flash;reboot"}
fi

if [ $no_flash -eq 0 -a "$erase_spi" != "yes" ] && echo "$tfcmd" | grep -q "flash"; then
    sparseargs="--sparseupdate"
fi

want_signing=0
if [ -n "$keyfile" ] || [ "$CHIPID" != "0x26" -a $rcm_boot -eq 1 ] || [ $no_flash -eq 1 -a $to_sign -eq 1 ]; then
    want_signing=1
fi
if [ $want_signing -eq 1 ]; then
    if [ "$CHIPID" = "0x23" -a -n "$sbk_keyfile" ]; then
        rm -rf signed_bootimg_dir
        mkdir signed_bootimg_dir
        cp xusb_t234_prod.bin signed_bootimg_dir/
        if [ -n "$MINRATCHET_CONFIG" ]; then
            for f in $MINRATCHET_CONFIG; do
                [ -e "$f" ] || continue
                cp "$f" signed_bootimg_dir/
            done
        fi
    fi
    tegraid="$CHIPID"
    localcfgfile="flash.xml"
    dtbfilename="$kernel_dtbfile"
    tbcdtbfilename="$TBCDTB_FILE"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="$kernfile"
    if [ -n "$binsargs_params" ]; then
        BINSARGS="--bins \"$binsargs_params\""
    else
        BINSARGS=
    fi
    BCT="--sdram_config"
    boot_chain_select="A"
    BL_DIR="."
    bctfilename=$(echo $sdramcfg_files | cut -d, -f1)
    bctfile1name=$(echo $sdramcfg_files | cut -d, -f2)
    BCTARGS="$bctargs $overlay_dtb_arg $custinfo_args --bct_backup"
    L4T_CONF_DTBO="L4TConfiguration.dtbo"
    rootfs_ab=0
    gen_read_ramcode=0
    debug_mode=0
    if [ "$CHIPID" = "0x23" ]; then
        flashername="uefi_t23x_general_with_dtb.bin"
        RCM_UEFIBL="uefi_t23x_general_with_dtb.bin"
        UEFIBL="uefi_t23x_general_with_dtb.bin"
        mb1filename="mb1_t234_prod.bin"
        pscbl1filename="psc_bl1_t234_prod.bin"
        tbcfilename="uefi_t23x_general.bin"
        rcm_tbcfile="uefi_t23x_general.bin"
        custinfofilename="$custinfo_out"
        SOSARGS="--applet mb1_t234_prod.bin "
        NV_ARGS=" "
        FLASHARGS="--chip 0x23 $hsm_arg --bl uefi_t23x_general_with_dtb.bin \
          --sdram_config $sdramcfg_files \
          --odmdata $odmdata \
          --applet mb1_t234_prod.bin \
          --cmd \"$tfcmd\" $skipuid \
          --cfg flash.xml \
          --bct_backup \
          --boot_chain A \
          $bctargs $overlay_dtb_arg $custinfo_args $ramcodeargs $extdevargs $sparseargs $BINSARGS"
    elif [ "$CHIPID" = "0x26" ]; then
        flashername="uefi_t26x_general_with_dtb.bin"
        RCM_UEFIBL="uefi_t26x_general_with_dtb.bin"
        UEFIBL="uefi_t26x_general_with_dtb.bin"
        mb1filename="mb1_t264_prod.bin"
        pscbl1filename="psc_bl1_t264_prod.bin"
        tbcfilename="uefi_t26x_general.bin"
        rcm_tbcfile=
        custinfofilename="$custinfo_out"
        SOSARGS="--applet applet_t264.bin "
        NV_ARGS=" "
        FLASHARGS="--chip 0x26 $hsm_arg --bl uefi_t26x_general_with_dtb.bin \
          --applet applet_t264.bin \
          --cmd \"$tfcmd\" $skipuid \
          --coldboot_pt_layout flash.xml \
          --bct_backup \
          --boot_chain A \
          --no_pva 0 \
          $bctargs $overlay_dtb_arg $custinfo_args $ramcodeargs $extdevargs $sparseargs $BINSARGS"
    fi
    FBARGS="--cmd \"$tfcmd\""
    . "$here/odmsign.func"
    (odmsign_ext) || exit 1
    if [ $bup_blob -eq 0 -a $no_flash -ne 0 ]; then
        mv flashcmd.txt secureflash.sh || exit 1
        chmod +x secureflash.sh
    fi
    if [ $also_sign_rcmboot -ne 0 ]; then
        outfolder="$(odmsign_get_folder)"
        rm -rf ${outfolder}_save
        mv ${outfolder} ${outfolder}_save
        rm -f secureflash.xml.save
        mv secureflash.xml secureflash.xml.save
        BCTARGS="$bctargs $rcm_overlay_dtb_arg $custinfo_args --bct_backup"
        L4T_CONF_DTBO="$rcm_bootcontrol_overlay"
        BINSARGS="--bins \"$binsargs_params; kernel $RCMBOOT_KERNEL; kernel_dtb $kernel_dtbfile\""
        if [ "$CHIPID" = "0x23" ]; then
            FLASHARGS="--chip 0x23 $hsm_arg --bl uefi_t23x_general_with_dtb.bin \
--sdram_config $sdramcfg_files \
--odmdata $odmdata \
--applet mb1_t234_prod.bin \
--cmd \"$tfcmd\" $skipuid \
--cfg flash.xml \
--bct_backup \
--boot_chain A \
$bctargs $rcm_overlay_dtb_arg $custinfo_args $ramcodeargs $extdevargs $sparseargs $BINSARGS"
        fi
        (rcm_boot=1 odmsign_ext) || exit 1
        rm -f flashcmd.txt
        rm -rf ${outfolder}
        mv ${outfolder}_save ${outfolder}
        cp -f ${outfolder}/* .
        rm -f secureflash.xml
        mv secureflash.xml.save secureflash.xml
    fi
    if [ $bup_blob -eq 0 -a $no_flash -ne 0 ]; then
        cp secureflash.sh flashcmd.txt
        rm -f APPFILE APPFILE_b DATAFILE
    fi
    if [ $bup_blob -eq 0 ]; then
        exit 0
    fi
    flashcmd="python3 $flashappname ${inst_args} $FLASHARGS"
else
    if [ "$CHIPID" = "0x23" ]; then
        flashcmd="python3 $flashappname ${inst_args} --chip 0x23 $hsm_arg --bl uefi_t23x_general_with_dtb.bin \
--sdram_config $sdramcfg_files \
--odmdata $odmdata \
--applet mb1_t234_prod.bin \
--cmd \"$tfcmd\" $skipuid \
--cfg flash.xml \
--bct_backup \
--boot_chain A \
$bctargs $overlay_dtb_arg $custinfo_args $ramcodeargs $extdevargs $sparseargs \
--bins \"$binsargs_params\""
    elif [ "$CHIPID" = "0x26" ]; then
        if [ $rcm_boot -ne 0 ]; then
            flashcmd="python3 $flashappname ${inst_args} --chip 0x26 $hsm_arg --bl uefi_t26x_general_with_dtb.bin \
--applet mb1_t264.bin \
--cmd \"$tfcmd\" $skipuid \
--rcmboot_pt_layout flash.xml \
--bct_backup \
--boot_chain A \
--no_pva 0 \
$bctargs $rcm_overlay_dtb_arg $custinfo_args $ramcodeargs $extdevargs $sparseargs \
--bins \"$binsargs_params\""
        else
            flashcmd="python3 $flashappname ${inst_args} --chip 0x26 $hsm_arg --bl uefi_t26x_general_with_dtb.bin \
--applet mb1_t264.bin \
--cmd \"$tfcmd\" $skipuid \
--coldboot_pt_layout flash.xml \
--bct_backup \
--boot_chain A \
--no_pva 0 \
$bctargs $overlay_dtb_arg $custinfo_args $ramcodeargs $extdevargs $sparseargs \
--bins \"$binsargs_params\""
        fi
    fi
fi

if [ $bup_blob -ne 0 ]; then
    [ -z "$keyargs" ] || flashcmd="${flashcmd} $keyargs"
    support_multi_spec=1
    clean_up=0
    dtbfilename="$kernel_dtbfile"
    tbcdtbfilename="$TBCDTB_FILE"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    spec="${BOARDID}-${FAB}-${BOARDSKU}-${BOARDREV}-1-${CHIPREV}-${MACHINE}-"
    if [ $(expr length "$spec") -ge 128 ]; then
        echo "ERR: TNSPEC must be shorter than 128 characters: $spec" >&2
        exit 1
    fi
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" generic "$keyfile" "$sbk_keyfile" $CHIPID || exit 1
    exit 0
fi

if [ $to_sign -ne 0 ]; then
    eval $flashcmd < /dev/null || exit 1
    exit 0
fi

if [ $no_flash -ne 0 ]; then
    if [ "$CHIPID" = "0x26" -a $rcm_boot -ne 0 ]; then
        [ -z "$keyargs" ] || flashcmd="${flashcmd} $keyargs"
        eval $flashcmd --no_flash < /dev/null || exit 1
    else
        echo "$flashcmd" | sed -e 's,--skipuid,,g' > flashcmd.txt
        chmod +x flashcmd.txt
        rm -f APPFILE APPFILE_b DATAFILE
    fi
else
    eval $flashcmd < /dev/null || exit 1
    if [ -n "$sdcard" -o $external_device -eq 1 ]; then
        if [ $external_device -eq 1 -a -n "$serial_number" ]; then
            make_sdcard_args="$make_sdcard_args --serial-number $serial_number"
        fi
        if [ -n "$pre_sdcard_sed" ]; then
            rm -f signed/flash.xml.tmp.in
            mv signed/flash.xml.tmp signed/flash.xml.tmp.in
            sed $pre_sdcard_sed  signed/flash.xml.tmp.in > signed/flash.xml.tmp
        fi
        $here/make-sdcard $make_sdcard_args signed/flash.xml.tmp "$@"
    fi
fi
