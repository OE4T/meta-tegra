#!/bin/sh

# Run custom extra init script(s) provided from seperate recipes.

init_extra_dir="/init-extra.d"

if [ -d $init_extra_dir ] && [ "$(ls -A $init_extra_dir)" ]; then
  for init_extra in "$init_extra_dir"/*; do
    ./"$init_extra" 2>&1 | tee "/tmp/flashpkg/flashpkg/logs/custom-extra-$(basename "$init_extra").log"
  done
else
  echo "No init_extra was found, ignoring" > /tmp/flashpkg/flashpkg/logs/custom-extra.log
fi
