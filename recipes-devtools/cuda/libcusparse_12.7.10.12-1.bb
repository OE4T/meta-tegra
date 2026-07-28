
require cuda-shared-binaries.inc

MAINSUM = "ff2ff19b43689efd6243a09203937fe3136f05a289674f9e9ed18d8157b5a4e9"
MAINSUM:x86-64 = "1554edbe273ad3fb2a0a83009b4d8e71e7c9f13da6695b3e55c17eceed92c2dc"
DEVSUM = "4d36fe6cf824037f1cf4f9d3edc95f5b988017a02ad0ce182aeef94428ebc1fd"
DEVSUM:x86-64 = "416c5ca9ceb26c7110a43344694783e618da83e7602683d1b146425a5f1b5275"

RDEPENDS:${PN} = "libnvjitlink"
RDEPENDS:${PN}-stubs = "libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
