require edk2-firmware.inc
require edk2-firmware-core-tegra-36.4.3.inc
require conf/image-uefi.conf

LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM += "file://../edk2-platforms/License.txt;md5=2b415520383f7964e96700ae12b4570a"
LIC_FILES_CHKSUM += "file://../edk2-nvidia/LICENSE;md5=52d8683e3d65a77ef84cc863e3f24f25"
LIC_FILES_CHKSUM += "file://../edk2-nvidia-non-osi/Silicon/NVIDIA/Drivers/NvGopDriver/NOTICE.nvgop-chips-platform.efi;md5=782048cadee3851a0da2b4a3fdae0d93"

inherit python3native

DEPENDS += "dtc-native python3-kconfiglib-native"

SRC_URI_EDK2_PLATFORMS = "git://github.com/NVIDIA/edk2-platforms.git;protocol=https;branch=r36.4.0-updates"
SRC_URI_EDK2_NVIDIA = "git://github.com/NVIDIA/edk2-nvidia.git;protocol=https;branch=r36.4.0-updates"
SRC_URI_EDK2_NONOSI = "git://github.com/NVIDIA/edk2-non-osi.git;protocol=https;branch=r36.4.0-updates"
SRC_URI_EDK2_NVIDIA_NONOSI = "git://github.com/NVIDIA/edk2-nvidia-non-osi.git;protocol=https;branch=r36.4.0-updates"

SRCREV_edk2-non-osi = "8c09bd0955338db38813e0d8ae1faa634f545f73"
SRCREV_edk2-nvidia = "5de7ef09811a06475248bf07661f7ae53e1c106d"
SRCREV_edk2-nvidia-non-osi = "7d8b0a5f9e3e9d6cc58ee7f3f4036cfc8f21970c"
SRCREV_edk2-platforms = "0b05eaed3d0c624a2084034fcc2bd0721809d041"

SRC_URI += "\
    ${SRC_URI_EDK2_PLATFORMS};name=edk2-platforms;destsuffix=edk2-tegra/edk2-platforms \
    ${SRC_URI_EDK2_NONOSI};name=edk2-non-osi;destsuffix=edk2-tegra/edk2-non-osi \
    ${SRC_URI_EDK2_NVIDIA};name=edk2-nvidia;destsuffix=edk2-tegra/edk2-nvidia \
    ${SRC_URI_EDK2_NVIDIA_NONOSI};name=edk2-nvidia-non-osi;destsuffix=edk2-tegra/edk2-nvidia-non-osi \
"

SRCREV_FORMAT = "edk2_edk2-platforms_edk2-non-osi_edk2-nvidia_edk2-nvidia-non-osi"

SRC_URI += "\
    file://0001-Update-tools_def.template-for-toolchain-differences.patch \
    file://0002-L4TLauncher-allow-for-empty-missing-APPEND-line-in-e.patch;patchdir=../edk2-nvidia \
    file://0003-XusbControllerDxe-use-BaseMemoryLib-functions.patch;patchdir=../edk2-nvidia \
    file://0004-MdePkg-Check-if-compiler-has-__has_builtin-before-tr.patch \
"

S = "${WORKDIR}/edk2-tegra/edk2"

COMPATIBLE_MACHINE = "(tegra)"

S_EDK2_NVIDIA = "${@os.path.normpath(os.path.join(d.getVar('S'), '../edk2-nvidia'))}"

# A bit of a hack here... S is the 'edk2' subtree, and all of the other
# subtrees are prefixed 'edk2', and this happens to work out nicely for
# the debug prefix mapping if we just put 'edk2' as last component of
# TARGET_DBGSRC_DIR.
TARGET_DBGSRC_DIR = "/usr/src/debug/${PN}/${PV}/edk2"

export DEBUG_PREFIX_MAP
# We've patched the tools_def template to pick ths flag up from the
# environment, so we don't end up with `date` commands inserted into
# makefiles for setting the RTC epoch.
export SOURCE_DATE_EPOCH_FLAG = "-DBUILD_EPOCH=${SOURCE_DATE_EPOCH}"

# NVIDIA has some additional tools_def definitions, which get added
# like this as part of the stuart build setup.
apply_tools_def_updates() {
    if [ -e ${S_EDK2_NVIDIA}/Silicon/NVIDIA/tools_def.inc ]; then
        echo "!include Silicon/NVIDIA/tools_def.inc" >> ${S}/Conf/tools_def.txt
    fi
}

# derived from edk2-nvidia/Silicon/NVIDIA/edk2nv/stuart/settings.py
def nvidia_edk2_packages_path(d):
    paths = ':'.join([os.path.normpath(os.path.join(d.getVar('S'), '../{}'.format(m))) for m in ['edk2/BaseTools', 'edk2', 'edk2-platforms', 'edk2-nvidia',
                                                                                                 'edk2-nvidia-non-osi', 'edk2-non-osi',
                                                                                                 'edk2-platforms/Features/Intel/OutOfBandManagement']])
    paths += ':' + os.path.normpath(os.path.join(d.getVar('B'), 'nvidia-config', 'Jetson'))
    return paths

do_configure[dirs] =+ "${B}/nvidia-config/Jetson"
do_compile[dirs] =+ "${B}/nvidia-config/Jetson"

PACKAGES_PATH = "${@nvidia_edk2_packages_path(d)}"

TEGRA_UEFI_MINIMAL ??= "0"
def nvidia_edk2_build_ids(d):
    if bb.utils.to_boolean(d.getVar('TEGRA_UEFI_MINIMAL')):
        return "-D BUILD_GUID=f98bcf32-fd20-4ba9-ada4-e0406947ca3c -DBUILD_NAME=JetsonMinimal"
    else:
        return "-D BUILD_GUID=49a79a15-8f69-4be7-a30c-a172f44abce7 -DBUILD_NAME=Jetson"

# BUILD_* taken from Silicon/NVIDIA/edk2nv/stuart/settings.py and
#                    builder.py, plus Platform/NVIDIA/Jetson/PlatformBuild.py
EDK2_EXTRA_BUILD = '-D "BUILDID_STRING=v${PV}" -D "BUILD_DATE_TIME=${@format_build_date(d)}" -D "BUILD_PROJECT_TYPE=EDK2" ${@nvidia_edk2_build_ids(d)}'

def format_build_date(d):
    import datetime
    return datetime.datetime.fromtimestamp(int(d.getVar("SOURCE_DATE_EPOCH")), datetime.timezone.utc).replace(microsecond=0).isoformat()
