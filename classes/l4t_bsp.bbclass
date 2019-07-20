L4T_BSP_NAME ??= "TX2-AGX"
L4T_BSP_PREFIX ??= "Jetson"

L4T_VERSION ?= "32.2.0"

def l4t_release_dir(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    return "r%s-%s_Release_v1.0" % (verparts[0], verparts[1])

L4T_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_release_dir(d)}/${L4T_BSP_NAME}"
