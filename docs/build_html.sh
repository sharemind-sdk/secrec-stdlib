#!/bin/sh
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

# When invokes without arguments,
# create a Doxyfile that is usable without CMake:
if [ $# -eq 0 ]; then
	sed -e '/OUTPUT_DIRECTORY/s,@CMAKE_CURRENT_BINARY_DIR@/generated_docs,,' \
		-e 's,@CMAKE_CURRENT_SOURCE_DIR@,..,' < Doxyfile.in > Doxyfile
fi

doxygen "$@"

for file in html/*.html; do
	sed '/.*<td class=\"paramtype\">.*<\/td>/ {
		N
		s/\[\[\(.\)\]/\[\[\1\]\]/
		s/\(.*\)\(\]\)\(.*\)\(\[\[.\]\]\)\(.*\)/\1\4\3\5/
	}'<"$file" >html/tempfile.txt
	mv html/tempfile.txt "$file"
done
