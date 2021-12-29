L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"
DEPENDS = "tegra-libraries-core tegra-libraries-eglcore virtual/egl egl-wayland"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-3d-core"

require tegra-debian-libraries-common.inc

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-libraries-vulkan_32.7.2.bb
MAINSUM = "97fd11faa79ab16f5525492628108a68a58a5c1c5127ba58509784d58b90729c"
MAINSUM:tegra210 = "29fa00ca54f7776503a608989ae7baa2459cc1a1d52e075819a0a07761192c25"
========
MAINSUM = "c873bf1c0605cb98e18ebd5a0d724bdf8a4fbb21b9af5fd0b862e4a44f091e2a"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-libraries-vulkan_34.1.0.bb

inherit features_check

REQUIRED_DISTRO_FEATURES = "vulkan opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvidia-vulkan-producer.so \
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
