SRC_URI = "${L4T_URI_BASE}/public_sources.tbz2;downloadfilename=${L4T_BSP_PREFIX}-public_sources-${L4T_VERSION}.tbz2"
SRC_MD5SUM ="4dff842987db945d41921c143cdcd9bb"
SRC_SHA256SUM = "8260b07ef2fa8a70bbe455137776044a18d4612e833d8b478ff17dd9c511f2b1"
SRC_MD5SUM_tegra210 = "fea40b4464e142b1897533919dbeb4f6"
SRC_SHA256SUM_tegra210 = "298286ff30adbec16bfeb65b6f2dea56733eb87afd78295f08999d65d1cafb38"
SRC_URI[md5sum] = "${SRC_MD5SUM}"
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
COMPATIBLE_MACHINE_tegra124 = "(-)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
