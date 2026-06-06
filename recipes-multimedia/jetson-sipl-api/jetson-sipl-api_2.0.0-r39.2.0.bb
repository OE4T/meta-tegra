
DESCRIPTION = "The Safe Image Processing Library (SIPL) is NVIDIA modular, \
    extensible framework for camera and image sensor integration, image \
    processing, and control, which supports continuous streaming of image data from camera sensors. \
"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://../../../share/doc/jetson_sipl_api/Tegra_Software_License_Agreement-Tegra-Linux.txt;md5=376d20bd5275442226fcdf54e4844ddf"

SRC_URI = "\
    ${L4T_URI_BASE}/Jetson_SIPL_API_R${L4T_VERSION}_aarch64.tbz2 \
    file://0001-Updates-for-OE-cross-builds.patch \
"
SRC_URI[sha256sum] = "42d95cd4fb90ba32e87c7055d5659d818aa09c739d671bf48d857cc29496720b"

inherit l4t_bsp cmake cuda features_check

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS += "tegra-libraries-camera tegra-libraries-nvsci tegra-libraries-openwfd fmt virtual/egl virtual/libgles2 libx11"

REQUIRED_DISTRO_FEATURES = "x11"

COMPATIBLE_MACHINE = "(tegra)"

S = "${UNPACKDIR}/usr/src/jetson_sipl_api/sipl"
B = "${S}"

EXTRA_OECMAKE = "\
    -DENABLE_CAMERA_HAL=ON \
    -DNV_EMBEDDED_L4T=ON \
"

FILES:${PN} += "${libdir}/nvsipl_drv"
RDEPENDS:${PN} += "tegra-libraries-camera-sipl"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
