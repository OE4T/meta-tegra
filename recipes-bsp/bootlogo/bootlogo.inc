# Helpers for bootlogo-custom

SPLASH_GRAPHIC_BACKGROUND ?= "white"
BMP_BLOB_SIZES ?= "1920x1080 1280x720 640x480"

# generate bitmaps by imagemagick based on single input image
cook_bitmaps() {
    local _input_file=$1

    for size in ${BMP_BLOB_SIZES}
    do
        local W=${size%x*}
        local H=${size#*x}
        bbdebug 1 "Resizing ${SPLASH_GRAPHIC_FILENAME} to ${W}x${H} with background ${SPLASH_GRAPHIC_BACKGROUND}"
        # resize input with size reduction only, pad remaining about center with color
        convert.im7 ${_input_file} -type truecolor -colorspace RGB -resize ${W}x${H}\> -gravity center -background ${SPLASH_GRAPHIC_BACKGROUND} -extent ${W}x${H} ${B}/logo-${H}.bmp
        LIST="${LIST}${B}/logo-${H}.bmp nvidia ${H};"
    done

    return 0
}

# compress blob function from genbmpblob_L4T.sh
compress_blob() {

    _blob_file=$1
    _blob_output_file=$2

    _blob_size_pos=20
    _header_len_offset=24
    _blob_header_len=`od -An -j ${_header_len_offset} -N 4 -t d4 ${B}/bmp.blob`

    _blob_size=`cat ${_blob_file} | wc -c`
    _compressed_blob=${_blob_file}.lz4

    head -c ${_blob_header_len} ${_blob_file} > ${_compressed_blob}

    _blob_size=`expr ${_blob_size} - ${_blob_header_len}`
    _cmd="lz4c -l -c1 -f stdin >> ${_compressed_blob}"
    tail -c ${_blob_size} ${_blob_file} | eval ${_cmd}

    # update blob size(4Bytes) in header
    _compressed_size=`cat ${_compressed_blob} | wc -c`

    local _i=${_blob_size_pos}
    local _end=`expr $_i + 4`
    local _byte=0
    while [ $_i -lt $_end ]
    do
        _byte=$(expr $_compressed_size % 256 || echo 0)
        _byte=`printf %o $_byte`
        printf "\\$_byte" | dd conv=notrunc of="${_compressed_blob}" bs=1 seek=${_i}
        # expr returns non-zero for purely fractional results
        _compressed_size=$(expr $_compressed_size / 256 || echo 0)
        _i=`expr $_i + 1`
    done

    bbdebug 1 "done with compression"

    cp "${_compressed_blob}" "${_blob_output_file}"

    return 0
}

