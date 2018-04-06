PACKAGECONFIG[glx] = "--enable-glx,--disable-glx,virtual/libgl"

EXTRA_TEGRA_RDEPENDS ?= ""
EXTRA_TEGRA_RDEPENDS_tegra = "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'mesa-stubs', '', d)}"

RDEPENDS_${PN} += "${EXTRA_TEGRA_RDEPENDS}"

inherit gtk-immodules-cache-tegra
