SRC_URI = "${L4T_URI_BASE}/Sources/public_sources.tbz2;downloadfilename=${L4T_BSP_PREFIX}-public_sources-${L4T_VERSION}.tbz2"
SRC_MD5SUM ="7d16574552f5c4ccc7d9b4c60dd9ab17"
SRC_SHA256SUM = "fdefe14164c5f563c0643006bd4af48ea4edad18befeed4d4b045a633c3687d1"
SRC_MD5SUM_tegra210 = "381c091328611d2851f7c0bfa7cce39b"
SRC_SHA256SUM_tegra210 = "1c614970bec3dd00286d0e5c971daabf880e7860df7f2f58c12e9027b0981764"
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
