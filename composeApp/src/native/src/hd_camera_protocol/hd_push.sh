#!/bin/bash

# 复制到目标目录的脚本
# 用法: ./copy_to_target.sh

# 设置源目录和目标目录
SOURCE_DIR="/home/hdlinux/proj/hd_camera_protocol"
TARGET_BASE_DIR="/d/git/HD-IoT/composeApp/src/native/src/hd_camera_protocol"

# 检查目标目录是否存在
if [ ! -d "$TARGET_BASE_DIR" ]; then
    echo "错误: 目标目录不存在: $TARGET_BASE_DIR"
    exit 1
fi

echo "开始复制文件到目标目录..."

# 复制 CMakeLists.txt
if [ -f "CMakeLists.txt" ]; then
    cp -v "CMakeLists.txt" "$TARGET_BASE_DIR/"
    echo "✓ CMakeLists.txt 复制完成"
else
    echo "⚠️  警告: CMakeLists.txt 不存在"
fi

# 复制 src 目录
if [ -d "src" ]; then
    mkdir -p "$TARGET_BASE_DIR/src"
    cp -rv ./src/* "$TARGET_BASE_DIR/src/"
    echo "✓ src 目录复制完成"
else
    echo "⚠️  警告: src 目录不存在"
fi

# 复制 include 目录
if [ -d "include" ]; then
    mkdir -p "$TARGET_BASE_DIR/include"
    cp -rv ./include/* "$TARGET_BASE_DIR/include/"
    echo "✓ include 目录复制完成"
else
    echo "⚠️  警告: include 目录不存在"
fi

# 复制根目录下的其他文件（除了脚本本身）
for file in ./*; do
    if [ -f "$file" ] && [ "$file" != "./copy_to_target.sh" ] && [ "$file" != "./copy_from_target.sh" ]; then
        case "$file" in
            *.h|*.c|*.txt|*.md|*.cmake)
                cp -v "$file" "$TARGET_BASE_DIR/"
                echo "✓ $(basename "$file") 复制完成"
                ;;
        esac
    fi
done

echo "✅ 所有文件复制完成!"
echo "目标目录: $TARGET_BASE_DIR"