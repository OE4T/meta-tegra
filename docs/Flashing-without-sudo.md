# Avoiding sudo

The `initrd-flash` script can be run normally, and will invoke the tools that absolutely require root access (specifcally `bmaptool`
and, for Thor-family modules, the NVIDIA unified flashing script) under `sudo` for you if you are not already running as the root user.

This page has suggestions for configuring your system to help with keeping `sudo` use to a minimum.

## Add yourself to some useful groups

Disk devices are usually set up such that the `disk` group has full access to them. Adding yourself
to that group can help, particularly for formatting/partitioning storage devices.

The `plugdev` group, while considered "legacy," can still be used for access to USB-connected devices.
Adding yourself to that group can help with the USB flashing process.

## Add udev rules to enable non-root access

Add the following udev rules to your configuration (usually to a file under `/etc/udev/rules.d`):
```
# Jetson AGX Orin main CPU
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7023", GROUP="plugdev", TAG+="uaccess"
# Jetson AGX Orin (32G) main CPU
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7223", GROUP="plugdev", TAG+="uaccess"
# Jetson AGX Orin/Thor supervisor CPU
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7045", GROUP="plugdev", TAG+="uaccess"
# Jetson Orin NX (16G)
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7323", GROUP="plugdev", TAG+="uaccess"
# Jetson Orin NX (8G)
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7423", GROUP="plugdev", TAG+="uaccess"
# Jetson Orin Nano
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7523", GROUP="plugdev", TAG+="uaccess"
# Jetson Orin Nano 4GB
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7623", GROUP="plugdev", TAG+="uaccess"
# Jetson AGX Thor dev kit
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7026", GROUP="plugdev", TAG+="uaccess"
# Jetson AGX Thor P3834-0001
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0955", ATTRS{idProduct}=="7326", GROUP="plugdev", TAG+="uaccess"
```

The GROUP assignment is for legacy `plugdev`-style access; the TAG addition is for the newer systemd-udevd method for granting user access.

## Add polkit rules

This is for Orin-family flashing over USB, when your host system uses of the `udisks2` package for mediating access to removable storage devices,
such as Ubuntu. The following commands install a configuration file to enable the `disk` group to perform some disk operations through `udisks2`
that might otherwise be denied or result in prompting for your login password:

```
mkdir -p /var/lib/polkit-1/localauthority/50-local.d/
cat << EOF > /var/lib/polkit-1/localauthority/50-local.d/com.github.oe4t.pkla
[Allow Mounting for Disk Group]
Identity=unix-group:disk
Action=org.freedesktop.udisks2.filesystem-mount
ResultAny=yes

[Allow Power Off Drive for Disk Group]
Identity=unix-group:disk
Action=org.freedesktop.udisks2.power-off-drive
ResultAny=yes
EOF
chmod 644 /var/lib/polkit-1/localauthority/50-local.d/com.github.oe4t.pkla
systemctl restart polkit
```
