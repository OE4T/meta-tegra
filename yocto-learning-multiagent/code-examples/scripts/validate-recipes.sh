#!/bin/bash
# Validate BitBake Recipes
#
# This script validates BitBake recipes for syntax errors,
# dependency issues, and best practices.
#
# Usage:
#   ./validate-recipes.sh [options] [recipe...]
#
# Options:
#   -l, --layer <path>     Validate all recipes in layer
#   -a, --all              Validate all recipes in all layers
#   -v, --verbose          Verbose output
#   -f, --fix              Attempt to fix common issues
#   -h, --help             Show help
#
# Examples:
#   ./validate-recipes.sh simple-app
#   ./validate-recipes.sh -l meta-custom
#   ./validate-recipes.sh --all --verbose

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
VERBOSE=0
FIX=0
ERRORS=0
WARNINGS=0
PASSED=0

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
    ((WARNINGS++))
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
    ((ERRORS++))
}

log_pass() {
    echo -e "${GREEN}[PASS]${NC} $1"
    ((PASSED++))
}

log_debug() {
    if [ $VERBOSE -eq 1 ]; then
        echo -e "${BLUE}[DEBUG]${NC} $1"
    fi
}

print_usage() {
    cat << EOF
Usage: $0 [options] [recipe...]

Validate BitBake recipes for errors and best practices

Options:
  -l, --layer <path>     Validate all recipes in layer directory
  -a, --all              Validate all recipes in all layers
  -v, --verbose          Enable verbose output
  -f, --fix              Attempt to fix common issues (experimental)
  -h, --help             Show this help

Examples:
  $0 simple-app gpio-lib
  $0 -l meta-custom/recipes-apps
  $0 --all --verbose

Checks performed:
  - Syntax validation
  - Required variables
  - License compliance
  - File structure
  - Dependency verification
  - Common pitfalls

EOF
}

check_bitbake_env() {
    log_debug "Checking BitBake environment..."

    if ! command -v bitbake &> /dev/null; then
        log_error "BitBake not found. Please source oe-init-build-env"
        return 1
    fi

    log_pass "BitBake environment OK"
    return 0
}

validate_recipe_syntax() {
    local recipe=$1

    log_debug "Validating recipe syntax: $recipe"

    # Parse recipe with bitbake
    if bitbake -e $recipe > /dev/null 2>&1; then
        log_pass "$recipe: Syntax OK"
        return 0
    else
        log_error "$recipe: Syntax error"
        bitbake -e $recipe 2>&1 | grep -i error || true
        return 1
    fi
}

check_required_variables() {
    local recipe_file=$1
    local recipe_name=$(basename $recipe_file .bb)

    log_debug "Checking required variables in: $recipe_file"

    local required_vars=(
        "SUMMARY"
        "LICENSE"
        "LIC_FILES_CHKSUM"
    )

    local has_error=0

    for var in "${required_vars[@]}"; do
        if ! grep -q "^${var}\s*=" $recipe_file; then
            log_warn "$recipe_name: Missing required variable: $var"
            has_error=1
        fi
    done

    if [ $has_error -eq 0 ]; then
        log_pass "$recipe_name: All required variables present"
    fi

    return $has_error
}

check_license_compliance() {
    local recipe_file=$1
    local recipe_name=$(basename $recipe_file .bb)

    log_debug "Checking license compliance: $recipe_file"

    # Check for LICENSE variable
    if ! grep -q "^LICENSE\s*=" $recipe_file; then
        log_error "$recipe_name: LICENSE variable missing"
        return 1
    fi

    # Check for LIC_FILES_CHKSUM
    if ! grep -q "^LIC_FILES_CHKSUM\s*=" $recipe_file; then
        log_error "$recipe_name: LIC_FILES_CHKSUM missing"
        return 1
    fi

    # Warn about placeholder checksums
    if grep -q "md5=placeholder" $recipe_file; then
        log_warn "$recipe_name: Placeholder license checksum found"
        return 1
    fi

    log_pass "$recipe_name: License compliance OK"
    return 0
}

