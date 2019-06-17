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
import shared3p_statistics_regression;
import stdlib;
import test_utility;

domain pd_shared3p shared3p;

template <domain D, dim N>
D bool[[N]] _isNegligible (D float32[[N]] a) {
    return abs(a) < 1e-4;
}

template <domain D, dim N>
D bool[[N]] _isNegligible (D float64[[N]] a) {
    return abs(a) < 1e-8;
}

template<type FT>
bool testLoess(FT proxy) {
    pd_shared3p FT[[1]] x = {9.14806043496355, 9.37075413297862, 2.86139534786344, 8.30447626067325, 6.41745518893003, 5.19095949130133, 7.36588314641267, 1.3466659723781, 6.56992290401831, 7.05064784036949, 4.5774177624844, 7.19112251652405, 9.34672247152776, 2.55428824340925, 4.62292822543532, 9.40014522755519, 9.78226428385824, 1.17487361654639, 4.74997081561014, 5.60332746244967};
    pd_shared3p FT[[1]] y = {0.72521521938788, 0.123352636731389, 0.77099112938544, 1.37356596292468, 0.175085579102816, -0.63054823241008, 1.07832789745638, 1.42785682735176, 0.506309345119511, 1.11231345496861, -0.622107395700289, 1.19376360251485, 0.272030393873059, 0.896703993777019, -0.994026885370565, 0.441088282342767, -0.346253507140514, 1.02647059144904, -0.545993182046701, -0.322793159881682};
    FT xmin = 1.0;
    FT xmax = 10.0;
    public LOESSResult<pd_shared3p, FT> res = loess(x, y, 0.5, xmin, xmax, 10u64);
    FT[[1]] interceptExpected = {2.01337326010854, 2.03742020314104, 2.08756901334219, 2.45858133763966, -0.0310308323423644, -3.58371124273634, -5.37952669716792, -3.3183172829887, 3.61830184166846, 6.11262882392136};
    FT[[1]] slopeExpected = {-0.530003827564528, -0.540755179379007, -0.556495366459796, -0.640237611583802, -0.0943196254444856, 0.598887636833, 0.89716189505699, 0.592796172985467, -0.322383520269567, -0.621346511264165};

    return
        all(_isNegligible(
                abs(interceptExpected - declassify(res.intercept)) /
                interceptExpected)) &&
        all(_isNegligible(
                abs(slopeExpected - declassify(res.slope)) /
                slopeExpected));
}

void main() {
	string testPrefix = "LOESS regression";
	test(testPrefix, testLoess(0f32), 0f32);
    test(testPrefix, testLoess(0f64), 0f64);
    test_report();
}
