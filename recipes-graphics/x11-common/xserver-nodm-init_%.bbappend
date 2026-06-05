# Pull in the srandrd-hotplug responder for any X session managed by
# xserver-nodm-init, which is what processes /etc/X11/Xsession.d/.
# The NVIDIA Xorg driver reports monitor hotplug only as X RandR events (not
# as udev/DRM uevents), so without this a display connected after boot is never
# configured until X is restarted. Attaching here rather than to a specific
# window manager covers matchbox-session, matchbox-session-sato, mini-x-session,
# and any other WM that uses the nodm Xsession infrastructure.
# Sessions with their own display management (e.g. GDM) don't use
# xserver-nodm-init.
RDEPENDS:${PN}:append:tegra = " srandrd-hotplug"
