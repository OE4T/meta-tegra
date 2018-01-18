DEPENDS += "tegra-sources"

WSDIR = "${TMPDIR}/work-shared/tegra-sources-21.6.0-r0/tegra-sources-21.6.0/"
TEGRA_SRC_SUBARCHIVE ?= "" 
TEGRA_DST ?= "${WORKDIR}"

do_fetch_tar() {
  tar -x -C ${TEGRA_DST} -f ${WSDIR}/${TEGRA_SRC_SUBARCHIVE}
}

addtask fetch_tar after do_unpack before do_patch

