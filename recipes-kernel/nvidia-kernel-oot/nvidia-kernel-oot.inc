DESCRIPTION = "Out-of-tree kernel modules and device trees for Jetson Linux platforms"
LICENSE = "GPL-2.0-only & BSD-3-Clause & (MIT | GPL-2.0-only)"
LIC_FILES_CHKSUM = "file://nvdisplay/COPYING;md5=1d5fa2a493e937d5a4b96e5e03b90f7c \
                    file://Makefile;beginline=2;endline=2;md5=a670216ac93e92dc066b1a876610705f \
		    file://nvidia-oot/Makefile;endline=1;md5=daad6f7f7a0a286391cd7773ccf79340"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit module
inherit ${TEGRA_UEFI_SIGNING_CLASS}

COMPATIBLE_MACHINE = "(tegra)"

B = "${S}"

COMMON_PATCHES = "\
    file://0001-Reapply-OE-patches-to-main-Makefile.patch \
    file://0002-Update-conftest-and-makefiles-for-OE-builds.patch;patchdir=nvdisplay \
    file://0003-conftest-add-fshort-wchar-to-compilation-commands.patch;patchdir=nvdisplay \
    file://0004-conftest-fix-dma_buf_has_dynamic_attachment-test.patch;patchdir=nvdisplay \
    file://0005-Kbuild-add-EXTRA_CFLAGS-to-ccflags-y-when-necessary.patch;patchdir=nvdisplay \
    file://0006-Kbuild-Fix-quoting-on-NV_VERSION_STRING.patch;patchdir=nvdisplay \
    file://0007-Kbuild-fix-symlink-commands.patch;patchdir=nvdisplay \
    file://0008-conftest-fix-drm_gem_object_vmap_has_map_arg-check.patch;patchdir=nvdisplay \
    file://0009-nvdisplay-kernel-open-correct-parameter-of-macro-MOD.patch;patchdir=nvdisplay \
    file://0010-nvdisplay-timer-replace-hrtimer_init-with-hrtimer_se.patch;patchdir=nvdisplay \
    file://0011-nvdisplay-nvidia-check-importer-operations-directly.patch;patchdir=nvdisplay \
    file://0012-nvdisplay-timer-replace-del_timer_sync-with-timer_de.patch;patchdir=nvdisplay \
    file://0013-nvdisplay-nvidia-drm-fb-add-format-info-parameter-to.patch;patchdir=nvdisplay \
    file://0014-nvdisplay-nvidia-drm-remove-unused-date-member.patch;patchdir=nvdisplay \
    file://0015-nvdisplay-nvidia-drm-add-constify-attribute-for-stru.patch;patchdir=nvdisplay \
    file://0016-tegra-virt-alt-Remove-leading-from-include-path-from.patch;patchdir=nvidia-oot \
    file://0017-conftest-work-around-stringify-issue-with-__assign_s.patch;patchdir=nvidia-oot \
    file://0018-gpio-Use-conftest-to-find-if-struct-gpio_chip-has-of.patch;patchdir=nvidia-oot \
    file://0019-drivers-Fix-gpio_chip-.set-callback-for-v6.17.patch;patchdir=nvidia-oot \
    file://0020-r8126-use-conftest-for-hrtimer_init.patch;patchdir=nvidia-oot \
    file://0021-drivers-Fix-from_timer-for-Linux-v6.16.patch;patchdir=nvidia-oot \
    file://0022-BT-deprecate-support-for-of_gpio-calls.patch;patchdir=nvidia-oot \
    file://0023-misc-bluedroid_pm-Use-wakeup_source_register-unregis.patch;patchdir=nvidia-oot \
    file://0024-misc-bluedroid-remove-duplicate-timer-declaration.patch;patchdir=nvidia-oot \
    file://0025-misc-bluedroid_pm-Fix-optional-GPIOs.patch;patchdir=nvidia-oot \
    file://0026-misc-bluedroid_pm-Verify-wakeup-registration.patch;patchdir=nvidia-oot \
    file://0027-misc-bluedroid_pm-Free-proc-on-failure.patch;patchdir=nvidia-oot \
    file://0028-misc-bluedroid_pm-Ensure-host_wake-is-configured.patch;patchdir=nvidia-oot \
    file://0029-kernel-oot-update-driver-license-to-GPL.patch;patchdir=nvidia-oot \
    file://0030-virt-hvc_sysfs-Fix-build-for-Linux-v6.16.patch;patchdir=nvidia-oot \
    file://0031-platform-tegra-uss-io-proxy-Migrate-to-GPIOD.patch;patchdir=nvidia-oot \
    file://0032-virt-tegra-Fix-build-for-Linux-v6.17.patch;patchdir=nvidia-oot \
    file://0033-drivers-crypto-add-priv-pointer-in-context-struct-to.patch;patchdir=nvidia-oot \
    file://0034-drivers-nvmap-calculate-page-address-explicitly.patch;patchdir=nvidia-oot \
    file://0035-ASoC-tegra-Fix-build-for-Linux-v6.15.patch;patchdir=nvidia-oot \
    file://0036-sound-soc-set-Codec-Stream-Card-name-explicitly.patch;patchdir=nvidia-oot \
    file://0037-crypto-tegra-Remove-an-incorrect-iommu_fwspec_free-c.patch;patchdir=nvidia-oot \
    file://0038-sound-soc-use-reasonable-SND_SOC_DAIFMT_CBx_CFx.patch;patchdir=nvidia-oot \
    file://0039-sound-soc-replace-idle_bias_off-with-idle_bias.patch;patchdir=nvidia-oot \
    file://0040-video-tegra-nvmap-Take-lock-before-reading-dmabuf-s-.patch;patchdir=nvidia-oot \
    file://0041-video-tegra-nvmap-Fix-f_count-for-Linux-v6.13.patch;patchdir=nvidia-oot \
    file://0042-drivers-realtek-bt-replace-set_bit-with-hci_set_quir.patch;patchdir=nvidia-oot \
    file://0043-net-marvell-oak-Fix-build-for-Linux-v6.16.patch;patchdir=nvidia-oot \
    file://0044-drm-tegra-syncpoint-base-support-for-chips-t186.patch;patchdir=nvidia-oot \
    file://0045-drm-tegra-move-disply-related-code-under-CONFIG_DRM_.patch;patchdir=nvidia-oot \
    file://0046-drm-tegra-gem-Open-code-drm_prime_gem_destroy.patch;patchdir=nvidia-oot \
    file://0047-drm-tegra-gem-Don-t-attach-dma-bufs-when-not-needed.patch;patchdir=nvidia-oot \
    file://0048-drm-tegra-Update-to-Linux-v6.8.patch;patchdir=nvidia-oot \
    file://0049-drm-tegra-Update-to-Linux-v6.12.patch;patchdir=nvidia-oot \
    file://0050-drm-tegra-Update-to-Linux-v6.13.patch;patchdir=nvidia-oot \
    file://0051-drm-tegra-Update-to-Linux-v6.14.patch;patchdir=nvidia-oot \
    file://0052-drm-tegra-Update-to-Linux-v6.16-rc1.patch;patchdir=nvidia-oot \
    file://0053-drm-tegra-Fix-build-for-Linux-v6.17.patch;patchdir=nvidia-oot \
    file://0054-net-can-mttcan-Fix-build-for-Linux-v6.16.patch;patchdir=nvidia-oot \
    file://0055-net-can-mttcan-Drop-support-for-legacy-kernels.patch;patchdir=nvidia-oot \
    file://0056-net-can-mttcan-Fix-build-for-Linux-v6.17.patch;patchdir=nvidia-oot \
    file://0057-nvmap-Remove-dma_buf_ops-flag-cache_sgt_mapping.patch;patchdir=nvidia-oot \
    file://0058-drm-tegra-Fix-build-for-Linux-v6.14.patch;patchdir=nvidia-oot \
    file://0059-sound-soc-replace-of_property_read_bool-with-of_prop.patch;patchdir=nvidia-oot \
    file://0060-bluetooth-fix-build-issues-with-Linux-v6.18.patch;patchdir=nvidia-oot \
    file://0061-tegra-hwpm-Fix-build-for-Linux-v6.13.patch;patchdir=hwpm \
    file://0062-gpu-nvgpu-correct-parameter-of-macro-MODULE_IMPORT_N.patch;patchdir=nvgpu \
    file://0063-nvgpu-timer-replace-hrtimer_init-with-hrtimer_setup.patch;patchdir=nvgpu \
"
EXTRA_PATCHES ??= ""

