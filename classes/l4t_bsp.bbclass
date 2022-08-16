HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
L4T_BSP_NAME ??= "Release"
L4T_SRCS_NAME ??= "sources"
L4T_BSP_PREFIX ??= "Jetson"

L4T_VERSION ?= "35.1.0"

# Version (date-time stamp) suffixes for nvidia-l4t-* packages
# in the package feeds.
L4T_BSP_DEB_DEFAULT_VERSION = "20220810203728"
L4T_BSP_DEB_VERSION ?= "${L4T_BSP_DEB_DEFAULT_VERSION}"

def l4t_bsp_debian_version_suffix(d):
    suffix = d.getVar('L4T_BSP_DEB_VERSION')
    return '-' + suffix if suffix else ''

def l4t_release_dir(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    return "r%s_Release_v%s.%s" % (verparts[0], verparts[1], verparts[2])

L4T_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_release_dir(d)}/${L4T_BSP_NAME}"
