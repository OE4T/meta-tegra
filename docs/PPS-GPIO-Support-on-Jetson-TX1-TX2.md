I thought I would add this here in the event someone else is searching for how to add a PPS input to TX1/TX2 systems.  Hours of reading and searching yielded nothing other than the fact that NVIDIA doesn't support it on the dev kits and they don't provide any more information. I hope that someone can take this and use it for what they need, whether on commercial carriers or even on the dev kit board -- maybe this is fairly common knowledge to those who work in device trees all the time, but for a noob to ARM and device trees, I would have found a page like this extremely valuable.

My setup is I have a TX1 on the Astro carrier from ConnectTech.  I'm using the pyro-r24.2.2 branch of meta-tegra and pyro for poky/meta-openembedded.

I requested the DTS files for the ASG001 (Astro carrier) from ConnectTech and created my own machine layer, using the jetson-tx1 machine from meta-tegra as a starting point.  This utilizes the 3.10 kernel.

To enable PPS support, I added the following block immediately below the gpio@6000d000 section of mono-tegra210-jetson-tx1-CTI-ASG001.dts:

```
        pps {
                gpios = <&{/gpio@6000d000} 187 0>;

                compatible = "pps-gpio";
                status = "okay";
        };
```

This only added PPS support to the device tree, however the 3.10 kernel doesn't support PPS GPIO clients on the device tree, so that support needed to be added by manually applying this patch to the source (I applied it in the tmp/work-shared kernel source git repo and created a patch I used in my linux-tegra bbappend):  [https://github.com/beagleboard/meta-beagleboard/blob/master/common-bsp/recipes-kernel/linux/linux-mainline-3.8/pps/0003-pps-gpio-add-device-tree-binding-and-support.patch](https://github.com/beagleboard/meta-beagleboard/blob/master/common-bsp/recipes-kernel/linux/linux-mainline-3.8/pps/0003-pps-gpio-add-device-tree-binding-and-support.patch)

For later releases (it appears as early as R27.1), PPS GPIO support for device trees is present in the linux-tegra kernel, so the only requirement is adding the pps block to the DTS.

Finally, ensure that CONFIG_PPS and CONFIG_PPS_CLIENT_GPIO are enabled in your kernel configuration (I copied the defconfig, modified it, and added a do_configure_prepend() to my bbappend).

```
do_configure_prepend() {
        cp ${WORKDIR}/defconfig-cti ${WORKDIR}/defconfig
}
```

At that point, build a typical image (I use core-image-full-cmdline - I take it others will work the same way) gives a functional PPS input into the kernel.