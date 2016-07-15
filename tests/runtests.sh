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

if [ ! -d "${SHAREMIND_PATH}" ]; then
    echo 'Required environment variable SHAREMIND_PATH missing or does not point to a directory!' 1>&2
    exit 1
fi

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

TEST_PATH="${ABSSP}"

if [ -z "${LOG_PATH}" ]; then
    LOG_PATH="${TEST_PATH}/`basename "$0"`.log"
fi

if [ -d "${SHAREMIND_PATH}/lib" ]; then
  NEW_LD_LIBRARY_PATH="${LD_LIBRARY_PATH}${LD_LIBRARY_PATH:+:}${SHAREMIND_PATH}/lib"
fi

SCC="${SHAREMIND_PATH}/bin/scc"
STDLIB="${SHAREMIND_PATH}/lib/sharemind/stdlib"
TEST_RUNNER="${SHAREMIND_PATH}/bin/SecreCTestRunner"

compile() {
    local SC="$1"
    local SB="$2"

    LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH}" "${SCC}" \
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

run_test() {
    local SB_BN="$1"
    local TEST_NAME="$2"
    local CWD=`pwd`; cd "`dirname ${TEST_RUNNER}`"

    ((LD_LIBRARY_PATH="${NEW_LD_LIBRARY_PATH}" \
            "./`basename ${TEST_RUNNER}`" --file "${SB_BN}" \
                    --logfile "${LOG_PATH}" --logmode append \
                | sed "s#^#${TEST_NAME}#g") \
         3>&1 1>&2 2>&3 3>&- | sed "s#^#${TEST_NAME}#g") \
         3>&1 1>&2 2>&3 3>&-
    cd "${CWD}"
}

run() {
    local SC="$1"
    local TESTSET="$2"
    local SC_BN=`basename "${SC}"`
    local SB_BN="${SC_BN%.sc}.sb"
    local SB=`mktemp --tmpdir sharemind_stlib_runtests.$$.XXXXXXXXXX.sb`
    add_on_exit "rm \"${SB}\""

    local TEST_NAME="[${SC}]: "
    if [ -n "${TESTSET}" ]; then
        TEST_NAME="[${TESTSET}\/`basename "${SC_BN}"`]: "
    fi

    compile "${SC}" "${SB}" && install "${SB}" "${SB_BN}" && run_test "${SB_BN}" "${TEST_NAME}"
}

run_all() {
    for TESTS in `find "${ABSSP}" -mindepth 1 -maxdepth 1 -type d | sort`; do
        local TESTS_BN=`basename "${TESTS}"`
        for TEST in `find "${TESTS}" -mindepth 1 -maxdepth 1 -type f -name "*.sc" | sort`; do
            run "${TEST}" "${TESTS_BN}"
        done
    done
}

if [ "x$1" = "x" ]; then
    run_all
elif [ -f "$1" ]; then
    run "$1"
elif [ -d "$1" ]; then
    for TEST in `find "$1" -mindepth 1 -maxdepth 1 -type f -name "*.sc" | sort`; do
        run "${TEST}" `basename "$1"`
    done
else
    echo "Usage of `basename "$0"`:"
    echo "runtests.sh [filename.sc]"
    echo "If no filename is specified, all tests will be run."
fi
