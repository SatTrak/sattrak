#!/bin/bash

DEVICE=/dev/video0
LOC="./snapshot.png"

gst-launch-0.10 v4l2src device=$DEVICE ! video/x-raw-yuv,framerate=30/1,width=800,height=600 ! ffmpegcolorspace ! pngenc ! filesink location=$LOC
