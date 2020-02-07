CUDA_PKG = "cuda-nvcc cuda-cuobjdump cuda-nvprune"
require cuda-shared-binaries-${PV}.inc

do_install_append(){
install -d -m 755 ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
echo "dir, ${prefix}/local/cuda-10.0" >> ${D}/${sysconfdir}/nvidia-container-runtime/host-files-for-container.d/cuda.csv
echo "sym, ${prefix}/local/cuda" >> ${D}/${sysconfdir}/nvidia-container-runtime/host-files-for-container.d/cuda.csv
} 
FILES_${PN} += "${prefix}/local/cuda-10.0/nvvm/lib*/*${SOLIBS} \
                ${sysconfdir}/nvidia-container-runtime/host-files-for-container.d \
"
FILES_${PN}-dev += "${prefix}/local/cuda-10.0/bin ${prefix}/local/cuda-10.0/nvvm/bin \
                    ${prefix}/local/cuda-10.0/nvvm/include ${prefix}/local/cuda-10.0/nvvm/lib*/*${SOLIBSDEV} \
                    ${prefix}/local/cuda-10.0/nvvm/libnvvm-samples ${prefix}/local/cuda-10.0/nvvm/libdevice"