check_file_structure() {
    local recipe_file=$1
    local recipe_dir=$(dirname $recipe_file)
    local recipe_name=$(basename $recipe_file .bb)
    local pn=$(echo $recipe_name | sed 's/_[0-9].*//')  # Remove version

    log_debug "Checking file structure: $recipe_file"

    # Check for files directory
    local files_dir="${recipe_dir}/${pn}"
    if [ ! -d "$files_dir" ]; then
        files_dir="${recipe_dir}/files"
    fi

    if [ -d "$files_dir" ]; then
        log_debug "Files directory found: $files_dir"

        # Check if SRC_URI references files
        if grep -q "file://" $recipe_file; then
            local missing_files=()

            # Extract file:// URIs and check if they exist
            while IFS= read -r file_uri; do
                local filename=$(echo $file_uri | sed 's/.*file:\/\///;s/\s.*//')
                if [ ! -f "$files_dir/$filename" ]; then
                    missing_files+=("$filename")
                fi
            done < <(grep "file://" $recipe_file | grep -v "^#")

            if [ ${#missing_files[@]} -gt 0 ]; then
                log_warn "$recipe_name: Missing files: ${missing_files[*]}"
                return 1
            fi
        fi
    fi

    log_pass "$recipe_name: File structure OK"
    return 0
}

check_dependencies() {
    local recipe=$1

    log_debug "Checking dependencies: $recipe"

    # Check build dependencies
    if bitbake -g $recipe > /dev/null 2>&1; then
        log_pass "$recipe: Dependencies OK"

        # Check for circular dependencies
        if [ -f "pn-depends.dot" ]; then
            if grep -q "cycle" pn-depends.dot 2>/dev/null; then
                log_warn "$recipe: Possible circular dependency detected"
                rm -f pn-depends.dot task-depends.dot
                return 1
            fi
            rm -f pn-depends.dot task-depends.dot
        fi

        return 0
    else
        log_error "$recipe: Dependency resolution failed"
        return 1
    fi
}

check_common_issues() {
    local recipe_file=$1
    local recipe_name=$(basename $recipe_file .bb)

    log_debug "Checking for common issues: $recipe_file"

    local issues=0

    # Check for hardcoded paths
    if grep -qE "/usr/local|/opt" $recipe_file; then
        log_warn "$recipe_name: Hardcoded path found (use variables instead)"
        ((issues++))
    fi

    # Check for missing S variable when using local files
    if grep -q "file://" $recipe_file && ! grep -q "^S\s*=" $recipe_file; then
        log_warn "$recipe_name: Using local files but S not set (should be S = \"\${WORKDIR}\")"
        ((issues++))
    fi

    # Check for deprecated syntax
    if grep -qE "DEPENDS_\$\{PN\}|RDEPENDS_\$\{PN\}" $recipe_file; then
        log_warn "$recipe_name: Old-style DEPENDS/RDEPENDS syntax found"
        ((issues++))
    fi

    # Check for missing do_install
    if ! grep -q "do_install()" $recipe_file && ! grep -q "inherit.*autotools\|cmake\|meson" $recipe_file; then
        log_warn "$recipe_name: No do_install task found (may be OK for some recipe types)"
        ((issues++))
    fi

    # Check for FILES_ patterns
    if grep -q "^FILES_" $recipe_file; then
        if ! grep -q "^FILES:\${PN}" $recipe_file; then
            log_warn "$recipe_name: Old-style FILES syntax (use FILES:\${PN} instead)"
            ((issues++))
        fi
    fi

    if [ $issues -eq 0 ]; then
        log_pass "$recipe_name: No common issues found"
    fi

    return $issues
}

validate_single_recipe() {
    local recipe=$1
    local checks_passed=0
    local checks_total=0

    echo
    log_info "==================================================================="
    log_info "Validating recipe: $recipe"
    log_info "==================================================================="

    # Find recipe file
    local recipe_file=""
    if [ -f "$recipe" ]; then
        recipe_file="$recipe"
        recipe=$(basename $recipe .bb)
    else
        # Try to find recipe using bitbake
        recipe_file=$(bitbake-layers show-recipes $recipe 2>/dev/null | grep "\.bb" | head -n 1 | awk '{print $1}')

        if [ -z "$recipe_file" ]; then
            log_error "Recipe not found: $recipe"
            return 1
        fi
    fi

    log_debug "Recipe file: $recipe_file"

    # Run checks
    ((checks_total++))
    if check_required_variables "$recipe_file"; then
        ((checks_passed++))
    fi

    ((checks_total++))
    if check_license_compliance "$recipe_file"; then
        ((checks_passed++))
    fi

    ((checks_total++))
    if check_file_structure "$recipe_file"; then
        ((checks_passed++))
    fi

    ((checks_total++))
    if check_common_issues "$recipe_file"; then
        ((checks_passed++))
    fi

    # These checks require BitBake environment
    if command -v bitbake &> /dev/null; then
        ((checks_total++))
        if validate_recipe_syntax "$recipe"; then
            ((checks_passed++))
        fi

        ((checks_total++))
        if check_dependencies "$recipe"; then
            ((checks_passed++))
        fi
    fi

    # Summary for this recipe
    echo
    log_info "Recipe: $recipe - Passed: $checks_passed/$checks_total checks"
}

find_recipes_in_layer() {
    local layer_path=$1

    log_info "Finding recipes in layer: $layer_path"

    find "$layer_path" -name "*.bb" -type f
}

print_summary() {
    echo
    echo "==================================================================="
    echo -e "${GREEN}Validation Summary${NC}"
    echo "==================================================================="
    echo -e "${GREEN}Passed:${NC}   $PASSED"
    echo -e "${YELLOW}Warnings:${NC} $WARNINGS"
    echo -e "${RED}Errors:${NC}   $ERRORS"
    echo "==================================================================="

    if [ $ERRORS -eq 0 ]; then
        echo -e "${GREEN}All validations completed successfully!${NC}"
        return 0
    else
        echo -e "${RED}Validation failed with $ERRORS error(s)${NC}"
        return 1
    fi
}

# Main script
main() {
    local layer_path=""
    local validate_all=0
    local recipes=()

    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -l|--layer)
                layer_path="$2"
                shift 2
                ;;
            -a|--all)
                validate_all=1
                shift
                ;;
            -v|--verbose)
                VERBOSE=1
                shift
                ;;
            -f|--fix)
                FIX=1
                log_warn "Auto-fix mode is experimental"
                shift
                ;;
            -h|--help)
                print_usage
                exit 0
                ;;
            -*)
                log_error "Unknown option: $1"
                print_usage
                exit 1
                ;;
            *)
                recipes+=("$1")
                shift
                ;;
        esac
    done

    log_info "==================================================================="
    log_info "BitBake Recipe Validator"
    log_info "==================================================================="

    # Check BitBake environment
    if ! check_bitbake_env; then
        log_warn "Some checks will be skipped (BitBake environment not available)"
    fi

    # Determine what to validate
    if [ $validate_all -eq 1 ]; then
        log_info "Validating all recipes in all layers..."
        mapfile -t recipes < <(bitbake-layers show-recipes | grep ":" | awk '{print $1}')

    elif [ -n "$layer_path" ]; then
        log_info "Validating recipes in layer: $layer_path"
        mapfile -t recipes < <(find_recipes_in_layer "$layer_path")

    elif [ ${#recipes[@]} -eq 0 ]; then
        log_error "No recipes specified"
        print_usage
        exit 1
    fi

    log_info "Found ${#recipes[@]} recipe(s) to validate"
    echo

    # Validate each recipe
    for recipe in "${recipes[@]}"; do
        validate_single_recipe "$recipe"
    done

    # Print summary
    print_summary
}

# Run main function
main "$@"
