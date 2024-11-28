TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/atf_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.7.6.inc
S = "${WORKDIR}/arm-trusted-firmware"

def generate_build_string(d):
    pv = d.getVar('PV').split('-')
    if len(pv) > 1:
        return 'BUILD_STRING={}'.format('-'.join(pv[1:]))

BUILD_STRING ?= "${@generate_build_string(d)}"

require arm-trusted-firmware.inc
