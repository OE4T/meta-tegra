
require cuda-shared-binaries.inc

MAINSUM = "6711ed3d94888a47dc86f942631754ce1c9b30735b50b7646b1da3cdf161dede"
MAINSUM:x86-64 = "a328ba9a8fba9e6368fc97c728c33f2ad28bb3043a7e1cb9d20e6895379a6eb5"
DEVSUM = "27c51aee0d3fa85b403af324dc52046bcf74c02681afe309ec9b9832df9e4e36"
DEVSUM:x86-64 = "37dcea2b57f3acbbc5a97891ae09cc30146ad47b49f0304e12347b25ac8ea381"

RDEPENDS:${PN} = "libcublas libcusparse libnvjitlink"
BBCLASSEXTEND = "native nativesdk"
