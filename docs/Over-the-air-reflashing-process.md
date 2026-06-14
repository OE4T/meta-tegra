There may be times when you need to perform the equivalent of a
re-flashing of your Jetson-based device without being able to use
the normal flashing process via USB. This is possible, although
there are some risks, and it requires careful setup and testing.

Possible applications:

* You need to alter the layout of the partitions in the Jetson's
  eMMC storage.

* You need to update a Jetson running software based off an older
  version of the L4T BSP to a newer version that requires a modified
  layout of the eMMC and/or SPI flash (for Jetsons that have a
  SPI flash boot device).

* You just need the equivalent of a full "factory reset" that
  restores the device to a pristine state.

This has been more necessary on previous Jetpack releases where
no supported upgrade path existed.

See legacy branch docs content for an implementation specific
to old releases which could be customized to support more recent
jetpack releases.

Updates to this page to document a similar approach in the
current master branch are welcome.