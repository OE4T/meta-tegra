# Start the RandR monitor-hotplug responder for this X session.
#
# The proprietary NVIDIA Xorg driver reports monitor hotplug only as X RandR
# events (not as udev/DRM uevents); unlike modesetting there is no udev event
# for display managers or session helpers to react to. Without this, a display
# connected after boot is never configured until X is restarted. srandrd
# listens for those RandR events and runs the command below; 'xrandr --auto'
# enables a newly-connected output at its preferred mode and disables outputs
# that were unplugged.
#
# This file is sourced by /etc/X11/Xsession, so launch srandrd in the
# background. '-n' keeps it as a single session-owned process (it still exits
# when the X server does); '-e' emits the already-connected outputs at startup,
# covering a monitor that was attached just before srandrd subscribed.
if [ -x /usr/bin/srandrd ]; then
    /usr/bin/srandrd -n -e /usr/bin/xrandr --auto &
fi
