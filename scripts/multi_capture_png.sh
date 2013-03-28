#!/bin/bash

DEVICE=/dev/video1
LOC="./images/frame%d.png"

gst-launch-0.10 v4l2src device=$DEVICE ! video/x-raw-yuv,framerate=30/1 ! ffmpegcolorspace ! pngenc snapshot=FALSE ! multifilesink location=$LOC