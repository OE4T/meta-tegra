HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
L4T_DEB_GROUP ?= ""
L4T_DEB_FEED_BASE ??= "https://repo.download.nvidia.com/jetson"

inherit l4t_bsp

L4T_DEB_COMP_DEFAULT = "main"
L4T_DEB_COMP ?= "${L4T_DEB_COMP_DEFAULT}"
L4T_X86_DEB_CLASS ??= "x86_64/focal"

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
            if d.getVar('L4T_DEB_FEED_SKIP_POOL_APPEND'):
                result.append("${L4T_DEB_FEED_BASE}/%s" % pkg)
            else:
                result.append("${L4T_DEB_FEED_BASE}/%s/pool/${L4T_DEB_COMP}/%s/%s/%s" % (debclass, subdir, group, pkg))
        return result

    common_debs = (d.getVar('SRC_COMMON_DEBS') or '').split()
    soc_debs = (d.getVar('SRC_SOC_DEBS') or '').split()
    if d.getVar('HOST_ARCH') == 'x86_64':
        return ' '.join(generate_uris(d, d.getVar('L4T_X86_DEB_CLASS'), common_debs + soc_debs))
    else:
        soc = d.getVar('L4T_DEB_SOCNAME')
        return ' '.join(generate_uris(d, 'common', common_debs) + generate_uris(d, soc, soc_debs))

def l4t_deb_pkgname(d, name):
    if not name.startswith('nvidia-l4t-'):
        name = 'nvidia-l4t-' + name
    return "%s_${L4T_VERSION}${@l4t_bsp_debian_version_suffix(d, pkgname='%s')}_arm64.deb" % (name, name)

l4t_deb_src_uri[vardepsexclude] += "L4T_DEB_SOCNAME"

SRC_URI = "${@l4t_deb_src_uri(d)}"
do_unpack[depends] += "zstd-native:do_populate_sysroot"

do_unpack[depends] += "tar-l4t-workaround-native:do_populate_sysroot"
EXTRANATIVEPATH:append:task-unpack = " tar-l4t-workaround-native"

do_unpack:prepend() {
    path = d.getVar('PATH')
    subpath = ':'.join([p for p in path.split(':') if 'tar-l4t-workaround-native' not in p])
    os.environ['TAR_WRAPPER_STRIPPED_PATH'] = subpath
    os.environ['PATH'] = path
}
