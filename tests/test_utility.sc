/*
 * Copyright (C) 2015 Cybernetica
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

module test_report;

import stdlib;

void test_report(uint tests, uint passed) {
    publish("test_count", tests);
    publish("passed_count", passed);
}

void test_report(int64 tests, int64 passed) {
    // SecreCTestRunner expects uint64s
    test_report((uint) tests, (uint) passed);
}

void test_report(uint32 tests, uint32 passed) {
    // SecreCTestRunner expects uint64s
    test_report((uint) tests, (uint) passed);
}

void test_report_error(float64 relative) {
    publish("f64_max_relative_error", relative);
}

void test_report_error(float32 relative) {
    publish("f32_max_relative_error", relative);
}
