NVIDIA_DEVNET_MIRROR ??= "file://NOTDEFINED"
HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
SRC_URI[vardepsexclude] += "NVIDIA_DEVNET_MIRROR"

python () {
    if d.getVar('NVIDIA_DEVNET_MIRROR') == 'file://NOTDEFINED':
        raise bb.parse.SkipRecipe("Recipe requires NVIDIA_DEVNET_MIRROR setup")
    if not d.getVar('NVIDIA_DEVNET_MIRROR').startswith('file://'):
        return
    # XXX
    # We don't want do_fetch's task signature to get changed if
    # the mirror is located locally vs. remotely.
    # XXX
    csfiles = d.getVarFlag('do_fetch', 'file-checksums').split()
    files = [os.path.basename(d.expand(uri.split(';')[0])) for uri in d.getVar('SRC_URI', False).split() \
                                                               if uri.startswith('${NVIDIA_DEVNET_MIRROR}')]
    for f in files:
        for csf in csfiles:
            if os.path.basename(csf.split(':')[0]) == f:
                csfiles.remove(csf)
    d.setVarFlag('do_fetch', 'file-checksums', ' '.join(csfiles))
}
