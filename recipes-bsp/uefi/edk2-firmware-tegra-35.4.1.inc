require edk2-firmware.inc
require edk2-firmware-core-tegra-35.4.1.inc
require conf/image-uefi.conf

LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM += "file://../edk2-platforms/License.txt;md5=2b415520383f7964e96700ae12b4570a"
LIC_FILES_CHKSUM += "file://../edk2-nvidia-non-osi/Silicon/NVIDIA/Drivers/NvGopDriver/NOTICE.nvgop-chips-platform.efi;md5=549bbaa72578510a18ba3c324465027c"

DEPENDS += "dtc-native"

EDK2_PLATFORMS_SRC_URI = "git://github.com/NVIDIA/edk2-platforms.git;protocol=https;branch=uefi-202305.2-updates"
EDK2_NVIDIA_SRC_URI = "git://github.com/NVIDIA/edk2-nvidia.git;protocol=https;branch=uefi-202305.2-updates"
EDK2_NONOSI_SRC_URI = "git://github.com/NVIDIA/edk2-non-osi.git;protocol=https;branch=uefi-202305.2-updates"
EDK2_NVIDIA_NONOSI_SRC_URI = "git://github.com/NVIDIA/edk2-nvidia-non-osi.git;protocol=https;branch=uefi-202305.2-updates"

SRCREV_edk2-non-osi = "bc0b6bfe2d35c734c60704d89488e6587def830d"
SRCREV_edk2-nvidia = "a8d027b18dc3f66f74de0183fff904669a33aad5"
SRCREV_edk2-nvidia-non-osi = "2df330e8a24474262ad1805ae73e2f77520a9a70"
SRCREV_edk2-platforms = "885476c282c5debfee3ba48289db2b5ab2637494"

SRC_URI += "\
    ${EDK2_PLATFORMS_SRC_URI};name=edk2-platforms;destsuffix=edk2-tegra/edk2-platforms \
    ${EDK2_NONOSI_SRC_URI};name=edk2-non-osi;destsuffix=edk2-tegra/edk2-non-osi \
    ${EDK2_NVIDIA_SRC_URI};name=edk2-nvidia;destsuffix=edk2-tegra/edk2-nvidia \
    ${EDK2_NVIDIA_NONOSI_SRC_URI};name=edk2-nvidia-non-osi;destsuffix=edk2-tegra/edk2-nvidia-non-osi \
"

SRCREV_FORMAT = "edk2_edk2-platforms_edk2-non-osi_edk2-nvidia_edk2-nvidia-non-osi"

SRC_URI += "file://0002-Disable-outline-atomics-in-eqos-driver.patch;patchdir=.."
SRC_URI += "file://0003-Fix-RCM-boot-detection.patch;patchdir=.."
SRC_URI += "file://0004-L4TLauncher-allow-for-empty-missing-APPEND-line-in-e.patch;patchdir=.."
SRC_URI += "file://0005-Use-bfd-linker.patch;patchdir=.."

S = "${WORKDIR}/edk2-tegra/edk2"

COMPATIBLE_MACHINE = "(tegra)"

S_EDK2_NVIDIA = "${@os.path.normpath(os.path.join(d.getVar('S'), '../edk2-nvidia'))}"

# derived from edk2-nvidia/Silicon/NVIDIA/edk2nv/stuart/settings.py
def nvidia_edk2_packages_path(d):
    return ':'.join([os.path.normpath(os.path.join(d.getVar('S'), '../{}'.format(m))) for m in ['edk2/BaseTools', 'edk2', 'edk2-platforms', 'edk2-nvidia',
                                                                                                'edk2-nvidia-non-osi', 'edk2-non-osi',
                                                                                                'edk2-platforms/Features/Intel/OutOfBandManagement']])


PACKAGES_PATH = "${@nvidia_edk2_packages_path(d)}"

EDK2_EXTRA_BUILD = '-D "BUILDID_STRING=v${PV}" -D "BUILD_DATE_TIME=${@format_build_date(d)}" -D "BUILD_PROJECT_TYPE=EDK2" -D "GENFW_FLAGS=--zero"'

def format_build_date(d):
    import datetime
    return datetime.datetime.fromtimestamp(int(d.getVar("SOURCE_DATE_EPOCH")), datetime.timezone.utc).replace(microsecond=0).isoformat()
