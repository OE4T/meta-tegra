
DESCRIPTION = "The Safe Image Processing Library (SIPL) is NVIDIA modular, \
    extensible framework for camera and image sensor integration, image \
    processing, and control, which supports continuous streaming of image data from camera sensors. \
"
LICENSE = "LicenseRef-Proprietary"
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

PACKAGES =+ "\
    ${PN}-drivers \
    ${PN}-driver-ar0234hawk \
    ${PN}-driver-eagle \
    ${PN}-driver-max20087 \
    ${PN}-driver-max96712 \
    ${PN}-driver-max96724 \
    ${PN}-driver-r0sim623 \
    ${PN}-driver-r0sim728 \
    ${PN}-driver-tegra-deser-power \
"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

FILES:${PN} += "${sbindir} ${libdir}/libeagle_driver.so ${libdir}/libsample_coe_driver.so"
FILES:${PN}-driver-ar0234hawk = "${libdir}/nvsipl_drv/libnvuddf_ar0234hawkcameramodule_library.so"
FILES:${PN}-driver-eagle = "${libdir}/nvsipl_drv/libnvuddf_eagle_library.so"
FILES:${PN}-driver-max20087 = "${libdir}/nvsipl_drv/libnvuddf_max20087_library.so"
FILES:${PN}-driver-max96712 = "${libdir}/nvsipl_drv/libnvuddf_max96712_library.so"
FILES:${PN}-driver-max96724 = "${libdir}/nvsipl_drv/libnvuddf_max96724_library.so"
FILES:${PN}-driver-r0sim623 = "${libdir}/nvsipl_drv/libnvuddf_r0sim623cameramodule_library.so"
FILES:${PN}-driver-r0sim728 = "${libdir}/nvsipl_drv/libnvuddf_r0sim728cameramodule_library.so"
FILES:${PN}-driver-tegra-deser-power = "${libdir}/nvsipl_drv/libnvuddf_tegra_deser_power_library.so"
RDEPENDS:${PN}-driver-ar0234hawk = "tegra-libraries-camera-sipl-nova0-hawk"
RDEPENDS:${PN}-driver-eagle = "tegra-libraries-camera-sipl-vb1940"
RDEPENDS:${PN}-driver-r0sim623 = "tegra-libraries-camera-sipl-imx623"
RDEPENDS:${PN}-driver-r0sim728 = "tegra-libraries-camera-sipl-imx728"
RDEPENDS:${PN}-driver-max96712 = "${PN}-driver-tegra-deser-power ${PN}-driver-max20087"
RDEPENDS:${PN}-driver-max96724 = "${PN}-driver-tegra-deser-power ${PN}-driver-max20087"

ALLOW_EMPTY:${PN}-drivers = "1"
RDEPENDS:${PN}-drivers = "\
    ${PN}-driver-ar0234hawk \
    ${PN}-driver-eagle \
    ${PN}-driver-max20087 \
    ${PN}-driver-max96712 \
    ${PN}-driver-max96724 \
    ${PN}-driver-r0sim623 \
    ${PN}-driver-r0sim728 \
"
