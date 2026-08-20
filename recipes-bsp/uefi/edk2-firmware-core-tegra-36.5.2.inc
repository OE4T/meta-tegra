LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRC_URI_EDK2 = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r36.5-updates"

SRCREV_edk2 = "30419afc4a6ea666434f9275057f579b46a07a5f"

SRC_URI = "${SRC_URI_EDK2};name=edk2;destsuffix=edk2-tegra/edk2"

S = "${WORKDIR}/edk2-tegra/edk2"

# XXX
# Remove do_qa_patch from the do_patch postfuncs list
# to avoid the potential for throwing an exception when
# the unimplemented-ptest checker walks the source tree
# and finds a directory named 'common.py' that it tries
# to process as a file.
# See https://github.com/OE4T/meta-tegra/issues/2201
# XXX
python() {
    patch_postfuncs = (d.getVarFlag('do_patch', 'postfuncs') or "").split()
    new_postfuncs = [f for f in patch_postfuncs if f != 'do_qa_patch']
    d.setVarFlag('do_patch', 'postfuncs', ' '.join(new_postfuncs))
}

