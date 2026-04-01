project(JNILIBSVM)

get_filename_component(SRC_DIR  ${CMAKE_SOURCE_DIR}/..  ABSOLUTE)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-exceptions -frtti")

file(GLOB_RECURSE jnilibsvm_sources ${SRC_DIR}/jni/jnilibsvm/*.*)
add_library(jnilibsvm SHARED
            ${jnilibsvm_sources})

target_link_libraries(jnilibsvm
                      android
                      log
                      dl)