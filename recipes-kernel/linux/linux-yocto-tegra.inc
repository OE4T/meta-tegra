require ${@'tegra-kernel.inc' if 'tegra' in d.getVar('MACHINEOVERRIDES').split(':') else ''}

# SoC / devkit kernel config now lives entirely in the tegra-kernel-cache kmeta
# (cfg/tegra.cfg shared, cfg/<machine>.cfg overlays, cfg/jetson-devkit.cfg for
# board peripherals) — not in recipe .cfg fragments.

# Shared tegra-kernel-cache kmeta (all tegra machines): vendor patch queues plus
# a per-machine BSP (selected by KMACHINE) that inherits genericarm64.
TEGRA_KERNEL_CACHE_URI ?= "git://github.com/OE4T/tegra-kernel-cache.git;protocol=https;branch=yocto-6.18"
SRC_URI:append:tegra = " \
    ${TEGRA_KERNEL_CACHE_URI};name=tegrameta;type=kmeta;destsuffix=tegra-meta \
"
SRCREV_FORMAT:tegra = "meta_machine_tegrameta"
SRCREV_tegrameta ?= "a78c980f4c0f9aec1ccc9a176fcdd5516be2faa5"

COMPATIBLE_MACHINE:tegra = "(tegra234|tegra264)"
KMACHINE:tegra = "${SOC_FAMILY}"

# Devkit board peripherals (Wi-Fi/Ethernet/sensors/codec). Overridable: a custom
# carrier board drops them with KERNEL_FEATURES_EXTRA_DEVKIT = "" in its machine
# conf, or points it at a different board feature.
KERNEL_FEATURES_EXTRA_DEVKIT ?= "features/tegra/jetson-devkit.scc"

KERNEL_FEATURES:append:tegra = " \
    features/tegra/tegra-patches.scc \
    features/tegra/tegra-distro.scc \
    ${KERNEL_FEATURES_EXTRA_DEVKIT} \
"
