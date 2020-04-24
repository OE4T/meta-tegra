require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "459f9e41ac458eef3c94ba0a5d367182af83c070454d9e38b3565252c039a827"
SRC_URI[dev.sha256sum] = "3a9ccd17e6916fda5cede14c206a8199fdba27f1118947a1f31cbf33132241df"

BBCLASSEXTEND = "native nativesdk"
