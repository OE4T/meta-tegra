CUDA_COMMON_LD_FLAGS = " \
  -Wl,-rpath-link,${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION}/lib \
  -Wl,-rpath,/usr/local/cuda-${CUDA_VERSION}/lib \
"

CUDA_COMMON_CMAKE = " \
  -DCUDA_TOOLKIT_TARGET_DIR=${STAGING_DIR_TARGET}/usr/local/cuda-${CUDA_VERSION} \
  -DCUDA_TOOLKIT_ROOT_DIR=${STAGING_DIR_NATIVE}/usr/local/cuda-${CUDA_VERSION} \
"

CUDA_COMMON_DEPENDS = " cuda-toolkit cuda-tools-native"
CUDA_COMMON_RDEPENDS = " cuda-cudart"

python __anonymous () {

  cuda_support = bb.utils.contains('MACHINE_FEATURES', 'cuda', 'True', 'False', d)

  if cuda_support == "True":

    d.setVar("PACKAGE_ARCH", "${SOC_FAMILY_PKGARCH}")
    d.appendVar("DEPENDS", "${CUDA_COMMON_DEPENDS}")
    d.appendVar("RDEPENDS_${PN}", "${CUDA_COMMON_RDEPENDS}")
    d.appendVar("EXTRA_OECMAKE", "${CUDA_COMMON_CMAKE}")
    d.appendVar("LDFLAGS","${CUDA_COMMON_LD_FLAGS}")
}
