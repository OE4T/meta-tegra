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
blocksize=4096

# These functions are used in odmsign.func but do not
# need to do anything when run from this script, as we
# have already copied needed files to the current working
# directory.
mkfilesoft() {
    :
}

cp2local() {
    :
}

signimage() {
    :
}

process_l4t_conf_dtbo() {
    :
}

partition_exists_in_PT_table() {
    [ "$1" = "BCT-boot-chain_backup" ]
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
    local value=$("$here/nvflashxmlparse" --get-filename "$partname" "$layoutfile")
    eval "$varname=\"$value\""
}

ARGS=$(getopt -n $(basename "$0") -l "bup,bup-type:,no-flash,sign,sdcard,spi-only,boot-only,external-device,rcm-boot,datafile:,usb-instance:,uefi-enc:" -o "u:v:s:b:B:yc:" -- "$@")
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

if [ ! -e ./flashvars ]; then
    echo "ERR: missing flash variables file" >&2
    exit 1
fi

. ./flashvars

if [ -z "$CHIPID" ]; then
    echo "ERR: CHIPID variable not set" >&2
    exit 1
fi

rcm_bootcontrol_overlay="L4TConfiguration-rcmboot.dtbo"
if [ $rcm_boot -eq 1 -a $to_sign -eq 0 ]; then
    overlay_dtb_files="$rcm_bootcontrol_overlay"
else
    overlay_dtb_files="$BOOTCONTROL_OVERLAYS"
fi
if [ -z "$overlay_dtb_files" ]; then
    overlay_dtb_files="$PLUGIN_MANAGER_OVERLAYS"
elif [ -n "$PLUGIN_MANAGER_OVERLAYS" ]; then
    overlay_dtb_files="$overlay_dtb_files,$PLUGIN_MANAGER_OVERLAYS"
fi
if [ -z "$overlay_dtb_files" ]; then
    overlay_dtb_files="$OVERLAY_DTB_FILE"
elif [ -n "$OVERLAY_DTB_FILE" ]; then
    overlay_dtb_files="$overlay_dtb_files,$OVERLAY_DTB_FILE"
fi
overlay_dtb_arg=
if [ -n "$overlay_dtb_files" ]; then
    overlay_dtb_arg="--overlay_dtb $overlay_dtb_files"
fi
if [ -n "$DCE_OVERLAY" ]; then
    overlay_dtb_arg="$overlay_dtb_arg --dce_overlay_dtb $DCE_OVERLAY"
fi

fuselevel="fuselevel_production"

# Temp file for storing cvm.bin in, if we need to query the board for its
# attributes
cvm_bin=$(mktemp cvm.bin.XXXXX)

skipuid=""
bootauth=""
BR_CID=
if [ -z "$CHIPREV" ]; then
    chipidargs=
    if [ "$CHIPID" = "0x23" ]; then
        chipidargs="--new_session --chip $CHIPID"
    fi
    BR_CID=$($here/tegrarcm_v2 ${chipidargs} ${inst_args} --uid | grep BR_CID | cut -d' ' -f2)
    chipid="$BR_CID"
    if [ -z "$chipid" ]; then
        echo "ERR: could not retrieve chip ID" >&2
        exit 1
    fi
    if [ "${chipid:6:2}" = "23" ]; then
        if [ "$CHIPID" != "0x23" ]; then
            echo "ERR: CHIPID ($CHIPID) does not match actual chip ID (0x${chipid:6:2})" >&2
            exit 1
        fi
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
    else
        echo "ERR: unrecognized chip ID: 0x${chipid:6:2}" >&2
        exit 1
    fi
    CHIPREV="${chipid:5:1}"
    skipuid="--skipuid"
    case $bootauth in
        PKC|SBKPKC)
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
elif [ "$CHIPID" = "0x23" ]; then
    skipuid="--skipuid"
fi

have_boardinfo=
keyargs=
[ -z "$keyfile" ] || keyargs="$keyargs --key $keyfile"
[ -z "$sbk_keyfile" ] || keyargs="$keyargs --encrypt_key $sbk_keyfile"
if [ -z "$FAB" -o -z "$BOARDID" ]; then
    if [ -n "$EMC_FUSE_DEV_PARAMS" ]; then
        sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "$EMC_FUSE_DEV_PARAMS"
    fi
    rm -f rcm_state
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
    fi
    skipuid=""
    have_boardinfo="yes"
