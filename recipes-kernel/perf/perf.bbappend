def kernel_disable_features(d):
    if bb.utils.vercmp_string(d.getVar('KERNEL_VERSION'), '4.17') < 0:
        # python3 is not supported until 4.17, so do not allow scripting
        return "scripting"
    else:
        return ""

PACKAGECONFIG:remove:tegra = "${@kernel_disable_features(d)}"
