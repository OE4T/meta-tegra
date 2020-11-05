#!/bin/sh
compiler="$1"
shift
if [ $(basename "$compiler") = "nvcc" ]; then
    "$compiler" --version
else
    "$@"
fi
