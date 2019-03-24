SRC_URI = "${L4T_URI_BASE}/${L4T_BSP_BASEPFX}-public_sources.tbz2;downloadfilename=${L4T_BSP_BASEPFX}-public_sources-32.1.0.tbz2"
SRC_MD5SUM = "8943880af9e731a4fa17f3bb7829aca7"
SRC_SHA256SUM = "b9fc4d07b39fcbe107cf496b0bc07e4e6ccaaaa67d0f0e21d004066ff34239c2"
SRC_MD5SUM_tegra210 = "bbd8350dea8dc061b40571c6fa7d3749"
SRC_SHA256SUM_tegra210 = "b9d7b277ee0cd3910a1344eadf4928552470eb38269d1bf431a9040fb2c945f5"
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
