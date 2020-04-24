require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "a01a204e6afefb0072424817c7133c5c18a9f3996fbf48f0b31297cb7c07e1ac"
SRC_URI[dev.sha256sum] = "4d572a2472564f5008c3cfe9b24dbef765a1531a73c43436c66a146b4d16cace"

BBCLASSEXTEND = "native nativesdk"
