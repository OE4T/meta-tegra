require cuda-shared-binaries-${PV}.inc

MAINSUM = "fe9cc7cbdab29035371e5f77d190aecd97bbf31ca66b3f62612bf62580417b16"
MAINSUM_x86-64 = "4ec0c9130e41b75efb46da9a1ff0df479fb4deeea3b377eacd3e942311cef9e5"
DEVSUM = "5c3bd9bc84170ec0c3465444409e7912046e2523e6a18d331b53901c0e57d229"
DEVSUM_x86-64 = "61aa1d77e33853f13dab7aadf3d2e035317428cc56411cc4a5bb5093928b8e85"

FILES_${PN}-dev_remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES_${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP_${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
