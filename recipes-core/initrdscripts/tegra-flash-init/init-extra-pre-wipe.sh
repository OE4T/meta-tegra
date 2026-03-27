#!/bin/sh

# Run custom extra pre wipe init script(s) provided from seperate recipes.

init_extra_pre_wipe_dir="/init-extra-pre-wipe.d"

if [ -d "${init_extra_pre_wipe_dir}" ] && [ "$(ls -A ${init_extra_pre_wipe_dir})" ]; then
  for script in "${init_extra_pre_wipe_dir}"/*; do
    ./"${script}" 2>&1 | tee "/tmp/flashpkg/flashpkg/logs/custom-extra-pre-wipe-$(basename "${script}").log"
  done
else
  echo "No init_extra_pre_wipe was found, ignoring" > /tmp/flashpkg/flashpkg/logs/custom-extra-pre-wipe.log
fi
