SUMMARY = "Monitor-hotplug responder for X sessions using xserver-nodm-init"
DESCRIPTION = "This package starts srandrd from the X session and has \
it run 'xrandr --auto' on each RandR event. It is intended for use with \
the NVIDIA proprietary Xorg driver, which reports monitor hotplug only as \
X RandR events rather than udev/DRM uevents."
HOMEPAGE = "https://github.com/OE4T"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://75srandrd-hotplug.sh"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/X11/Xsession.d
    install -m 0755 ${S}/75srandrd-hotplug.sh ${D}${sysconfdir}/X11/Xsession.d/75srandrd-hotplug.sh
}

# srandrd provides the event daemon; xrandr is the CLI it execs on each event.
RDEPENDS:${PN} = "srandrd xrandr"
