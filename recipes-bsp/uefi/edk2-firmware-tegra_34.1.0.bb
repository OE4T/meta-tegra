require edk2-firmware.inc
require conf/image-uefi.conf

inherit deploy l4t_bsp

DESCRIPTION = "UEFI EDK2 Firmware for Jetson platforms"

# EDK2
LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"
# EDK2 Platforms - parallel to edk2 directory in NVIDIA tarball
LIC_FILES_CHKSUM += "file://../edk2-platforms/License.txt;md5=2b415520383f7964e96700ae12b4570a"

L4T_BSP_NAME = "${L4T_SRCS_NAME}"
SRC_URI = "${L4T_URI_BASE}/nvidia-l4t-jetson-uefi-source-${L4T_VERSION}.tbz2"
SRC_URI[sha256sum] = "e8d60a4da230e35700a5062cb4cec240c9186b154be41ec71bb8b361297b461e"

SRC_URI += "file://0001-Fix-VLA-parameter-warning.patch"
SRC_URI += "file://0002-Replace-libc-mem-calls-with-EDK2-defined-calls.patch"

COMPATIBLE_MACHINE = "(tegra)"
PROVIDES = "virtual/bootloader"

S = "${WORKDIR}/uefi/edk2"
S:task-patch = "${WORKDIR}/uefi"

EDK2_PLATFORM = "Jetson"
EDK2_PLATFORM_DSC = "Platform/NVIDIA/Jetson/Jetson.dsc"
S_EDK2_NVIDIA = "${@os.path.normpath(os.path.join(d.getVar('S'), '../edk2-nvidia'))}"

def nvidia_edk2_packages_path(d):
    return ':'.join([os.path.normpath(os.path.join(d.getVar('S'), '../{}'.format(m))) for m in ['edk2', 'edk2-platforms', 'edk2-nvidia', 'edk2-nvidia-non-osi']])

PACKAGES_PATH = "${@nvidia_edk2_packages_path(d)}"

EDK2_BIN_NAME = "uefi_jetson.bin"
EDK2_EXTRA_BUILD = '-D "BUILDID_STRING=v${PV}" -D "BUILD_DATE_TIME=${@format_build_date(d)}" -D "BUILD_PROJECT_TYPE=EDK2" -D "GENFW_FLAGS=--zero"'

def format_build_date(d):
    import datetime
    return datetime.datetime.fromtimestamp(int(d.getVar("SOURCE_DATE_EPOCH")), datetime.timezone.utc).replace(microsecond=0).isoformat()

do_compile:append() {
    rm -rf ${B}/images
    mkdir ${B}/images
    python3 ${S_EDK2_NVIDIA}/Silicon/NVIDIA/Tools/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/UEFI_NS.Fv \
        ${B}/images/${EDK2_BIN_NAME}
    PYTHONPATH=${S_EDK2_NVIDIA}/Silicon/NVIDIA/Tools python3 <<EOF
from GenVariableStore import GenVariableStore
GenVariableStore("${S_EDK2_NVIDIA}/Platform/NVIDIA/Jetson/JetsonVariablesDesc.json", "${B}/images/uefi_jetson_variables.bin")
EOF
    python3 ${S_EDK2_NVIDIA}/Silicon/NVIDIA/Tools/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/L4TLauncher.efi \
        ${B}/images/BOOTAA64.efi
}

do_install() {
    install -d ${D}${EFIDIR}
    install -m 0644 ${B}/images/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
}

PACKAGES = "l4t-launcher"
FILES:l4t-launcher = "${EFIDIR}"

do_deploy() {
    install -d ${DEPLOYDIR}
    for f in ${EDK2_BIN_NAME} uefi_jetson_variables.bin; do
        install ${B}/images/$f ${DEPLOYDIR}/
    done
}

addtask deploy before do_build after do_install
