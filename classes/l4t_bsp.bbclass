HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
L4T_BSP_NAME ??= "T186"
L4T_SRCS_NAME ??= "sources/T186"
L4T_BSP_PREFIX ??= "Tegra186"

L4T_VERSION ?= "32.5.1"
L4T_ALT_VERSION ?= "32.5.0"

def l4t_release_dir(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    return "r%s_Release_v%s.%s" % (verparts[0], verparts[1], verparts[2])

def l4t_alt_release_dir(d):
    verparts = d.getVar('L4T_ALT_VERSION').split('.')
    return "r%s_Release_v%s.%s" % (verparts[0], verparts[1], verparts[2])

L4T_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_release_dir(d)}/${@l4t_release_dir(d)}/${L4T_BSP_NAME}"
L4T_ALT_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_alt_release_dir(d)}/${L4T_BSP_NAME}"
