SUMMARY = "Python wrapper for Nvidia CUDA"
HOMEPAGE = "http://mathema.tician.de/software/pycuda"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f3f85642a528c8fca0fe71e7a031203"

SRC_URI = "https://files.pythonhosted.org/packages/46/61/47d3235a4c13eec5a5f03594ddb268f4858734e02980afbcd806e6242fa5/pycuda-${PV}.tar.gz"

SRC_URI[sha256sum] = "effa3b99b55af67f3afba9b0d1b64b4a0add4dd6a33bdd6786df1aa4cc8761a5"

S = "${WORKDIR}/pycuda-${PV}"

inherit  cuda distutils3

DEPENDS = "python3-setuptools-native python3-cython-native python3-cython python3-numpy-native"

RDEPENDS_${PN} += "python3-appdirs python3-decorator python3-mako python3-pytools python3-core \
	python3-crypt python3-io python3-math python3-numbers python3-numpy python3-pkg-resources python3-py python3-six \
"

do_configure() {
	# special configururation
	${PYTHON_PN} -u ${S}/configure.py
	# disable CURAND otherwise cannot compile
	sed -i 's?CUDA_ENABLE_CURAND = True?CUDA_ENABLE_CURAND = False?g' ${B}/siteconf.py
	cp ${B}/siteconf.py ${S}
}
