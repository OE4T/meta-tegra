# Minimal TensorRT-Enterprise package for building the OSS samples in OE.
#
# The one NVIDIA ships (libnvinfer-cmake-dev) describes the Debian layout,
# lib/aarch64-linux-gnu with every builder resource, plugin and tool present,
# and its generated import check fails on the sysroot an OE recipe sees. This
# defines only the targets the OSS top-level CMakeLists.txt consumes, against
# the sysroot paths passed in by the recipe.

set(TensorRT_PRODUCT_IS_ENTERPRISE     ON)
set(TensorRT_PRODUCT_IS_RTX            OFF)
set(TensorRT_PRODUCT_IS_AUTOMOTIVE     OFF)
set(TensorRT_PRODUCT_IS_SAFE_INFERENCE OFF)
set(TensorRT_CMAKE_NAMESPACE           TRT)
set(TensorRT_ENABLE_DLA                OFF)

file(STRINGS "${TRT_SYSROOT_INCDIR}/NvInferVersion.h" _trt_ver
     REGEX "#define TRT_(MAJOR|MINOR|PATCH)_ENTERPRISE")
foreach(_part MAJOR MINOR PATCH)
    foreach(_line IN LISTS _trt_ver)
        if(_line MATCHES "TRT_${_part}_ENTERPRISE +([0-9]+)")
            set(TensorRT_VERSION_${_part} ${CMAKE_MATCH_1})
        endif()
    endforeach()
endforeach()
set(TensorRT_VERSION
    "${TensorRT_VERSION_MAJOR}.${TensorRT_VERSION_MINOR}.${TensorRT_VERSION_PATCH}")

find_package(CUDAToolkit REQUIRED)

add_library(TRT::trt_cudart INTERFACE IMPORTED)
target_link_libraries(TRT::trt_cudart INTERFACE CUDA::cudart_static)

add_library(TRT::tensorrt_headers INTERFACE IMPORTED)
target_include_directories(TRT::tensorrt_headers INTERFACE "${TRT_SYSROOT_INCDIR}")

foreach(_pair tensorrt:nvinfer nvonnxparser:nvonnxparser tensorrt_plugins:nvinfer_plugin)
    string(REPLACE ":" ";" _pair "${_pair}")
    list(GET _pair 0 _target)
    list(GET _pair 1 _lib)
    add_library(TRT::${_target} SHARED IMPORTED)
    set_target_properties(TRT::${_target} PROPERTIES
        IMPORTED_LOCATION "${TRT_SYSROOT_LIBDIR}/lib${_lib}.so.${TensorRT_VERSION_MAJOR}"
        IMPORTED_SONAME "lib${_lib}.so.${TensorRT_VERSION_MAJOR}"
        INTERFACE_LINK_LIBRARIES TRT::tensorrt_headers)
endforeach()
