#!/bin/bash

DEVICE=/dev/video0
LOC="./images/snapshot.png"

gst-launch-0.10 v4l2src device=$DEVICE ! video/x-raw-yuv,framerate=30/1 ! ffmpegcolorspace ! pngenc ! filesink location=$LOC
