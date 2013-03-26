#!/bin/bash

DEVICE=/dev/video0
VLOC="./videos/temp.mpg"
FLOC="./images/frame%03d.png"
FPS=30
SIZE=640x480
DURATION=10

# Remove old files
rm ./images/*

ffmpeg -y -f alsa -i pulse -f v4l2 -s $SIZE -i $DEVICE -t $DURATION $VLOC
ffmpeg -y -i $VLOC -r $FPS $FLOC
