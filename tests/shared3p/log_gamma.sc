/*
 * Copyright (C) Cybernetica
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

import shared3p;
import stdlib;
import test_utility;

domain pd_shared3p shared3p;

void main() {
    string testPrefix = "logGamma";
    float64[[1]] expected = {0.693147180559945,1.79175946922805,3.17805383034795,4.78749174278205,6.5792512120101,8.52516136106541,10.6046029027453,12.8018274800815};
    pd_shared3p uint[[1]] in = iota(8u64) + 3;
    pd_shared3p float64[[1]] lg = logGamma((float64) in);
    float64[[1]] res = declassify(lg);
    float64[[1]] relErr = abs(res - expected) / expected;
    bool good = all(relErr < 1e-6);
    test(testPrefix, good, 0f64);
    test_report();
}
