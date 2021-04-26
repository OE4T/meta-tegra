require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "7d06a0bdd16b16c2384de79e488cde5bb580b3bc9ee46bd1e23d17ce6d260e01"
SRC_URI[dev.sha256sum] = "239060c552e98511b8a752127c57bd86cd1261af5062b99f85d94a12ef61d876"

BBCLASSEXTEND = "native nativesdk"

