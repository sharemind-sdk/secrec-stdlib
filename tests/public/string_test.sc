/*
 * Copyright (C) 2026 Cybernetica
 *
 * Research/Commercial License Usage
 * Licensees holding a valid Research License or Commercial License
 * for the Software may use this file according to the written
 * agreement between you and Cybernetica.
 *
 * GNU Lesser General Public License Usage
 * Alternatively, this file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPLv3 included in the
 * packaging of this file.  Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl-3.0.html.
 *
 * For further information, please contact us at sharemind@cyber.ee.
 */

import stdlib;
import test_utility;

void main() {
    uint8[[1]] hello_world_bytes = {72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33};
    string hello_world_string = __string_from_bytes(hello_world_bytes);
    test("string operations 0", hello_world_string == "Hello World!");

    test("string operations 1", substring(hello_world_string, (uint)0, (uint)5) == "Hello");
    test("string operations 2", substring(hello_world_string, (uint)6, (uint)11) == "World");
    test("string operations 3", substring(hello_world_string, (uint)11, (uint)12) == "!");

    hello_world_string = __string_from_bytes(__bytes_from_string(hello_world_string));

    test("string operations 4", substring(hello_world_string, (uint)0, (uint)5) == "Hello");
    test("string operations 5", substring(hello_world_string, (uint)6, (uint)11) == "World");
    test("string operations 6", substring(hello_world_string, (uint)11, (uint)12) == "!");

    test_report();
}
