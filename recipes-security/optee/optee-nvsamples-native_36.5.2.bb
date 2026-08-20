DESCRIPTION = "NVIDIA OP-TEE Tools"
HOMEPAGE = "https://developer.nvidia.com/embedded"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6938d70d5e5d49d31049419e85bb82f8"

require optee-l4t.inc

TEGRA_SRC_SUBARCHIVE_OPTS = "--strip-components=1 optee/samples"

S = "${WORKDIR}/samples"

COMPATIBLE_MACHINE = ""

inherit_defer native

INHIBIT_DEFAULT_DEPS = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/hwkey-agent/host/tool/gen_ekb/gen_ekb.py ${D}${bindir}
}

INHIBIT_SYSROOT_STRIP = "1"
