UMMARY = "NVIDIA L4T sources"
DESCRIPTION = "Downloads NVIDIA L4T sources for sharing with other recipes"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://sha1sums.txt;md5=9564736e81ec5b6202455c58e8a4e7ce"

SRC_URI = "http://developer2.download.nvidia.com/embedded/L4T/r21_Release_v6.0/r21.6.0-sources.tbz2;downloadfilename=tegra-sources-21.6.0.tbz2;subdir=${PN}-${PV}"
SRC_URI[md5sum] = "867dad3d856839141b3cc8edf5355b05"
SRC_URI[sha256sum] = "a379f9cd4ea8523049a5c44226e9837bfb22553b91e1fd8d180d3cf0b9ac75a1"

WORKDIR = "${TMPDIR}/work-shared/tegra-sources-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:tegra-sources::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/tegra-sources-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/tegra-sources-${PV}-*"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = ""
PACKAGES = ""

COMPATIBLE_MACHINE = "(tegra124)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

deltask do_configure
deltask do_compile
deltask do_package
deltask do_package_write_rpm
deltask do_package_write_ipk
deltask do_package_write_deb
deltask do_install
deltask do_populate_sysroot
deltask do_package_qa
deltask do_packagedata
deltask do_rm_work

#unpack_tar_in_tar() {
#    cd ${WORKDIR}
#    tar -x -j -f ${SRC_ARCHIVE} ${TEGRA_SRC_SUBARCHIVE} --to-command="tar -x -j --no-same-owner -f-"
#}

#python do_unpack() {
#    src_uri = (d.getVar('SRC_URI', True) or "").split()
#    rootdir = d.getVar('WORKDIR', True)
#    p_dir = os.path.join(d.getVar('S', True), 'patches')
#    bb.utils.remove(p_dir, True)
#    try:
#        fetcher = bb.fetch2.Fetch(src_uri, d)
#        urldata = fetcher.ud[src_uri[0]]
#        urldata.setup_localpath(d)
#    except bb.fetch2.BBFetchException as e:
#        raise bb.build.FuncFailed(e)
#
#    d.setVar('SRC_ARCHIVE', urldata.localpath)
#    bb.build.exec_func("unpack_tar_in_tar", d)
#}

