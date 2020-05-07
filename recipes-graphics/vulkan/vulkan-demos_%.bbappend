# Tegra Vulkan support is not available for Wayland
ANY_OF_DISTRO_FEATURES_tegra = ""
REQUIRED_DISTRO_FEATURES_append_tegra = " x11"
PACKAGECONFIG_tegra = "xcb"

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
