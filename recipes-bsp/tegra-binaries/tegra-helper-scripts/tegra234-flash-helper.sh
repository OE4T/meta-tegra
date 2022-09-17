#!/bin/bash
bup_blob=0
keyfile=
sbk_keyfile=
user_keyfile=
spi_only=
sdcard=
no_flash=0
flash_cmd=
imgfile=
dataimg=
inst_args=""
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

ARGS=$(getopt -n $(basename "$0") -l "bup,no-flash,sdcard,spi-only,datafile:,usb-instance:,user_key:" -o "u:v:s:b:B:yc:" -- "$@")
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
    --no-flash)
        no_flash=1
        shift
        ;;
    --sdcard)
        sdcard=yes
        shift
        ;;
    --spi-only)
        spi_only=yes
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
    --user_key)
        user_keyfile="$2"
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

if [ -z "$FLASHVARS" ]; then
    echo "ERR: flash variable set not defined" >&2
    exit 1
fi

# Temp file for storing cvm.bin in, if we need to query the board for its
# attributes
cvm_bin=$(mktemp cvm.bin.XXXXX)

skipuid=""
if [ -z "$CHIPREV" ]; then
    chipid=`$here/tegrarcm_v2 --uid | grep BR_CID | cut -d' ' -f2`
    if [ -z "$chipid" ]; then
        echo "ERR: could not retrieve chip ID" >&2
        exit 1
    fi
    if [ "${chipid:6:2}" != "23" ]; then
        echo "ERR: chip ID mismatch for Orin" >&2
        exit 1
    fi
    flval="0x${chipid:2:1}"
    flval=$(printf "%x" "$((flval & 0x8))")
    tmp_1="0x${chipid:3:2}"
    tmp_1=$(printf "%2.2x" "$((tmp_1 & 0xf0))")
    flval="${flval}${tmp_1}"
    case "${flval}" in
        000)
            echo "ERR: non-production chip found" >&2
            exit 1
            ;;
        800)
            # non-secured
            break
            ;;
        810|820|830|840|850)
            # RSA/ECDSA P-256/ECDSA P-512/ED25519/XMSS
            break
            ;;
        890|8a0|8b0|8c0|8d0)
            # SBK + RSA/ECDSA P-256/ECDSA P-512/ED25519/XMSS
            break
            ;;
        *)
            echo "ERR: unrecognized fused configuration 0x$flval" >&2
            exit 1
    esac
    CHIPREV="${chipid:5:1}"
    skipuid="--skipuid"
fi

if [ -z "$FAB" -o -z "$BOARDID" ]; then
    if ! python3 $flashappname ${inst_args} --chip 0x23 --applet mb1_t234_prod.bin $skipuid \
         --bins "mb2_applet applet_t234.bin" --cmd "dump eeprom cvm ${cvm_bin}; dump custinfo ${custinfo_out}; reboot recovery"; then
        echo "ERR: could not retrieve EEPROM board information" >&2
        exit 1
    fi
    # The chip_info.bin_bak file is created as a side effect of the above tegraflash.py invocation
    if [ ! -e chip_info.bin_bak ]; then
        echo "ERR: chip_info.bin_bak missing after dumping boardinfo" >&2
        exit 1
    fi
    CHIP_SKU=$($here/chkbdinfo -C chip_info.bin_bak)
    # XXX- these don't appear to be used
    # chip_minor_revision=$($here/chkbdinfo -M chip_info.bin_bak)
    # bootrom_revision=$($here/chkbdinfo -O chip_info.bin_bak)
    # ramcode_id=$($here/chkbdinfo -R chip_info.bin_bak)
    # -XXX
    skipuid=""
fi

if [ -z "$CHIP_SKU" ]; then
    # see DEFAULT_CHIP_SKU in p3701.conf.common
    CHIP_SKU="00:00:00:D0"
fi

if [ -n "$BOARDID" ]; then
    boardid="$BOARDID"
else
    boardid=`$here/chkbdinfo -i ${cvm_bin} | tr -d '[:space:]'`
    BOARDID="$boardid"
fi
if [ -n "$FAB" ]; then
    board_version="$FAB"
