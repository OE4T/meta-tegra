require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "1cf024814383a2ec21e562e1e89809050dbae36bceed6ee276affe03185ca266"
SRC_URI[dev.sha256sum] = "f6594cd55b89a4e45ca3a382cef99c6bd3d2efb7ab222a82f261a79cc9e066ce"

BBCLASSEXTEND = "native nativesdk"
