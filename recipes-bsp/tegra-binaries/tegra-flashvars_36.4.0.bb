DESCRIPTION = "Machine-specific variables for tegraflash"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
B = "${WORKDIR}/build"

def generate_flashvar_settings(d):
    vars = sorted([v for v in d.getVar('TEGRA_FLASHVARS').split() if d.getVar('TEGRA_FLASHVAR_' + v)])
    need_subst = ' '.join([v for v in vars if '@' in d.getVar('TEGRA_FLASHVAR_' + v)])
    result = 'FLASHVARS="{}"\nOVERLAY_DTB_FILE="{}"\n'.format(need_subst, d.getVar('OVERLAY_DTB_FILE'))
    result += 'CHIPID={}\nPLUGIN_MANAGER_OVERLAYS="{}"\n'.format(d.getVar('NVIDIA_CHIP'), ','.join(d.getVar('TEGRA_PLUGIN_MANAGER_OVERLAYS').split()))
    flashvar_values = '\n'.join(['{}="{}"'.format(v, d.getVar('TEGRA_FLASHVAR_' + v)) for v in d.getVar('TEGRA_FLASHVARS').split() if d.getVar('TEGRA_FLASHVAR_' + v)])
    if flashvar_values:
        result += flashvar_values + '\n'
    result += '\n'.join(['CHECK_{}="{}"'.format(v, d.getVar('TEGRA_FLASH_CHECK_' + v)) for v in d.getVar('TEGRA_FLASH_CHECK_VARS').split() if d.getVar('TEGRA_FLASH_CHECK_' + v)])
    return result

INHIBIT_DEFAULT_DEPS = "1"

do_configure[noexec] = "1"

do_compile() {
    rm -f flashvars
    cat > flashvars <<EOF
${@generate_flashvar_settings(d)}
EOF
}
do_compile[vardeps] += "TEGRA_FLASHVARS ${@' '.join(['TEGRA_FLASHVAR_' + v for v in d.getVar('TEGRA_FLASHVARS').split()])}"
do_compile[vardeps] += "TEGRA_FLASH_CHECK_VARS ${@' '.join(['TEGRA_FLASH_CHECK_' + v for v in d.getVar('TEGRA_FLASH_CHECK_VARS').split()])}"

do_install() {
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${B}/flashvars ${D}${datadir}/tegraflash/
}

FILES:${PN} = "${datadir}/tegraflash"
PACKAGE_ARCH = "${MACHINE_ARCH}"
