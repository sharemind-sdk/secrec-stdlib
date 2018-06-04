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

# readlink on OS X does not behave as on Linux
# http://stackoverflow.com/questions/1055671/how-can-i-get-the-behavior-of-gnus-readlink-f-on-a-mac
if [ `uname -s` = "Darwin" ]; then
    CWD=`pwd`
    TARGET_FILE=$0

    cd "`dirname "${TARGET_FILE}"`"
    TARGET_FILE=`basename "${TARGET_FILE}"`

    # Iterate down a (possible) chain of symlinks
    while [ -L "${TARGET_FILE}" ]
    do
        TARGET_FILE=`readlink "${TARGET_FILE}"`
        cd "`dirname "${TARGET_FILE}"`"
        TARGET_FILE=`basename "${TARGET_FILE}"`
    done
    unset -v TARGET_FILE

    # Compute the canonicalized name by finding the physical path
    # for the directory we're in and appending the target file.
    ABSSP=`pwd -P`
    cd "$CWD"
    unset -v CWD
else
    ABSS=`readlink -f "$0"`
    ABSSP=`dirname "${ABSS}"`
    unset -v ABSS
fi

SHAREMIND_PATH="${SHAREMIND_PATH:-${ABSSP}/..}"

if [ ! -d "${SHAREMIND_PATH}" ]; then
    echo 'Environment variable SHAREMIND_PATH does not point to a directory!' 1>&2
    exit 1
fi

echo "SHAREMIND_PATH='${SHAREMIND_PATH}'"

# http://www.linuxjournal.com/content/use-bash-trap-statement-cleanup-temporary-files
declare -a ON_EXIT_ITEMS

function on_exit() {
    for ITEM in "${ON_EXIT_ITEMS[@]}"; do
        eval "${ITEM}"
    done
}

function add_on_exit() {
    local N=${#ON_EXIT_ITEMS[*]}
    ON_EXIT_ITEMS["${N}"]="$*"
    if [[ "${N}" -eq 0 ]]; then
        trap on_exit EXIT
    fi
}

if [ -d "${SHAREMIND_PATH}" ]; then
  L=$(find "${SHAREMIND_PATH}" -name *.so -print0 | xargs -0 -n1 dirname | sort -u)
  L=$(for d in $L; do echo -n "$(cd "$d"; pwd):"; done)
  L="${L%:}"
  if [ "$L" != "" ]; then
    NEW_LD_LIBRARY_PATH="${LD_LIBRARY_PATH}${LD_LIBRARY_PATH:+:}$L"
  fi
  unset -v L
fi

if [ -z "${EMULATOR_CONF}" ]; then
    EMULATOR_CONF="emulator.conf"
fi

TEST_PATH="${SHAREMIND_PATH}/lib/sharemind/test"
SCC="${SHAREMIND_PATH}/bin/scc"
STDLIB="${SHAREMIND_PATH}/lib/sharemind/stdlib"
EMULATOR="${SHAREMIND_PATH}/bin/sharemind-emulator"
TEST_PARSER="${SHAREMIND_PATH}/bin/emulatortestparser.py"

compile() {
    local SC="$1"
    local SB="$2"

    LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH:-${LD_LIBRARY_PATH}}" "${SCC}" \
        --include "${TEST_PATH}" --include "${STDLIB}" \
        --input "${SC}" --output "${SB}"
}

run() {
    local SB="$1"
    local TEST_NAME="$2"
    (cd "`dirname ${EMULATOR}`" &&
        ((LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH:-${LD_LIBRARY_PATH}}" \
                "./`basename ${EMULATOR}`" --conf="${EMULATOR_CONF}" \
                --outFile=emulator.out --force "${SB}" \
                    | sed "s#^#${TEST_NAME}#g") \
            3>&1 1>&2 2>&3 3>&- | sed "s#^#${TEST_NAME}#g") \
            3>&1 1>&2 2>&3 3>&-
        )
    (cd "`dirname ${EMULATOR}`" &&
        ((python "${TEST_PARSER}" < emulator.out | sed "s#^#${TEST_NAME}#g") \
            3>&1 1>&2 2>&3 3>&- | sed "s#^#${TEST_NAME}#g") \
            3>&1 1>&2 2>&3 3>&-
        )
}

run_test() {
    local SC="$1"
    local TEST="$2"
    local SC_BN=`basename "${SC}"`
    local SB=`mktemp --tmpdir sharemind_stlib_runtests.$$.XXXXXXXXXX.sb`
    add_on_exit "rm \"${SB}\""

    local TEST_NAME="[${SC}]: "
    if [ -n "${TEST}" ]; then
        TEST_NAME="[${TEST}]: "
    fi

    compile "${SC}" "${SB}" && run "${SB}" "${TEST_NAME}"
}

run_testset() {
    local TESTSET=`echo "$1" | sed 's/\/\+$//'`
    local TESTSET_BN=`basename "${TESTSET}"`
    local TESTSET_PREFIX="${TESTSET::-${#TESTSET_BN}}"
    for TEST in `find "${TESTSET}" -mindepth 1 -type f -name "*.sc" | sort`; do
        run_test "${TEST}" "${TEST:${#TESTSET_PREFIX}}"
    done
}

run_all() {
    for TESTSET in `find "${TEST_PATH}" -mindepth 1 -maxdepth 1 -type d | sort`; do
        run_testset "${TESTSET}"
    done
}

if [ "x$1" = "x" ]; then
    run_all
elif [ -f "$1" ]; then
    run_test "$1"
elif [ -d "$1" ]; then
    run_testset "$1"
else
    echo "Usage of `basename "$0"`:"
    echo "runtests.sh [filename.sc]"
    echo "If no filename is specified, all tests will be run."
fi
