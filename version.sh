#!/bin/sh

# Determines the year.month tag or git hash that is appended to the
# version number

version="$1"
res=''

if [ -x "$(command -v git)" ]; then
    releases=$(git tag -l --points-at HEAD | grep release | sed -e 's/release_//')
    final=$(echo $releases | tr ' ' '\n' | grep -v rc | sort | tail -1)
    # temporarily replace _rc with space because . sorts before _
    # which would cause a.b_rc1 to be sorted before a_rc1
    rc=$(echo $releases | tr ' ' '\n' | grep rc | sed -e 's/_rc\([0-9]*\)/ \1/' | sort | sed -e 's/ \([0-9]*\)/_rc\1/' | tail -1)
    if ! [ "$final" = '' ]; then
        res="$version ($final)"
    elif ! [ "$rc" = '' ]; then
        res="$version ($rc)"
    else
        commit=$(git rev-parse --short HEAD)
        res="$version (git-$commit)"
    fi
else
    res="$version"
fi

echo $res

