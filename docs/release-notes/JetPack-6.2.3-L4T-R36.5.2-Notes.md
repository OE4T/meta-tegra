# Changes from L4T R36.5.0/JetPack 6.2.2

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R36.5.2 page](https://developer.nvidia.com/embedded/jetson-linux-r3652)
* [JetPack 6.2.3 page](https://developer.nvidia.com/embedded/jetpack-sdk-623)

## BSP changes

This release mainly contains bugfixes. 

See NVIDIA release notes for information on other changes/improvements.

### Kernel changes

The Linux kernel (`linux-jammy-nvidia-tegra`) was updated to `5.15.199`.

The kernel source is now fetched directly from the upstream NVIDIA repository
([`nv-tegra/3rdparty/canonical/linux-jammy`](https://gitlab.com/nvidia/nv-tegra/3rdparty/canonical/linux-jammy),
branch `l4t/l4t-r36.5.2-Ubuntu-nvidia-tegra-igx-1050.50`) instead of the OE4T fork
(`OE4T/linux-jammy-nvidia-tegra`) that was used previously. Any changes needed on top of
the NVIDIA sources are now carried as patches in the recipe rather than as commits on a
forked branch. For this release, that is two backported upstream fixes for build
reproducibility (`build_OID_registry` and `conmakehash` no longer embed the full source
path in the generated files).

The NVIDIA out-of-tree drivers (`nvidia-kernel-oot`) have been updated to `jetson_36.5.2` tag. 

## JetPack changes

No major updates to the JetPack SDK.

## DeepStream SDK

No updates on the compute stack (`CUDA`, `cuDNN`, `TensorRT`).

No update to the DeepStream SDK. Version 7.1 remains compatible with JetPack 6.2.3.