SRC_URI += "${COMMON_PATCHES} ${EXTRA_PATCHES}"

do_unpack[depends] += "tegra-kernel-makefile:do_populate_sysroot"

unpack_makefile_from_bsp() {
    [ -e ${S}/Makefile ] || cp ${STAGING_DATADIR}/nvidia-kernel/Makefile ${S}/
}
do_unpack[postfuncs] += "unpack_makefile_from_bsp"

KERNEL_MODULE_PACKAGE_PREFIX = "nv-"

# Out-of-tree drivers that are named identically to, and
# should used instead of, in-tree drivers built with the kernel.
#
# N.B.: If this list changes, update the PREFERRED_RPROVIDER
# settings in conf/machine/include/tegra-common.inc accordingly
TEGRA_OOT_REPLACEMENT_DRIVERS = "\
    host1x \
    snd-soc-tegra186-asrc \
    snd-soc-tegra186-dspk \
    snd-soc-tegra210-admaif \
    snd-soc-tegra210-adx \
    snd-soc-tegra210-ahub \
    snd-soc-tegra210-amx \
    snd-soc-tegra210-dmic \
    snd-soc-tegra210-i2s \
    snd-soc-tegra210-mixer \
    snd-soc-tegra210-mvc \
    snd-soc-tegra210-ope \
    snd-soc-tegra210-sfc \
    tegra-bpmp-thermal \
    tegra-drm \
"

