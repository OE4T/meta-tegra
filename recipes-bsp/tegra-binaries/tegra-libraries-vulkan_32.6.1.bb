L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore virtual/egl egl-wayland"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "c6b3b72a3abbc8f88013d4447e9570465573f2f85ee79b2f1d7cdfc2ccbf669d"
MAINSUM:tegra210 = "96f7eaa8832fbe68d585e5dfed9b1c10db738e53289e8a06c6b4bd5535ea120f"

inherit features_check

REQUIRED_DISTRO_FEATURES = "vulkan opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvvulkan-producer.so \
"

do_install() {
    install_libraries
    install -d ${D}/usr/lib/aarch64-linux-gnu/tegra
    if ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'true', 'false', d)}; then
        install -m644 ${S}/usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json ${D}/usr/lib/aarch64-linux-gnu/tegra/
        install -d ${D}${sysconfdir}/vulkan/icd.d
        ln -sf /usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json ${D}${sysconfdir}/vulkan/icd.d/
    fi
}

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
FILES:${PN} += "/usr/lib/aarch64-linux-gnu"
CONTAINER_CSV_FILES:append = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' ${sysconfdir}/vulkan/icd.d/* /usr/lib/aarch64-linux-gnu/tegra/nvidia_icd.json', '', d)}"
