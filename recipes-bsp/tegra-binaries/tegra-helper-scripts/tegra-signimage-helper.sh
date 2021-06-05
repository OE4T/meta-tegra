#!/bin/bash
keyfile=
user_keyfile=
to_remove=
split=True
chip=
encrypting=
allfilestype=
ratchet=

ARGS=$(getopt -n $(basename "$0") -l "user_key:,chip:,nosplit,type:,minratchet_config:" -o "u:v:" -- "$@")
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
	--user_key)
	    user_keyfile="$2"
	    shift 2
	    ;;
	--type)
	    allfilestype="$2"
	    shift 2
	    ;;
	--minratchet_config)
	    ratchet="--minratchet_config $2"
	    shift 2
	    ;;
	--nosplit)
	    split=False
	    shift
	    ;;
	-u)
	    keyfile="$2"
	    shift 2
	    ;;
	-v)
	    # Accepted here to tell us that we need to
	    # generate an all-zeros user_keyfile
	    encrypting=yes
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

tmpuserkey=
if [ -z "$user_keyfile" -a "$encrypting" = "yes" ]; then
    tmpuserkey=$(mktemp)
    echo "0x00000000 0x00000000 0x00000000 0x00000000" > "$tmpuserkey"
    user_keyfile=$(readlink -f "$tmpuserkey")
    echo "Using null key for encryption" >&2
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
    if ! "$signimg" --file "$filetosign"  --type "$ftype" --key "$keyfile" --encrypt_key "$user_keyfile" --chip "$chip" --split $split $ratchet; then
	echo "Error signing $filetosign" >&2
	rc=1
    fi
done
[ -z "$tmpuserkey" ] || rm -f "$tmpuserkey"
exit $rc
