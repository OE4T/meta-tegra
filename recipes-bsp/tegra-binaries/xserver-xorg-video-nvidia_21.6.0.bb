require tegra-binaries-${PV}.inc

DRV_EXTRAS = "usr/lib/arm-linux-gnueabihf/tegra/libglx.so"

require xserver-xorg-video-nvidia.inc

# We extract the xorg.conf fragment from the configs tarball
RDEPENDS_${PN} += "tegra-configs-xorg"

