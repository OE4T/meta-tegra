require cuda-shared-binaries.inc

MAINSUM = "1a84f215e5d3abc29ec6a678d2094009807128d18a54a8944378ac7a3e982dfb"
MAINSUM:x86-64 = "2be5408d267ed07223b632986a1a76bee756e1858616aea4f1c9a06f803de0a3"
DEVSUM = "5082e9bdbc701ef04172a52979155eda16baf487b436ff32de0d6dcd81b6501f"
DEVSUM:x86-64 = "0bb0896178fd85286ba6a0579f33bd6e645f15fa02717651c8c5b75f211d065b"

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