EXTRA_OEMAKE += '\
    IGNORE_PREEMPT_RT_PRESENCE=1 KERNEL_PATH="${STAGING_KERNEL_BUILDDIR}" \
    CC="${KERNEL_CC} -Wno-error=enum-int-mismatch" CXX="${KERNEL_CC} -x c++" LD="${KERNEL_LD}" AR="${KERNEL_AR}" \
    OBJCOPY="${KERNEL_OBJCOPY}" \
'

do_compile() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake modules
    oe_runmake dtbs
}

do_sign_dtbs() {
    for dtbf in ${KERNEL_DEVICETREE}; do
        local dtb="${B}/kernel-devicetree/generic-dts/dtbs/${dtbf}"
        if [ -e "$dtb" ]; then
            tegra_uefi_attach_sign "$dtb"
        fi
    done
}
do_sign_dtbs[dirs] = "${B}"
do_sign_dtbs[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"
do_sign_dtbs[file-checksums] += "${TEGRA_UEFI_SIGNING_FILECHECKSUMS}"

addtask sign_dtbs after do_compile before do_install

do_install() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake MODLIB="${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}" modules_install
    install -d ${D}/boot/devicetree
    install -m 0644 ${B}/kernel-devicetree/generic-dts/dtbs/* ${D}/boot/devicetree/
    install -d ${D}${includedir}/${BPN}
    ln -s ${BPN} ${D}${includedir}/kernel-module-${BPN}
    find ${B} -name Module.symvers -type f | xargs sed -e's:${B}/::g' >${D}${includedir}/${BPN}/Module.symvers

    cp -R ${S}/nvidia-oot/include/* ${D}/${includedir}/${BPN}

    # include device-tree source for building external dtb
    install -d ${D}/usr/src/device-tree
    cp -R ${S}/hardware/nvidia/ ${D}/usr/src/device-tree
}

SYSROOT_DIRS += "/boot/devicetree"
SYSROOT_DIRS += "/usr/src/device-tree"

KERNEL_MODULE_PROBECONF = "nvgpu"
module_conf_nvgpu = 'options nvgpu devfreq_timer="delayed"'

PACKAGES =+ "${PN}-devicetrees ${PN}-display ${PN}-cameras ${PN}-bluetooth ${PN}-wifi ${PN}-canbus ${PN}-virtualization ${PN}-alsa ${PN}-test ${PN}-base ${PN}-extra"
PROVIDES += "kernel-module-${BPN}"
FILES:${PN}-devicetrees = "/boot/devicetree"
FILES:${PN}-dev = "\
    ${includedir}/${BPN} \
    ${includedir}/kernel-module-${BPN} \
    /usr/src/device-tree \
"
ALLOW_EMPTY:${PN}-display = "1"
ALLOW_EMPTY:${PN}-cameras = "1"
ALLOW_EMPTY:${PN}-bluetooth = "1"
ALLOW_EMPTY:${PN}-wifi = "1"
ALLOW_EMPTY:${PN}-canbus = "1"
ALLOW_EMPTY:${PN}-virtualization = "1"
ALLOW_EMPTY:${PN}-alsa = "1"
ALLOW_EMPTY:${PN}-test = "1"
ALLOW_EMPTY:${PN}-base = "1"
ALLOW_EMPTY:${PN}-extra = "1"

RDEPENDS:${PN}-display = "${TEGRA_OOT_DISPLAY_DRIVERS}"
RDEPENDS:${PN}-cameras = "${TEGRA_OOT_CAMERA_DRIVERS}"
RDEPENDS:${PN}-bluetooth = "${TEGRA_OOT_BLUETOOTH_DRIVERS}"
RDEPENDS:${PN}-wifi = "${TEGRA_OOT_WIFI_DRIVERS}"
RDEPENDS:${PN}-canbus = "${TEGRA_OOT_CANBUS_DRIVERS}"
RDEPENDS:${PN}-virtualization = "${TEGRA_OOT_VIRTUALIZATION_DRIVERS}"
RDEPENDS:${PN}-alsa = "${TEGRA_OOT_ALSA_DRIVERS}"
RDEPENDS:${PN}-test = "${TEGRA_OOT_TEST_DRIVERS}"
RDEPENDS:${PN}-base = "${TEGRA_OOT_BASE_DRIVERS}"
RRECOMMENDS:${PN}-extra = "${TEGRA_OOT_EXTRA_DRIVERS}"

TEGRA_OOT_ALL_DRIVER_PACKAGES = ""
TEGRA_OOT_EXTRA_DISPLAY_DRIVERS ??= ""
TEGRA_OOT_DISPLAY_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvidia \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvidia-drm \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvidia-modeset \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-dce \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-drm \
    ${TEGRA_OOT_EXTRA_DISPLAY_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_DISPLAY_DRIVERS}"
TEGRA_OOT_EXTRA_CAMERA_DRIVERS ??= ""
TEGRA_OOT_CAMERA_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-ar1335-common \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-bmi088 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-cam-cdi-tsc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-camchar \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-cam-fsync \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-capture-ivc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-cdi-dev \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-cdi-gpio \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-cdi-mgr \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-cdi-pwm \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-hsp-mailbox-client \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-isc-dev \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-isc-gpio \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-isc-mgr \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-isc-pwm \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-ivc-bus \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-ivc-cdev \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-ivc-ext \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-lt6911uxc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max9295 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max9296 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max96712 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-maxim-gmsl-dp-serializer \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-maxim-gmsl-hdmi-serializer \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-ar0234 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-hawk-owl \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhost-capture \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhost-isp5 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhost-nvcsi \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhost-nvcsi-t194 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhost-vi5 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhost-vi-tpg-t19x \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-imx185 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-imx219 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-imx274 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-imx318 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-imx390 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-imx477 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-ov5693 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pca9570 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-camera \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-camera-platform \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-camera-rtcpu \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-virtual-i2c-mux \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max929x \
    ${TEGRA_OOT_EXTRA_CAMERA_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_CAMERA_DRIVERS}"
TEGRA_OOT_EXTRA_BLUETOOTH_DRIVERS ??= ""
TEGRA_OOT_BLUETOOTH_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-bluedroid-pm \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-rtk-btusb \
    ${TEGRA_OOT_EXTRA_BLUETOOTH_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_BLUETOOTH_DRIVERS}"
TEGRA_OOT_EXTRA_WIFI_DRIVERS ??= ""
TEGRA_OOT_WIFI_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-rtl8822ce \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-rtl8852ce \
    ${TEGRA_OOT_EXTRA_WIFI_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_WIFI_DRIVERS}"
TEGRA_OOT_EXTRA_CANBUS_DRIVERS ??= ""
TEGRA_OOT_CANBUS_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-mttcan \
    ${TEGRA_OOT_EXTRA_CANBUS_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_CANBUS_DRIVERS}"
TEGRA_OOT_EXTRA_VIRUTALIZATION_DRIVERS ??= ""
TEGRA_OOT_VIRTUALIZATION_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-hvc-sysfs \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nv-virtio-console-poc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pcie-tegra-vf \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra-virt-t210ref-pcm \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-virt-alt-admaif \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-gr-comm \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-hv-mtd \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-hv-pm-ctl \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-hv-vblk-oops \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-hv-vcpu-yield \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-nvvse-cryptodev \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-vblk \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-vnet \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-userspace-ivc-mempool \
    ${TEGRA_OOT_EXTRA_VIRUTALIZATION_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_VIRTUALIZATION_DRIVERS}"
TEGRA_OOT_EXTRA_ALSA_DRIVERS ??= ""
TEGRA_OOT_ALSA_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra186-arad \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra186-asrc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra186-dspk \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-admaif \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-adx \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-afc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-ahub \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-amx \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-dmic \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-i2s \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-iqc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-mixer \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-mvc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-ope \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra210-sfc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra-machine-driver \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-snd-soc-tegra-utils \
    ${TEGRA_OOT_EXTRA_ALSA_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_ALSA_DRIVERS}"
TEGRA_OOT_EXTRA_TEST_DRIVERS ??= ""
TEGRA_OOT_TEST_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-bootloader-debug \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-camera-diagnostics \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-mods \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pci-epf-dma-test \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-pcie-dma-test \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-rtcpu-debug \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-sensor-kernel-tests \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegracam-log \
   ${TEGRA_OOT_EXTRA_TEST_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_TEST_DRIVERS}"
TEGRA_OOT_EXTRA_BASE_DRIVERS ??= ""
TEGRA_OOT_BASE_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-arm64-ras \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-cpuidle-debugfs \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-cpuidle-tegra-auto \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-fusb301 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-governor-pod-scaling \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-host1x \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-host1x-fence \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-host1x-nvhost \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-i2c-nvvrs11 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-mc-hwpm \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-mc-utils \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvethernet \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvgpu \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhost-nvdla \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhost-pva \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvhwpm \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvidia-vrs-pseq \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvmap \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvpmodel-clk-cap \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvpps \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvsciipc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvvrs-pseq-rtc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pinctrl-tegra194-pexclk-padctrl \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pinctrl-tegra234-dpaux \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pwm-tegra-tachometer \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-spi-tegra210-quad \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra234-aon \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra234-oc-event \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra23x-perf-uncore \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra23x-psc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-aon-ivc-echo \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-bpmp \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-cactmon-mc-all \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-mce \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-se \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-se-nvrng \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-wmark \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-thermal-trip-event \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tsecriscv \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-ufs-tegra \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-watchdog-tegra-t18x \
    ${TEGRA_OOT_EXTRA_BASE_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_BASE_DRIVERS}"
TEGRA_OOT_EXTRA_OTHER_DRIVERS ??= ""
TEGRA_OOT_OTHER_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-f75308 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-gpio-max77851 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-i2c-tegra-slave-byte \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-ioctl-example \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-kfuse \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-lan743x \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max77851 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max77851-poweroff \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max77851-regulator \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max77851-thermal \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-max77851-wdt \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvidia-p2p \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvscic2c-pcie-epc \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-nvscic2c-pcie-epf \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-oak-pci \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pci-epf-tegra-vnet \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pex9749-thermal \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-pinctrl-max77851 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-rtc-max77851 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-safety-i2s \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-softdog-platform \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-spi-aurix-tegra \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-spi-tegra124-slave \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-cec \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-fsicom \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-hv-vse-safety \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-pcie-edma \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-tegra-uss-io-proxy \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-ti-fpdlink-dp-serializer \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-wch \
    ${TEGRA_OOT_EXTRA_OTHER_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_OTHER_DRIVERS}"
TEGRA_OOT_EXTRA_EXTRA_DRIVERS ??= ""
TEGRA_OOT_EXTRA_DRIVERS ?= "\
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-r8126 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-r8168 \
    ${KERNEL_MODULE_PACKAGE_PREFIX}kernel-module-ufs-tegra-provision \
    ${TEGRA_OOT_EXTRA_EXTRA_DRIVERS} \
"
TEGRA_OOT_ALL_DRIVER_PACKAGES += "${TEGRA_OOT_EXTRA_DRIVERS}"


PACKAGES += "${TEGRA_OOT_ALL_DRIVER_PACKAGES}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

python oot_update_rprovides() {
    import re
    override_drivers = set(d.getVar('TEGRA_OOT_REPLACEMENT_DRIVERS').split())
    pkg_prefix = d.getVar('KERNEL_MODULE_PACKAGE_PREFIX')
    if not pkg_prefix:
        return
    kernel_version = d.getVar('KERNEL_VERSION')
    module_prefix = pkg_prefix + (d.getVar('KERNEL_PACKAGE_NAME') or 'kernel') + '-module-'
    virt_module_prefix = (d.getVar('KERNEL_PACKAGE_NAME') or 'kernel') + '-module-'
    module_suffix = d.getVar('KERNEL_MODULE_PACKAGE_SUFFIX')
    packages = d.getVar('PACKAGES').split()
    enumerated_drivers = set(d.getVar('TEGRA_OOT_ALL_DRIVER_PACKAGES').split())
    pkg_pat = re.compile(re.escape(module_prefix) + r'(.*)' + re.escape(module_suffix))
    for oot_pkg in d.getVar('PACKAGES').split():
        m = pkg_pat.match(oot_pkg)
        if m is None:
            continue
        basename = m.group(1)
        bb.debug(1, "Processing: %s (driver %s)" % (oot_pkg, basename))
        if module_prefix + basename not in enumerated_drivers:
            bb.warn("out-of-tree kernel module %s not listed in TEGRA_OOT_ALL_DRIVER_PACKAGES" % (module_prefix + basename))
        # Unprefixed name with version suffix, and without version suffix
        newprovides = oot_pkg[len(pkg_prefix):] + " " + virt_module_prefix + basename
        bb.note("Adding %s to RPROVIDES:%s" % (newprovides, oot_pkg))
        d.appendVar('RPROVIDES:' + oot_pkg, ' ' + newprovides)
        if basename in override_drivers:
            d.setVar('RREPLACES:' + oot_pkg, newprovides)
            d.setVar('RCONFLICTS:' + oot_pkg, newprovides)
        rdepstr = d.getVar('RDEPENDS:' + oot_pkg)
        if not rdepstr:
            continue
        rdeps = rdepstr.split()
        newdeps = []
        changed = False
        for dep in rdeps:
            if pkg_pat.match(dep) and dep not in packages:
                newdeps.append(dep[len(pkg_prefix):])
                changed = True
            else:
                newdeps.append(dep)
        if changed:
            newdepstr = ' '.join(newdeps)
            bb.note("Updating RDEPENDS:%s to %s" % (oot_pkg, newdepstr))
            d.setVar('RDEPENDS:' + oot_pkg, newdepstr)
}
PACKAGESPLITFUNCS += "oot_update_rprovides"
