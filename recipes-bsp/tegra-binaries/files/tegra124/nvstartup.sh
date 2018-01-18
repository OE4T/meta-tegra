#! /bin/bash

#Port of original upstart startup script for TK1 to systemd
	
	# power state
	if [ -e /sys/power/state ]; then
		chmod 0666 /sys/power/state
	fi

	# Set minimum cpu freq.
	if [ -e /sys/devices/soc0/family ]; then
		SOCFAMILY="`cat /sys/devices/soc0/family`"
	fi

	if [ "$SOCFAMILY" = "Tegra13" ] &&
		[ -e /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq ]; then
		sudo bash -c "echo -n 510000 > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
	fi

	# CPU hotplugging
	if [ -d /sys/devices/system/cpu/cpuquiet/tegra_cpuquiet ] ; then
		echo 500 > /sys/devices/system/cpu/cpuquiet/tegra_cpuquiet/down_delay
		echo 1 > /sys/devices/system/cpu/cpuquiet/tegra_cpuquiet/enable
	elif [ -w /sys/module/cpu_tegra3/parameters/auto_hotplug ] ; then
		# compatibility for prior kernels without cpuquiet support
		echo 1 > /sys/module/cpu_tegra3/parameters/auto_hotplug
	fi

	# lp2 idle state
	if [ -e /sys/module/cpuidle/parameters/power_down_in_idle ] ; then
		echo "Y" > /sys/module/cpuidle/parameters/power_down_in_idle
	elif [ -e /sys/module/cpuidle/parameters/lp2_in_idle ] ; then
		# compatibility for prior kernels
		echo "Y" > /sys/module/cpuidle/parameters/lp2_in_idle
	fi

	# mmc read ahead size
	if [ -e /sys/block/mmcblk0/queue/read_ahead_kb ]; then
	   echo 2048 > /sys/block/mmcblk0/queue/read_ahead_kb
	fi
	if [ -e /sys/block/mmcblk1/queue/read_ahead_kb ]; then
		echo 2048 > /sys/block/mmcblk1/queue/read_ahead_kb
	fi

	#FIXME remove once kernel CL merges into main Bug 1231069
	for uartInst in 0 1 2 3
	do
		uartNode="/dev/ttyHS$uartInst"
		if [ -e "$uartNode" ]; then
			ln -s /dev/ttyHS$uartInst /dev/ttyTHS$uartInst
		fi
	done
	# remove power to dc.0 for jetson-tk1
	machine=`cat /sys/devices/soc0/machine`
	if [ "${machine}" = "jetson-tk1" ] ; then
		echo 4 > /sys/class/graphics/fb0/blank
		if [ -e /sys/devices/platform/tegra-otg/enable_device ] ; then
			echo 0 > /sys/devices/platform/tegra-otg/enable_device
		fi
		if [ -e /sys/devices/platform/tegra-otg/enable_host ] ; then
			echo 1 > /sys/devices/platform/tegra-otg/enable_host
		fi
	fi

	# CPU frequency governor
	if [ -e /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors ]; then
		read governors < /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors
		case $governors in
			*interactive*)
				echo interactive > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
				if [ -e /sys/devices/system/cpu/cpufreq/interactive ] ; then
					echo "1224000" > /sys/devices/system/cpu/cpufreq/interactive/hispeed_freq
					echo "19000" >/sys/devices/system/cpu/cpufreq/interactive/above_hispeed_delay
					echo "65" > /sys/devices/system/cpu/cpufreq/interactive/target_loads
					echo "30000" > /sys/devices/system/cpu/cpufreq/interactive/min_sample_time
				fi
					;;
			*)
					;;
		esac
	fi

	# CPU frequency boost on input event
	if [ -e /sys/module/input_cfboost/parameters/boost_freq ]; then
		echo "1224000" > /sys/module/input_cfboost/parameters/boost_freq
	fi
