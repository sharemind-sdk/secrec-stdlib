#
# Copyright (C) Cybernetica AS
#
# All rights are reserved. Reproduction in whole or part is prohibited
# without the written consent of the copyright owner. The usage of this
# code is subject to the appropriate license agreement.
#

CMAKE_MINIMUM_REQUIRED(VERSION 2.8)
PROJECT(SHAREMIND_STDLIB CXX)

INCLUDE("${CMAKE_CURRENT_SOURCE_DIR}/config.local" OPTIONAL)
INCLUDE("${CMAKE_CURRENT_BINARY_DIR}/config.local" OPTIONAL)

FIND_PACKAGE(Doxygen REQUIRED)

FILE(GLOB_RECURSE SharemindStdlib_ALLFILES
     "${CMAKE_CURRENT_SOURCE_DIR}/lib/*.sc"
     "${CMAKE_CURRENT_SOURCE_DIR}/tests/*.sc"
     "${CMAKE_CURRENT_SOURCE_DIR}/tests/*.sh"
)
ADD_CUSTOM_TARGET("include_SharemindStdlib_ALLFILES_in_IDE" SOURCES
                  ${SharemindStdlib_ALLFILES})

# Stdlib:

# Note that this globbing is done by cmake, not by make, hence sometimes clean might be required.
FILE(GLOB SharemindStdlib_FILES "${CMAKE_CURRENT_SOURCE_DIR}/lib/*.sc")
INSTALL(FILES ${SharemindStdlib_FILES}
        DESTINATION "lib/sharemind/stdlib")

# Stdlib documentation:

ADD_CUSTOM_TARGET(stdlib_docs ALL
                  COMMAND "./build_html.sh"
                  WORKING_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/docs")

INSTALL(DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/docs/html"
        DESTINATION "share/doc/sharemind/stdlib")