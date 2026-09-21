cmake_minimum_required(VERSION 3.22.1)
project(whocr_translation LANGUAGES C CXX)

set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TOOLS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_SERVER OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_APP OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_COMMON OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_UI OFF CACHE BOOL "" FORCE)

# Hy-MT's released GGUF predates the later Q2_0 enum insertion in the
# upstream STQ branch. Apply the small, idempotent wire-format compatibility
# patch to the initialized submodule before compiling it.
find_package(Git REQUIRED)
set(LLAMA_CPP_DIR "${CMAKE_CURRENT_LIST_DIR}/../../../../third_party/llama.cpp")
set(STQ_GGUF_PATCH "${CMAKE_CURRENT_LIST_DIR}/llama-stq-gguf.patch")
execute_process(
    COMMAND ${GIT_EXECUTABLE} -C "${LLAMA_CPP_DIR}" apply --check "${STQ_GGUF_PATCH}"
    RESULT_VARIABLE STQ_PATCH_NEEDED
)
if(STQ_PATCH_NEEDED EQUAL 0)
    execute_process(
        COMMAND ${GIT_EXECUTABLE} -C "${LLAMA_CPP_DIR}" apply "${STQ_GGUF_PATCH}"
        RESULT_VARIABLE STQ_PATCH_RESULT
    )
    if(NOT STQ_PATCH_RESULT EQUAL 0)
        message(FATAL_ERROR "Could not apply Hy-MT STQ1_0 GGUF compatibility patch")
    endif()
endif()

# This checkout is pinned to the STQ1_0 implementation required by the
# bundled Hy-MT1.5 1.25-bit GGUF model.
add_subdirectory(
    "${LLAMA_CPP_DIR}"
    "${CMAKE_BINARY_DIR}/llama-cpp"
)

add_library(hymt_jni SHARED hy_mt_jni.cpp)
target_compile_features(hymt_jni PRIVATE cxx_std_17)
target_include_directories(hymt_jni PRIVATE
    "${CMAKE_CURRENT_LIST_DIR}/../../../../third_party/llama.cpp/include"
    "${CMAKE_CURRENT_LIST_DIR}/../../../../third_party/llama.cpp/ggml/include"
)
target_link_libraries(hymt_jni PRIVATE llama log android)
