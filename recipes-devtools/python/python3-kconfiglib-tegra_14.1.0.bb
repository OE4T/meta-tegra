DESCRIPTION = "Kconfiglib is a Kconfig implementation in Python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=712177a72a3937909543eda3ad1bfb7c"

SRC_URI[md5sum] = "4ad68618824d4bad1d1de1d7eb838bba"
SRC_URI[sha256sum] = "bed2cc2216f538eca4255a83a4588d8823563cdd50114f86cf1a2674e602c93c"

PROVIDES = "python3-kconfiglib"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
    python3-curses \
    python3-shell \
    python3-tkinter \
"

python () {
    if 'meta-python' in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe("meta-python layer present, recipe not required")
}
