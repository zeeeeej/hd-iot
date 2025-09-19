#!/bin/bash
cp /d/git/HD-IoT/composeApp/src/native/src/hd_camera_protocol/CMakeLists.txt /home/hdlinux/proj/hd_camera_protocol

cp /d/git/HD-IoT/composeApp/src/native/src/hd_camera_protocol/src/*.c  /home/hdlinux/proj/hd_camera_protocol/src

cp /d/git/HD-IoT/composeApp/src/native/src/hd_camera_protocol/include/*.h /home/hdlinux/proj/hd_camera_protocol/include
echo "✅ 所有文件复制完成!"
