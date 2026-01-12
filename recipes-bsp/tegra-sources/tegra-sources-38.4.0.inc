L4T_BSP_NAME = "${L4T_SRCS_NAME}"
SRC_URI = "${L4T_URI_BASE}/public_sources.tbz2;downloadfilename=${L4T_BSP_PREFIX}-public_sources-${L4T_VERSION}.tbz2"
SRC_URI[sha256sum] = "6c8504c5e2d90b394ec223511ff892c4854ae615a955c166d3057a8d9abe54b6"

inherit l4t_bsp

unpack_tar_in_tar() {
    cd ${UNPACKDIR}
    for subarchive in ${TEGRA_SRC_SUBARCHIVE}; do
        tar -x -j -f ${SRC_ARCHIVE} $subarchive --to-command="tar -x -j --no-same-owner -f- ${TEGRA_SRC_SUBARCHIVE_OPTS}"
    done
    if [ -n "${TEGRA_SRC_EXTRA_SUBARCHIVE}" ]; then
        tar -x -j -f ${SRC_ARCHIVE} ${TEGRA_SRC_EXTRA_SUBARCHIVE} --to-command="tar -x -j --no-same-owner -f- ${TEGRA_SRC_EXTRA_SUBARCHIVE_OPTS}"
    fi
}

python do_unpack() {
    import shutil

    sourcedir = d.getVar('S')

    d.setVar("SOURCE_BASEDIR", sourcedir)

    src_uri = (d.getVar('SRC_URI') or "").split()

    basedir = None
    workdir = d.getVar('WORKDIR')
    unpackdir = d.getVar('UNPACKDIR')

    p_dir = os.path.join(sourcedir, 'patches')
    bb.utils.remove(p_dir, True)

    if sourcedir.startswith(workdir) and not sourcedir.startswith(unpackdir):
        basedir = sourcedir.replace(workdir, '').strip("/").split('/')[0]
        if basedir:
            bb.utils.remove(workdir + '/' + basedir, True)
            d.setVar("SOURCE_BASEDIR", workdir + '/' + basedir)

    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
    except bb.fetch2.BBFetchException as e:
        raise bb.build.FuncFailed(e)
    other_src_uris = []
    for s in src_uri:
        urldata = fetcher.ud[s]
        urldata.setup_localpath(d)
        if urldata.localpath.endswith(".tbz2"):
            d.setVar('SRC_ARCHIVE', urldata.localpath)
            bb.build.exec_func("unpack_tar_in_tar", d)
        else:
            other_src_uris.append(s)
    if other_src_uris:
        fetcher.unpack(d.getVar('UNPACKDIR'), urls=other_src_uris)

    if basedir and os.path.exists(unpackdir + '/' + basedir):
        shutil.move(unpackdir + '/' + basedir, workdir + '/' + basedir)
}

PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"
