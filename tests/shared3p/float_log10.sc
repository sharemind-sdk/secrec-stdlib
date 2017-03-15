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
PrecisionTest<T> test_log10(T data){
    T max_absolute = 0, max_relative = 0;
    pd_shared3p T[[1]] a = {0.1,0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 2, 3, 4, 5, 10, 50, 100, 500, 1000, 5000, 10000};
    T[[1]] b (20) = {-0.9999999999999998,-0.6989700043360187,-0.5228787452803376,-0.39794000867203755,-0.30102999566398114,-0.22184874961635637,-0.1549019599857432,-9.691001300805638e-2,-4.5757490560675115e-2,0.30102999566398114,0.47712125471966244,0.6020599913279623,0.6989700043360187,1.0,1.6989700043360185,2.0,2.6989700043360183,2.9999999999999996,3.6989700043360187,4.0};
    pd_shared3p T[[1]] c = log10(a);
    T[[1]] d = abs(declassify(c) - b);

    max_absolute = max(d);
    max_relative = max(d / abs(b));

    public PrecisionTest<T> rv;
    rv.res = true;
    rv.max_abs_error = max_absolute;
    rv.max_rel_error = max_relative;

    return rv;
}

void main(){
    string test_prefix = "Float32/64 log10 precision";
    {
        PrecisionTest<float32> rv = test_log10(0::float32);
        test(test_prefix, rv);
    }
    {
        PrecisionTest<float64> rv = test_log10(0::float64);
        test(test_prefix, rv);
    }

    test_report();
}
