LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRC_URI_EDK2 = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r36.5.1-updates"

SRCREV_edk2 = "c80eba3cb8aafaac243f45434f6d3a42a4145ed3"

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

CVE_PRODUCT = "tianocore:edk2"
CVE_VERSION = "202408"

CVE_STATUS_GROUPS += "CVE_STATUS_EDK2_FIXED_UPSTREAM CVE_STATUS_EDK2_FIXED_INTREE CVE_STATUS_EDK2_DATE_BOUND CVE_STATUS_EDK2_X86"
CVE_STATUS_EDK2_FIXED_UPSTREAM = "\
    CVE-2024-1298 \
"
CVE_STATUS_EDK2_FIXED_INTREE = "\
    CVE-2014-4859 \
    CVE-2014-4860 \
    CVE-2014-8271 \
    CVE-2019-14553 \
    CVE-2019-14559 \
    CVE-2019-14562 \
    CVE-2019-14563 \
    CVE-2019-14575 \
    CVE-2019-14586 \
    CVE-2019-14587 \
"
CVE_STATUS_EDK2_DATE_BOUND = "\
    CVE-2017-5731 \
    CVE-2019-14584 \
"
CVE_STATUS_EDK2_X86 = "CVE-2025-3770"

CVE_STATUS_EDK2_FIXED_UPSTREAM[status] = "fixed-version: fixed in edk2-stable202405 (284dbac43d), which this source includes; NVD carries no CPE"
CVE_STATUS_EDK2_FIXED_INTREE[status] = "fixed-version: fix verified present in this source; neither NVD nor the CNA record carries a usable range"
CVE_STATUS_EDK2_DATE_BOUND[status] = "fixed-version: fixes 2ec7953d49, 041d89bc0f and 26442d11e6 are in this source; NVD bounds these with a release date, not a version"
CVE_STATUS_EDK2_X86[status] = "not-applicable-platform: x86-only UefiCpuPkg SMM code, which AArch64 Tegra does not build"
CVE_STATUS[CVE-2024-38798] = "vulnerable-investigating: fixed upstream in edk2-stable202511, which this 202408-based branch has not merged"
