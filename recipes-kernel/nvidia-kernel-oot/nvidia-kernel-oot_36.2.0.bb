DESCRIPTION = "Out-of-tree kernel modules and device trees for Jetson Linux platforms"
LICENSE = "GPL-2.0-only & BSD-3-Clause & (MIT | GPL-2.0-only)"
LIC_FILES_CHKSUM = "file://nvdisplay/COPYING;md5=1d5fa2a493e937d5a4b96e5e03b90f7c \
                    file://Makefile;beginline=2;endline=2;md5=a670216ac93e92dc066b1a876610705f \
		    file://nvidia-oot/Makefile;endline=1;md5=daad6f7f7a0a286391cd7773ccf79340"

inherit module deploy
inherit ${TEGRA_UEFI_SIGNING_CLASS}

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

SRC_URI += "file://0001-Makefile-update-for-OE-builds.patch \
            file://0002-Fix-nvdisplay-modules-builds.patch"

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

do_sign_dtbs() {
    for dtbf in ${KERNEL_DEVICETREE}; do
        tegra_uefi_attach_sign "${B}/nvidia-oot/device-tree/platform/generic-dts/dtbs/${dtbf}"
    done
}
do_sign_dtbs[dirs] = "${B}"
do_sign_dtbs[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"

addtask sign_dtbs after do_compile before do_install

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

PACKAGES =+ "${PN}-devicetrees ${PN}-display ${PN}-cameras ${PN}-bluetooth ${PN}-wifi ${PN}-canbus ${PN}-virtualization ${PN}-alsa ${PN}-test ${PN}-base"
FILES:${PN}-devicetrees = "/boot/devicetree"
ALLOW_EMPTY:${PN}-display = "1"
ALLOW_EMPTY:${PN}-cameras = "1"
ALLOW_EMPTY:${PN}-bluetooth = "1"
ALLOW_EMPTY:${PN}-wifi = "1"
ALLOW_EMPTY:${PN}-canbus = "1"
ALLOW_EMPTY:${PN}-virtualization = "1"
ALLOW_EMPTY:${PN}-alsa = "1"
ALLOW_EMPTY:${PN}-test = "1"
ALLOW_EMPTY:${PN}-base = "1"

RDEPENDS:${PN}-display = "${TEGRA_OOT_DISPLAY_DRIVERS}"
RDEPENDS:${PN}-cameras = "${TEGRA_OOT_CAMERA_DRIVERS}"
RDEPENDS:${PN}-bluetooth = "${TEGRA_OOT_BLUETOOTH_DRIVERS}"
RDEPENDS:${PN}-wifi = "${TEGRA_OOT_WIFI_DRIVERS}"
RDEPENDS:${PN}-canbus = "${TEGRA_OOT_CANBUS_DRIVERS}"
RDEPENDS:${PN}-virtualization = "${TEGRA_OOT_VIRTUALIZATION_DRIVERS}"
RDEPENDS:${PN}-test = "${TEGRA_OOT_TEST_DRIVERS}"
RDEPENDS:${PN}-base = "${TEGRA_OOT_BASE_DRIVERS}"

TEGRA_OOT_EXTRA_DISPLAY_DRIVERS ??= ""
TEGRA_OOT_DISPLAY_DRIVERS ?= "\
    kernel-module-nvidia \
    kernel-module-nvidia-drm \
    kernel-module-nvidia-modeset \
    kernel-module-tegra-dce \
    kernel-module-tegra-drm-next \
    ${TEGRA_OOT_EXTRA_DISPLAY_DRIVERS} \
"
TEGRA_OOT_EXTRA_CAMERA_DRIVERS ??= ""
TEGRA_OOT_CAMERA_DRIVERS ?= "\
    kernel-module-ar1335-common \
    kernel-module-bmi088 \
    kernel-module-cam-cdi-tsc \
    kernel-module-camchar \
    kernel-module-camera-diagnostics \
    kernel-module-cam-fsync \
    kernel-module-capture-ivc \
    kernel-module-cdi-dev \
    kernel-module-cdi-gpio \
    kernel-module-cdi-mgr \
    kernel-module-cdi-pwm \
    kernel-module-hsp-mailbox-client \
    kernel-module-isc-dev \
    kernel-module-isc-gpio \
    kernel-module-isc-mgr \
    kernel-module-isc-pwm \
    kernel-module-ivc-bus \
    kernel-module-ivc-cdev \
    kernel-module-ivc-ext \
    kernel-module-lt6911uxc \
    kernel-module-max9295 \
    kernel-module-max9296 \
    kernel-module-max96712 \
    kernel-module-maxim-gmsl-dp-serializer \
    kernel-module-maxim-gmsl-hdmi-serializer \
    kernel-module-nv-ar0234 \
    kernel-module-nv-hawk-owl \
    kernel-module-nvhost-capture \
    kernel-module-nvhost-isp5 \
    kernel-module-nvhost-nvcsi \
    kernel-module-nvhost-nvcsi-t194 \
    kernel-module-nvhost-vi5 \
    kernel-module-nvhost-vi-tpg-t19x \
    kernel-module-nv-imx185 \
    kernel-module-nv-imx219 \
    kernel-module-nv-imx274 \
    kernel-module-nv-imx318 \
    kernel-module-nv-imx390 \
    kernel-module-nv-imx477 \
    kernel-module-nv-ov5693 \
    kernel-module-pca9570 \
    kernel-module-tegra-camera \
    kernel-module-tegra-camera-platform \
    kernel-module-tegra-camera-rtcpu \
    kernel-module-virtual-i2c-mux \
    ${TEGRA_OOT_EXTRA_CAMERA_DRIVERS} \
"
TEGRA_OOT_EXTRA_BLUETOOTH_DRIVERS ??= ""
TEGRA_OOT_BLUETOOTH_DRIVERS ?= "\
    kernel-module-bluedroid-pm \
    kernel-module-rtk-btusb \
    ${TEGRA_OOT_EXTRA_BLUETOOTH_DRIVERS} \
"
TEGRA_OOT_EXTRA_WIFI_DRIVERS ??= ""
TEGRA_OOT_WIFI_DRIVERS ?= "\
    kernel-module-rtl8822ce \
    ${TEGRA_OOT_EXTRA_WIFI_DRIVERS} \
"
TEGRA_OOT_EXTRA_CANBUS_DRIVERS ??= ""
TEGRA_OOT_CANBUS_DRIVERS ?= "\
    kernel-module-mttcan \
    ${TEGRA_OOT_EXTRA_CANBUS_DRIVERS} \
"
TEGRA_OOT_EXTRA_VIRUTALIZATION_DRIVERS ??= ""
TEGRA_OOT_VIRTUALIZATION_DRIVERS ?= "\
    kernel-module-hvc-sysfs \
    kernel-module-nv-virtio-console-poc \
    kernel-module-pcie-tegra-vf \
    kernel-module-tegra-gr-comm \
    kernel-module-tegra-hv-mtd \
    kernel-module-tegra-hv-pm-ctl \
    kernel-module-tegra-hv-vblk-oops \
    kernel-module-tegra-hv-vcpu-yield \
    kernel-module-tegra-nvvse-cryptodev \
    kernel-module-tegra-vblk \
    kernel-module-tegra-vnet \
    kernel-module-userspace-ivc-mempool \
    ${TEGRA_OOT_EXTRA_VIRUTALIZATION_DRIVERS} \
"
TEGRA_OOT_EXTRA_ALSA_DRIVERS ??= ""
TEGRA_OOT_ALSA_DRIVERS ?= "\
    kernel-module-snd-soc-tegra186-arad-oot \
    kernel-module-snd-soc-tegra186-asrc-oot \
    kernel-module-snd-soc-tegra186-dspk-oot \
    kernel-module-snd-soc-tegra210-admaif-oot \
    kernel-module-snd-soc-tegra210-adx-oot \
    kernel-module-snd-soc-tegra210-afc-oot \
    kernel-module-snd-soc-tegra210-ahub-oot \
    kernel-module-snd-soc-tegra210-amx-oot \
    kernel-module-snd-soc-tegra210-dmic-oot \
    kernel-module-snd-soc-tegra210-i2s-oot \
    kernel-module-snd-soc-tegra210-iqc-oot \
    kernel-module-snd-soc-tegra210-mixer-oot \
    kernel-module-snd-soc-tegra210-mvc-oot \
    kernel-module-snd-soc-tegra210-ope-oot \
    kernel-module-snd-soc-tegra210-sfc-oot \
    kernel-module-snd-soc-tegra-machine-driver-oot \
    kernel-module-snd-soc-tegra-utils-oot \
    ${TEGRA_OOT_EXTRA_ALSA_DRIVERS} \
"

TEGRA_OOT_EXTRA_TEST_DRIVERS ??= ""
TEGRA_OOT_TEST_DRIVERS ?= "${TEGRA_OOT_EXTRA_TEST_DRIVERS}"
TEGRA_OOT_EXTRA_BASE_DRIVERS ??= ""
TEGRA_OOT_BASE_DRIVERS ?= "\
    kernel-module-arm64-ras \
    kernel-module-cpuidle-debugfs \
    kernel-module-cpuidle-tegra-auto \
    kernel-module-fusb301 \
    kernel-module-governor-pod-scaling \
    kernel-module-host1x-fence \
    kernel-module-host1x-next \
    kernel-module-host1x-nvhost \
    kernel-module-i2c-nvvrs11 \
    kernel-module-mc-hwpm \
    kernel-module-mc-utils \
    kernel-module-nvethernet \
    kernel-module-nvgpu \
    kernel-module-nvhost-nvdla \
    kernel-module-nvhost-pva \
    kernel-module-nvhwpm \
    kernel-module-nvidia-vrs-pseq \
    kernel-module-nvmap \
    kernel-module-nvpmodel-clk-cap \
    kernel-module-nvpps \
    kernel-module-nvsciipc \
    kernel-module-nvvrs-pseq-rtc \
    kernel-module-pwm-tegra-tachometer \
    kernel-module-spi-tegra210-quad \
    kernel-module-tegra194-gte \
    kernel-module-tegra234-aon \
    kernel-module-tegra234-oc-event \
    kernel-module-tegra23x-perf-uncore \
    kernel-module-tegra23x-psc \
    kernel-module-tegra-aon-ivc-echo \
    kernel-module-tegra-bpmp \
    kernel-module-tegra-cactmon-mc-all \
    kernel-module-tegra-mce \
    kernel-module-tegra-se \
    kernel-module-tegra-se-nvrng \
    kernel-module-tegra-wmark \
    kernel-module-thermal-trip-event \
    kernel-module-tsecriscv \
    kernel-module-watchdog-tegra-t18x \
    ${TEGRA_OOT_EXTRA_BASE_DRIVERS} \
"
PACKAGE_ARCH = "${MACHINE_ARCH}"