fi

if [ -n "$BOARDID" ]; then
    boardid="$BOARDID"
else
    boardid=$($here/chkbdinfo -i ${cvm_bin} | tr -d '[:space:]')
    BOARDID="$boardid"
fi

if [ "$CHIPID" = "0x23" -a -z "$CHIP_SKU" ]; then
    # see DEFAULT_CHIP_SKU in p3701.conf.common
    # or DFLT_CHIP_SKU in p3767.conf.common
    if [ "$BOARDID" = "3767" ]; then
        CHIP_SKU="00:00:00:D3"
    else
        CHIP_SKU="00:00:00:D0"
    fi
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

rm -f boardvars.sh
cat >boardvars.sh <<EOF
BOARDID="$BOARDID"
FAB="$FAB"
BOARDSKU="$BOARDSKU"
BOARDREV="$BOARDREV"
CHIPREV="$CHIPREV"
CHIP_SKU="$CHIP_SKU"
EOF
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

if [ "$BOARDID" = "3701" -a "$FAB" = "301" ]; then
    RAMCODE=0
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
                EMMC_BCT=$(echo "$EMMC_BCT" | sed -e"s,3701-0000,3701-$BOARDSKU,")
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
        BPFDTBREV="a02"
        PMCREV="a03"
        PMICREV="a02"
        if [ "$BOARDSKU" = "0000" -o "$BOARDSKU" = "0002" ]; then
            if [ "$FAB" = "TS1" -o "$FAB" = "EB1" ]; then
                PINMUXREV="a01"
                BPFDTBREV="a00"
                PMCREV="a01"
                PMICREV="a00"
            fi
        fi
        if [ "$BOARDSKU" = "0001" -o "$BOARDSKU" = "0003" -o "$BOARDSKU" = "0005" ]; then
            EMMC_BCT="tegra234-p3767-0001-sdram-l4t.dts"
            WB0SDRAM_BCT="tegra234-p3767-0001-wb0sdram-l4t.dts"
        elif [ "$BOARDSKU" = "0004" ]; then
            EMMC_BCT="tegra234-p3767-0004-sdram-l4t.dts"
            WB0SDRAM_BCT="tegra234-p3767-0004-wb0sdram-l4t.dts"
        fi
        PINMUX_CONFIG=$(echo "$PINMUX_CONFIG" | sed -e"s,@PINMUXREV@,$PINMUXREV,")
        PMC_CONFIG=$(echo "$PMC_CONFIG" | sed -e"s,@PMCREV@,$PMCREV,")
        PMIC_CONFIG=$(echo "$PMIC_CONFIG" | sed -e"s,@PMICREV@,$PMICREV,")
        BPFDTB_FILE=$(echo "$BPFDTB_FILE" | sed -e"s,@BPFDTBREV@,$BPFDTBREV,")
    fi

    if [ -n "$RAMCODE" ]; then
        ramcodeargs="--ramcode $RAMCODE"
    fi

    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${DEV_PARAMS}";
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${DEV_PARAMS_B}";
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${EMC_FUSE_DEV_PARAMS}";
fi

echo "Board ID($BOARDID) version($FAB) sku($BOARDSKU) revision($BOARDREV)"

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

if [ "$CHIPID" = "0x23" ]; then
    BINSARGS="psc_fw pscfw_t234_prod.bin; \
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
spe_fw spe_t234.bin; \
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
         --cpubl uefi_jetson.bin \
         $overlay_dtb_arg $custinfo_args"
fi

if [ $rcm_boot -ne 0 ]; then
    BINSARGS="$BINSARGS; kernel $kernfile; kernel_dtb $kernel_dtbfile"
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

