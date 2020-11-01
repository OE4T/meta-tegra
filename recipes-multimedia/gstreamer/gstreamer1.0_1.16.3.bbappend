
FILESEXTRAPATHS_prepend := "${THISDIR}/gstreamer1.0:"

# https://gitlab.freedesktop.org/gstreamer/gstreamer/-/merge_requests/570
SRC_URI += "file://0001-bufferpool-only-resize-in-reset-when-maxsize-is-larger.patch"
