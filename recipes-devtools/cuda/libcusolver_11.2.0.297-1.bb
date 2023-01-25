require cuda-shared-binaries.inc

MAINSUM = "f2e39c718793bfbe6dd99bac3ccf8029b7f869bb92160fce5b281e32b4535c6e"
MAINSUM:x86-64 = "bbc11e47aa6697f0fc99b74a86900928b879bda7aba7e93677f364e8de00bedb"
DEVSUM = "110bb052dc2f4e7d1170a8094488679dccdc25bb140aa63fcc9b63922410308a"
DEVSUM:x86-64 = "eb7ae312e60428d3d3c5528decfe8d882bfdaca9b58cddd131aa1a43b668e895"

RDEPENDS:${PN} = "libcublas"
BBCLASSEXTEND = "native nativesdk"
