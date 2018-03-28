#
# Copyright (C) 2015 Cybernetica
#
# Research/Commercial License Usage
# Licensees holding a valid Research License or Commercial License
# for the Software may use this file according to the written
# agreement between you and Cybernetica.
#
# GNU Lesser General Public License Usage
# Alternatively, this file may be used under the terms of the GNU Lesser
# General Public License version 3 as published by the Free Software
# Foundation and appearing in the file LICENSE.LGPLv3 included in the
# packaging of this file.  Please review the following information to
# ensure the GNU Lesser General Public License version 3 requirements
# will be met: http://www.gnu.org/licenses/lgpl-3.0.html.
#
# For further information, please contact us at sharemind@cyber.ee.
#

CMAKE_MINIMUM_REQUIRED(VERSION 3.0)
PROJECT(SharemindStdlib VERSION 2.1.2 LANGUAGES NONE)

INCLUDE("${CMAKE_CURRENT_SOURCE_DIR}/config.local" OPTIONAL)
INCLUDE("${CMAKE_CURRENT_BINARY_DIR}/config.local" OPTIONAL)

FIND_PACKAGE(SharemindCMakeHelpers REQUIRED)

SharemindSetProjectVersion()


FIND_PACKAGE(Doxygen REQUIRED)

FILE(GLOB_RECURSE SharemindStdlib_ALLFILES
     "${CMAKE_CURRENT_SOURCE_DIR}/docs/*"
     "${CMAKE_CURRENT_SOURCE_DIR}/lib/*.sc"
     "${CMAKE_CURRENT_SOURCE_DIR}/tests/*.sc"
     "${CMAKE_CURRENT_SOURCE_DIR}/tests/*.sh"
)
ADD_CUSTOM_TARGET("include_SharemindStdlib_ALLFILES_in_IDE" SOURCES
                  ${SharemindStdlib_ALLFILES})

EXECUTE_PROCESS(
  COMMAND "./version.sh" "${PROJECT_VERSION}"
  WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
  OUTPUT_VARIABLE VERSION_STRING
  OUTPUT_STRIP_TRAILING_WHITESPACE
)

# Stdlib:

# Note that this globbing is done by cmake, not by make, hence sometimes clean might be required.
FILE(GLOB SharemindStdlib_FILES "${CMAKE_CURRENT_SOURCE_DIR}/lib/*.sc")
INSTALL(FILES ${SharemindStdlib_FILES}
        DESTINATION "lib/sharemind/stdlib"
        COMPONENT "stdlib")

# Stdlib documentation:
FILE(MAKE_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/generated_docs")
CONFIGURE_FILE("${CMAKE_CURRENT_SOURCE_DIR}/docs/Doxyfile.in"
               "${CMAKE_CURRENT_BINARY_DIR}/Doxyfile")
ADD_CUSTOM_TARGET(stdlib_docs ALL
                  COMMAND "${CMAKE_CURRENT_SOURCE_DIR}/docs/build_html.sh"
                          "${CMAKE_CURRENT_BINARY_DIR}/Doxyfile"
                  WORKING_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/generated_docs")
INSTALL(DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/generated_docs/html"
        DESTINATION "share/doc/sharemind/stdlib"
        COMPONENT "doc")

# Stdlib tests:

# Note that this globbing is done by cmake, not by make, hence sometimes clean might be required.
FILE(GLOB SharemindStdlibTest_FILES "${CMAKE_CURRENT_SOURCE_DIR}/tests/*.sh"
                                    "${CMAKE_CURRENT_SOURCE_DIR}/tests/*.py")
INSTALL(PROGRAMS ${SharemindStdlibTest_FILES}
        DESTINATION "bin"
        COMPONENT "tests")

INSTALL(DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/tests/"
        DESTINATION "lib/sharemind/test"
        COMPONENT "tests"
        FILES_MATCHING PATTERN "*.sc")


# Packaging:
SharemindSetupPackaging()
SharemindAddComponentPackage("stdlib"
    NAME "secrec-stdlib"
    DESCRIPTION "SecreC Standard Library"
    DEB_SECTION "devel"
    DEB_DEPENDS
        "scc (>= 2.0.0)"
)
SharemindAddComponentPackage("doc"
    NAME "secrec-stdlib-doc"
    DESCRIPTION "SecreC Standard Library documentation"
    DEB_SECTION "doc"
)
SharemindAddComponentPackage("tests"
    NAME "secrec-stdlib-tests"
    DESCRIPTION "SecreC Standard Library tests"
    DEB_SECTION "misc"
    DEB_DEPENDS
        "bash"
        "python"
        "scc (>= 2.0.0)"
        "secrec-stdlib"
)
SharemindPackagingFinalize()
