require edk2-firmware.inc
require edk2-firmware-core-tegra-38.2.1.inc
require conf/image-uefi.conf

LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM += "file://../edk2-platforms/License.txt;md5=2b415520383f7964e96700ae12b4570a"
LIC_FILES_CHKSUM += "file://../edk2-nvidia/LICENSE;md5=52d8683e3d65a77ef84cc863e3f24f25"
LIC_FILES_CHKSUM += "file://../edk2-nvidia-non-osi/Silicon/NVIDIA/Drivers/NvGopDriver/NOTICE.nvgop-chips-platform.efi;md5=782048cadee3851a0da2b4a3fdae0d93"
LIC_FILES_CHKSUM += "file://../edk2-nvidia-non-osi/Silicon/NVIDIA/Drivers/NvGopDriver/NOTICE.nvgop-gb1xx-platform.efi;md5=9e5f17821be35eed9a8cd2c7fc05897d"

inherit python3native

DEPENDS += "dtc-native python3-kconfiglib-native"

SRC_URI_EDK2_PLATFORMS = "git://github.com/NVIDIA/edk2-platforms.git;protocol=https;branch=r38.2-updates"
SRC_URI_EDK2_NVIDIA = "git://github.com/NVIDIA/edk2-nvidia.git;protocol=https;branch=r38.2-updates"
SRC_URI_EDK2_NONOSI = "git://github.com/NVIDIA/edk2-non-osi.git;protocol=https;branch=r38.2-updates"
SRC_URI_EDK2_NVIDIA_NONOSI = "git://github.com/NVIDIA/edk2-nvidia-non-osi.git;protocol=https;branch=r38.2-updates"

SRCREV_edk2-non-osi = "889f9c90de53e7c8729d1f449b8781a5477f0df4"
SRCREV_edk2-nvidia = "899cdbc99a47b40721a6a074d966b4a00959412b"
SRCREV_edk2-nvidia-non-osi = "144b4ffcef44e0927d8d6d0c522bce9591544a6e"
SRCREV_edk2-platforms = "0dbe07c94b64c9ae8680637cf2302cadbf04bfb9"

SRC_URI += "\
    ${SRC_URI_EDK2_PLATFORMS};name=edk2-platforms;destsuffix=edk2-tegra/edk2-platforms \
    ${SRC_URI_EDK2_NONOSI};name=edk2-non-osi;destsuffix=edk2-tegra/edk2-non-osi \
    ${SRC_URI_EDK2_NVIDIA};name=edk2-nvidia;destsuffix=edk2-tegra/edk2-nvidia \
    ${SRC_URI_EDK2_NVIDIA_NONOSI};name=edk2-nvidia-non-osi;destsuffix=edk2-tegra/edk2-nvidia-non-osi \
"

SRCREV_FORMAT = "edk2_edk2-platforms_edk2-non-osi_edk2-nvidia_edk2-nvidia-non-osi"

SRC_URI += "\
    file://0001-Update-tools_def.template-for-toolchain-differences.patch \
    file://0002-MdePkg-BaseFdtLib-fix-build-with-gcc-15.patch \
    file://0003-L4TLauncher-allow-for-empty-missing-APPEND-line-in-e.patch;patchdir=../edk2-nvidia \
    file://0004-XusbControllerDxe-use-BaseMemoryLib-functions.patch;patchdir=../edk2-nvidia \
    file://0005-PrePi-use-GCC5-instead-of-GCC-in-build-options.patch;patchdir=../edk2-nvidia \
"

S = "${UNPACKDIR}/edk2-tegra/edk2"

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
                                                                                                 'edk2-platforms/Features',
                                                                                                 'edk2-platforms/Features/Intel/OutOfBandManagement']])
    paths += ':' + os.path.normpath(os.path.join(d.getVar('B'), 'nvidia-config', 'Tegra', '${EDK2_PLATFORM}'))
    return paths

do_configure[dirs] =+ "${B}/nvidia-config/Tegra/${EDK2_PLATFORM}"
do_compile[dirs] =+ "${B}/nvidia-config/Tegra/${EDK2_PLATFORM}"

PACKAGES_PATH = "${@nvidia_edk2_packages_path(d)}"

# BUILD_* taken from Silicon/NVIDIA/edk2nv/stuart/settings.py and
#                    builder.py, plus Platform/NVIDIA/Tegra/PlatformBuild.py
EDK2_EXTRA_BUILD = '-D "BUILDID_STRING=v${PV}" -D "BUILD_DATE_TIME=${@format_build_date(d)}" -D "BUILD_PROJECT_TYPE=EDK2"'
TEGRA_BUILD_GUID = '-D "BUILD_GUID=$(cat ${B}/nvidia-config/Tegra/${EDK2_PLATFORM}/PLATFORM_GUID)" -D "BUILD_NAME=${EDK2_PLATFORM}"'

def format_build_date(d):
    import datetime
    return datetime.datetime.fromtimestamp(int(d.getVar("SOURCE_DATE_EPOCH")), datetime.timezone.utc).replace(microsecond=0).isoformat()
