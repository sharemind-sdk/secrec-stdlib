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

module test_utility_random;

template <type T, dim N>
T[[N]] randomize(T[[N]] data) {
    __syscall ("blockingURandomize", __ref data);
    return data;
}

template<type T>
T random_float(T data) {
    T rand = 1;
    for (uint i = 0; i < 2; ++i) {
        uint32 temp;
        int8 temp2;
        while(temp == 0 || temp2 == 0) {
            temp = randomize(temp);
            temp2 = randomize(temp2);
        }
        T scalar = (T) temp;
        T scalar2 = (T) temp2;
        if ((i % 2) == 0) {
            rand *= scalar;
            rand *= scalar2;
        } else {
            rand /= scalar;
            rand /= scalar2;
        }
    }
    return rand;
}

float32[[1]] randomize(float32[[1]] data) {
    uint s = size(data);
    for (uint i = 0; i < s; ++i)
        data[i] = random_float(0::float32);
    return data;
}

float64[[1]] randomize(float64[[1]] data) {
    uint s = size(data);
    for (uint i = 0; i < s; ++i)
        data[i] = random_float(0::float64);
    return data;
}
