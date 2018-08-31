SRC_URI = "https://developer.download.nvidia.com/embedded/L4T/r21_Release_v7.0/r21.7.0-sources.tbz2;downloadfilename=tegra-sources-21.7.0.tbz2"
SRC_URI[md5sum] = "620d0979ffbbc8cbe8cb36faacee386b"
SRC_URI[sha256sum] = "4523ce03e18d2507a180a0b67e3ef136d4b73837f48063146c471fe575282440"

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

COMPATIBLE_MACHINE = "(tegra124)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
