PACKAGECONFIG_GRAPHICS:append:tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', 'eglfs-egldevice', d)}"

QT_QPA_DEFAULT_EGLFS_INTEGRATION ?= "${@bb.utils.contains('PREFERRED_RPROVIDER_tegra-gbm-backend', 'tegra-libraries-gbm-backend', 'eglfs_kms_egldevice', 'eglfs_kms', d)}"
EXTRA_OECMAKE:append:tegra = " -DQT_QPA_DEFAULT_EGLFS_INTEGRATION=${QT_QPA_DEFAULT_EGLFS_INTEGRATION}"
