require edk2-firmware.inc
require edk2-firmware-core-tegra-35.6.0.inc
require conf/image-uefi.conf

LICENSE .= " & Proprietary"

LIC_FILES_CHKSUM += "file://../edk2-platforms/License.txt;md5=2b415520383f7964e96700ae12b4570a"
LIC_FILES_CHKSUM += "file://../edk2-nvidia/LICENSE;md5=52d8683e3d65a77ef84cc863e3f24f25"
LIC_FILES_CHKSUM += "file://../edk2-nvidia-non-osi/Silicon/NVIDIA/Drivers/NvGopDriver/NOTICE.nvgop-chips-platform.efi;md5=549bbaa72578510a18ba3c324465027c"

DEPENDS += "dtc-native"

EDK2_PLATFORMS_SRC_URI = "git://github.com/NVIDIA/edk2-platforms.git;protocol=https;branch=r35.6.0-upstream-20220830-updates"
EDK2_NVIDIA_SRC_URI = "git://github.com/NVIDIA/edk2-nvidia.git;protocol=https;branch=r35.6.0-updates"
EDK2_NONOSI_SRC_URI = "git://github.com/NVIDIA/edk2-non-osi.git;protocol=https;branch=r35.6.0-upstream-20220830-updates"
EDK2_NVIDIA_NONOSI_SRC_URI = "git://github.com/NVIDIA/edk2-nvidia-non-osi.git;protocol=https;branch=r35.6.0-updates"

SRCREV_edk2-non-osi = "61662e8596dd9a64e3372f9a3ba6622d2628607c"
SRCREV_edk2-nvidia = "c101ba515b2737fb78d8929c2852f5c8f9607330"
SRCREV_edk2-nvidia-non-osi = "3a123f53db225accabca01889d97fe2db43aceeb"
SRCREV_edk2-platforms = "3c3b1168017073c2bb2d97336c5929ebae805be1"

SRC_URI += "\
    ${EDK2_PLATFORMS_SRC_URI};name=edk2-platforms;destsuffix=edk2-tegra/edk2-platforms \
    ${EDK2_NONOSI_SRC_URI};name=edk2-non-osi;destsuffix=edk2-tegra/edk2-non-osi \
    ${EDK2_NVIDIA_SRC_URI};name=edk2-nvidia;destsuffix=edk2-tegra/edk2-nvidia \
    ${EDK2_NVIDIA_NONOSI_SRC_URI};name=edk2-nvidia-non-osi;destsuffix=edk2-tegra/edk2-nvidia-non-osi \
"

SRCREV_FORMAT = "edk2_edk2-platforms_edk2-non-osi_edk2-nvidia_edk2-nvidia-non-osi"

# edk2-nvidia
SRC_URI += "file://0002-Fix-RCM-boot-detection.patch;patchdir=../edk2-nvidia"
SRC_URI += "file://0003-L4TLauncher-allow-for-empty-missing-APPEND-line-in-e.patch;patchdir=../edk2-nvidia"

# edk2
SRC_URI += "file://0001-Use-bfd-linker.patch;patchdir=../edk2"

# edk2-platforms
SRC_URI += "file://0004-Ext4Pkg-Advertise-CSUM_SEED-as-supported.patch;patchdir=../edk2-platforms"

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
