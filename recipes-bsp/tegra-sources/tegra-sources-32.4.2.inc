L4T_BSP_NAME = "${L4T_SRCS_NAME}"
SRC_URI = "${L4T_URI_BASE}/public_sources.tbz2;downloadfilename=${L4T_BSP_PREFIX}-public_sources-${L4T_VERSION}.tbz2"
SRC_SHA256SUM = "08e8cfd97fe72385207ae104869c9ca2add097dcf07970f000baf15b0b292c9e"
SRC_SHA256SUM_tegra210 = "37bbefacdeb382af5c1f2cbf3a4a035062bd32cda7f9d73cc6da42fdb1a21275"
SRC_URI[sha256sum] = "${SRC_SHA256SUM}"

inherit l4t_bsp

unpack_tar_in_tar() {
    cd ${WORKDIR}
    tar -x -j -f ${SRC_ARCHIVE} ${TEGRA_SRC_SUBARCHIVE} --to-command="tar -x -j --no-same-owner -f-"
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

    d.setVar('SRC_ARCHIVE', urldata.localpath)
    bb.build.exec_func("unpack_tar_in_tar", d)
}

COMPATIBLE_MACHINE = "tegra"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
