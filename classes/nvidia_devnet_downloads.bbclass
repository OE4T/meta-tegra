NVIDIA_DEVNET_MIRROR ??= "file://NOTDEFINED"
SRC_URI[vardepsexclude] += "NVIDIA_DEVNET_MIRROR"

python () {
    if d.getVar('NVIDIA_DEVNET_MIRROR') == 'file://NOTDEFINED':
        raise bb.parse.SkipRecipe("Recipe requires NVIDIA_DEVNET_MIRROR setup")
}
