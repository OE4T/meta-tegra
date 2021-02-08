#!/bin/sh
# bitbake fetcher passes --no-same-owner -xpf <filename>
tarargs=
extraarg=
while [ $# -gt 0 ]; do
    case "$1" in
	-x*f)
	    tarargs="$tarargs $1 $2"
	    if [ "$2" = "data.tar.zst" ]; then
		tarargs="$tarargs -Iunzstd"
	    fi
	    shift 2
	    ;;
	*)
	    tarargs="$tarargs $1"
	    shift
	    ;;
    esac
done
PATH="$TAR_WRAPPER_STRIPPED_PATH" tar $tarargs
