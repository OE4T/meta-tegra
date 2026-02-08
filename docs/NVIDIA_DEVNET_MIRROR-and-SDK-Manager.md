Jetpack 4.3 content as well as [CUDA host tool support before this PR](https://github.com/OE4T/meta-tegra/pull/677) was not anonymously downloadable from NVIDIA's servers and requires an `NVIDIA_DEVNET_MIRROR` setup with the path to SDK manager downloads.  

Attempting to build recipes which require host tool CUDA support will faied with message:
```
ERROR: Nothing PROVIDES 'cuda-binaries-ubuntu1804-native'
cuda-binaries-ubuntu1804-native was skipped: Recipe requires NVIDIA_DEVNET_MIRROR setup
```
To resolve, you must use the NVIDIA SDK manager to download the cotent to your build host, then add this setting to your build configuration (e.g., in conf/local.conf under your build directory):
```
NVIDIA_DEVNET_MIRROR = "file://path/to/downloads"
```
By default, the SDK Manager downloads to a directory called Downloads/nvidia/sdkm_downloads under your $HOME directory, so use that path in the above setting.

See example in [tegra-demo-distro](https://github.com/OE4T/tegra-demo-distro/blob/b034c9293c6a7cc5fcc11cf00e6df42c1600c20b/layers/meta-tegrademo/conf/template-tegrademo/local.conf.sample#L36) which demonstrates setting the path to the default download directory used by NVIDIA SDK manager.

