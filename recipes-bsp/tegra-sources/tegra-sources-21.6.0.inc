SRC_URI = "http://developer2.download.nvidia.com/embedded/L4T/r21_Release_v6.0/r21.6.0-sources.tbz2;downloadfilename=tegra-sources-21.6.0.tbz2"
SRC_URI[md5sum] = "867dad3d856839141b3cc8edf5355b05"
SRC_URI[sha256sum] = "a379f9cd4ea8523049a5c44226e9837bfb22553b91e1fd8d180d3cf0b9ac75a1"

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
