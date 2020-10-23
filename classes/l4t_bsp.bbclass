HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
L4T_BSP_NAME ??= "T186"
L4T_SRCS_NAME ??= "Sources/T186"
L4T_BSP_PREFIX ??= "Tegra186"

L4T_VERSION ?= "32.4.4"

def l4t_release_dir(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    base = "r%s_Release_v%s.%s" % (verparts[0], verparts[1], verparts[2])
    return base + "/" + base + "-GMC3"

L4T_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_release_dir(d)}/${L4T_BSP_NAME}"
