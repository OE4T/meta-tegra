SRC_URI = "http://developer2.download.nvidia.com/embedded/L4T/r28_Release_v1.0/BSP/source_release.tbz2;downloadfilename=tegra-sources-28.1.0.tbz2"
SRC_URI[md5sum] = "b13f3e54a49483674ae2ef77dab1e0e5"
SRC_URI[sha256sum] = "2d585b50acf6d8d86ed1a356531f20da953bca2d85e5227365f7823f59d088b3"

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

COMPATIBLE_MACHINE = "(jetsontx2|jetsontx1)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
