# UEFI Capsule Update

## Rollback protection

By default the `LowestSupportedVersion` field in UEFI Capsule FMP Payload Header is set to the current L4T version, which prevents bootloader downgrade.
`TEGRA_UEFI_LOWEST_SUPPORTED_VERSION` variable can be used to override this behaviour.

For example to allow downgrading to L4T 36.4.3 add the following line to your machine config:
```
TEGRA_UEFI_LOWEST_SUPPORTED_VERSION = "0x00240403"
```
