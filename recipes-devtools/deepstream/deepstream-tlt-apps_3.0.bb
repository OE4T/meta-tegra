DESCRIPTION = "NVIDIA Transfer Learning Toolkit sample applications"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=dbef1fb16cd9e5c5249a8e0c5e639fb0"

DEPENDS = " deepstream-5.0"

SRC_URI = "git://github.com/NVIDIA-AI-IOT/deepstream_tao_apps.git;branch=release/tlt3.0"
SRCREV = "16405b1a725a888cf6473cdd4fd0cb7effbf393f"

S = "${WORKDIR}/git"

inherit pkgconfig cuda

TARGET_LDFLAGS += " -L${RECIPE_SYSROOT}/opt/nvidia/deepstream/deepstream-5.0/lib"
EXTRA_OEMAKE += " CC='${CXX}' CUDA_VER=${CUDA_VERSION}"

TARGET_CC_ARCH += "${LDFLAGS}"

TLT_SAMPLES_PATH = "${bindir}/tlt_samples"

do_install () {
    install -d ${D}${libdir}
    install -m 0755 ${S}/post_processor/libnvds_infercustomparser_tlt.so ${D}${libdir}/libnvds_infercustomparser_tlt.so.${PV}

    install -d ${D}${TLT_SAMPLES_PATH}
    install -m 0755 ${S}/apps/tlt_detection/ds-tlt-detection ${D}${TLT_SAMPLES_PATH}
    install -m 0755 ${S}/apps/tlt_segmentation/ds-tlt-segmentation ${D}${TLT_SAMPLES_PATH}
    install -m 0755 ${S}/apps/tlt_classifier/ds-tlt-classifier ${D}${TLT_SAMPLES_PATH}
}

PACKAGES += "${PN}-samples ${PN}-custom-parser"
FILES_${PN} = ""
ALLOW_EMPTY_${PN} = "1"
FILES_${PN}-custom-parser = "${libdir}"
FILES_${PN}-samples = "${TLT_SAMPLES_PATH}"
RDEPENDS_${PN} += "${PN}-samples ${PN}-custom-parser"
RDEPENDS_${PN}-samples += "deepstream-5.0"
