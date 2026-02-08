# SPI support on 40 pin header - Jetson Nano devkit

For enabling SPI support for Jetson Nano please use this [patch](https://github.com/OE4T/meta-tegra/files/6452741/0001-nvidia-platform-t210-enable-SPI0-pins-on-40-pin-head.patch.txt). This patch cover `Jetson nano` (eMMC and SDcard version) only.

SPI devices after applying the patch are available on `/dev/spidev0.0` and `/dev/spidev0.1` (as generic spidev devices). You can use `spidev_test` tool and shortcut `MOSI`/`MISO` pins to test if communication is working as expected.

**Note:** some extension boards with SPI chips maybe will not work due to the level shifters which are assembled on 40 pin header. 
Please refer to [40 pin header considerations](https://developer.nvidia.com/embedded/downloads#?search=40-Pin%20Expansion%20Header%20GPIO%20Usage%20Considerations) for more details.  