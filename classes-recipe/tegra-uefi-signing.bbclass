def tegra_uefi_signing_deps(d, tasks=False):
    if not d.getVar('TEGRA_UEFI_DB_KEY') or not d.getVar('TEGRA_UEFI_DB_CERT'):
        return ''
    deps = ['openssl-native', 'sbsigntool-native', 'coreutils-native']
    if tasks:
        return ' '.join([d + ':do_populate_sysroot' for d in deps])
    return ' '.join(deps)

TEGRA_UEFI_DB_KEY ??= ""
TEGRA_UEFI_DB_CERT ??= ""
TEGRA_UEFI_SIGNING_TASKDEPS ?= "${@tegra_uefi_signing_deps(d, tasks=True)}"
TEGRA_UEFI_SIGNING_DEPENDS ?= "${@tegra_uefi_signing_deps(d)}"
TEGRA_UEFI_USE_SIGNED_FILES ?= "${@'true' if d.getVar('TEGRA_UEFI_DB_KEY') and d.getVar('TEGRA_UEFI_DB_CERT') else 'false'}"

# Standard signing, input file modified with signature
tegra_uefi_sbsign() {
    if [ -n "${TEGRA_UEFI_DB_KEY}" -a -n "${TEGRA_UEFI_DB_CERT}" ]; then
        sbsign --key "${TEGRA_UEFI_DB_KEY}" --cert "${TEGRA_UEFI_DB_CERT}" --output "$1" "$1"
    fi
}

# Separate signature file, for NVIDIA's L4TLauncher 
tegra_uefi_split_sign() {
    if [ -n "${TEGRA_UEFI_DB_KEY}" -a -n "${TEGRA_UEFI_DB_CERT}" ]; then
        openssl cms -sign -signer "${TEGRA_UEFI_DB_CERT}" -inkey "${TEGRA_UEFI_DB_KEY}" -binary -in "$1" -outform der -out "$1".sig
    fi
}

# Signature attached to end, another NVIDIA special
# Input file remains intact; output file has ".signed" suffix
tegra_uefi_attach_sign() {
    cp "$1" "$1.signed"
    if [ -n "${TEGRA_UEFI_DB_KEY}" -a -n "${TEGRA_UEFI_DB_CERT}" ]; then
        openssl cms -sign -signer "${TEGRA_UEFI_DB_CERT}" -inkey "${TEGRA_UEFI_DB_KEY}" -binary -in "$1" -outform der -out "$1".sig.tmp
        truncate --size=%2048 "$1.signed"
        cat "$1".sig.tmp >> "$1.signed"
        rm "$1".sig.tmp
    fi
}
