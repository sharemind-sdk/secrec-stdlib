#!/bin/sh

# Determines the year.month tag or git hash that is appended to the
# version number

version="$1"
res=''

if [ -x "$(command -v git)" ]; then
    releases=$(git tag -l --points-at HEAD | grep release | sed -e 's/release_//' | sort)
    final=$(echo $releases | tr ' ' '\n' | grep -v rc | tail -1)
    rc=$(echo $releases | tr ' ' '\n' | grep rc | tail -1)
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

