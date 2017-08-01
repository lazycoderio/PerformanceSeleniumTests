#!/bin/bash

function tab () {
    local cmd=""
    local cdto="$PWD"
    local args="$@"

    if [ -d "$1" ]; then
        cdto=`cd "$1"; pwd`
        args="${@:2}"
    fi

    if [ -n "$args" ]; then
        cmd="; $args"
    fi

    if [ $TERM_PROGRAM = "Apple_Terminal" ]; then
        osascript
            -e "tell application \"Terminal\"" \
                -e "tell application \"System Events\" to keystroke \"t\" using {command down}" \
                -e "do script \"cd $cdto; clear $cmd\" in front window" \
            -e "end tell"
            > /dev/null
    fi
    elif [ $TERM_PROGRAM = "iTerm.app" ]; then
        osascript
            -e "tell application \"iTerm\"" \
                -e "tell current terminal" \
                    -e "launch session \"Default Session\"" \
                    -e "tell the last session" \
                        -e "write text \"cd \"$cdto\"$cmd\"" \
                    -e "end tell" \
                -e "end tell" \
            -e "end tell" \
            > /dev/null
    fi
}

tab path_to_script1 sh script1
tab path_to_script2 sh script2
tab path_to_script3 sh script3