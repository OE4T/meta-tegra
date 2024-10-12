require cuda-shared-binaries.inc

MAINSUM = "d48aadaf52731f2e058a976581e55f0ad18a24dafb020a1324a8c7a293cba5ef"
MAINSUM:x86-64 = "704a0b1be11bf95288dab3eee3854cb5ef9041c65978dbda1f175fe4efb4ec96"
DEVSUM = "a8e59753cab9e361454df3d4408b589e36659fe074ddb249ba6f36a2bfbeeaf7"
DEVSUM:x86-64 = "56d1dee0937011377f498a44930ba9019534e0180a7b9ff0ed793ce9d5c783ec"

do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
}

BBCLASSEXTEND = "native nativesdk"
