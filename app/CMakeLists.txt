cmake_minimum_required(VERSION 3.10.2)

project(BluetoothPlusPlus)

add_library(btif SHARED src/main/cpp/btif.cpp)

include_directories(include)

find_library(log-lib log)
target_link_libraries(bpp_qti ${log-lib})
