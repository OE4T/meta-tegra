require cuda-shared-binaries-${PV}.inc

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-container-csv = "1"
CONTAINER_CSV_FILES_tegra = ""

BBCLASSEXTEND = "native nativesdk"
