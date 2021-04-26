require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "ede4930b8a7d8098590b3691785cf225fdbb08e3cb7853ca8f0ffc7313e89b3f"
SRC_URI[dev.sha256sum] = "e3d786efc6d92e24c2527a2a76685de4cfc92e609a3b6ab1258cc820a5c867e4"

BBCLASSEXTEND = "native nativesdk"
