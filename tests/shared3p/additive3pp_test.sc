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

module sorting_test;

import stdlib;
import matrix;
import shared3p;
import shared3p_matrix;
import oblivious;
import shared3p_random;
import shared3p_sort;
import shared3p_bloom;
import shared3p_string;
import shared3p_aes;
import shared3p_join;
import profiling;
import shared3p;
import test_utility;

domain pd_shared3p shared3p;

template<type T>
bool test_sign(T data){
    pd_shared3p T[[1]] temp (15);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T[[1]] result = declassify(sign(temp));
    T[[1]] control (size(result));
    for(uint i = 0; i < size(control);++i){
        if(vec[i] < 0){
            control[i] = -1;
        }
        if(vec[i] > 0){
            control[i] = 1;
        }
        if(vec[i] == 0){
            control[i] = 0;
        }
    }

    return all(result == control);
}

template<type T,type T2>
bool test_abs(T data, T2 data2){
    pd_shared3p T[[1]] temp (15);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T2[[1]] result = declassify(abs(temp));
    T2[[1]] control (size(result));
    for(uint i = 0; i < size(control);++i){
        if(vec[i] < 0){
            control[i] = (T2)(-vec[i]);
        }
        if(vec[i] >= 0){
            control[i] = (T2)(vec[i]);
        }
    }

    return all(result == control);
}

template<type T>
bool test_sum(T data){
    pd_shared3p T[[1]] temp (10);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T result = declassify(sum(temp));
    T control = 0;
    for(uint i = 0; i < size(vec);++i){
        control += vec[i];
    }

    return control == result;
}


template<type T>
bool test_sum2(T data){
    pd_shared3p T[[1]] temp (10);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T[[1]] result = declassify(sum(temp,2::uint));
    uint startingIndex = 0;
    uint endIndex = size(vec) / 2;
    T[[1]] control (2)= 0;
    for(uint i = 0;i < 2;++i){
        for(uint j = startingIndex;j < endIndex;++j){
            control[i] += vec[j];
        }
        startingIndex = 5;
        endIndex = 10;
    }

    return all(control == result);
}

struct uint_tag {}
struct int_tag {}

