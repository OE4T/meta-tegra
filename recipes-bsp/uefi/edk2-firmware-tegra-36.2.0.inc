require edk2-firmware.inc
require edk2-firmware-core-tegra-36.2.0.inc
require conf/image-uefi.conf

LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM += "file://../edk2-platforms/License.txt;md5=2b415520383f7964e96700ae12b4570a"
LIC_FILES_CHKSUM += "file://../edk2-nvidia/LICENSE;md5=52d8683e3d65a77ef84cc863e3f24f25"
LIC_FILES_CHKSUM += "file://../edk2-nvidia-non-osi/Silicon/NVIDIA/Drivers/NvGopDriver/NOTICE.nvgop-chips-platform.efi;md5=549bbaa72578510a18ba3c324465027c"

inherit python3native

DEPENDS += "dtc-native python3-kconfiglib-native"

SRC_URI_EDK2_PLATFORMS = "git://github.com/NVIDIA/edk2-platforms.git;protocol=https;branch=r36.2-updates"
EDK2_NVIDIA_SRC_URI = "git://github.com/NVIDIA/edk2-nvidia.git;protocol=https;branch=r36.2-updates"
EDK2_NONOSI_SRC_URI = "git://github.com/NVIDIA/edk2-non-osi.git;protocol=https;branch=r36.2-updates"
EDK2_NVIDIA_NONOSI_SRC_URI = "git://github.com/NVIDIA/edk2-nvidia-non-osi.git;protocol=https;branch=r36.2-updates"

SRCREV_edk2-non-osi = "8c09bd0955338db38813e0d8ae1faa634f545f73"
SRCREV_edk2-nvidia = "9dbd33920c4020c514a664a272e32e51b5a579c4"
SRCREV_edk2-nvidia-non-osi = "07c97d7773a38b81d3e84016a1cc50470d908785"
SRCREV_edk2-platforms = "885476c282c5debfee3ba48289db2b5ab2637494"

SRC_URI += "\
    ${SRC_URI_EDK2_PLATFORMS};name=edk2-platforms;destsuffix=edk2-tegra/edk2-platforms \
    ${EDK2_NONOSI_SRC_URI};name=edk2-non-osi;destsuffix=edk2-tegra/edk2-non-osi \
    ${EDK2_NVIDIA_SRC_URI};name=edk2-nvidia;destsuffix=edk2-tegra/edk2-nvidia \
    ${EDK2_NVIDIA_NONOSI_SRC_URI};name=edk2-nvidia-non-osi;destsuffix=edk2-tegra/edk2-nvidia-non-osi \
"

SRCREV_FORMAT = "edk2_edk2-platforms_edk2-non-osi_edk2-nvidia_edk2-nvidia-non-osi"

# edk2-nvidia
#SRC_URI += "file://0002-Fix-RCM-boot-detection.patch;patchdir=../edk2-nvidia"
SRC_URI += "file://0003-L4TLauncher-allow-for-empty-missing-APPEND-line-in-e.patch;patchdir=../edk2-nvidia"

S = "${WORKDIR}/edk2-tegra/edk2"

COMPATIBLE_MACHINE = "(tegra)"

S_EDK2_NVIDIA = "${@os.path.normpath(os.path.join(d.getVar('S'), '../edk2-nvidia'))}"

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

EDK2_EXTRA_BUILD = '-D "BUILDID_STRING=v${PV}" -D "BUILD_DATE_TIME=${@format_build_date(d)}" -D "BUILD_PROJECT_TYPE=EDK2" -D "GENFW_FLAGS=--zero"'

def format_build_date(d):
    import datetime
    return datetime.datetime.fromtimestamp(int(d.getVar("SOURCE_DATE_EPOCH")), datetime.timezone.utc).replace(microsecond=0).isoformat()
