## Dynamically apply the RT patches to Kirkstone

1. Append the recipe to run an additional step prior to running the do_patch stage

`recipes-kernel/linux/linux-tegra_4.9.bbappend`

```bitbake
do_patch:prepend() {
    oldwd=$PWD
    cd ${S}/scripts
    ./rt-patch.sh apply-patches
    cd ${S}
    git add . && git commit -m 'Apply PREEMPT_RT patches'
    cd $oldwd
}
```

## Dynamically apply the RT patches to Dunfell

1. Append the recipe to run an additional step prior to running the do_patch stage

`recipes-kernel/linux/linux-tegra_4.9.bbappend`

```bitbake
do_patch_prepend() {
    oldwd=$PWD
    cd ${S}/scripts
    ./rt-patch.sh apply-patches
    cd ${S}
    git add . && git commit -m 'Apply PREEMPT_RT patches'
    cd $oldwd
}
```


## How to use the Real-Time Kernel by switching to a patched branch

1. Find which git has your meta-tegra is currently using
```
user@pc:~/meta-tegra$ cat recipes-kernel/linux/linux-tegra_4.9.bb | grep -n SRCREV -A5 -B5
13-
14-LINUX_VERSION_EXTENSION ?= "-l4t-r${@'.'.join(d.getVar('L4T_VERSION').split('.')[:2])}"
15-SCMVERSION ??= "y"
16-
17-SRCBRANCH = "patches${LINUX_VERSION_EXTENSION}"
18:SRCREV = "0be1a57448010ae60505acf4e2153638455cee7c"
19-KBRANCH = "${SRCBRANCH}"
20-SRC_REPO = "github.com/OE4T/linux-tegra-4.9"
21-KERNEL_REPO = "${SRC_REPO}"
22-SRC_URI = "git://${KERNEL_REPO};name=machine;branch=${KBRANCH} \
23-	   ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
```
2. Fork https://github.com/OE4T/linux-tegra-4.9
3. `git clone https://github.com/you/linux-tegra-4.9`
4. `cd linux-tegra-4.9`
5. `git checkout -b patches-l4t-r32.4-rt patches-l4t-r32.4` (or specify the git hash from the SRCREV setting in the recipe)
6. `cd scripts`
7. `./rt-patch.sh apply-patches`
8. `cd ..`
9. `git add .`
10. `git commit -m "Applied PREEMPT RT Patch to kernel"`
11. `git push --set-upstream origin patches-l4t-r32.4-rt`
12. git log to find the commit hash for your rt patched kernel source
13. Now go back to your meta-tegra folder
14. `touch recipes-kernel/linux/linux-tegra_4.9.bbappend`
15. put the below in your linux-tegra_4.9.bbappend

```
SRCBRANCH = "patches${LINUX_VERSION_EXTENSION}-rt"
SRCREV = "##YOUR_HASH###"
KBRANCH = "${SRCBRANCH}"
SRC_REPO = "##YOUR_FORK###"
KERNEL_REPO = "${SRC_REPO}"
SRC_URI = "git://${KERNEL_REPO};name=machine;branch=${KBRANCH};protocol=ssh \
           ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
"
```
