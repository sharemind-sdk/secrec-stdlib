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

import stdlib;
import shared3p;
import shared3p_random;
import test_utility;

domain pd_shared3p shared3p;

template<domain D, type T>
bool test_min(D T data){
    D T[[1]] temp (25);
    temp = randomize(temp);
    D T[[1]] vec = declassify(temp);
    D T result = min(temp);
    D T control = 0;
    for(uint i = 0; i < size(vec);++i){
        if(i == 0){
            control = vec[i];
        }
        else{
            if(declassify(vec[i]) < declassify(control)){
                control = vec[i];
            }
        }
    }

    return declassify(control) == declassify(result);
}

template<domain D, type T>
bool test_min2(D T data){
    D T[[1]] temp (10);
    D T[[1]] temp2 (10);
    temp = randomize(temp);
    temp2 = randomize(temp2);
    D T[[1]] vec = temp;
    D T[[1]] vec2 = temp2;
    D T[[1]] result = min(temp,temp2);
    D T[[1]] control (10) = 0;
    for(uint i = 0; i < size(vec);++i){
        if(declassify(vec[i]) <= declassify(vec2[i])){
            control[i] = vec[i];
        }
        else{
            control[i] = vec2[i];
        }
    }

    return all(declassify(control) == declassify(result));
}

template<domain D, type T>
bool test_max(D T data){
    D T[[1]] temp (25);
    temp = randomize(temp);
    D T[[1]] vec = temp;
    D T result = max(temp);
    D T control = 0;
    for(uint i = 0; i < size(vec);++i){
        if(declassify(vec[i]) > declassify(control)){
            control = vec[i];
        }
    }

    return declassify(control) == declassify(result);
}

template<domain D, type T>
bool test_max2(D T data){
    D T[[1]] temp (10);
    D T[[1]] temp2 (10);
    temp = randomize(temp);
    temp2 = randomize(temp2);
    D T[[1]] vec = temp;
    D T[[1]] vec2 = temp2;
    D T[[1]] result = max(temp,temp2);
    D T[[1]] control (10) = 0;
    for(uint i = 0; i < size(vec);++i){
        if(declassify(vec[i]) >= declassify(vec2[i])){
            control[i] = vec[i];
        }
        else{
            control[i] = vec2[i];
        }
    }

    return all(declassify(control) == declassify(result));
}

template<domain D, type T>
bool test_reshare(D T data){
    D T scalar = 0;
    scalar = randomize(scalar);
    D T scalar2 = reshare(reshare(scalar));

    if (declassify(scalar) != declassify(scalar2))
        return false;

    D T[[1]] vector (15) = 0;
    vector = randomize(vector);
    D T[[1]] vector2 = reshare(reshare(vector));

    if (!all(declassify(vector) == declassify(vector2)))
        return false;

    return true;
}

template<domain D, type T, type U, type V>
void convTest(string name, D T[[1]] x, D U dummy, V[[1]] y) {
   D U[[1]] z = reshare(x);
   test(name, all(declassify(z) == y));
}

void int8ToXor8Test() {
   pd_shared3p int8[[1]] x (10) = {55, -45, -113, 85, 87, -73, -27, -15, -56, -109};
   pd_shared3p xor_uint8 dummy = 0;
   uint8[[1]] y (10) = {55, 211, 143, 85, 87, 183, 229, 241, 200, 147};
   convTest("[int8] Int to xor", x, dummy, y);
}

void int16ToXor16Test() {
   pd_shared3p int16[[1]] x (10) = {-18258, -25463, 24586, -27999, -6621, 5760, -4876, -24920, -12355, 9882};
   pd_shared3p xor_uint16 dummy = 0;
   uint16[[1]] y (10) = {47278, 40073, 24586, 37537, 58915, 5760, 60660, 40616, 53181, 9882};
   convTest("[int16] Int to xor", x, dummy, y);
}

void int32ToXor32Test() {
   pd_shared3p int32[[1]] x (10) = {657010390, -390652091, 679742933, 464949106, 909901862, 412333247, 212339037, -1002137623, 1549870009, 1207921783};
   pd_shared3p xor_uint32 dummy = 0;
   uint32[[1]] y (10) = {657010390, 3904315205, 679742933, 464949106, 909901862, 412333247, 212339037, 3292829673, 1549870009, 1207921783};
   convTest("[int32] Int to xor", x, dummy, y);
}

void int64ToXor64Test() {
   pd_shared3p int64[[1]] x (10) = {6911461533151042471, -4221554645635911083, -6518402290555174768, -5813548148959267251, -5163294253093278263, -6093998330166406275, -5965183803573730444, -6658553995594222000, 4007016438257298880, -6634279755582745257};
   pd_shared3p xor_uint64 dummy = 0;
   uint64[[1]] y (10) = {6911461533151042471, 14225189428073640533, 11928341783154376848, 12633195924750284365, 13283449820616273353, 12352745743543145341, 12481560270135821172, 11788190078115329616, 4007016438257298880, 11812464318126806359};
   convTest("[int64] Int to xor", x, dummy, y);
}

void main() {
    string test_prefix = "Min";
    { pd_shared3p xor_uint8 t; test(test_prefix, test_min(t), t); }
    { pd_shared3p xor_uint16 t; test(test_prefix, test_min(t), t); }
    { pd_shared3p xor_uint32 t; test(test_prefix, test_min(t), t); }
    { pd_shared3p xor_uint64 t; test(test_prefix, test_min(t), t); }

    test_prefix = "Min (2)";
    { pd_shared3p xor_uint8 t; test(test_prefix, test_min2(t), t); }
    { pd_shared3p xor_uint16 t; test(test_prefix, test_min2(t), t); }
    { pd_shared3p xor_uint32 t; test(test_prefix, test_min2(t), t); }
    { pd_shared3p xor_uint64 t; test(test_prefix, test_min2(t), t); }

    test_prefix = "Max";
    { pd_shared3p xor_uint8 t; test(test_prefix, test_max(t), t); }
    { pd_shared3p xor_uint16 t; test(test_prefix, test_max(t), t); }
    { pd_shared3p xor_uint32 t; test(test_prefix, test_max(t), t); }
    { pd_shared3p xor_uint64 t; test(test_prefix, test_max(t), t); }

    test_prefix = "Max (2)";
    { pd_shared3p xor_uint8 t; test(test_prefix, test_max2(t), t); }
    { pd_shared3p xor_uint16 t; test(test_prefix, test_max2(t), t); }
    { pd_shared3p xor_uint32 t; test(test_prefix, test_max2(t), t); }
    { pd_shared3p xor_uint64 t; test(test_prefix, test_max2(t), t); }

    test_prefix = "Resharing";
    { pd_shared3p xor_uint8 t; test(test_prefix, test_reshare(t), t); }
    { pd_shared3p xor_uint16 t; test(test_prefix, test_reshare(t), t); }
    { pd_shared3p xor_uint32 t; test(test_prefix, test_reshare(t), t); }
    { pd_shared3p xor_uint64 t; test(test_prefix, test_reshare(t), t); }
    int8ToXor8Test();
    int16ToXor16Test();
    int32ToXor32Test();
    int64ToXor64Test();
    test_report();
}
