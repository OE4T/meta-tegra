L4T_BSP_NAME = "${L4T_SRCS_NAME}"
SRC_URI = "${L4T_URI_BASE}/public_sources.tbz2;downloadfilename=Jetson-public_sources-${L4T_VERSION}.tbz2"
SRC_URI[sha256sum] = "c6acf2c69ba3a2b4431b986a37d1145b40277a0c7bd9400cb430098e7e638526"

inherit l4t_bsp

unpack_tar_in_tar() {
    cd ${WORKDIR}
    tar -x -j -f ${SRC_ARCHIVE} ${TEGRA_SRC_SUBARCHIVE} --to-command="tar -x -j --no-same-owner -f- ${TEGRA_SRC_SUBARCHIVE_OPTS}"
    if [ -n "${TEGRA_SRC_EXTRA_SUBARCHIVE}" ]; then
        tar -x -j -f ${SRC_ARCHIVE} ${TEGRA_SRC_EXTRA_SUBARCHIVE} --to-command="tar -x -j --no-same-owner -f- ${TEGRA_SRC_EXTRA_SUBARCHIVE_OPTS}"
    fi
}

python do_unpack() {
    src_uri = (d.getVar('SRC_URI', True) or "").split()
    rootdir = d.getVar('WORKDIR', True)
    p_dir = os.path.join(d.getVar('S', True), 'patches')
    bb.utils.remove(p_dir, True)
    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
        urldata = fetcher.ud[src_uri[0]]
        urldata.setup_localpath(d)
    except bb.fetch2.BBFetchException as e:
        raise bb.build.FuncFailed(e)

    if urldata.localpath.endswith(".tbz2"):
        d.setVar('SRC_ARCHIVE', urldata.localpath)
        bb.build.exec_func("unpack_tar_in_tar", d)
}

COMPATIBLE_MACHINE = "tegra"
PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"
