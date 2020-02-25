L4T_BSP_NAME ??= "t186ref_release_aarch64"
L4T_SRCS_NAME ??= "Sources/T186"
L4T_BSP_PREFIX ??= "Tegra186"

L4T_VERSION ?= "32.3.1"

def l4t_release_dir(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    if int(verparts[2]) == 0:
        return "r%s-%s_Release_v1.0" % (verparts[0], verparts[1])
    else:
        return "r%s-%s-%s_Release_v1.0" % (verparts[0], verparts[1], verparts[2])

L4T_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_release_dir(d)}/${L4T_BSP_NAME}"
