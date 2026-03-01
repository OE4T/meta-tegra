On the `zeus` and later branches (for L4T R32.2.3 and later), the `l4t-usb-device-mode` recipe is available to set up USB gadgets on a Jetson device for network and serial TTY access. The setup is similar to what's provided in the L4T/JetPack BSP, except:
* the scripts in the BSP under `/opt/nvidia/l4t-usb-device-mode` have been replaced by a combination of systemd, udev, and `libusbgx` configuration files;
* the USB device identifier uses the Linux Foundation vendor ID; and
* no mass storage gadget is created

Note that as of this writing, support for creating both an ECM gadget and an RNDIS gadget is provided, but the RNDIS gadget has not been tested.

## Prerequisites
1. You must have the `meta-oe` layer from [meta-openembedded](https://git.openembedded.org/meta-openembedded/) in your build for the `libusbgx` recipe.
2. You must use systemd, and include udev and networkd support in its configuration (both of which are on by default in OE-Core zeus).

## Network configuration
The systemd-networkd configuration files provided automatically create an `l4tbr0` bridge device that combines the `usb0` ECM interface and the `rndis0` RNDIS interface. The bridge is assigned the IP address 192.168.55.1 and runs a DHCP server to serve the address 192.168.55.100 to the host side of the USB connection.

## Serial port configuration
The serial port is called `/dev/ttyGS0` on the device, and a udev rule automatically starts `serial-getty` on the device when it is created.  If the connecting host is running Linux, the corresponding serial TTY will be `/dev/ttyACM0` (or another `/dev/ttyACMx` device if there are multiple such devices on your host system).

## Using device mode support
To use device mode support, just include `l4t-usb-device-mode` in your image.