NVIDIA_DEVNET_MIRROR ??= "file://NOTDEFINED"

python () {
    if d.getVar('NVIDIA_DEVNET_MIRROR') == 'file://NOTDEFINED':
        raise bb.parse.SkipRecipe("Recipe requires NVIDIA_DEVNET_MIRROR setup")
}