else
    board_version=`$here/chkbdinfo -f ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
    FAB="$board_version"
fi
if [ -n "$BOARDSKU" ]; then
    board_sku="$BOARDSKU"
else
    board_sku=`$here/chkbdinfo -k ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
    BOARDSKU="$board_sku"
fi
if [ "${BOARDREV+isset}" = "isset" ]; then
    board_revision="$BOARDREV"
else
    board_revision=`$here/chkbdinfo -r ${cvm_bin} | tr -d '[:space:]' | tr [a-z] [A-Z]`
    BOARDREV="$board_revision"
fi

[ -f ${cvm_bin} ] && rm -f ${cvm_bin}

[ -n "$BOARDID" ] || BOARDID=3701
[ -n "$FAB" ] || FAB=TS4
[ -n "$fuselevel" ] || fuselevel=fuselevel_production
[ -n "${BOOTDEV}" ] || BOOTDEV="mmcblk0p1"

if [ "$BOARDID" = "3701" -a "$FAB" = "301" ]; then
    RAMCODE=0
fi

if echo "$CHIP_SKU" | grep -q ":" 2>/dev/null; then
    chip_sku=$(echo "$CHIP_SKU" | cut -d: -f4)
else
    chip_sku=$CHIP_SKU
fi

case $chip_sku in
    00)
        ;;
    90|97|9E)
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

if [ "$chip_sku" = "00" -o "$chip_sku" = "D0" ] &&
       echo "$FAB" | egrep -q '^(TS[123]|EB[123]|[012]00)$'; then
    PINMUX_CONFIG="tegra234-mb1-bct-pinmux-p3701-0000.dtsi"
    PMC_CONFIG="tegra234-mb1-bct-padvoltage-p3701-0000.dtsi"
fi

if [ "$BOARDID" = "3701" -a "$BOARDSKU" != "0000" ]; then
    BPFDTB_FILE=$(echo "$BPFDTB_FILE" | sed -e"s,p3701-0000,p3701-$BOARDSKU,")
    dtb_file=$(echo "$dtb_file" | sed -e"s,p3701-0000,p3701-$BOARDSKU,")
fi

if [ "${fuselevel}" = "fuselevel_production" ]; then
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${DEV_PARAMS}";
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${DEV_PARAMS_B}";
    sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" "${EMC_FUSE_DEV_PARAMS}";
fi

rm -f ${MACHINE}_bootblob_ver.txt
echo "NV3" >${MACHINE}_bootblob_ver.txt
. bsp_version
echo "# R$BSP_BRANCH , REVISION: $BSP_MAJOR.$BSP_MINOR" >>${MACHINE}_bootblob_ver.txt
echo "BOARDID=$BOARDID BOARDSKU=$BOARDSKU FAB=$FAB" >>${MACHINE}_bootblob_ver.txt
date "+%Y%m%d%H%M%S" >>${MACHINE}_bootblob_ver.txt
bytes=`cksum ${MACHINE}_bootblob_ver.txt | cut -d' ' -f2`
cksum=`cksum ${MACHINE}_bootblob_ver.txt | cut -d' ' -f1`
echo "BYTES:$bytes CRC32:$cksum" >>${MACHINE}_bootblob_ver.txt
if [ -z "$sdcard" ]; then
    appfile=$(basename "$imgfile").img
    if [ -n "$dataimg" ]; then
    datafile=$(basename "$dataimg").img
    fi
else
    appfile="$imgfile"
    datafile="$dataimg"
fi
appfile_sed=
if [ $bup_blob -ne 0 ]; then
    kernfile="${kernfile:-boot.img}"
    appfile_sed="-e/APPFILE/d -e/DATAFILE/d"
elif [ $no_flash -eq 0 -a -z "$sdcard" ]; then
    appfile_sed="-es,APPFILE_b,$appfile, -es,APPFILE,$appfile, -es,DATAFILE,$datafile,"
else
    pre_sdcard_sed="-es,APPFILE_b,$appfile, -es,APPFILE,$appfile,"
    if [ -n "$datafile" ]; then
    pre_sdcard_sed="$pre_sdcard_sed -es,DATAFILE,$datafile,"
    touch DATAFILE
    fi
    touch APPFILE APPFILE_b
