require tegra-binaries-${PV}.inc

DRV_EXTRAS = ""
GLXEXT = "libglxserver_nvidia.so"

require xserver-xorg-video-nvidia.inc

# Starting with R28.1, we extract the xorg.conf fragment
# from the configs tarball
RDEPENDS_${PN} += "tegra-configs-xorg"
