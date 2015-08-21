#!/bin/sh
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

if [ -z "${SHAREMIND_PATH}" ]; then
    exit 1
fi

if [ -z "${TMP_PATH}" ]; then
    TMP_PATH="." # e.g. /tmp
fi

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
  ABSSP=`dirname "$ABSS"`
  unset -v ABSS
fi

TEST_PATH="${ABSSP}"

if [ -d "${SHAREMIND_PATH}/lib" ]; then
  LD_LIBRARY_PATH="${LD_LIBRARY_PATH}${LD_LIBRARY_PATH:+:}${SHAREMIND_PATH}/lib"
fi

SCC="${SHAREMIND_PATH}/bin/scc"
STDLIB="${SHAREMIND_PATH}/lib/sharemind/stdlib"
TEST_RUNNER="${SHAREMIND_PATH}/bin/SecreCTestRunner"

compile() {
    local SC="$1"
    local SB="$2"

    LD_LIBRARY_PATH="${LD_LIBRARY_PATH}" "${SCC}" \
        --include "${TEST_PATH}" --include "${STDLIB}" \
        --input "${SC}" --output "${SB}"
}

install() {
    local SB="$1"
    local SB_BN=`basename "${SB}"`

    for i in `seq 1 3`; do
        local SCRIPTS_PATH="${SHAREMIND_PATH}/bin/miner${i}/scripts"
        mkdir -p "${SCRIPTS_PATH}"
        cp "${SB}" "${SCRIPTS_PATH}/${SB_BN}"
        local RV=$?; if [ ${RV} -ne 0 ]; then return ${RV}; fi
    done
}

run() {
    local SC="$1"
    local SC_BN=`basename "${SC}"`
    local SB_BN=`echo "${SC_BN}" | sed 's/\.sc$//' | sed 's/$/.sb/'`
    local SB="${TMP_PATH}/${SB_BN}"

    local TEST_NAME="[`basename "${SC_BN}"`]: "

    compile "${SC}" "${SB}"
    local RV=$?; if [ ${RV} -ne 0 ]; then return ${RV}; fi

    install "${SB}"
    local RV=$?; if [ ${RV} -ne 0 ]; then return ${RV}; fi

    local CWD=`pwd`; cd "`dirname "${TEST_RUNNER}"`"
    LD_LIBRARY_PATH="${LD_LIBRARY_PATH}" "${TEST_RUNNER}" --file "${SB_BN}" | sed "s/^/${TEST_NAME}/g"
    local RV=$?; cd "${CWD}"; return ${RV}
}

run_all() {
    for TESTS in `find "${ABSSP}" -mindepth 1 -maxdepth 1 -type d | sort`; do
        local TESTS_BN=`basename "${TESTS}"`
        echo "Testset: ${TESTS_BN}"
        for TEST in `find "${TESTS}" -mindepth 1 -maxdepth 1 -type f -name "*.sc" | sort`; do
            run "${TEST}"
            local RV=$?; if [ ${RV} -ne 0 ]; then return ${RV}; fi
        done
    done

    return 0
}

if [ "x$1" = "x" ]; then
    run_all
elif [ -f "$1" ]; then
    run "$1"
else
    echo "Usage of `basename "$0"`:"
    echo "runtests.sh [filename.sc]"
    echo "If no filename is specified, all tests will be run."
fi
