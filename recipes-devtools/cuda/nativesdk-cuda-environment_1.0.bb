DESCRIPTION = "SDK environment setup for CUDA toolchain"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://cuda_environment_setup.sh.in \
           file://cuda_toolchain.cmake"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit nativesdk

do_install() {
    install -d ${D}${datadir}/cmake/OEToolchainConfig.cmake.d
    install -m 0644 ${S}/cuda_toolchain.cmake ${D}${datadir}/cmake/OEToolchainConfig.cmake.d/
    install -d ${D}${SDKPATHNATIVE}/environment-setup.d
    sed -e"s,@CUDA_VERSION@,${CUDA_VERSION},g" ${S}/cuda_environment_setup.sh.in > ${D}${SDKPATHNATIVE}/environment-setup.d/cuda.sh
    chmod 0644 ${D}${SDKPATHNATIVE}/environment-setup.d/cuda.sh
}

FILES:${PN} = "${SDKPATHNATIVE}"
FILES:${PN}-dev = ""
