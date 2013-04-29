#!/bin/bash
# Usage: capture_frames.sh [device] [duration]
# Image format: YYYY-MM-DD_hh:mm:ss:xxx.png (x is ms)

NOW=$(date +"%F_%T")
OUT=out
DIR=$OUT/$NOW
VLOC="$DIR/video.avi"
FLOC="$DIR/frames/frame%03d.png"
LOG="ffmpeg.log"
DEVICE=$1
FPS=10/1
SIZE=320x240
DURATION=$2

echo $LOG
export FFREPORT=file=$LOG

# Make directories
if [ ! -d "$OUT" ]; then
    mkdir $OUT
fi

if [ ! -d "$DIR" ]; then
    mkdir $DIR
fi

if [ ! -d "$DIR/frames" ]; then
    mkdir $DIR/frames
fi

#sysctl vm.overcommit_memory=1
#ffmpeg -y -v verbose -an -r $FPS -s $SIZE -f v4l2 -i $DEVICE -r $FPS -t $DURATION -timestamp now $VLOC
ffmpeg -y -v verbose -report -an -vsync 2 -f v4l2 -i $DEVICE -vcodec copy -t $DURATION -timestamp now $VLOC
ffmpeg -y -v verbose -i $VLOC $FLOC
#sysctl vm.overcommit_memory=0
