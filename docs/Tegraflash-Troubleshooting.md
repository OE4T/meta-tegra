This page includes some guidance about how to resolve or work around issues with device flashing using the tegraflash package build by the Yocto build.

# General Troubleshooting Tips/Suggestions
1. Make sure you are using the correct flashing operation for your device/target storage.  See the table [here](Flashing-the-Jetson-Dev-Kit.md) for guidance.
    * If your target can support either method, try the alternate method as a troubleshooting step.
2. Try swapping USB cables/ensure you are using a high quality cable.
3. Try power cycling the device/entering tegraflash mode from power on rather than reboot.
4. Try running as sudo root or root rather than a user account, especially if any error message mention permissions.
5. Switch to an alternative USB host controller as several people have noticed issues with these.   See [this issue](https://github.com/OE4T/meta-tegra/issues/1794#issuecomment-2585357649) for instance.
    * If you are using a USB 3.0 add-in card, switch to the one connected to the motherboard.
    * Try a USB 2.0 port if you have no other USB 3.0 controllers.
6. Note any failures in logs for the respective flashing method
    * Start with the console log.
    * Connect the serial console on the target device if possible.
    * For initrd-flash steps, consult the host and device logs which are output at the end of the flash process.
7. Suspect issues with partition table, especially if you've modified the partition table or increase sizes of partitions
    * Obscure errors like `cp: cannot stat 'signed/*': No such file or directory` typically mean you've got some problem with your custom partition table and/or target storage device size.  See [this issue](https://github.com/orgs/OE4T/discussions/1795) for example.
8. Attempt to reproduce with a devkit and a similar setup from [tegra-demo-distro](https://github.com/OE4T/tegra-demo-distro).
9. Use hardware recovery mode entry rather than reboot force-recovery
    * See instructions at [Flashing-the-Jetson-Dev-Kit](Flashing-the-Jetson-Dev-Kit.md#setting-up-for-flashing) for putting the device in recovery mode.
    * Although it's possible to use `reboot force-recovery`, note the issues [here](https://forums.developer.nvidia.com/t/mb1-bl-crash-when-rebooting-to-rcm-from-b-slot/309503/13) which can occur in some scenarios.  Using hardware recovery is typically a safer option if you are experiencing issues with tegraflash.
10. Check, if the power-saving TLP Package is installed and running (preferably installed on notebooks/laptops to save battery power). This package disturbs the flashing process. Use `sudo apt remove tlp` and reboot your host computer to remove it before flashing.
11. Use command line to extract tegraflash.tar.gz image file. When extracting by using a GUI app, esp.img file become corrupted. To use  command-line like `tar -xf your-image.tegraflash.tar.gz` and follow normal flashing procedure with doflash.sh script.