#!/usr/bin/env python3

from subprocess import run, PIPE
import re, sys

def cmd(x):
    return run(x, shell=True, stdout=PIPE, universal_newlines=True).stdout

def releaseKey(relStr):
    ver = re.match("[0-9\.]*", relStr)
    if ver == None:
        return []
    ver = ver.group(0)
    rc = re.search("_rc([0-9]*)$", relStr)
    if rc == None:
        return [ver, "final"]
    else:
        return [ver, rc.group(1)]

def main():
    if len(sys.argv) != 2:
        print("Expecting version number as input", file=sys.stderr)
        exit(1)

    version = sys.argv[1]
    haveGit = cmd("command -v git") != ""

    if haveGit:
        releases = cmd("git tag -l --points-at HEAD | grep release | sed -e 's/release_//'").split('\n')
        releases.pop() # pop empty line
        if releases != []:
            releases.sort(key=releaseKey)
            latest = releases.pop()
            version += " (" + latest + ")"
        else:
            commit = cmd("git rev-parse --short HEAD").replace('\n', '')
            version += " (git-" + commit + ")"

    print(version)

if __name__ == "__main__":
    main()
