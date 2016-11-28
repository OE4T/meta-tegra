SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r24_Release_v2.1/BSP/sources_r24.2.1.tbz2;downloadfilename=tegra-sources-24.2.1.tbz2"
SRC_URI[md5sum] = "e371bec43cae9ed89e3a1fe990d4f203"
SRC_URI[sha256sum] = "a7465c84e3e5ff3bbeb81b98fdeb96f85f491aef3f1ed3bcb46f4df1344546b0"

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

COMPATIBLE_MACHINE = "(tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
