LICENSE .= " AND LicenseRef-Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRC_URI_EDK2 = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=r39.2.1-updates"

SRCREV_edk2 = "5acf429321649bc191baf19de19670216a75cfb0"

SRC_URI = "${SRC_URI_EDK2};name=edk2;destsuffix=edk2-tegra/edk2"

S = "${UNPACKDIR}/edk2-tegra/edk2"

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
CVE_VERSION = "202505"

CVE_STATUS_GROUPS += "CVE_STATUS_EDK2_FIXED_UPSTREAM CVE_STATUS_EDK2_FIXED_INTREE CVE_STATUS_EDK2_DATE_BOUND CVE_STATUS_EDK2_X86"
CVE_STATUS_EDK2_FIXED_UPSTREAM = "\
    CVE-2024-1298 \
    CVE-2024-38796 \
    CVE-2024-38797 \
    CVE-2024-38805 \
    CVE-2025-2295 \
    CVE-2025-2296 \
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
    CVE-2026-97631 \
"
CVE_STATUS_EDK2_DATE_BOUND = "\
    CVE-2017-5731 \
    CVE-2019-14584 \
"
CVE_STATUS_EDK2_X86 = "CVE-2025-3770"

CVE_STATUS_EDK2_FIXED_UPSTREAM[status] = "fixed-version: CNA record bounds this below edk2-stable202505; NVD carries no CPE"
CVE_STATUS_EDK2_FIXED_INTREE[status] = "fixed-version: fix verified present in this source; neither NVD nor the CNA record carries a usable range"
CVE_STATUS_EDK2_DATE_BOUND[status] = "fixed-version: NVD bounds these with a release date, 2017-11-07 and 2020-10-21, which no comparison can apply to the 202505 tag"
CVE_STATUS_EDK2_X86[status] = "not-applicable-platform: x86-only UefiCpuPkg SMM code, which AArch64 Tegra does not build"
CVE_STATUS[CVE-2024-38798] = "vulnerable-investigating: fixed upstream in edk2-stable202511, which this 202505-based branch has not merged"
