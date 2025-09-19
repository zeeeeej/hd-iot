cmake_minimum_required(VERSION 3.10)
project(hd_camera_protocol LANGUAGES C)

# 设置编译选项
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

# 设置MSYS2 MINGW64特定的编译选项
if(MINGW)
    add_compile_options(-Wall -Wextra -O2)
    add_definitions(-DHD_CAMERA_PROTOCOL_EXPORTS)
endif()

# 设置输出目录
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

# 包含目录
include_directories(
    ${CMAKE_SOURCE_DIR}/include
)

# 源文件列表（只包含.c文件）
set(SOURCES
    src/hd_camera_protocol.c
    src/hd_camera_protocol_property.c
    src/hd_camera_protocol_extra_cmd.c
    src/hd_camera_protocol_cmd.c
    src/hd_camera_protocol_auto_cmd.c
    src/hd_md5.c
)

# 创建共享库（DLL）- 只包含源文件
add_library(hd_camera_protocol SHARED ${SOURCES})

# 针对特定目标设置编译选项
target_compile_options(hd_camera_protocol PRIVATE
        -Wall
        -Wextra
        -Wno-unused-parameter      # 忽略未使用参数警告
        -Wno-unused-variable       # 忽略未使用变量警告
        -Wno-unused-function       # 忽略未使用函数警告
#        -Wno-sign-compare          # 如果需要，也可以忽略符号比较警告
)

# 设置库的输出名称
set_target_properties(hd_camera_protocol PROPERTIES
    OUTPUT_NAME "hd_camera_protocol"
    VERSION 1.0.0
    SOVERSION 1
)

# 安装规则（可选）
install(TARGETS hd_camera_protocol
    RUNTIME DESTINATION bin
    LIBRARY DESTINATION ../lib
    ARCHIVE DESTINATION ../lib
)

install(DIRECTORY include/ DESTINATION include
    FILES_MATCHING PATTERN "*.h"
)