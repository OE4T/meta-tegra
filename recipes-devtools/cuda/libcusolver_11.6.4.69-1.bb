require cuda-shared-binaries.inc

MAINSUM = "c4301dc5e91369e3eefd326f4552febdf7daceb232712c72b8094f8b92809fdd"
MAINSUM:x86-64 = "1af292e7cbdbd3c0baabd7d261f524793efdd171ad54f88e141fdc609879be21"
DEVSUM = "e58aca1de60ae405e597e1b4c748372c230416b53067afbc4841dc4d80530fd2"
DEVSUM:x86-64 = "92f0310acfb1e62b52fc962a6431c3be58746c396fa79f7a55a40cc0dc9f8f6b"

RDEPENDS:${PN} = "libcublas libcusparse libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