fi

dtb_file_basename=$(basename "$dtb_file")
kernel_dtbfile="kernel_$dtb_file_basename"
rm -f "$kernel_dtbfile"
cp "$dtb_file" "$kernel_dtbfile"

if [ "$spi_only" = "yes" ]; then
    if [ ! -e "$here/nvflashxmlparse" ]; then
    echo "ERR: missing nvflashxmlparse script" >&2
    exit 1
    fi
    "$here/nvflashxmlparse" --extract -t spi -o flash.xml.tmp "$flash_in" || exit 1
else
    cp "$flash_in" flash.xml.tmp
fi
sed -e"s,VERFILE,${MACHINE}_bootblob_ver.txt," -e"s,BPFDTB_FILE,$BPFDTB_FILE," \
    -e"s,TBCDTB-FILE,$dtb_file," -e"s, DTB_FILE,$kernel_dtbfile," \
    $appfile_sed flash.xml.tmp > flash.xml
rm flash.xml.tmp

BINSARGS="psc_fw pscfw_t234_prod.bin; \
mts_mce mce_flash_o10_cr_prod.bin; \
mb2_applet applet_t234.bin; \
mb2_bootloader mb2_t234.bin; \
xusb_fw xusb_t234_prod.bin; \
dce_fw display-t234-dce.bin; \
nvdec nvdec_t234_prod.fw; \
bpmp_fw $BPF_FILE; \
bpmp_fw_dtb $BPFDTB_FILE; \
sce_fw camera-rtcpu-sce.img; \
rce_fw camera-rtcpu-t234-rce.img; \
ape_fw adsp-fw.bin; \
spe_fw spe_t234.bin; \
tlk tos-optee_t234.img; \
eks eks.img"

custinfo_args=
if [ -f "$custinfo_out" ]; then
    custinfo_args="--cust_info $custinfo_out"
fi
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
         --bldtb $BLDTB \
         --concat_cpubl_bldtb \
         --cpubl uefi_jetson.bin \
         --overlay_dtb $OVERLAY_DTB_FILE $custinfo_args"

if [ $bup_blob -ne 0 -o "$sdcard" = "yes" ]; then
    tfcmd=sign
    skipuid="--skipuid"
else
    if [ -z "$sdcard" -a $no_flash -eq 0 -a "$spi_only" != "yes" ]; then
    rm -f "$appfile"
    $here/mksparse -b ${blocksize} --fillpattern=0 "$imgfile" "$appfile" || exit 1
    if [ -n "$datafile" ]; then
        rm -f "$datafile"
        $here/mksparse -b ${blocksize} --fillpattern=0 "$dataimg" "$datafile" || exit 1
    fi
    fi
    tfcmd=${flash_cmd:-"flash;reboot"}
fi

