require tegra-shared-binaries.inc

# The nvidia libraries are linked against glibc so other libc can't be used
python __anonymous () {
    if d.getVar('TCLIBC', True) != "glibc":
        raise bb.parse.SkipPackage("nvidia libraries requires glibc")
}
