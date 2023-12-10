DESCRIPTION = "Out-of-tree kernel modules and device trees for Jetson Linux platforms"
LICENSE = "GPL-2.0-only & BSD-3-Clause & (MIT | GPL-2.0-only)"
LIC_FILES_CHKSUM = "file://nvdisplay/COPYING;md5=1d5fa2a493e937d5a4b96e5e03b90f7c \
                    file://Makefile;beginline=2;endline=2;md5=a670216ac93e92dc066b1a876610705f \
		    file://nvidia-oot/Makefile;endline=1;md5=daad6f7f7a0a286391cd7773ccf79340"

inherit module deploy

TEGRA_SRC_SUBARCHIVE = "\
    Linux_for_Tegra/source/kernel_oot_modules_src.tbz2 \
    Linux_for_Tegra/source/nvidia_kernel_display_driver_source.tbz2 \
"
TEGRA_SRC_SUBARCHIVE_OPTS = "-C ${S}"
TEGRA_SRC_EXTRA_SUBARCHIVE = "Linux_for_Tegra/source/kernel_src.tbz2"
TEGRA_SRC_EXTRA_SUBARCHIVE_OPTS = "-C ${S} hardware/nvidia"
require recipes-bsp/tegra-sources/tegra-sources-36.2.0.inc

do_unpack[depends] += "tegra-binaries:do_preconfigure"

unpack_makefile_from_bsp() {
    cp ${L4T_BSP_SHARED_SOURCE_DIR}/source/Makefile ${S}/
}
do_unpack[postfuncs] += "unpack_makefile_from_bsp"

SRC_URI += "file://0001-Makefile-update-for-OE-builds.patch"

COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}/${BPN}"
B = "${S}"

PROVIDES = "virtual/dtb"

EXTRA_OEMAKE += '\
    IGNORE_PREEMPT_RT_PRESENCE=1 \
    CC="${KERNEL_CC}" CXX="${KERNEL_CC} -x c++" LD="${KERNEL_LD}" AR="${KERNEL_AR}" \
    OBJCOPY="${KERNEL_OBJCOPY}" \
'

do_compile() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake modules
    oe_runmake dtbs
}

do_install() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake MODLIB="${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}" modules_install
    install -d ${D}/boot/devicetree
    install -m 0644 ${B}/nvidia-oot/device-tree/platform/generic-dts/dtbs/* ${D}/boot/devicetree/
    install -d ${D}${includedir}/${BPN}
    find ${B} -name Module.symvers -type f | xargs sed -e's:${B}/::g' >${D}${includedir}/${BPN}/Module.symvers
}

do_deploy() {    
    install -d ${DEPLOYDIR}/devicetree
    install -m 0644 ${B}/nvidia-oot/device-tree/platform/generic-dts/dtbs/* ${DEPLOYDIR}/devicetree/
}

addtask deploy before do_build after do_install

SYSROOT_DIRS += "/boot/devicetree"

KERNEL_MODULE_PROBECONF = "nvidia nvgpu"
module_conf_nvidia = 'options nvidia rm_firmware_active="all" NVreg_RegistryDwords="RMHdcpKeyglobZero=1"'
module_conf_nvgpu = 'options nvgpu devfreq_timer="delayed"'

PACKAGES =+ "${PN}-devicetrees"
FILES:${PN}-devicetrees = "/boot/devicetree"

PACKAGE_ARCH = "${MACHINE_ARCH}"
