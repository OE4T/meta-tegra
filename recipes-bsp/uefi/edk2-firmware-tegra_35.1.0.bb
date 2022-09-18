require edk2-firmware.inc
require conf/image-uefi.conf

inherit deploy

DESCRIPTION = "UEFI EDK2 Firmware for Jetson platforms"

LICENSE .= "& Proprietary"

LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"
LIC_FILES_CHKSUM += "file://../edk2-platforms/License.txt;md5=2b415520383f7964e96700ae12b4570a"
LIC_FILES_CHKSUM += "file://../edk2-nvidia-non-osi/Silicon/NVIDIA/Drivers/NvGopDriver/NOTICE.nvgop-chips-platform.efi;md5=549bbaa72578510a18ba3c324465027c"

DEPENDS += "dtc-native"
DEPENDS:append:tegra194 = " nvdisp-init"

EDK2_SRC_URI = "gitsm://github.com/NVIDIA/edk2.git;protocol=https;branch=rel-35-edk2-stable-202205"
EDK2_PLATFORMS_SRC_URI = "git://github.com/NVIDIA/edk2-platforms.git;protocol=https;branch=rel-35-upstream-20220208"
EDK2_NVIDIA_SRC_URI = "git://github.com/NVIDIA/edk2-nvidia.git;protocol=https;branch=rel-35"
EDK2_NONOSI_SRC_URI = "git://github.com/NVIDIA/edk2-non-osi.git;protocol=https;branch=rel-35-upstream-20220404"
EDK2_NVIDIA_NONOSI_SRC_URI = "git://github.com/NVIDIA/edk2-nvidia-non-osi.git;protocol=https;branch=rel-35"

# These correspond to the jetson-r35.1 tag in each repo
SRCREV_edk2 = "7d82301fd1b08a16e144ac0a038ef7352c4b570c"
SRCREV_edk2-non-osi = "7dcfcf88b8a99cc3ed381cb571d1f2e34e4734d4"
SRCREV_edk2-nvidia = "716592d2a8ec2c7c26eb9c80d1b3b845a6984317"
SRCREV_edk2-nvidia-non-osi = "6d6249728aeacbeaf9c7ed0cb029786c3a22c5f1"
SRCREV_edk2-platforms = "c9e377b00fc086fcb5a5b41663a0149bde9bcc2e"

SRC_URI = "\
    ${EDK2_SRC_URI};name=edk2;destsuffix=edk2-tegra/edk2;nobranch=1 \
    ${EDK2_PLATFORMS_SRC_URI};name=edk2-platforms;destsuffix=edk2-tegra/edk2-platforms \
    ${EDK2_NONOSI_SRC_URI};name=edk2-non-osi;destsuffix=edk2-tegra/edk2-non-osi \
    ${EDK2_NVIDIA_SRC_URI};name=edk2-nvidia;destsuffix=edk2-tegra/edk2-nvidia \
    ${EDK2_NVIDIA_NONOSI_SRC_URI};name=edk2-nvidia-non-osi;destsuffix=edk2-tegra/edk2-nvidia-non-osi \
"

SRCREV_FORMAT = "edk2_edk2-platforms_edk2-non-osi_edk2-nvidia_edk2-nvidia-non-osi"

SRC_URI += "file://0001-Fix-eeprom-customer-part-numbers.patch"
SRC_URI += "file://0002-Replace-libc-mem-calls-with-EDK2-defined-calls.patch"
SRC_URI += "file://0003-Disable-outline-atomics-in-eqos-driver.patch"

S = "${WORKDIR}/edk2-tegra/edk2"
S:task-patch = "${WORKDIR}/edk2-tegra"

COMPATIBLE_MACHINE = "(tegra)"

EDK2_PLATFORM = "Jetson"
EDK2_PLATFORM_DSC = "Platform/NVIDIA/Jetson/Jetson.dsc"
S_EDK2_NVIDIA = "${@os.path.normpath(os.path.join(d.getVar('S'), '../edk2-nvidia'))}"

# derived from edk2-nvidia/Silicon/NVIDIA/edk2nv/stuart/settings.py
def nvidia_edk2_packages_path(d):
    return ':'.join([os.path.normpath(os.path.join(d.getVar('S'), '../{}'.format(m))) for m in ['edk2/BaseTools', 'edk2', 'edk2-platforms', 'edk2-nvidia',
                                                                                                'edk2-nvidia-non-osi', 'edk2-non-osi',
                                                                                                'edk2-platforms/Features/Intel/OutOfBandManagement']])


PACKAGES_PATH = "${@nvidia_edk2_packages_path(d)}"

EDK2_BIN_NAME = "uefi_jetson.bin"
NVDISPLAY_INIT_DEFAULT = ""
NVDISPLAY_INIT_DEFAULT:tegra194 = "${DEPLOY_DIR_IMAGE}/nvdisp-init.bin"
NVDISPLAY_INIT ?= "${NVDISPLAY_INIT_DEFAULT}"
EDK2_EXTRA_BUILD = '-D "BUILDID_STRING=v${PV}" -D "BUILD_DATE_TIME=${@format_build_date(d)}" -D "BUILD_PROJECT_TYPE=EDK2" -D "GENFW_FLAGS=--zero"'

def format_build_date(d):
    import datetime
    return datetime.datetime.fromtimestamp(int(d.getVar("SOURCE_DATE_EPOCH")), datetime.timezone.utc).replace(microsecond=0).isoformat()

do_compile:append() {
    rm -rf ${B}/images
    mkdir ${B}/images
    python3 ${S_EDK2_NVIDIA}/Silicon/NVIDIA/Tools/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/UEFI_NS.Fv \
        ${B}/images/${EDK2_BIN_NAME}.tmp
    if [ -n "${NVDISPLAY_INIT}" ]; then
        cat "${NVDISPLAY_INIT}" ${B}/images/${EDK2_BIN_NAME}.tmp > ${B}/images/${EDK2_BIN_NAME}
	rm ${B}/images/${EDK2_BIN_NAME}.tmp
    else
	mv ${B}/images/${EDK2_BIN_NAME}.tmp ${B}/images/${EDK2_BIN_NAME}
    fi
    python3 ${S_EDK2_NVIDIA}/Silicon/NVIDIA/Tools/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/L4TLauncher.efi \
        ${B}/images/BOOTAA64.efi
    for f in ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/Silicon/NVIDIA/Tegra/DeviceTree/DeviceTree/OUTPUT/*.dtb; do
	[ -e "$f" ] || continue
	fbase=$(basename "$f" ".dtb")
	cp $f ${B}/images/$fbase.dtbo
    done
}

do_install() {
    install -d ${D}${EFIDIR}
    install -m 0644 ${B}/images/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
}

PACKAGES = "l4t-launcher"
FILES:l4t-launcher = "${EFIDIR}"
INSANE_SKIP:l4t-launcher = "buildpaths"

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/images/${EDK2_BIN_NAME} ${DEPLOYDIR}/
    for f in ${B}/images/*.dtbo; do
	[ -e "$f" ] || continue
	install -m 0644 $f ${DEPLOYDIR}/
    done
}

addtask deploy before do_build after do_install
