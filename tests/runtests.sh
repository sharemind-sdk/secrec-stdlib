#!/bin/sh

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
