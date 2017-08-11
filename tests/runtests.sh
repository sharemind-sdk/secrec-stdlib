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
# SHAREMIND_TEST_LOG_PATH (default: .) Directory path for test logs
# SHAREMIND_TEST_LOG_FILE (default: ${SHAREMIND_TEST_LOG_PATH}/stdlibtests.log) Test log filename
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

SHAREMIND_PATH=$(cd "${SHAREMIND_PATH:-${ABSSP}/..}"; pwd)

if [ ! -d "${SHAREMIND_PATH}" ]; then
    echo 'Environment variable SHAREMIND_PATH does not point to a directory!' 1>&2
    exit 1
fi

CACHE_DIR="${SHAREMIND_PATH}/tmp/runtests_cache"

# Check for GDB
RUN_GDB="${RUN_GDB:-1}"

set +e
type gdb >/dev/null 2>&1
set -e
HAVE_GDB="$?"

if [ -z "${SHAREMIND_TEST_LOG_PATH}" ]; then
    SHAREMIND_TEST_LOG_PATH="."
fi

if [ ! -d "${SHAREMIND_TEST_LOG_PATH}" ]; then
  echo "SHAREMIND_TEST_LOG_PATH does not point to a directory!" 2>&1
  exit 1
fi

TEST_LOG_FILE_PATH="${SHAREMIND_TEST_LOG_FILE:-${SHAREMIND_TEST_LOG_PATH}/stdlibtests.log}"

if [ -d "${SHAREMIND_PATH}/lib" ]; then
  NEW_LD_LIBRARY_PATH="${LD_LIBRARY_PATH}${LD_LIBRARY_PATH:+:}${SHAREMIND_PATH}/lib"
fi

TEST_PATH="${SHAREMIND_PATH}/lib/sharemind/test"
SCC="${SHAREMIND_PATH}/bin/scc"
STDLIB="${SHAREMIND_PATH}/lib/sharemind/stdlib"
TEST_RUNNER="${SHAREMIND_PATH}/bin/sharemind-secrec-test-runner"

declare -A BYTECODES

install() {
    SOURCE="$1"
    TARGET_FN="$2"
    for I in `seq 1 3`; do
        local SCRIPTS_PATH="${SHAREMIND_PATH}/bin/miner${I}/scripts"
        mkdir -p "${SCRIPTS_PATH}"
        local TARGET="${SCRIPTS_PATH}/${TARGET_FN}"
        if [ "$SOURCE" -nt "$TARGET" ]; then
            cp -f "$SOURCE" "$TARGET"
        fi
    done
}

uninstall() {
    TARGET_FN="$1"
    for I in `seq 1 3`; do
        local SCRIPTS_PATH="${SHAREMIND_PATH}/bin/miner${I}/scripts"
        local TARGET="${SCRIPTS_PATH}/${TARGET_FN}"
        rm -f "$TARGET"
    done
}

compile() {
    local SC="$1"
    local TEST_NAME="$2"
    local SC_BN=`basename "$SC"`
    local SB_BN="${SC_BN%.sc}.sb"
    local HASH=$(sha512sum "$SC"|awk '{print $1}')
    local SB_DIR="${CACHE_DIR}/$HASH"
    local SB="${SB_DIR}/$SB_BN"

    if [ "$SC" -nt "$SB" ]; then
        if [ ! -d "${CACHE_DIR}" ]; then
            echo "Creating compile cache at \"${CACHE_DIR}\""
        fi
        mkdir -p "$SB_DIR"
        echo "[scc] $TEST_NAME"
        LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH:-${LD_LIBRARY_PATH}}" "${SCC}" \
            --include "${TEST_PATH}" --include "${STDLIB}" \
            --input "${SC}" --output "${SB}" || exit
    fi
    BYTECODES["$TEST_NAME"]="$SB"
}

run_normal() {
    local SB_BN="$1"
    local TEST_NAME="$2"
    (cd "`dirname ${TEST_RUNNER}`" &&
        ((LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH:-${LD_LIBRARY_PATH}}" \
                "./`basename ${TEST_RUNNER}`" --conf controller.cfg --file "${SB_BN}" \
                        --logfile "${TEST_LOG_FILE_PATH}" --logmode append \
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
                        "./`basename ${TEST_RUNNER}`" --conf controller.cfg --file "${SB_BN}" \
                                --logfile "${TEST_LOG_FILE_PATH}" --logmode append \
                            | sed "s#^#${TEST_NAME}#g") \
             3>&1 1>&2 2>&3 3>&- | sed "s#^#${TEST_NAME}#g") \
             3>&1 1>&2 2>&3 3>&-
        )
}

run() {
    local TEST_NAME="$1"
    local SB=${BYTECODES["$TEST_NAME"]}
    local TEST_BN=$(basename "${TEST_NAME%.sc}.sb")
    install "$SB" "$TEST_BN"
    if [ "${RUN_GDB}" -eq 0 ] && [ "${HAVE_GDB}" -eq 0 ]; then
        run_gdb "$TEST_BN" "[${TEST_NAME}]: "
    else
        run_normal "$TEST_BN" "[${TEST_NAME}]: "
    fi
    uninstall "$TEST_BN"
}

declare -A TESTS

scan_test() {
    TESTS[$(basename $1)]="$1"
}

scan_testset() {
    local TESTSET=`echo "$1" | sed 's/\/\+$//'`
    local TESTSET_BN=`basename "${TESTSET}"`
    local TESTSET_PREFIX="${TESTSET::-${#TESTSET_BN}}"
    for TEST in `find "${TESTSET}" -mindepth 1 -type f -name "*.sc" | sort`; do
        TESTS["${TEST:${#TESTSET_PREFIX}}"]="$TEST"
    done
}

scan_all() {
    for TESTSET in `find "${TEST_PATH}" -mindepth 1 -maxdepth 1 -type d | sort`; do
        scan_testset "${TESTSET}"
    done
}

main() {
    echo "SHAREMIND_PATH=$SHAREMIND_PATH"
    SCAN_NAME="$1"
    shift
    scan_${SCAN_NAME} "$@"
    IFS=$'\n' SORTED_TEST_NAMES=($(sort <<<"${!TESTS[*]}"))
    unset IFS
    for TEST_NAME in "${SORTED_TEST_NAMES[@]}"; do
        compile "${TESTS[$TEST_NAME]}" "$TEST_NAME"
    done
    for TEST_NAME in "${SORTED_TEST_NAMES[@]}"; do
        run "$TEST_NAME"
    done
}

if [ -z "$1" ]; then
    main all
elif [ -f "$1" ]; then
    main test "$1"
elif [ -d "$1" ]; then
    main testset "$1"
else
    echo "Usage of `basename "$0"`:"
    echo "runtests.sh [filename.sc]"
    echo "If no filename is specified, all tests will be run."
    exit 1
fi
