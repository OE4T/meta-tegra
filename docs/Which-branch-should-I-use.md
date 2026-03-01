# Currently maintained branches #

Last update: 28 Feb 2026

The [OE4T demo distro](https://github.com/OE4T/tegra-demo-distro) has corresponding branches to demonstrate full builds for the Jetson platforms supported by this layer.

Branches are named for the OE-Core branch name each one tracks; see [this page](https://wiki.yoctoproject.org/wiki/Releases) for Yocto Project releases and branches.

For Jetson Linux (L4T) releases:
* our `master` branch, and other `master-` prefixed branches, track the latest available Jetson Linux releases and OE-Core master.
* branches corresponding to regular (non-LTS) OE-Core release branches track the latest Jetson Linux release at the time the branch is created
* branches corresponding to long-term support (LTS) OE-Core release branches are kept up-to-date with the Jetson Linux releases. When there is a significant Jetson Linux upgrade, an additional LTS branch is created for the older release series.

Active branches:
* **master** - never stable, [L4T R36.5.0/JetPack 6.2.2](JetPack-6.2.2-L4T-R36.5.0-Notes.md) for AGX Orin/Orin NX/Orin Nano
* **master-l4t-r38.4.x** - never stable, [L4T R38.4.0/JetPack 7.1](JetPack-7.1-L4T-R38.4.x-Notes.md) for AGX Thor
* **whinlatter** - [L4T R36.4.4/JetPack 6.2.1](JetPack-6.2.1-L4T-R36.4.4-Notes.md) for AGX Orin/Orin NX/Orin Nano
* **scarthgap** - [L4T R36.5.0/JetPack 6.2.2](JetPack-6.2.2-L4T-R36.5.0-Notes.md) for AGX Orin/Orin NX/Orin Nano
* **scarthgap-l4t-r35.x** - [L4T R35.6.4/JetPack 5.1.6](JetPack-5.1.6-L4T-R35.6.4-Notes.md) for AGX Xavier/Xavier NX/AGX Orin/Orin NX/Orin Nano
* **kirkstone** - [L4T R35.6.4/JetPack 5.1.6](JetPack-5.1.6-L4T-R35.6.4-Notes.md) for AGX Xavier/Xavier NX/AGX Orin/Orin NX/Orin Nano
* **kirkstone-l4t-r32.7.x** - [L4T R32.7.6/JetPack 4.6.6](L4T-R32.7.6-Notes.md) for TX1/TX2/TX2-NX/Xavier/Xavier-NX/Nano/Nano-2GB


Deprecated branches that receive less attention:
* **master-l4t-r38.2.x** - never stable, [L4T R38.2.2/JetPack 7.0](JetPack-7.0-L4T-R38.2.x-Notes.md) for AGX Thor
* **walnascar** - [L4T R36.4.3/JetPack 6.2](JetPack-6.2-L4T-R36.4.3-Notes.md) for AGX Orin/Orin NX/Orin Nano

Older branches, no longer actively maintained:
* **styhead** - [L4T R36.4.0/JetPack 6.1](JetPack-6.1-L4T-R36.4.0-Notes.md) for AGX Orin/Orin NX/Orin Nano
* **nanbield** - [L4T R35.4.1/JetPack 5.1.2](JetPack-5.1.2-L4T-R35.4.1-Notes.md) for AGX Xavier/Xavier NX/AGX Orin/Orin NX/Orin Nano
* **mickledore** - [L4T R35.4.1/JetPack 5.1.2](JetPack-5.1.2-L4T-R35.4.1-Notes.md) for AGX Xavier/Xavier NX/AGX Orin/Orin NX/Orin Nano
* **langdale** - [L4T R35.2.1/JetPack 5.1](JetPack-5.1-L4T-R35.2.1-Notes.md) for AGX Xavier/Xavier NX/AGX Orin/Orin NX
* **honister** - [L4T R32.6.1/JetPack 4.6](L4T-R32.6.1-Notes.md) for TX1/TX2/TX2-NX/Xavier/Xavier-NX/Nano/Nano-2GB
* **hardknott** - [L4T R32.5.2/JetPack 4.5.1](L4T-R32.5.2-Notes.md) for TX1/TX2/TX2-NX/Xavier/Xavier-NX/Nano/Nano-2GB
* **gatesgarth** - [L4T R32.4.4/JetPack 4.4.1](L4T-R32.4.4-Notes.md) for TX1/TX2/Xavier/Xavier-NX/Nano/Nano-2GB
* **dunfell-l4t-r32.6.1** - [L4T R32.6.1/JetPack 4.6](L4T-R32.6.1-Notes.md) for TX1/TX2/TX2-NX/Xavier/Xavier-NX/Nano/Nano-2GB
* **dunfell-l4t-r32.5.0** - [L4T R32.5.2/JetPack 4.5.1](L4T-R32.5.2-Notes.md) for TX1/TX2/TX2-NX/Xavier/Xavier-NX/Nano/Nano-2GB
* **dunfell-l4t-r32.4.3** - [L4T R32.4.3/JetPack 4.4](L4T-R32.4.3-Notes.md) for TX1/TX2/Xavier/Xavier-NX/Nano
* **dunfell-l4t-r32.4.2** - L4T R32.4.2/JetPack 4.4DP for TX1/TX2/Xavier/Xavier-NX/Nano
* **dunfell-l4t-r32.3.1** - [L4T R32.3.1/JetPack 4.3](L4T-R32.3.1-Notes.md) for TX1/TX2/Xavier/Nano
* **dunfell** - [L4T R32.7.4/JetPack 4.6.4](L4T-R32.7.4-Notes.md) for TX1/TX2/TX2-NX/Xavier/Xavier-NX/Nano/Nano-2GB
* **zeus-l4t-r32.3.1** - L4T R32.3.1/JetPack 4.3 for TX1/TX2/Xavier/Nano
* **zeus** - L4T R32.2.3/JetPack 4.2.3 for TX1/TX2/Xavier/Nano
* **warrior** - L4T R32.1/JetPack 4.2 for TX1/TX2/Xavier/Nano, L4T R21.7 for TK1
* **warrior-l4t-r32.2** - L4T R32.2/JetPack 4.2.1 for TX1/TX2/Xavier/Nano, L4T R21.7 for TK1
* **thud** - L4T R28.2.1 for TX1 and TX2, L4T R21.7 for TK1
* **thud-l4t-r28.3** - L4T R28.3 for TX1/TX2, L4T R21.7 for TK1
* **thud-l4t-r32.1** - L4T R32.1 for TX1/TX2, L4T R21.7 for TK1 (not fully tested)
* **thud-l4t-r32.3.1** - L4T R32.3.1/JetPack 4.3 for TX1/TX2/Xavier/Nano (not fully tested)
* **sumo** - L4T R28.4.0 for TX1 and TX2, L4T R21.7 for TK1
* **rocko** - L4T R28.1 for TX1 and TX2, L4T R21.6 for TK1
    * **rocko-l4t-r28.2** - updates TX1 and TX2 to L4T R28.2.1, TK1 to L4T R21.7
* **pyro** - L4T R24.2.1 for TX1, R27.1 for TX2
    * **pyro-l4t-r24.2.2** - updates TX1 to L4T R24.2.2
    * **pyro-l4t-r28.1** - updates TX1 and TX2 to L4T R28.1
* **morty** - L4T R24.2.1 for TX1, R21.5 for TK1, no TX2 support
* **krogoth** - L4T R24.1 for TX1
* **jethro** - used for initial development, very out of date

Work-in-progress branches: any branch prefixed with **wip-** is work in progress, and can radically change or be deleted at any time.
