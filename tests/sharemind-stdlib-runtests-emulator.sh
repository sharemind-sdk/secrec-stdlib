#!/bin/bash
#
# Copyright (C) 2015 Cybernetica
#
# Research/Commercial License Usage
# Licensees holding a valid Research License or Commercial License
# for the Software may use this file according to the written
# agreement between you and Cybernetica.
#
# GNU Lesser General Public License Usage
# Alternatively, this file may be used under the terms of the GNU Lesser
# General Public License version 3 as published by the Free Software
# Foundation and appearing in the file LICENSE.LGPLv3 included in the
# packaging of this file.  Please review the following information to
# ensure the GNU Lesser General Public License version 3 requirements
# will be met: http://www.gnu.org/licenses/lgpl-3.0.html.
#
# For further information, please contact us at sharemind@cyber.ee.
#

# Exit status of piped commands is zero only if all commands succeed
set -o pipefail
set -e

ABSSP=$(
    # readlink on OS X does not behave as on Linux
    # http://stackoverflow.com/questions/1055671/how-can-i-get-the-behavior-of-gnus-readlink-f-on-a-mac
    if [ "$(uname -s)" = "Darwin" ]; then
        TARGET_FILE=$0
        (
            cd "$(dirname "${TARGET_FILE}")"
            TARGET_FILE=$(basename "${TARGET_FILE}")

            # Iterate down a (possible) chain of symlinks
            while [ -L "${TARGET_FILE}" ]; do
                TARGET_FILE=$(readlink "${TARGET_FILE}")
                cd "$(dirname "${TARGET_FILE}")"
                TARGET_FILE=$(basename "${TARGET_FILE}")
            done

            # Compute the canonicalized name by finding the physical path
            # for the directory we're in and appending the target file.
            pwd -P
        )
    else
        dirname "$(readlink -f "$0")"
    fi
)

SHAREMIND_PATH="${SHAREMIND_PATH:-${ABSSP}/..}"

if [ ! -d "${SHAREMIND_PATH}" ]; then
    echo 'Environment variable SHAREMIND_PATH does not point to a directory!' 1>&2
    exit 1
fi

echo "SHAREMIND_PATH='${SHAREMIND_PATH}'"

NEW_LD_LIBRARY_PATH=$(
    find "${SHAREMIND_PATH}" -type f -name '*.so' \
    | while read -r f; do echo "${f%/*}"; done \
    | sort -u \
    | while read -r d; do printf ':%s' "${d}"; done;
    echo "${LD_LIBRARY_PATH:+:}${LD_LIBRARY_PATH}")
NEW_LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH#:}"

if [ -z "${EMULATOR_CONF}" ]; then
    EMULATOR_CONF="emulator.conf"
fi

TEST_PATH="$(cd "${ABSSP}/../lib/sharemind/test" && pwd)"
SCC="${SHAREMIND_PATH}/bin/scc"
EMULATOR="${SHAREMIND_PATH}/bin/sharemind-emulator"
TEST_PARSER="${ABSSP}/sharemind-stdlib-emulator-test-parser.py"

compile() {
    local SC="$1"
    local SB="$2"

    LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH}" "${SCC}" \
        --include "${TEST_PATH}" \
        --input "${SC}" --output "${SB}"
}

run() {
    local SB="$1"
    local TEST_NAME="$2"
    (cd "$(dirname "${EMULATOR}")" &&
        ( (LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH}" \
                "./$(basename "${EMULATOR}")" --conf="${EMULATOR_CONF}" \
                --outFile=emulator.out --force "${SB}" \
                    | sed "s#^#${TEST_NAME}#g") \
            3>&1 1>&2 2>&3 3>&- | sed "s#^#${TEST_NAME}#g") \
            3>&1 1>&2 2>&3 3>&-
        )
    (cd "$(dirname "${EMULATOR}")" &&
        ( (python "${TEST_PARSER}" < emulator.out | sed "s#^#${TEST_NAME}#g") \
            3>&1 1>&2 2>&3 3>&- | sed "s#^#${TEST_NAME}#g") \
            3>&1 1>&2 2>&3 3>&-
        )
}

run_test() {
    local SC="$1"
    local SC_BN
    SC_BN="$(basename "$SC")"
    local TEST="$2"
    (
        set -euo pipefail
        TMPDIR=$(mktemp -d)
        # shellcheck disable=2064 # We intend to expand $TMPDIR now, not later:
        trap "rm -rf \"${TMPDIR}\"" EXIT
        SB="${TMPDIR}/${SC_BN%.sc}.sb"

        compile "${SC}" "${SB}"
        run "${SB}" "[${TEST:-$SC_BN}]: "
    )
}

run_testset() {
    local TESTSET
    TESTSET=$(echo "$1" | sed 's/\/\+$//')
    local TESTSET_BN
    TESTSET_BN=$(basename "${TESTSET}")
    local TESTSET_PREFIX="${TESTSET::-${#TESTSET_BN}}"
    local TEST
    for TEST in $(find "${TESTSET}" -mindepth 1 -type f -name "*.sc" | sort); do
        run_test "${TEST}" "${TEST:${#TESTSET_PREFIX}}"
    done
}

run_all() {
    local TESTSET
    for TESTSET in $(find "${TEST_PATH}" -mindepth 1 -maxdepth 1 -type d | sort); do
        run_testset "${TESTSET}"
    done
}

if [ "$#" -eq 0 ]; then
    run_all
elif [[ "$#" -eq 1 && -f "$1" ]]; then
    run_test "$1"
elif [[ "$#" -eq 1 && -d "$1" ]]; then
    run_testset "$1"
else
    PN=$(basename "$0")
    cat <<EOF
Usage:
    ${PN}
        Runs all tests.
    ${PN} <directory>
        Runs all tests in the given directory.
    ${PN} <filename.sc>
        Runs all tests in the given file.
EOF
    exit 1
fi
