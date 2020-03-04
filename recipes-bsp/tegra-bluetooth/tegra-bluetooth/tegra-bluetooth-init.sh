#!/bin/sh
[ -x /usr/sbin/brcm_patchram_plus ] || exit 0
[ ! -f /proc/bluetooth/sleep/lpm ] || echo 1 > /proc/bluetooth/sleep/lpm
exec /usr/sbin/brcm_patchram_plus  \
                       --enable_hci --use_baudrate_for_download \
                       --scopcm=0,2,0,0,0,0,0,0,0,0 --baudrate 3000000 \
                       --patchram /lib/firmware/bcm4354.hcd --no2bytes \
                       --enable_lpm --tosleep=50000 /dev/ttyTHS3
