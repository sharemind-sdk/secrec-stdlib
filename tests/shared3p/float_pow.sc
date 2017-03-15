/*
 * Copyright (C) 2017 Cybernetica
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
import test_utility;

domain pd_shared3p shared3p;

template<type T>
PrecisionTest<T> test_pow(T proxy) {
    pd_shared3p T[[1]] a = {0.1, 0.1, 0.1, 0.1, 0.1, 2, 2, 2, 2, 2, 1000, 1000, 1000, 1000};
    pd_shared3p T[[1]] b = {0.1, 1, 2, 100, 1000, 0.1, 1, 2, 100, 1000, 0.1, 1, 2, 100};
    T[[1]] correct = {0.794328234724281, 0.1, 0.01, 1.00000000000001e-100, 0, 1.07177346253629, 2, 4, 1.26765060022823e+30, 1.07150860718627e+301, 1.99526231496888, 1000, 1e6, 1e300};
    pd_shared3p T[[1]] res = pow(a, b);
    T[[1]] d = abs(declassify(res) - correct);

    public PrecisionTest<T> rv;
    rv.res = true;
    rv.max_abs_error = max(d);
    rv.max_rel_error = max(d / abs(correct));

    return rv;
}

void main() {
    string test_prefix = "Float32 pow precision";
    {
        PrecisionTest<float32> rv = test_pow(0 :: float32);
        test(test_prefix, rv);
    }
    {
        test_prefix = "Float64 pow precision";
        PrecisionTest<float64> rv = test_pow(0 :: float64);
        test(test_prefix, rv);
    }
    test_report();
}
