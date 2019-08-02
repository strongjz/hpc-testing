#!/usr/bin/env bash

#1 sort file small to largest

#2 Multiply k percent by the total number of values, index.

#3 if index is whole round up , then go to 4 else to 5

#4 count left to right, stop at index, that number is your pecentile

#5 (index and index + 1 ) / 2 is your pecentile
round() {
  printf "%.${2}f" "${1}"
}

function start() {

    SORTED=( `sort $file` )
    COUNT=${#SORTED[@]}


    echo "count: $COUNT"

    INDEX=$(bc <<< "$COUNT * $PERCENT")


    echo "INDEX: $INDEX"

    ROUND=$(round $INDEX 0)

     if [[ "$INDEX" =~ ^[0-9]+(\.[0-9]+)?$ ]]; then
        I=$(round ${SORTED[$ROUND]} 2)
        J=$(round ${SORTED[$ROUND - 1]} 2)
	K=$(bc <<< "$I + $J" )
	echo "K: $K I: $I J: $J"

	
        AVG=$(bc <<< "$K  / 2 " )
        echo "Percentile AVG $AVG"

    else

        echo "Index: $ROUND"

        echo "Percentile ${SORTED[$ROUND]}"
    fi
}

usage() {
    echo "Usage: $0 [-f file name]" 1>&2; exit 1;
}

while getopts ":f:p:" o; do
    case "${o}" in
        f)
            file=${OPTARG}
            ;;
        p)
            PERCENT=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [[ ! -f $file  ]]; then
    echo "$file does not exist"
    exit 1
fi

if [[  -z $PERCENT ]];then 
	echo "PERCENT NEEDED"
	exit 1

fi

start

