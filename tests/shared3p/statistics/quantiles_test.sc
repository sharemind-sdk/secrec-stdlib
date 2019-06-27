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

import stdlib;
import shared3p;
import shared3p_statistics_summary;
import test_utility;

domain pd_shared3p shared3p;

int64[[1]] input = {137, -56, 36, 63, 40, -11, 151, -9, 202, -6, 130, 229, -139, -28, -13, 64, -28, -266, -244, 132};
pd_shared3p bool[[1]] ia(size(input)) = true;
float64[[1]] probs = {0.0, 0.5, 1.0};
float64[[1]] expected = {-266, 15, 229};

template<type IT, type FT>
bool qtest(IT intProxy, FT floatProxy) {
    pd_shared3p IT[[1]] x = (IT) input;
    float64[[1]] res = (float64) declassify(quantiles(x, ia, probs));
    return all(isNegligible(abs(res - expected) / expected));
}

void main() {
    string test_prefix = "Quantiles";
    test(test_prefix, qtest(0i32, 0f32), 0i32);
    test(test_prefix, qtest(0i64, 0f64), 0i64);
    test(test_prefix, qtest(0f32, 0f32), 0f32);
    test(test_prefix, qtest(0f64, 0f64), 0f64);
    test_report();
}
