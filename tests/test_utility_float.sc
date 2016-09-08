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

module test_utility_float;

import stdlib;

float32 f32Precision = 1e-5;
float64 f64Precision = 1e-14;

template<domain D : shared3p, dim N>
bool isRelativeErrorSmall(D float32[[N]] result, D float32[[N]] expected) {
    assert(size(result) == size(expected));
    return all(abs(declassify((result - expected) / expected)) < f32Precision);
}

template<domain D : shared3p, dim N>
bool isRelativeErrorSmall(D float32[[N]] result, float32[[N]] expected) {
    D float32[[N]] tmp = expected;
    return isRelativeErrorSmall(result, tmp);
}

template<domain D : shared3p, dim N>
bool isRelativeErrorSmall(float32[[N]] result, D float32[[N]] expected) {
    D float32[[N]] tmp = result;
    return isRelativeErrorSmall(tmp, expected);
}

template<domain D : shared3p, dim N>
bool isRelativeErrorSmall(D float64[[N]] result, D float64[[N]] expected) {
    assert(size(result) == size(expected));
    return all(abs(declassify((result - expected) / expected)) < f64Precision);
}

template<domain D : shared3p, dim N>
bool isRelativeErrorSmall(D float64[[N]] result, float64[[N]] expected) {
    D float64[[N]] tmp = expected;
    return isRelativeErrorSmall(result, tmp);
}

template<domain D : shared3p, dim N>
bool isRelativeErrorSmall(float64[[N]] result, D float64[[N]] expected) {
    D float64[[N]] tmp = result;
    return isRelativeErrorSmall(tmp, expected);
}