temp_user_dir=
if [ -n "$keyfile" ]; then
    if [ -n "$sbk_keyfile" ]; then
        if [ -z "$user_keyfile" ]; then
            rm -f "null_user_key.txt"
            echo "0x00000000 0x00000000 0x00000000 0x00000000" > null_user_key.txt
            user_keyfile=$(readlink -f null_user_key.txt)
        fi
        rm -rf signed_bootimg_dir
        mkdir signed_bootimg_dir
        cp "$kernfile" "$kernel_dtbfile" signed_bootimg_dir/
        if [ -n "$MINRATCHET_CONFIG" ]; then
            for f in $MINRATCHET_CONFIG; do
                [ -e "$f" ] || continue
                cp "$f" signed_bootimg_dir/
            done
        fi
        oldwd="$PWD"
        cd signed_bootimg_dir
        if [ -x $here/l4t_sign_image.sh ]; then
            signimg="$here/l4t_sign_image.sh";
        else
            hereparent=$(readlink -f "$here/.." 2>/dev/null)
            if [ -n "$hereparent" -a -x "$hereparent/l4t_sign_image.sh" ]; then
                signimg="$hereparent/l4t_sign_image.sh"
            fi
        fi
        if [ -z "$signimg" ]; then
            echo "ERR: missing l4t_sign_image script" >&2
            exit 1
        fi
        "$signimg" --file "$kernfile"  --key "$keyfile" --encrypt_key "$user_keyfile" --chip 0x23 --split False $MINRATCHET_CONFIG &&
            "$signimg" --file "$kernel_dtbfile"  --key "$keyfile" --encrypt_key "$user_keyfile" --chip 0x23 --split False $MINRATCHET_CONFIG
        rc=$?
        cd "$oldwd"
        if [ $rc -ne 0 ]; then
            echo "Error signing kernel image or device tree" >&2
            exit 1
        fi
        temp_user_dir=signed_bootimg_dir
    fi
    CHIPID="0x23"
    tegraid="$CHIPID"
    localcfgfile="flash.xml"
    dtbfilename="$kernel_dtbfile"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="$kernfile"
    BINSARGS="--bins \"$BINSARGS\""
    flashername=uefi_jetson_with_dtb.bin
    BCT="--sdram_config"
    bctfilename=`echo $sdramcfg_files | cut -d, -f1`
    bctfile1name=`echo $sdramcfg_files | cut -d, -f2`
    SOSARGS="--applet mb1_t234_prod.bin "
    NV_ARGS=" "
    BCTARGS="$bctargs"
    rootfs_ab=0
    rcm_boot=0
    external_device=0
    . "$here/odmsign.func"
    (odmsign_ext) || exit 1
    if [ $bup_blob -eq 0 -a $no_flash -ne 0 ]; then
        if [ -f flashcmd.txt ]; then
            chmod +x flashcmd.txt
            ln -sf flashcmd.txt ./secureflash.sh
        else
            echo "WARN: signing completed successfully, but flashcmd.txt missing" >&2
        fi
        rm -f APPFILE APPFILE_b DATAFILE null_user_key.txt
    fi
    if [ $bup_blob -eq 0 ]; then
        if [ -n "$temp_user_dir" ]; then
            cp "$temp_user_dir"/*.encrypt.signed .
            rm -rf "$temp_user_dir"
        fi
        exit 0
    fi
    touch odmsign.func
fi

flashcmd="python3 $flashappname ${inst_args} --chip 0x23 --bl uefi_jetson_with_dtb.bin \
          --sdram_config $sdramcfg_files
          --odmdata $odmdata \
          --applet mb1_t234_prod.bin \
          --cmd \"$tfcmd\" $skipuid \
          --cfg flash.xml \
          --boot_chain A \
          --bct_backup \
          --secondary_gpt_backup \
          $bctargs \
          --bins \"$BINSARGS\""

if [ $bup_blob -ne 0 ]; then
    [ -z "$keyfile" ] || flashcmd="${flashcmd} --key \"$keyfile\""
    [ -z "$sbk_keyfile" ] || flashcmd="${flashcmd} --encrypt_key \"$sbk_keyfile\""
    support_multi_spec=1
    clean_up=0
    dtbfilename="$kernel_dtbfile"
    tbcdtbfilename="$dtb_file"
    bpfdtbfilename="$BPFDTB_FILE"
    localbootfile="boot.img"
    . "$here/l4t_bup_gen.func"
    spec="${BOARDID}-${FAB}-${BOARDSKU}-${BOARDREV}-1-${CHIPREV}-${MACHINE}-${BOOTDEV}"
    if [ $(expr length "$spec") -ge 128 ]; then
        echo "ERR: TNSPEC must be shorter than 128 characters: $spec" >&2
        exit 1
    fi
    l4t_bup_gen "$flashcmd" "$spec" "$fuselevel" t186ref "$keyfile" "$sbk_keyfile" 0x23 || exit 1
else
    eval $flashcmd < /dev/null || exit 1
    if [ -n "$sdcard" ]; then
        if [ -n "$pre_sdcard_sed" ]; then
            rm -f signed/flash.xml.tmp.in
            mv signed/flash.xml.tmp signed/flash.xml.tmp.in
            sed $pre_sdcard_sed  signed/flash.xml.tmp.in > signed/flash.xml.tmp
        fi
        $here/make-sdcard $make_sdcard_args signed/flash.xml.tmp "$@"
    fi
fi
