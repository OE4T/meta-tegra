DESCRIPTION = "Generates DTB overlay containing UEFI certificate information for Tegra platforms"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

inherit deploy nopackages

DEPENDS = "dtc-native tegra-flashtools-native util-linux-native efitools-native"

TEGRA_FLASH_DIR = "${RECIPE_SYSROOT_NATIVE}/usr/bin/tegra-flash"

TEGRA_UEFI_PK_CERT    ?= ""

TEGRA_UEFI_KEK_1_CERT ?= "${TEGRA_UEFI_KEK_CERT}"
TEGRA_UEFI_KEK_2_CERT ?= ""
TEGRA_UEFI_KEK_3_CERT ?= ""

TEGRA_UEFI_DB_1_KEY   ?= "${TEGRA_UEFI_DB_KEY}"
TEGRA_UEFI_DB_1_CERT  ?= "${TEGRA_UEFI_DB_CERT}"
TEGRA_UEFI_DB_2_CERT  ?= ""
TEGRA_UEFI_DB_3_CERT  ?= ""

do_configure() {
    if [ ! -s "${TEGRA_UEFI_PK_CERT}" ]; then
        bbfatal "Please provide a non-empty PK.crt"
    fi
    if [ ! -s "${TEGRA_UEFI_KEK_1_CERT}" ]; then
        bbfatal "Please provide a non-empty KEK_1.crt"
    fi
    if [ ! -s "${TEGRA_UEFI_DB_1_KEY}" ]; then
        bbfatal "Please provide a non-empty db_1.key"
    fi
    if [ ! -s "${TEGRA_UEFI_DB_1_CERT}" ]; then
        bbfatal "Please provide a non-empty db_1.crt"
    fi

    conf_file="${B}/uefi_keys.conf"
    GUID=$(uuidgen)

    echo "# Auto-generated UEFI keys configuration" > ${conf_file}
    echo >> ${conf_file}
    echo "UEFI_DB_1_KEY_FILE=\"$(basename ${TEGRA_UEFI_DB_1_KEY})\"" >> ${conf_file}
    echo "UEFI_DB_1_CERT_FILE=\"$(basename ${TEGRA_UEFI_DB_1_CERT})\"" >> ${conf_file}
    echo >> ${conf_file}
    cert-to-efi-sig-list -g "${GUID}" "${TEGRA_UEFI_PK_CERT}" "${B}/PK.esl"
    echo "UEFI_DEFAULT_PK_ESL=\"PK.esl\"" >> ${conf_file}
    echo >> ${conf_file}

    cnt=0
    for cert in "${TEGRA_UEFI_KEK_1_CERT}" "${TEGRA_UEFI_KEK_2_CERT}" "${TEGRA_UEFI_KEK_3_CERT}"; do
        idx=$(expr $cnt + 1)
        esl="${B}/KEK_${idx}.esl"

        if [ -n "${cert}" ]; then
            cert-to-efi-sig-list -g "${GUID}" "${cert}" "${esl}"
            echo "UEFI_DEFAULT_KEK_ESL_${cnt}=\"KEK_${idx}.esl\"" >> ${conf_file}
        fi

        cnt=$(expr $cnt + 1)
    done
    echo >> ${conf_file}

    cnt=0
    for cert in "${TEGRA_UEFI_DB_1_CERT}" "${TEGRA_UEFI_DB_2_CERT}" "${TEGRA_UEFI_DB_3_CERT}"; do
        idx=$(expr $cnt + 1)
        esl="${B}/DB_${idx}.esl"

        if [ -n "${cert}" ]; then
            cert-to-efi-sig-list -g "${GUID}" "${cert}" "${esl}"
            echo "UEFI_DEFAULT_DB_ESL_${cnt}=\"db_${idx}.esl\"" >> ${conf_file}
        fi

        cnt=$(expr $cnt + 1)
    done
}

do_compile() {
    # This tool expects all files mentioned on uefi_keys.conf
    # to exist at the same directory as it.
    # see https://docs.nvidia.com/jetson/archives/r36.4.3/DeveloperGuide/SD/Security/SecureBoot.html#create-a-uefi-keys-config-file
    cp ${TEGRA_UEFI_DB_1_KEY} ${B}
    cp ${TEGRA_UEFI_DB_1_CERT} ${B}

    ${TEGRA_FLASH_DIR}/gen_uefi_keys_dts.sh ${B}/uefi_keys.conf
}

do_install() {
    install -d ${D}${datadir}/tegra-uefi-keys/
    install -m 0644 ${B}/UefiDefaultSecurityKeys.dtbo ${D}${datadir}/tegra-uefi-keys/
    if [ -e "${B}/UefiUpdateSecurityKeys.dtbo" ]; then
        install -m 0644 ${B}/UefiUpdateSecurityKeys.dtbo ${D}${datadir}/tegra-uefi-keys/
    fi
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/UefiDefaultSecurityKeys.dtbo ${DEPLOYDIR}/
    if [ -e "${B}/UefiUpdateSecurityKeys.dtbo" ]; then
        install -m 0644 ${B}/UefiUpdateSecurityKeys.dtbo ${DEPLOYDIR}/
    fi
}

addtask deploy before do_build after do_compile

PACKAGE_ARCH = "${MACHINE_ARCH}"
