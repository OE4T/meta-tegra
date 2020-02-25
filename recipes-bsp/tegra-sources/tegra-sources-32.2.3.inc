L4T_BSP_NAME = "${L4T_SRCS_NAME}"
SRC_URI = "${L4T_URI_BASE}/public_sources.tbz2;downloadfilename=${L4T_BSP_PREFIX}-public_sources-${PV}.tbz2"
SRC_MD5SUM ="5031232bf0231e0a5c1c250a95f9feba"
SRC_SHA256SUM = "8772eddf47027186b5ef8770acd2dc6eecb96d6a8badca20a1cf0c6a8fa2db8e"
SRC_MD5SUM_tegra210 = "96e592ef7f0bb94b900b98d6bf014872"
SRC_SHA256SUM_tegra210 = "676b4c04ad2cca0ed691215b53f8528bd14d09b47c73528a67fd63068369a67b"
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
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
