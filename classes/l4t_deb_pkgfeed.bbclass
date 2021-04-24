HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
L4T_DEB_GROUP ?= ""
L4T_DEB_FEED_BASE ??= "https://repo.download.nvidia.com/jetson"

inherit l4t_bsp

L4T_DEB_COMP_DEFAULT = "main"
L4T_DEB_COMP_DEFAULT_x86-64 = "r${@'.'.join(d.getVar('L4T_VERSION').split('.')[0:2])}"
L4T_DEB_COMP ?= "${L4T_DEB_COMP_DEFAULT}"

def l4t_deb_src_uri(d):
    def generate_uris(d, debclass, deblist):
        result = []
        for pkg in deblist:
            pkgelements = pkg.split(';')
            pkgbase = pkgelements[0].split('_')[0]
            name = None
            for pe in pkgelements:
                try:
                    tag, val = pe.split('=')
                    if tag == 'name':
                        name = val
                        break
                except ValueError:
                    pass
            group = None
            if name:
                group = d.getVarFlag('L4T_DEB_GROUP', name)
            group = group or d.getVar('L4T_DEB_GROUP') or pkgbase
            subdir = group[0:4] if group.startswith('lib') else group[0]
            result.append("${L4T_DEB_FEED_BASE}/%s/pool/${L4T_DEB_COMP}/%s/%s/%s" % (debclass, subdir, group, pkg))
        return result

    common_debs = (d.getVar('SRC_COMMON_DEBS') or '').split()
    soc_debs = (d.getVar('SRC_SOC_DEBS') or '').split()
    if d.getVar('HOST_ARCH') == 'x86_64':
        return ' '.join(generate_uris(d, 'x86_64', common_debs + soc_debs))
    else:
        soc = d.getVar('L4T_DEB_SOCNAME')
        return ' '.join(generate_uris(d, 'common', common_debs) + generate_uris(d, soc, soc_debs))

l4t_deb_src_uri[vardepsexclude] += "L4T_DEB_SOCNAME"

SRC_URI = "${@l4t_deb_src_uri(d)}"
