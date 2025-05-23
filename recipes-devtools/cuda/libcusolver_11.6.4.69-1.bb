require cuda-shared-binaries.inc

MAINSUM = "c4301dc5e91369e3eefd326f4552febdf7daceb232712c72b8094f8b92809fdd"
MAINSUM:x86-64 = "1af292e7cbdbd3c0baabd7d261f524793efdd171ad54f88e141fdc609879be21"
DEVSUM = "e58aca1de60ae405e597e1b4c748372c230416b53067afbc4841dc4d80530fd2"
DEVSUM:x86-64 = "fe9007f52555e79e84ffd2fd8aa94d453efe9554561b259d57d7899a00a50c67"

RDEPENDS:${PN} = "libcublas libcusparse libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
