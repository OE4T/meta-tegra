DEPENDS:append:tegra = " l4t-nvidia-glheaders"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

DISTRO_FEATURES:append:tegra = " glvnd"

PACKAGECONFIG:tegra ?= "egl gles1 gles2 ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 glx', '', d)}"

RPROVIDES:${PN}:append:tegra = "${@bb.utils.contains('PACKAGECONFIG', 'x11', ' libgl', '', d)}"
RPROVIDES:${PN}-dev:append:tegra = "${@bb.utils.contains('PACKAGECONFIG', 'x11', ' libgl-dev', '', d)}"
RDEPENDS:${PN}:append:tegra = " tegra-libraries-eglcore tegra-libraries-glescore ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'tegra-libraries-glxcore', '', d)}"
RDEPENDS:${PN}-dev:append:tegra = " l4t-nvidia-glheaders-dev"
RRECOMMENDS:${PN}:append:tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'egl-wayland', '', d)}"
