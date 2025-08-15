#!/bin/bash
keyfile=
encrypt_keyfile=
to_remove=
split=True
chip=
encrypting=
allfilestype=
enable_user_kdk=""

ARGS=$(getopt -n $(basename "$0") -l "encrypt_key:,key:chip:,nosplit,type:,enable_user_kdk" -o "u:v:" -- "$@")
if [ $? -ne 0 ]; then
    echo "Error parsing options" >&2
    exit 1
fi
eval set -- "$ARGS"
unset ARGS

while true; do
    case "$1" in
        --chip)
            chip="$2"
            shift 2
            ;;
        -v|--encrypt_key)
            encrypt_keyfile="$2"
            shift 2
            ;;
        --type)
            allfilestype="$2"
            shift 2
            ;;
        --nosplit)
            split=False
            shift
            ;;
        --enable_user_kdk)
            enable_user_kdk="--enable_user_kdk True"
            shift
            ;;
        -u|--key)
            keyfile="$2"
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

if [ -z "$chip" ]; then
    echo "ERR: --chip option not specified" >&2
    exit 1
fi

if [ -n "$enable_user_kdk" -a "$chip" != "0x23" ]; then
    echo "ERR: only chip 0x23 supports --enable_user_kdk" >&2
    exit 1
fi

here=$(readlink -f $(dirname "$0"))

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

if [ "$keyfile" = "None" ]; then
    keyfile=""
fi

rc=0
while [ -n "$1" ]; do
    filetosign="$1"
    shift
    ftype="$allfilestype"
    if [ -z "$ftype" ]; then
        if echo "$filetosign" | grep -q "Image"; then
            ftype="kernel"
        elif echo "$filetosign" | grep -q "\.dtb$"; then
            ftype="kernel_dtb"
        else
            ftype="data"
        fi
        echo "Setting --type $ftype for $filetosign" >&2
    fi
    if ! "$signimg" --file "$filetosign"  --type "$ftype" --key "$keyfile" --encrypt_key "$encrypt_keyfile" --chip "$chip" --split $split $enable_user_kdk; then
        echo "Error signing $filetosign" >&2
        rc=1
    fi
done
exit $rc
