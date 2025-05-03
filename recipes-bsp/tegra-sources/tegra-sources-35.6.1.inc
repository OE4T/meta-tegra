L4T_BSP_NAME = "${L4T_SRCS_NAME}"
SRC_URI = "${L4T_URI_BASE}/public_sources.tbz2;downloadfilename=${L4T_BSP_PREFIX}-public_sources-${L4T_VERSION}.tbz2"
SRC_URI[sha256sum] = "0b1d3077c3ca3487ae5b9cd66da9a611e1fb30097123265d9453d9975aee8b89"

inherit l4t_bsp

unpack_tar_in_tar() {
    cd ${WORKDIR}
    tar -x -j -f ${SRC_ARCHIVE} ${TEGRA_SRC_SUBARCHIVE} --to-command="tar -x -j --no-same-owner -f- ${TEGRA_SRC_SUBARCHIVE_OPTS}"
    if [ -n "${TEGRA_SRC_EXTRA_SUBARCHIVE}" ]; then
        tar -x -j -f ${SRC_ARCHIVE} ${TEGRA_SRC_EXTRA_SUBARCHIVE} --to-command="tar -x -j --no-same-owner -f- ${TEGRA_SRC_EXTRA_SUBARCHIVE_OPTS}"
    fi
}

python do_unpack() {
    src_uri = (d.getVar('SRC_URI') or "").split()
    rootdir = d.getVar('WORKDIR')
    p_dir = os.path.join(d.getVar('S'), 'patches')
    bb.utils.remove(p_dir, True)
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
        fetcher.unpack(d.getVar('WORKDIR'), urls=other_src_uris)
}

COMPATIBLE_MACHINE = "tegra"
PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"
