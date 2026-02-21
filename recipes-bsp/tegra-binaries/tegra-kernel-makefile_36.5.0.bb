require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

do_compile() {
    :
}
do_install() {
	install -m 0644 -D -t ${D}${datadir}/nvidia-kernel ${S}/source/Makefile
}

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev = "${datadir}"

