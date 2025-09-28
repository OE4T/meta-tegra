CUDA_PKG = "${BPN}"

DEPENDS:tegra = "tegra-libraries-cuda"

require cuda-shared-binaries.inc

MAINSUM = "a5573f4bb22dad6446c871fea34a1e1c500936257c634386346871c51f9db195"
MAINSUM:x86-64 = "3caa8d8ab7e22f4e6b1d2aa59dd6a0658b40bc020aeadb8bb4533eb2379c5b7f"

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/compat"

RDEPENDS:${PN} = "tegra-libraries-cuda"
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
