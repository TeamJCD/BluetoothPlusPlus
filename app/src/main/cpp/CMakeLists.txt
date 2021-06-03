cmake_minimum_required(VERSION 3.10.2)

project("BluetoothPlusPlus")

add_library(bpp SHARED bpp_jni.cpp injector.cpp ptrace.cpp utils.cpp)

include_directories(include)

find_library(log-lib log)
target_link_libraries(bpp ${log-lib})
