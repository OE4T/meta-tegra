SUMMARY = "NVIDIA firmware and drivers"
DESCRIPTION = "NVIDIA L4T proprietary binary-only firmware and drivers"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://nv_tegra/LICENSE;md5=60ad17cc726658e8cf73578bea47b85f \
                    file://nv_tegra/LICENSE.brcm_patchram_plus;md5=38fb07f0dacf4830bc57f40a0fb7532e"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r23_Release_v1.0/Tegra210_Linux_R${PV}_armhf.tbz2"
SRC_URI[md5sum] = "4773c4aff47f464f0a6eb2a8342b05e7"
SRC_URI[sha256sum] = "aacb0b1e9571df43621a6fd32101f8f52f9387ecb84e96930fb5682a68676c15"

COMPATIBLE_MACHINE = "(jetson-tx1)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

B = "${WORKDIR}/build"
S = "${WORKDIR}/Linux_for_Tegra"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 lib/firmware usr/sbin
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}/lib
    cp -R -f ${B}/lib/firmware ${D}/lib/
    install -d ${D}${sbindir}
    install -m 0755 ${B}/usr/sbin/brcm_patchram_plus ${D}${sbindir}
}

PACKAGES = "${PN}-brcm ${PN}-brcm-patchram ${PN}"
FILES_${PN}-brcm = "/lib/firmware/brcm /lib/firmware/bcm4354.hcd"
FILES_${PN}-brcm-patchram = "${sbindir}/brcm_patchram_plus"
FILES_${PN} = "/lib/firmware/tegra21x*"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
