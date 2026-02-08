# Instructions for r35

See https://github.com/OE4T/tegra-demo-distro/discussions/310#discussioncomment-10534547

1. Grab the pinmux spreadsheet and configure the pins the way you need then generate the new files https://developer.nvidia.com/downloads/jetson-orin-nx-and-orin-nano-series-pinmux-config-template. 
2. This will give you three new dtsi files. You need to match up what these are with your machine and meld them to get the changes you need from the machine. The relevant recipe is `tegra-bootfiles`
3. Build in these recipes `libgpiod libgpiod-tools libgpiod-dev`
4. Back at the command line run `gpioinfo` and grep on your gpio you want. For my case I wanted `GGPIO3_PCC.00`
5. Take the controller name (0 or 1 for me) and the line and you should now be able to `gpioset -c 1 12=1` to set. where the c is the controller number and 12 is the line number.

Good reference https://docs.nvidia.com/jetson/archives/r35.3.1/DeveloperGuide/text/HR/JetsonModuleAdaptationAndBringUp/JetsonOrinNxNanoSeries.html#generating-the-pinmux-dtsi-files

# Jetpack 4 instructions for Controlling the pin states on the Jetson TX2 SoM

There's two ways:
* Through bootloader configuration.
* Through using the virtual /sys filesystem in userspace.


## Pin settings in bootloader configuration

### Summary

You need to do the following:

1. Download an Microsoft Excel sheet(!) containing some macros(!!) and the L4T ("Linux For Tegra") package from Nvidias downloadcenter. Note: For this you need a Nvidia developer account.
1. In the Excel sheet, select the desired pin configuration using cell dropdown menus. Use the embedded macro to write out some device tree files.
1. Use a Python script which comes with L4T to convert the device tree files into something file the bootloader can understand.
1. Embed the bootloader configuration in the Yocto source tree.

### Detailed steps

As an example, the following guide walks you through reconfiguring pin A9 from it's default state (output GND) to input with weak pull-up.

1. The MS Excel part:
    1. Visit the [Nvidia developer download center](https://developer.nvidia.com/embedded/downloads) and search for `Jetson TX2 Series Pinmux`. [Here's a direct link for 1.08](https://developer.nvidia.com/embedded/dlc/jetson-tx2-series-module-pinmux). Download and run it with macros enabled.
    1. On the second sheet you'll find the configuration for pin `A9` on (at the time of writing) line 246. Cells in columns `AR` and `AS` define it as output grounding the signal. Change these cells to `Input` and `Int PU`.
    1. At the very top of the sheet, click the button labeled `Generate DT file`. Some dialogues will pop up which asks for stuff and have an effect on the filename.
1. The Python part:
    1. Go to the [Nvidia developer download center](https://developer.nvidia.com/embedded/downloads) and search for `Jetson Linux Driver Package (L4T)`. Follow the link to the `L4T x.y.z Release Page`. (For example, [here's the one for R32.4.3](https://developer.nvidia.com/embedded/linux-tegra-r32.4.3).) There, you should find a link labeled `L4T Driver Package (BSP)` leading to some tarball named similar to `Tegra186_Linux_Rx.y.z_aarch64.tbz2`. (Again, as an example [here's the one for R32.4.3](https://developer.nvidia.com/embedded/L4T/r32_Release_v4.3/t186ref_release_aarch64/Tegra186_Linux_R32.4.3_aarch64.tbz2).). Uncompress it and change to `Linux_for_Tegra/kernel/pinmux/t186/` inside.
    1. Run the `pinmux-dts2cfg.py` in the following way:
    ```
    python pinmux-dts2cfg.py \
        --pinmux \
        addr_info.txt \
        gpio_addr_info.txt \
        por_val.txt \
        --mandatory_pinmux_file mandatory_pinmux.txt \
        /path/to/your/excel-created/tegra18x-jetson-tx2-config-template-*-pinmux.dtsi \
        /path/to/your/excel-created/tegra18x-jetson-tx2-config-template-*-gpio-*.dtsi \
        1.0 \
        > /tmp/new.cfg
    ```
    If it throws errors, it might be [related to this](https://forums.developer.nvidia.com/t/pinmux-dts2cfg-py-errors-with-default-pinmux/185782).
1. Add a patch in your distro layer reflecting the pin settings in your `/tmp/new.cfg` created above.


## Controlling/reading the pin state from userspace

You can control/read the pin value from the virtual /sys filesystem [but not the pull up/down state](https://forums.developer.nvidia.com/t/how-to-configure-gpio-to-input-pullup-from-userspace/191759/5).

Software-wise, the GPIOs have other names than on the schematic. Nvidia doesn't make it easy to go from schematic name (like `A9`) to the /sys name (like `gpio488`). The following user contributed posts explain it better than anything Nvidia has come up with so far:
* https://forums.developer.nvidia.com/t/gpio-doesnt-work/49203/14
* https://forums.developer.nvidia.com/t/gpio-doesnt-work/49203/2
* [This post](https://forums.developer.nvidia.com/t/what-is-the-gpio-number-is-g8-and-f7-pin-at-tx2/82220/3) contains the equations in the links above solved for all possible input values.

Having found out the /sys name for your pin, you can take following snippets as an example:

The following snippet sets the gpio to output-low.
```
# GPIO488 is A9 on the SoM
pin=488
echo $pin > /sys/class/gpio/export
echo out  > /sys/class/gpio/gpio$pin/direction
echo 0    > /sys/class/gpio/gpio$pin/value
```

The following snippet sets the pin to input and reads its logical state:
```
# GPIO488 is A9 on the SoM
pin=488
echo $pin > /sys/class/gpio/export
echo in   > /sys/class/gpio/gpio$pin/direction
cat         /sys/class/gpio/gpio$pin/value
```