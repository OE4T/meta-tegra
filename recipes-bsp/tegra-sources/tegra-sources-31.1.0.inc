SRC_URI = "https://developer.download.nvidia.com/embedded/L4T/r31_Release_v1.0/BSP/public_sources.tbz2;downloadfilename=public_sources-31.1.0.tbz2"
SRC_URI[md5sum] = "6d50dbf1616efb2c373763410938a2a5"
SRC_URI[sha256sum] = "cc9137afbba71f9acc1e85d1b25186c8bef730dc6b225690e7c86bef0375d2b3"

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

COMPATIBLE_MACHINE = "(tegra186|tegra194)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