template<type T>
pd_shared3p T[[1]] prefix_sum_data(int_tag t) {
    pd_shared3p T[[1]] x = {-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    x = shuffle(x);
    return x;
}

template<type T>
pd_shared3p T[[1]] prefix_sum_data(uint_tag t) {
    pd_shared3p T[[1]] x = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    x = shuffle(x);
    return x;
}

template<type T, type Tag>
bool test_prefix_sum(T proxy, Tag tag) {
    pd_shared3p T[[1]] tmp = prefix_sum_data(tag);
    uint n = size(tmp);
    T[[1]] pub = declassify(tmp);
    T[[1]] prsum(n);

    prsum[0] = pub[0];
    for (uint i = 1; i < n; ++i) {
        prsum[i] = prsum[i - 1] + pub[i];
    }

    return all(declassify(prefixSum(tmp)) == prsum);
}

template<type T, type Tag>
bool test_inv_prefix_sum(T proxy, Tag tag) {
    pd_shared3p T[[1]] tmp = prefix_sum_data(tag);
    uint n = size(tmp);
    T[[1]] pub = declassify(tmp);
    T[[1]] prSumPub(n);

    prSumPub[0] = pub[0];
    for (uint i = 1; i < n; ++i) {
        prSumPub[i] = prSumPub[i - 1] + pub[i];
    }

    pd_shared3p T[[1]] prSum = prSumPub;

    return all(declassify(invPrefixSum(prSum)) == pub);
}

template<type T>
bool test_product(T data){
    pd_shared3p T[[1]] temp (10);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T result = declassify(product(temp));
    T control = 1;
    for(uint i = 0; i < size(vec);++i){
        control *= vec[i];
    }

    return control == result;
}

template<type T>
bool test_product2(T data){
    pd_shared3p T[[1]] temp (10);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T[[1]] result = declassify(product(temp,2::uint));
    T[[1]] control (2)= 1;
    uint startingIndex = 0;
    uint endIndex = size(vec) / 2;
    for(uint i = 0; i < 2;++i){
        for(uint j = startingIndex; j < endIndex; ++j){
            control[i] *= vec[j];
        }
        startingIndex += size(vec) / 2;
        endIndex += size(vec) / 2;
    }

    return all(control == result);
}

bool test_any(){
    bool result = true;
    pd_shared3p bool[[1]] vec (6) = {true,false,true,true,false,false};
    pd_shared3p bool[[1]] vec2 (6) = {true,false,false,false,false,false};
    pd_shared3p bool[[1]] vec3 (6) = true;
    pd_shared3p bool[[1]] vec4 (6) = false;
    pd_shared3p bool control = any(vec);
    if(declassify(control) != true){result = false;}

    control = any(vec2);
    if(declassify(control) != true){result = false;}

    control = any(vec3);
    if(declassify(control) != true){result = false;}

    control = any(vec4);
    if(declassify(control) != false){result = false;}

    return result;
}

bool test_all(){
    bool result = true;
    pd_shared3p bool[[1]] vec (6) = {true,false,true,true,false,false};
    pd_shared3p bool[[1]] vec2 (6) = {true,true,true,false,true,true};
    pd_shared3p bool[[1]] vec3 (6) = true;
    pd_shared3p bool[[1]] vec4 (6) = false;
    pd_shared3p bool control = all(vec);
    if(declassify(control) == true){result = false;}

    control = all(vec2);
    if(declassify(control) == true){result = false;}

    control = all(vec3);
    if(declassify(control) != true){result = false;}

    control = all(vec4);
    if(declassify(control) == true){result = false;}

    return result;
}


template<type T>
bool test_min(T data){
    pd_shared3p T[[1]] temp (25);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T result = declassify(min(temp));
    T control = 0;
    for(uint i = 0; i < size(vec);++i){
        if(i == 0){
            control = vec[i];
        }
        else{
            if(vec[i] < control){
                control = vec[i];
            }
        }
    }

    return control == result;
}


template<type T>
bool test_min2(T data){
    pd_shared3p T[[1]] temp (25);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T[[1]] result = declassify(min(temp,5::uint));
    T[[1]] control (5)= 0;
    uint startingIndex = 0;
    uint endIndex = 5;
    for(uint i = 0; i < 5; ++i){
        for(uint j = startingIndex; j < endIndex;++j){
            if(j == startingIndex){
                control[i] = vec[j];
            }
            else{
                if(vec[j] < control[i]){
                    control[i] = vec[j];
                }
            }
        }
        startingIndex += 5;
        endIndex += 5;
    }

    return all(control == result);
}

template<type T>
bool test_min3(T data){
    pd_shared3p T[[1]] temp (10);
    pd_shared3p T[[1]] temp2 (10);
    temp = randomize(temp);
    temp2 = randomize(temp2);
    T[[1]] vec = declassify(temp);
    T[[1]] vec2 = declassify(temp2);
    T[[1]] result = declassify(min(temp,temp2));
    T[[1]] control (10) = 0;
    for(uint i = 0; i < size(vec);++i){
        if(vec[i] <= vec2[i]){
            control[i] = vec[i];
        }
        else{
            control[i] = vec2[i];
        }
    }

    return all(control == result);
}

template<type T>
bool test_max(T data){
    pd_shared3p T[[1]] temp (25);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T result = declassify(max(temp));
    T control = 0;
    for(uint i = 0; i < size(vec);++i){
        if(vec[i] > control){
            control = vec[i];
        }
    }

    return control == result;
}

template<type T>
bool test_max2(T data){
    pd_shared3p T[[1]] temp (25);
    temp = randomize(temp);
    T[[1]] vec = declassify(temp);
    T[[1]] result = declassify(max(temp,5::uint));
    T[[1]] control (5)= 0;
    uint startingIndex = 0;
    uint endIndex = 5;
    for(uint i = 0; i < 5; ++i){
        for(uint j = startingIndex; j < endIndex;++j){
            if(j == startingIndex){
                control[i] = vec[j];
            }
            else{
                if(vec[j] > control[i]){
                    control[i] = vec[j];
                }
            }
        }
        startingIndex += 5;
        endIndex += 5;
    }

    return all(control == result);
}

template<type T>
bool test_max3(T data){
    pd_shared3p T[[1]] temp (10);
    pd_shared3p T[[1]] temp2 (10);
    temp = randomize(temp);
    temp2 = randomize(temp2);
    T[[1]] vec = declassify(temp);
    T[[1]] vec2 = declassify(temp2);
    T[[1]] result = declassify(max(temp,temp2));
    T[[1]] control (10) = 0;
    for(uint i = 0; i < size(vec);++i){
        if(vec[i] >= vec2[i]){
            control[i] = vec[i];
        }
        else{
            control[i] = vec2[i];
        }
    }
    return all(control == result);
}

template<domain D, type T>
void testCeiling(string name, D T[[1]] x, T[[1]] y) {
    test("[$name\] Ceiling", all(declassify(ceiling(x)) == y));
}

void testCeilingFloat32() {
    pd_shared3p float32[[1]] x = {6.91e-13, -6.9e-32, -6.55e36, -7.34e23, -5.34e26, 5.73e23, -6.82, -7.85e-6, 7.07e-37, -8.91e-3};
    float32[[1]] y = {1, 0, -6.55e36, -7.34e23, -5.34e26, 5.73e23, -6, 0, 1, 0};
    testCeiling("float32", x, y);
}

void testCeilingFloat64() {
    pd_shared3p float64[[1]] x = {15.892356329, 5.12974291, 7.5009235790235, -52.325623, -12.5002362, -1.873258, -5.25e67, 5.2e-31, 2.71e114, 5.4e-77};
    float64[[1]] y = {16, 6, 8, -52, -12, -1, -5.25e67, 1, 2.71e114, 1};
    testCeiling("float64", x, y);
}

template<domain D, type T>
void testFloor(string name, D T[[1]] x, T[[1]] y) {
    test("[$name\] Floor", all(declassify(floor(x)) == y));
}

void testFloorFloat32() {
    pd_shared3p float32[[1]] x = {8.90e-11, 5.18e30, -6.97e-19, 6.34e24, -3.11e-22, -2.31e-24, 4.84e7, 8.61e-13, 1.12e3, 5.82e31};
    float32[[1]] y = {0, 5.18e30, -1, 6.34e24, -1, -1, 4.84e7, 0, 1.12e3, 5.82e31};
    testFloor("float32", x, y);
}

void testFloorFloat64() {
    pd_shared3p float64[[1]] x = {15.892356329, 5.12974291, 7.5009235790235, -52.325623, -12.5002362, -1.873258, -3.62e233, -3.89e240, 3.21e-240, 3.11e219};
    float64[[1]] y = {15, 5, 7, -53, -13, -2, -3.62e233, -3.89e240, 0, 3.11e219};
    testFloor("float64", x, y);
}

void testMinFloat32() {
    pd_shared3p float32[[1]] res(4);
    float32[[1]] correct = {-1, 0, -13.37e30, 13.37e-30};

    {
        pd_shared3p float32[[1]] x = {-1, 0};
        res[0] = min(x);
    }
    {
        pd_shared3p float32[[1]] x = {0, 1};
        res[1] = min(x);
    }
    {
        pd_shared3p float32[[1]] x = {-13.37e30, 0};
        res[2] = min(x);
    }
    {
        pd_shared3p float32[[1]] x = {13.37e-30, 13.37e-29};
        res[3] = min(x);
    }

    test("[float32] Min", all(declassify(res) == correct));
}

void testMinFloat64() {
    pd_shared3p float64[[1]] res(4);
    float64[[1]] correct = {-1, 0, -13.37e240, 13.37e-240};

    {
        pd_shared3p float64[[1]] x = {-1, 0};
        res[0] = min(x);
    }
    {
        pd_shared3p float64[[1]] x = {0, 1};
        res[1] = min(x);
    }
    {
        pd_shared3p float64[[1]] x = {-13.37e240, 0};
        res[2] = min(x);
    }
    {
        pd_shared3p float64[[1]] x = {13.37e-240, 13.37e-200};
        res[3] = min(x);
    }

    test("[float64] Min", all(declassify(res) == correct));
}

void testMaxFloat32() {
    pd_shared3p float32[[1]] res(4);
    float32[[1]] correct = {0, 1, 0, 13.37e-29};

    {
        pd_shared3p float32[[1]] x = {-1, 0};
        res[0] = max(x);
    }
    {
        pd_shared3p float32[[1]] x = {0, 1};
        res[1] = max(x);
    }
    {
        pd_shared3p float32[[1]] x = {-13.37e30, 0};
        res[2] = max(x);
    }
    {
        pd_shared3p float32[[1]] x = {13.37e-30, 13.37e-29};
        res[3] = max(x);
    }

    test("[float32] Max", all(declassify(res) == correct));
}

void testMaxFloat64() {
    pd_shared3p float64[[1]] res(4);
    float64[[1]] correct = {0, 1, 0, 13.37e-200};

    {
        pd_shared3p float64[[1]] x = {-1, 0};
        res[0] = max(x);
    }
    {
        pd_shared3p float64[[1]] x = {0, 1};
        res[1] = max(x);
    }
    {
        pd_shared3p float64[[1]] x = {-13.37e240, 0};
        res[2] = max(x);
    }
    {
        pd_shared3p float64[[1]] x = {13.37e-240, 13.37e-200};
        res[3] = max(x);
    }

    test("[float64] Max", all(declassify(res) == correct));
}

void main(){
    string test_prefix = "Sign";
    test(test_prefix, test_sign(0::int8), 0::int8);
    test(test_prefix, test_sign(0::int16), 0::int16);
    test(test_prefix, test_sign(0::int32), 0::int32);
    test(test_prefix, test_sign(0::int64), 0::int64);

    test_prefix = "Abs";
    test(test_prefix, test_abs(0::int8, 0::uint8), 0::int8);
    test(test_prefix, test_abs(0::int16, 0::uint16), 0::int16);
    test(test_prefix, test_abs(0::int32, 0::uint32), 0::int32);
    test(test_prefix, test_abs(0::int64, 0::uint64), 0::int64);

    test_prefix = "Sum";
    test(test_prefix, test_sum(0::uint8), 0::uint8);
    test(test_prefix, test_sum(0::uint16), 0::uint16);
    test(test_prefix, test_sum(0::uint32), 0::uint32);
    test(test_prefix, test_sum(0::uint64), 0::uint64);
    test(test_prefix, test_sum(0::int8), 0::int8);
    test(test_prefix, test_sum(0::int16), 0::int16);
    test(test_prefix, test_sum(0::int32), 0::int32);
    test(test_prefix, test_sum(0::int64), 0::int64);

    test_prefix = "Sum (2)";
    test(test_prefix, test_sum2(0::uint8), 0::uint8);
    test(test_prefix, test_sum2(0::uint16), 0::uint16);
    test(test_prefix, test_sum2(0::uint32), 0::uint32);
    test(test_prefix, test_sum2(0::uint64), 0::uint64);
    test(test_prefix, test_sum2(0::int8), 0::int8);
    test(test_prefix, test_sum2(0::int16), 0::int16);
    test(test_prefix, test_sum2(0::int32), 0::int32);
    test(test_prefix, test_sum2(0::int64), 0::int64);

    test_prefix = "Prefix sum";
    uint_tag ut;
    int_tag it;
    test(test_prefix, test_prefix_sum(0u8, ut), 0u8);
    test(test_prefix, test_prefix_sum(0u16, ut), 0u16);
    test(test_prefix, test_prefix_sum(0u32, ut), 0u32);
    test(test_prefix, test_prefix_sum(0u64, ut), 0u64);
    test(test_prefix, test_prefix_sum(0i8, it), 0i8);
    test(test_prefix, test_prefix_sum(0i16, it), 0i16);
    test(test_prefix, test_prefix_sum(0i32, it), 0i32);
    test(test_prefix, test_prefix_sum(0i64, it), 0i64);

    test_prefix = "Inverse prefix sum";
    test(test_prefix, test_inv_prefix_sum(0u8, ut), 0u8);
    test(test_prefix, test_inv_prefix_sum(0u16, ut), 0u16);
    test(test_prefix, test_inv_prefix_sum(0u32, ut), 0u32);
    test(test_prefix, test_inv_prefix_sum(0u64, ut), 0u64);
    test(test_prefix, test_inv_prefix_sum(0i8, it), 0i8);
    test(test_prefix, test_inv_prefix_sum(0i16, it), 0i16);
    test(test_prefix, test_inv_prefix_sum(0i32, it), 0i32);
    test(test_prefix, test_inv_prefix_sum(0i64, it), 0i64);

    test_prefix = "Product";
    test(test_prefix, test_product(0::uint8), 0::uint8);
    test(test_prefix, test_product(0::uint16), 0::uint16);
    test(test_prefix, test_product(0::uint32), 0::uint32);
    test(test_prefix, test_product(0::uint64), 0::uint64);
    test(test_prefix, test_product(0::int8), 0::int8);
    test(test_prefix, test_product(0::int16), 0::int16);
    test(test_prefix, test_product(0::int32), 0::int32);
    test(test_prefix, test_product(0::int64), 0::int64);

    test_prefix = "Product (2)";
    test(test_prefix, test_product2(0::uint8), 0::uint8);
    test(test_prefix, test_product2(0::uint16), 0::uint16);
    test(test_prefix, test_product2(0::uint32), 0::uint32);
    test(test_prefix, test_product2(0::uint64), 0::uint64);
    test(test_prefix, test_product2(0::int8), 0::int8);
    test(test_prefix, test_product2(0::int16), 0::int16);
    test(test_prefix, test_product2(0::int32), 0::int32);
    test(test_prefix, test_product2(0::int64), 0::int64);

    test_prefix = "Any and All functions";
    test(test_prefix, test_any());
    test(test_prefix, test_all());

    test_prefix = "True Prefix Length";
    {
        bool res = true;
        for (uint i = 0; i < 5; ++i){
            pd_shared3p bool[[1]] arr (10);
            arr = randomize(arr);
            bool[[1]] arr2 = declassify(arr);
            uint result = declassify(truePrefixLength(arr));
            uint control = 0;
            for(uint j = 0; j < size(arr2);++j){
                if(arr2[j]){
                    control += 1;
                }
                else{
                    break;
                }
            }

            if (control != result) {
                res = false;
                break;
            }
        }

        test(test_prefix, res);
    }

    test_prefix = "Min";
    test(test_prefix, test_min(0::uint8), 0::uint8);
    test(test_prefix, test_min(0::uint16), 0::uint16);
    test(test_prefix, test_min(0::uint32), 0::uint32);
    test(test_prefix, test_min(0::uint64), 0::uint64);
    test(test_prefix, test_min(0::int8), 0::int8);
    test(test_prefix, test_min(0::int16), 0::int16);
    test(test_prefix, test_min(0::int32), 0::int32);
    test(test_prefix, test_min(0::int64), 0::int64);

    test_prefix = "Min (2)";
    test(test_prefix, test_min2(0::uint8), 0::uint8);
    test(test_prefix, test_min2(0::uint16), 0::uint16);
    test(test_prefix, test_min2(0::uint32), 0::uint32);
    test(test_prefix, test_min2(0::uint64), 0::uint64);
    test(test_prefix, test_min2(0::int8), 0::int8);
    test(test_prefix, test_min2(0::int16), 0::int16);
    test(test_prefix, test_min2(0::int32), 0::int32);
    test(test_prefix, test_min2(0::int64), 0::int64);

    test_prefix = "Min (3)";
    test(test_prefix, test_min3(0::uint8), 0::uint8);
    test(test_prefix, test_min3(0::uint16), 0::uint16);
    test(test_prefix, test_min3(0::uint32), 0::uint32);
    test(test_prefix, test_min3(0::uint64), 0::uint64);
    test(test_prefix, test_min3(0::int8), 0::int8);
    test(test_prefix, test_min3(0::int16), 0::int16);
    test(test_prefix, test_min3(0::int32), 0::int32);
    test(test_prefix, test_min3(0::int64), 0::int64);

    test_prefix = "Max";
    test(test_prefix, test_max(0::uint8), 0::uint8);
    test(test_prefix, test_max(0::uint16), 0::uint16);
    test(test_prefix, test_max(0::uint32), 0::uint32);
    test(test_prefix, test_max(0::uint64), 0::uint64);
    test(test_prefix, test_max(0::int8), 0::int8);
    test(test_prefix, test_max(0::int16), 0::int16);
    test(test_prefix, test_max(0::int32), 0::int32);
    test(test_prefix, test_max(0::int64), 0::int64);

    test_prefix = "Max (2)";
    test(test_prefix, test_max2(0::uint8), 0::uint8);
    test(test_prefix, test_max2(0::uint16), 0::uint16);
    test(test_prefix, test_max2(0::uint32), 0::uint32);
    test(test_prefix, test_max2(0::uint64), 0::uint64);
    test(test_prefix, test_max2(0::int8), 0::int8);
    test(test_prefix, test_max2(0::int16), 0::int16);
    test(test_prefix, test_max2(0::int32), 0::int32);
    test(test_prefix, test_max2(0::int64), 0::int64);

    test_prefix = "Max (3)";
    test(test_prefix, test_max3(0::uint8), 0::uint8);
    test(test_prefix, test_max3(0::uint16), 0::uint16);
    test(test_prefix, test_max3(0::uint32), 0::uint32);
    test(test_prefix, test_max3(0::uint64), 0::uint64);
    test(test_prefix, test_max3(0::int8), 0::int8);
    test(test_prefix, test_max3(0::int16), 0::int16);
    test(test_prefix, test_max3(0::int32), 0::int32);
    test(test_prefix, test_max3(0::int64), 0::int64);

    testCeilingFloat32();
    testCeilingFloat64();
    testFloorFloat32();
    testFloorFloat64();
    testMinFloat32();
    testMinFloat64();
    testMaxFloat32();
    testMaxFloat64();

    test_report();
}
