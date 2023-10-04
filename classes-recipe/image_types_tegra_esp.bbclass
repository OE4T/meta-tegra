# Adapted from oe_mkext234fs in image_types.bbclass
oe_mkespfs() {
	fstype="$1"
	extra_imagecmd=""

	if [ $# -gt 1 ]; then
		shift
		extra_imagecmd=$@
	fi

	# Create a sparse image block.  ESP partition must be 64K blocks.
	bbdebug 1 Executing "dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype seek=65536 count=0 bs=1024"
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype seek=65536 count=0 bs=1024
	bbdebug 1 "Actual Rootfs size:  `du -s ${IMAGE_ROOTFS}`"
	bbdebug 1 "Actual Partition size: `stat -c '%s' ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype`"
	bbdebug 1 Executing "mkfs.vfat -F 32 -I $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype "
	mkfs.vfat -F 32 -I $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype
	mcopy -i ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype -s ${IMAGE_ROOTFS}/* ::/
	# Error codes 0-3 indicate successfull operation of fsck (no errors or errors corrected)
	fsck.vfat -pvfV ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype
}
do_image_esp[depends] += "dosfstools-native:do_populate_sysroot mtools-native:do_populate_sysroot"
IMAGE_TYPES += "esp"
IMAGE_CMD:esp = "oe_mkespfs esp ${EXTRA_IMAGECMD}"
EXTRA_IMAGECMD:esp ?= ""
