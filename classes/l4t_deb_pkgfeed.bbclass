L4T_DEB_GROUP ?= "${BPN}"
L4T_DEB_FEED_BASE ??= "https://repo.download.nvidia.com/jetson"

def l4t_deb_src_uri(d):
    import string
    common_debs = (d.getVar('SRC_COMMON_DEBS') or '').split()
    soc_debs = (d.getVar('SRC_SOC_DEBS') or '').split()
    soc = d.getVar('L4T_DEB_SOCNAME')
    group = d.getVar('L4T_DEB_GROUP')
    if group.startswith('lib'):
        subdir = group[0:4]
        return ' '.join(["${L4T_DEB_FEED_BASE}/common/pool/main/%s/%s/%s" % (subdir, pkg.split('_')[0].rstrip(string.digits), pkg) for pkg in common_debs] +
                    ["${L4T_DEB_FEED_BASE}/%s/pool/main/%s/%s/%s" % (soc, subdir, pkg.split('_')[0].rstrip(string.digits), pkg) for pkg in soc_debs])
    subdir = group[0:4] if group.startswith('lib') else group[0]
    return ' '.join(["${L4T_DEB_FEED_BASE}/common/pool/main/%s/%s/%s" % (subdir, group, pkg) for pkg in common_debs] +
                    ["${L4T_DEB_FEED_BASE}/%s/pool/main/%s/%s/%s" % (soc, subdir, group, pkg) for pkg in soc_debs])

SRC_URI = "${@l4t_deb_src_uri(d)}"
