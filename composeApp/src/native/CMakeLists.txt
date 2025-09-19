cmake_minimum_required(VERSION 3.10)
project(native-library)

# 设置C标准
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

# 查找Java开发包
find_package(Java REQUIRED)
find_package(JNI REQUIRED)

if(JNI_FOUND)
    message(STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    message(STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
endif()

# 设置输出目录
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

# 添加头文件目录
include_directories(
        ${JNI_INCLUDE_DIRS}
        ${CMAKE_SOURCE_DIR}/src/hd_camera_protocol/include
)

# 添加链接目录
link_directories(${CMAKE_SOURCE_DIR}/src/lib)

# 创建共享库
add_library(native-library SHARED
        src/native-library.c
)

# 设置目标属性
set_target_properties(native-library PROPERTIES
        LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin
        RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin
        OUTPUT_NAME "native-library"
        PREFIX ""
        SUFFIX ".dll"
)

# 链接库 - 修正链接选项
target_link_libraries(native-library
        ${JNI_LIBRARIES}
        hd_camera_protocol  # 直接链接库名，不需要.dll后缀
)

# 复制依赖的DLL到输出目录（推荐）
add_custom_command(TARGET native-library POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy_if_different
        ${CMAKE_SOURCE_DIR}/src/lib/libhd_camera_protocol.dll
        ${CMAKE_BINARY_DIR}/bin/
        COMMENT "Copying libhd_camera_protocol.dll to output directory"
)

# 安装目标（可选）
install(TARGETS native-library
        RUNTIME DESTINATION bin
        LIBRARY DESTINATION lib
)

install(FILES ${CMAKE_SOURCE_DIR}/src/lib/libhd_camera_protocol.dll
        DESTINATION bin
)