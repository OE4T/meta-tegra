L4T_DEB_COPYRIGHT_MD5 = "03753bf7be89a121c8d3fd11c4267db9"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore virtual/egl egl-wayland"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

MAINSUM = "31b5c0de9fc673a540e6c331ae18cb981fc6de1145f4be76419bed0f9de1959b"
MAINSUM:tegra210 = "13fa8733d146999ac1a6e2c37c70c2f4b64988c5bcd5a664f2fda4bb4a04680b"

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