want_signing=0
if [ -n "$keyfile" ] || [ $rcm_boot -eq 1 ] || [ $no_flash -eq 1 -a $to_sign -eq 1 ]; then
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
    BINSARGS="--bins \"$BINSARGS\""
    BCT="--sdram_config"
    boot_chain_select="A"
    if [ "$CHIPID" = "0x23" ]; then
        flashername="uefi_jetson_with_dtb.bin"
        UEFIBL="uefi_jetson_with_dtb.bin"
        mb1filename="mb1_t234_prod.bin"
        pscbl1filename="psc_bl1_t234_prod.bin"
        tbcfilename="uefi_jetson.bin"
        custinfofilename="$custinfo_out"
        SOSARGS="--applet mb1_t234_prod.bin "
        NV_ARGS=" "
    fi
    BL_DIR="."
    bctfilename=$(echo $sdramcfg_files | cut -d, -f1)
    bctfile1name=$(echo $sdramcfg_files | cut -d, -f2)
    BCTARGS="$bctargs --bct_backup"
    L4T_CONF_DTBO="L4TConfiguration.dtbo"
    rootfs_ab=0
    FLASHARGS="--chip 0x23 --bl uefi_jetson_with_dtb.bin \
          --sdram_config $sdramcfg_files \
          --odmdata $odmdata \
          --applet mb1_t234_prod.bin \
          --cmd \"$tfcmd\" $skipuid \
          --cfg flash.xml \
          --bct_backup \
          --boot_chain A \
          $bctargs $ramcodeargs $extdevargs $BINSARGS"
    FBARGS="--cmd \"$tfcmd\""
    . "$here/odmsign.func"
    (odmsign_ext_sign_and_flash) || exit 1
    if [ "$CHIPID" = "0x23" ]; then
        cp uefi_jetson.bin rcmboot_uefi_jetson.bin
        rcm_overlay_dtbs="$rcm_bootcontrol_overlay"
        if [ -n "$PLUGIN_MANAGER_OVERLAYS" ]; then
            rcm_overlay_dtbs="$rcm_overlay_dtbs,$PLUGIN_MANAGER_OVERLAYS"
        fi
        if [ -n "$OVERLAY_DTB_FILE" ]; then
            rcm_overlay_dtbs="$rcm_overlay_dtbs,$OVERLAY_DTB_FILE"
        fi
        rcmbootsigncmd="python3 $flashappname $keyargs --chip 0x23 --odmdata $odmdata --bldtb $TBCDTB_FILE --concat_cpubl_bldtb --overlay_dtb $rcm_overlay_dtbs \
                    --cmd \"sign rcmboot_uefi_jetson.bin bootloader_stage2 A_cpu-bootloader\""
        eval $rcmbootsigncmd || exit 1
    fi
    if [ $bup_blob -eq 0 -a $no_flash -ne 0 ]; then
        if [ -f flashcmd.txt ]; then
            chmod +x flashcmd.txt
            ln -sf flashcmd.txt ./secureflash.sh
        else
            echo "WARN: signing completed successfully, but flashcmd.txt missing" >&2
        fi
        rm -f APPFILE APPFILE_b DATAFILE
    fi
    if [ $bup_blob -eq 0 ]; then
        exit 0
    fi
    touch odmsign.func
    flashcmd="python3 $flashappname ${inst_args} $FLASHARGS"
else
    flashcmd="python3 $flashappname ${inst_args} --chip 0x23 --bl uefi_jetson_with_dtb.bin \
          --sdram_config $sdramcfg_files \
          --odmdata $odmdata \
          --applet mb1_t234_prod.bin \
          --cmd \"$tfcmd\" $skipuid \
          --cfg flash.xml \
          --bct_backup \
          --boot_chain A \
          $bctargs $extdevargs \
          --bins \"$BINSARGS\""
fi

if [ $bup_blob -ne 0 ]; then
    [ -z "$keyfile" ] || flashcmd="${flashcmd} --key \"$keyfile\""
    [ -z "$sbk_keyfile" ] || flashcmd="${flashcmd} --encrypt_key \"$sbk_keyfile\""
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
    echo "$flashcmd" | sed -e 's,--skipuid,,g' > flashcmd.txt
    chmod +x flashcmd.txt
    rm -f APPFILE APPFILE_b DATAFILE
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
