PACKAGECONFIG:tegra ?= "tui libunwind"
# Need to enable -fcommon on C compilations and disable POSIX yacc
# mode in bison
EXTRA_OEMAKE:append:tegra = " EXTRA_CFLAGS='-ldw -fcommon' YFLAGS='--file-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}'"
