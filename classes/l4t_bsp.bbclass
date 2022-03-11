HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
L4T_BSP_NAME ??= "T186"
L4T_SRCS_NAME ??= "Sources/T186"
L4T_BSP_PREFIX ??= "Jetson"

L4T_VERSION ?= "32.7.1"

# Version (date-time stamp) suffixes for nvidia-l4t-* packages
# in the package feeds.
#
# Original L4T R32.7.1 release
L4T_BSP_DEB_DEFAULT_VERSION_T186 = "20220219090344"
L4T_BSP_DEB_DEFAULT_VERSION = "${L4T_BSP_DEB_DEFAULT_VERSION_T186}"
L4T_BSP_DEB_DEFAULT_VERSION:tegra210 = "20220219090432"

L4T_BSP_DEB_VERSION ?= "${L4T_BSP_DEB_DEFAULT_VERSION}"

def l4t_bsp_debian_version_suffix(d):
    suffix = d.getVar('L4T_BSP_DEB_VERSION')
    return '-' + suffix if suffix else ''

def l4t_release_dir(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    return "r%s_Release_v%s.%s" % (verparts[0], verparts[1], verparts[2])

L4T_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_release_dir(d)}/${L4T_BSP_NAME}"
