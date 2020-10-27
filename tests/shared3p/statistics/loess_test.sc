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
    public LOESSResult<pd_shared3p, FT> loessRes = loess(x, y, 0.5, xmin, xmax, 10u64, 1u64);
    FT[[1]] yhat = declassify(loessRes.predictions);
    FT[[1]] expected = {1.483369432544005, 0.956331006707545, 0.426573066546283, -0.09225413189280163, -0.5817571341963559, -0.004697605143984873, 0.8185822487156384, 1.109264907582611, 0.5198053843252097, -0.1793523694445547};
    return all(_isNegligible(abs(yhat - expected) / expected));
}

template<type T, type FT>
bool testLoessInt(T proxy, FT floatProxy) {
    pd_shared3p T[[1]] x = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    pd_shared3p T[[1]] y = {8, 9, 1, -8, -10, -3, 7, 10, 4, -5};
    FT xmin = 1.0;
    FT xmax = 10.0;
    public LOESSResult<pd_shared3p, FT> res = loess(x, y, 0.5, xmin, xmax, 10u64, 1u64);
    FT[[1]] yhat = (FT) declassify(res.predictions);
    FT[[1]] expected = {10.06138113656549, 5.648218943235182, 0.7136894824707802, -5.995826377295515, -7.42320534223709, -2.14106844741238, 4.995826377295487, 7.423205342236997, 2.313850362637183, -3.522204404808598};
    return all(_isNegligible(abs(yhat - expected) / expected));
}

template<type FT>
bool testLoessDegree2(FT proxy) {
    pd_shared3p FT[[1]] x = {9.14806043496355, 9.37075413297862, 2.86139534786344, 8.30447626067325, 6.41745518893003, 5.19095949130133, 7.36588314641267, 1.3466659723781, 6.56992290401831, 7.05064784036949, 4.5774177624844, 7.19112251652405, 9.34672247152776, 2.55428824340925, 4.62292822543532, 9.40014522755519, 9.78226428385824, 1.17487361654639, 4.74997081561014, 5.60332746244967};
    pd_shared3p FT[[1]] y = {0.72521521938788, 0.123352636731389, 0.77099112938544, 1.37356596292468, 0.175085579102816, -0.63054823241008, 1.07832789745638, 1.42785682735176, 0.506309345119511, 1.11231345496861, -0.622107395700289, 1.19376360251485, 0.272030393873059, 0.896703993777019, -0.994026885370565, 0.441088282342767, -0.346253507140514, 1.02647059144904, -0.545993182046701, -0.322793159881682};
    FT xmin = 1.0;
    FT xmax = 10.0;
    public LOESSResult<pd_shared3p, FT> loessRes = loess(x, y, 0.5, xmin, xmax, 10u64, 2u64);
    FT[[1]] yhat = declassify(loessRes.predictions);
    FT[[1]] expected = {1.275458187666516, 1.0374942092224, 0.5271546048933428, -0.5224016605113906, -0.6417525558753707, -0.1138259068417937, 0.9020014039836403, 1.387624723459258, 0.8158732915440193, -0.8317565699179543};
    print("yhat");
    printVector(yhat);
    return all(_isNegligible(abs(yhat - expected) / expected));
}

void main() {
	string testPrefix = "LOESS regression";
    test(testPrefix, testLoess(0f64), 0f64);
    test(testPrefix, testLoessInt(0i64, 0f64), 0i64);
    testPrefix = "LOESS regression (degree 2)";
    test(testPrefix, testLoessDegree2(0f64), 0f64);
    test_report();
}
