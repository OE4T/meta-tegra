DESCRIPTION = "SDK type target for building a standalone tarball containing tools needed for \
signing Jetson boot firmware files"
LICENSE = "MIT"

TOOLCHAIN_TARGET_TASK = ""

TOOLCHAIN_HOST_TASK = "\
    nativesdk-tegra-flashtools \
    nativesdk-tegra-helper-scripts \
    nativesdk-sdk-provides-dummy \
"
MULTIMACH_TARGET_SYS = "${SDK_ARCH}-nativesdk${SDK_VENDOR}-${SDK_OS}"
PACKAGE_ARCH = "${SDK_ARCH}_${SDK_OS}"
PACKAGE_ARCHS = ""
TARGET_ARCH = "none"
TARGET_OS = "none"

#SDK_PACKAGE_ARCHS += "buildtools-dummy-${SDKPKGSUFFIX}"

TOOLCHAIN_OUTPUTNAME ?= "${SDK_ARCH}-tegra-signing-tools-${DISTRO_VERSION}"

SDK_TITLE = "Tegra signing tools"

RDEPENDS = "${TOOLCHAIN_HOST_TASK}"

inherit populate_sdk toolchain-scripts-base nopackages
deltask install
deltask populate_sysroot
do_populate_sdk[stamp-extra-info] = "${PACKAGE_ARCH}"

REAL_MULTIMACH_TARGET_SYS = "none"
INHIBIT_DEFAULT_DEPS = "1"

create_sdk_files:append () {
	rm -f ${SDK_OUTPUT}/${SDKPATH}/site-config-*
	rm -f ${SDK_OUTPUT}/${SDKPATH}/environment-setup-*
	rm -f ${SDK_OUTPUT}/${SDKPATH}/version-*

	# Generate new (mini) sdk-environment-setup file
	script=${1:-${SDK_OUTPUT}/${SDKPATH}/environment-setup-${SDK_SYS}}
	touch $script
	echo 'export PATH="${SDKPATHNATIVE}${bindir_nativesdk}:${SDKPATHNATIVE}${sbindir_nativesdk}:${SDKPATHNATIVE}${base_bindir_nativesdk}:${SDKPATHNATIVE}${base_sbindir_nativesdk}:$PATH"' >> $script
	echo 'export OECORE_NATIVE_SYSROOT="${SDKPATHNATIVE}"' >> $script

	toolchain_create_sdk_version ${SDK_OUTPUT}/${SDKPATH}/version-${SDK_SYS}

	cat >> $script <<EOF
if [ -d "\$OECORE_NATIVE_SYSROOT/environment-setup.d" ]; then
	for envfile in \$OECORE_NATIVE_SYSROOT/environment-setup.d/*.sh; do
		. \$envfile
	done
fi
EOF

	if [ "${SDKMACHINE}" = "i686" ]; then
		echo 'export NO32LIBS="0"' >>$script
		echo 'echo "$BB_ENV_PASSTHROUGH_ADDITIONS" | grep -q "NO32LIBS"' >>$script
		echo '[ $? != 0 ] && export BB_ENV_PASSTHROUGH_ADDITIONS="NO32LIBS $BB_ENV_PASSTHROUGH_ADDITIONS"' >>$script
	fi
}
