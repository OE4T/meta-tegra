L4T_DEB_COPYRIGHT_MD5 = "ce4d36df31e6cc73581fd2a25d16834e"

require tegra-debian-libraries-common.inc

MAINSUM = "c6b3b72a3abbc8f88013d4447e9570465573f2f85ee79b2f1d7cdfc2ccbf669d"
MAINSUM:tegra210 = "96f7eaa8832fbe68d585e5dfed9b1c10db738e53289e8a06c6b4bd5535ea120f"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvwinsys.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
