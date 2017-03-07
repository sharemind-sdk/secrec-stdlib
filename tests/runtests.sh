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

# This script reads the following environment variables:
# SHAREMIND_PATH (default: ..) Sharemind install prefix
# LOG_PATH (default: runtests.sh.log) Test log
# RUN_GDB (default: 1) Flag turning on/off GDB debugging (0 means on)

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

# Check for GDB
RUN_GDB="${RUN_GDB:-1}"

type gdb >/dev/null 2>&1
HAVE_GDB="$?"

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

if [ -z "${LOG_PATH}" ]; then
    LOG_PATH="`basename "$0"`.log"
fi

if [ -d "${SHAREMIND_PATH}/lib" ]; then
  NEW_LD_LIBRARY_PATH="${LD_LIBRARY_PATH}${LD_LIBRARY_PATH:+:}${SHAREMIND_PATH}/lib"
fi

TEST_PATH="${SHAREMIND_PATH}/lib/sharemind/test"
SCC="${SHAREMIND_PATH}/bin/scc"
STDLIB="${SHAREMIND_PATH}/lib/sharemind/stdlib"
TEST_RUNNER="${SHAREMIND_PATH}/bin/SecreCTestRunner"

compile() {
    local SC="$1"
    local SB="$2"

    LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH:-${LD_LIBRARY_PATH}}" "${SCC}" \
        --include "${TEST_PATH}" --include "${STDLIB}" \
        --input "${SC}" --output "${SB}"
}

install() {
    local ORIGIN="$1"
    local TARGET_FILENAME="$2"

    for I in `seq 1 3`; do
        local SCRIPTS_PATH="${SHAREMIND_PATH}/bin/miner${I}/scripts"
        mkdir -p "${SCRIPTS_PATH}"
        cp "${ORIGIN}" "${SCRIPTS_PATH}/${TARGET_FILENAME}"
    done
}

run() {
    local SB_BN="$1"
    local TEST_NAME="$2"
    (cd "`dirname ${TEST_RUNNER}`" &&
        ((LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH:-${LD_LIBRARY_PATH}}" \
                "./`basename ${TEST_RUNNER}`" --file "${SB_BN}" \
                        --logfile "${LOG_PATH}" --logmode append \
                    | sed "s#^#${TEST_NAME}#g") \
             3>&1 1>&2 2>&3 3>&- | sed "s#^#${TEST_NAME}#g") \
             3>&1 1>&2 2>&3 3>&-
        )
}

run_gdb() {
    local SB_BN="$1"
    local TEST_NAME="$2"
    (cd "`dirname ${TEST_RUNNER}`" &&
        ((LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH:-${LD_LIBRARY_PATH}}" \
                gdb -return-child-result -batch -quiet \
                    -ex 'run' \
                    -ex 'backtrace' \
                    -ex 'thread apply all backtrace' \
                    -ex 'thread apply all backtrace full' \
                    -ex 'info registers' \
                    --args \
                        "./`basename ${TEST_RUNNER}`" --file "${SB_BN}" \
                                --logfile "${LOG_PATH}" --logmode append \
                            | sed "s#^#${TEST_NAME}#g") \
             3>&1 1>&2 2>&3 3>&- | sed "s#^#${TEST_NAME}#g") \
             3>&1 1>&2 2>&3 3>&-
        )
}

run_test() {
    local SC="$1"
    local TEST="$2"
    local SC_BN=`basename "${SC}"`
    local SB_BN="${SC_BN%.sc}.sb"
    local SB=`mktemp --tmpdir sharemind_stlib_runtests.$$.XXXXXXXXXX.sb`
    add_on_exit "rm \"${SB}\""

    local TEST_NAME="[${SC}]: "
    if [ -n "${TEST}" ]; then
        TEST_NAME="[${TEST}]: "
    fi

    compile "${SC}" "${SB}" && install "${SB}" "${SB_BN}" && \
    if [ "${RUN_GDB}" -eq 0 ] && [ "${HAVE_GDB}" -eq 0 ]; then
        run_gdb "${SB_BN}" "${TEST_NAME}"
    else
        run "${SB_BN}" "${TEST_NAME}"
    fi
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
