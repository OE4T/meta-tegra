FILESEXTRAPATHS_prepend := "${THISDIR}/cmake:"
SRC_URI += "\
    file://findcuda-dont-reset-cflags.patch \
    file://0001-Fix-location-of-libcublas-in-CUDA-10.1-and-later.patch \
"
