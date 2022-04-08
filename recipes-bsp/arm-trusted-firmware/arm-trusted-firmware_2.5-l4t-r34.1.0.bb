TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/atf_src.tbz2"
<<<<<<<< HEAD:recipes-bsp/arm-trusted-firmware/arm-trusted-firmware_1.3-l4t-32.7.2.bb
require recipes-bsp/tegra-sources/tegra-sources-32.7.2.inc
========
require recipes-bsp/tegra-sources/tegra-sources-34.1.0.inc
>>>>>>>> 7f7ae890 (arm-trusted-firmware: update for R34.1.0):recipes-bsp/arm-trusted-firmware/arm-trusted-firmware_2.5-l4t-r34.1.0.bb
S = "${WORKDIR}/arm-trusted-firmware"

def generate_build_string(d):
    pv = d.getVar('PV').split('-')
    if len(pv) > 1:
        return 'BUILD_STRING={}'.format('-'.join(pv[1:]))

BUILD_STRING ?= "${@generate_build_string(d)}"

require arm-trusted-firmware.inc
