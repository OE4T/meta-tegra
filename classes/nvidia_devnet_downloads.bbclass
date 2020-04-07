NVIDIA_DEVNET_MIRROR ??= "file://NOTDEFINED"
SRC_URI[vardepsexclude] += "NVIDIA_DEVNET_MIRROR"
NVIDIA_DEVNET_OPTIONAL ??= "0"

python () {
    if bb.utils.to_boolean(d.getVar('NVIDIA_DEVNET_OPTIONAL', False)):
        if d.getVar('NVIDIA_DEVNET_MIRROR') == 'file://NOTDEFINED':
            d.setVar('HAVE_DEVNET_MIRROR', '0')
            return
    if d.getVar('NVIDIA_DEVNET_MIRROR') == 'file://NOTDEFINED':
        raise bb.parse.SkipRecipe("Recipe requires NVIDIA_DEVNET_MIRROR setup")
    d.setVar('HAVE_DEVNET_MIRROR', '1')
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
