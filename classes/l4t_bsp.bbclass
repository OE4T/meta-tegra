HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
L4T_BSP_NAME ??= "T186"
L4T_SRCS_NAME ??= "sources/T186"
L4T_BSP_PREFIX ??= "Jetson"

L4T_VERSION ?= "32.7.6"

L4T_BSP_DEB_DEFAULT_VERSION_T186 = "20241104234601"
L4T_BSP_DEB_DEFAULT_VERSION = "${L4T_BSP_DEB_DEFAULT_VERSION_T186}"
L4T_BSP_DEB_DEFAULT_VERSION:tegra210 = "20241104234540"

L4T_BSP_DEB_VERSION ?= "${L4T_BSP_DEB_DEFAULT_VERSION}"

# Version (date-time stamp) suffixes for nvidia-l4t-* packages
# in the package feeds.
L4T_BSP_DEB_ORIG_VERSION = ""
L4T_BSP_DEB_PACKAGES_USING_ORIG_VERSION = ""

def l4t_bsp_debian_version_suffix(d, pkgname=None):
    if pkgname is None:
        pkgname = d.getVar('L4T_DEB_TRANSLATED_BPN')
    if pkgname in [ 'nvidia-l4t-' + p for p in d.getVar('L4T_BSP_DEB_PACKAGES_USING_ORIG_VERSION').split()]:
        suffix = d.getVar('L4T_BSP_DEB_ORIG_VERSION')
    else:
        suffix = d.getVar('L4T_BSP_DEB_VERSION')
    return '-' + suffix if suffix else ''

def l4t_release_dir(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    return "r%s_Release_v%s.%s" % (verparts[0], verparts[1], verparts[2])

L4T_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_release_dir(d)}/${L4T_BSP_NAME}"
