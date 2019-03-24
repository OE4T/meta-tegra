L4T_BSP_NAME ??= "jax-tx2"
L4T_BSP_BASEPFX ??= "JAX-TX2"
L4T_BSP_PREFIX ??= "${L4T_BSP_BASEPFX}-Tegra"

L4T_VERSION ?= "32.1.0"

def l4t_release_dir(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    return "r%s_Release_v%s.%s" % tuple(verparts)

L4T_URI_BASE ?= "https://developer.download.nvidia.com/embedded/L4T/${@l4t_release_dir(d)}/${L4T_BSP_NAME}/BSP"
