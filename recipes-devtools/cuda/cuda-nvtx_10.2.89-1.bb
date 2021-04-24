CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "b4f9dedf0c21dbc75daadbabfde6aa17a73b6bdad0d5a18fae3da6e9717bfc99"
MAINSUM_x86-64 = "39e003953fe1efb566b78c5523bedf96d9ce812a11efae6eab7250b13d984f6e"

BBCLASSEXTEND = "native nativesdk"
