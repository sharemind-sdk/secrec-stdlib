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

# Point these variables to the root directories of your SecreC
# standard libary and Sharemind repositories. Without a trailing slash please.
STDLIB=
SHAREMIND=
# Point this to the SecreC compiler
SCC=

compile() { # params $1 - filename.sc, $2 - filename.sb
    $SCC -I $STDLIB/lib -I $STDLIB/tests --input $1 --output $2
    cp $2 $SHAREMIND/install.debug/bin/miner1/scripts
    cp $2 $SHAREMIND/install.debug/bin/miner2/scripts
    cp $2 $SHAREMIND/install.debug/bin/miner3/scripts
}

runTest() { # param $1 - filename.sc
    source=$1
    bytecode=${1%.sc}.sb

    compile $source $bytecode

    cd $SHAREMIND/install.debug/bin
    ./SecreCTestRunner $(basename $bytecode)
}

runAllTests() {
    subdirs=$(find $(pwd) -mindepth 1 -type d)
    for dir in $subdirs
    do
        echo $(basename $dir)
        files=$(find $dir -type f -name "*.sc")
        for file in $files
        do
            runTest $file
        done
        echo ""
    done
}

if [ "$1" = "" ]
then
    wait $!
    runAllTests
elif [ -f $1 ]
then
    wait $!
    runTest $1
else # help
    echo "Usage of runtests.sh:"
    echo "runtests.sh [filename.sc]"
    echo "If no filename is specified, all tests will be run."
fi
