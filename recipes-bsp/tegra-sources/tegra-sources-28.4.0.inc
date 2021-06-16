
#SRC_URI = "https://developer.nvidia.com/embedded/L4T/r28_Release_v4.0/sources/T186/public_sources.tbz2;downloadfilename=public_sources-28.4.0.tbz2"
SRC_URI = "http://172.17.0.1/public_sources.tar.gz;downloadfilename=public_sources-28.4.0.tar.gz"

SRC_URI[md5sum] = "4e79a224fe00a7725d4fca6d1db58e46"
SRC_URI[sha256sum] = "bfb65816f387d55e4cbaf9911065d5587a1a3a358b8caf947974bc332e0bf59d"

unpack_tar_in_tar() {
    cd ${WORKDIR}
    tar -x -z -f ${SRC_ARCHIVE} ${TEGRA_SRC_SUBARCHIVE} --to-command="tar -x -j --no-same-owner -f-"
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

COMPATIBLE_MACHINE = "(tegra186)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
