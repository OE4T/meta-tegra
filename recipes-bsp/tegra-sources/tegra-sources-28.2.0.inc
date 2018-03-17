SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r28_Release_v2.0/GA/BSP/${SRC_PREFIX}_sources.tbz2;downloadfilename=${SRC_PREFIX}_sources-28.2.0.tbz2"
SRC_PREFIX_tegra186 = "tx2"
SRC_PREFIX_tegra210 = "tx1"

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

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

python __anonymous () {
    soc = d.getVar('SOC_FAMILY') or ""
    if soc == "tegra186":
        d.setVarFlag("SRC_URI", "md5sum", "72f9d8dae8230d5cdc3fe1c28feaf414")
        d.setVarFlag("SRC_URI", "sha256sum", "733b324047a762a7c28e41322891fe7f7ecd2243004a03a8b45bc2bc9ea3ce30")
    elif soc == "tegra210":
        d.setVarFlag("SRC_URI", "md5sum", "de36b265c437e0a45286e12bc9610ab9")
        d.setVarFlag("SRC_URI", "sha256sum", "eb7cacbe0f5700748de59a59eba0f8df907b7c319ebc250b9b5d1bad03214e8a")
}

PR = "r0"
