HOMEPAGE = "https://developer.nvidia.com/embedded/jetpack"
L4T_DEB_GROUP ?= ""
L4T_DEB_FEED_BASE ??= "https://repo.download.nvidia.com/jetson"

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
            result.append("${L4T_DEB_FEED_BASE}/%s/pool/main/%s/%s/%s" % (debclass, subdir, group, pkg))
        return result

    common_debs = (d.getVar('SRC_COMMON_DEBS') or '').split()
    soc_debs = (d.getVar('SRC_SOC_DEBS') or '').split()
    soc = d.getVar('L4T_DEB_SOCNAME')
    return ' '.join(generate_uris(d, 'common', common_debs) + generate_uris(d, soc, soc_debs))

l4t_deb_src_uri[vardepsexclude] += "L4T_DEB_SOCNAME"

SRC_URI = "${@l4t_deb_src_uri(d)}"
do_unpack[depends] += "zstd-native:do_populate_sysroot"

do_unpack[depends] += "tar-l4t-workaround-native:do_populate_sysroot"
EXTRANATIVEPATH_append_task-unpack = " tar-l4t-workaround-native"

do_unpack_prepend() {
    path = d.getVar('PATH')
    subpath = ':'.join([p for p in path.split(':') if 'tar-l4t-workaround-native' not in p])
    os.environ['TAR_WRAPPER_STRIPPED_PATH'] = subpath
    os.environ['PATH'] = path
}
