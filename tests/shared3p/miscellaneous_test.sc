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

module miscellaneous_test;

import stdlib;
import shared3p;
import shared3p_random;
import test_utility;

domain pd_shared3p shared3p;

// NOTE: This is a hack to generate somewhat random floats.
template <domain D : shared3p>
D float32[[1]] randomize(D float32[[1]] arr) {
    D uint[[1]] tmp(size(arr));
    return (float32)declassify(randomize(tmp)) / (float32)declassify(randomize(tmp));
}

template <domain D : shared3p>
D float64[[1]] randomize(D float64[[1]] arr) {
    D uint[[1]] tmp(size(arr));
    return (float64)declassify(randomize(tmp)) / (float64)declassify(randomize(tmp));
}

template<domain D, type T>
bool cast_bool_to_type(D T data) {
    bool result = true;
    D bool[[1]] temp (5);
    D bool[[1]] a = randomize(temp);
    D T[[1]] b (5) = (T)a;
    for (uint i = 0; i < 5; ++i) {
        if (declassify(a[i]) == true && declassify(b[i]) == 0) {
            result = false;
            break;
        }
        if (declassify(a[i]) == false && declassify(b[i]) == 1) {
            result = false;
            break;
        }
    }

    return result;
}

template<domain D, type T>
bool cast_type_to_bool(D T data) {
    bool result = true;
    D T[[1]] temp (10);
    D T[[1]] a = randomize(temp);
    a[0] = 0;
    a[1] = 1;
    a[2] = -1;

    D bool[[1]] b (10) = (bool)a;

    for (uint i = 0; i < 10; ++i) {
        if (declassify(b[i]) == true && declassify(a[i]) == 0) {
            result = false;
            break;
        }
        if (declassify(b[i]) == false && declassify(a[i]) != 0) {
            result = false;
            break;
        }
    }

    return result;
}

template<domain D, type T>
bool cast_float_to_bool(D T data) {
    bool result = true;
    D T[[1]] temp (10);
    D T[[1]] a = randomize(temp);
    a[0] = 0;
    a[1] = 1;
    a[2] = -1;

    D bool[[1]] b (10) = (bool)a;

    for (uint i = 0; i < 10; ++i) {
        if (declassify(b[i]) == true && abs(declassify(a[i])) < 1.0) {
            result = false;
            break;
        }
        if (declassify(b[i]) == false && abs(declassify(a[i])) >= 1.0) {
            result = false;
            break;
        }
    }

    return result;
}

template<domain D1, type T1, domain D2, type T2, dim N>
bool cast_type_to_type(D1 T1 [[N]] t1, D2 T2 [[N]] t2) {
    D2 T2[[1]] c = (T2)(t1);
    return all(declassify(c) == declassify(t2));
}

template<domain D, type T, type U>
void convTest(string name1, string name2, D T[[1]] x, U[[1]] y) {
   test("[$name1\, $name2\] Casting values", all(declassify((U)(x)) == y));
}

void float32ToFloat64Test() {
    pd_shared3p float32[[1]] x = {0, 1, 5.8e9, 16, 3.38e2};
    float64[[1]] y = {0, 1, 5.8e9, 16, 3.38e2};
    convTest("float32", "float64", x, y);
}

void float32ToInt8Test() {
   pd_shared3p float32[[1]] x = {0.0, 1.17549e-38, 1.0, -98.12907, 4.871303, 110.6676, -53.25512, 62.77517, -101.5022, -64.87247};
   int8[[1]] y = {0, 0, 1, -98, 4, 110, -53, 62, -101, -64};
   convTest("float32", "int8", x, y);
}

void float32ToInt16Test() {
   pd_shared3p float32[[1]] x = {0.0, 1.17549e-38, 1.0, -22670.28, -14073.18, -26256.64, -25024.43, -11851.27, -17813.15, 25735.13};
   int16[[1]] y = {0, 0, 1, -22670, -14073, -26256, -25024, -11851, -17813, 25735};
   convTest("float32", "int16", x, y);
}

void float32ToInt32Test() {
   pd_shared3p float32[[1]] x = {0.0, 1.17549e-38, 1.0, 4473403, 4593648, 8278661, -2754798, 206693, -181742, -8189004};
   int32[[1]] y = {0, 0, 1, 4473403, 4593648, 8278661, -2754798, 206693, -181742, -8189004};
   convTest("float32", "int32", x, y);
}

void float32ToInt64Test() {
   pd_shared3p float32[[1]] x = {0.0, 1.17549e-38, 1.0, 6279086, -2838606, -6011487, 1842222, -7904507, -863255, 2482582};
   int64[[1]] y = {0, 0, 1, 6279086, -2838606, -6011487, 1842222, -7904507, -863255, 2482582};
   convTest("float32", "int64", x, y);
}

void float32ToUint8Test() {
   pd_shared3p float32[[1]] x = {0.0, 1.17549e-38, 1.0, 155.2418, 92.17343, 141.0221, 89.2679, 18.87935, 125.2439, 49.74959};
   uint8[[1]] y = {0, 0, 1, 155, 92, 141, 89, 18, 125, 49};
   convTest("float32", "uint8", x, y);
}

void float32ToUint16Test() {
   pd_shared3p float32[[1]] x = {0.0, 1.17549e-38, 1.0, 7425.207, 45462.6, 15092.68, 37815.58, 7813.595, 44092.13, 48335.55};
   uint16[[1]] y = {0, 0, 1, 7425, 45462, 15092, 37815, 7813, 44092, 48335};
   convTest("float32", "uint16", x, y);
}

void float32ToUint32Test() {
   pd_shared3p float32[[1]] x = {0.0, 1.17549e-38, 1.0, 8889381, 4893845, 3320645, 5945922, 5821593, 1957735, 3780257};
   uint32[[1]] y = {0, 0, 1, 8889381, 4893845, 3320645, 5945922, 5821593, 1957735, 3780257};
   convTest("float32", "uint32", x, y);
}

void float32ToUint64Test() {
   pd_shared3p float32[[1]] x = {0.0, 1.17549e-38, 1.0, 8673933, 3866533, 4536299, 548599, 3624221, 7360995, 5281006};
   uint64[[1]] y = {0, 0, 1, 8673933, 3866533, 4536299, 548599, 3624221, 7360995, 5281006};
   convTest("float32", "uint64", x, y);
}

void float64ToFloat32Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 5.29e-122, 6.61e-150, -1.74e-145, -3.01e-16, 2.11e-220, -5.77e-155, 2.17e-166};
   float32[[1]] y = {0.0, 0, 1.0, 0, 0, 0, -3.01e-16, 0, 0, 0};
   convTest("float64", "float32", x, y);
}

void float64ToInt8Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 67.34256046636686, 18.53741706553278, -60.4708076984191, 56.26073831375213, -97.28398198151534, -48.52230757773533, 126.2882510850292};
   int8[[1]] y = {0, 0, 1, 67, 18, -60, 56, -97, -48, 126};
   convTest("float64", "int8", x, y);
}

void float64ToInt16Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 11692.83007815261, 27103.47913318653, 15818.07401064132, -1854.237357416921, -32742.02943785659, 12336.50976824283, 20923.09959576575};
   int16[[1]] y = {0, 0, 1, 11692, 27103, 15818, -1854, -32742, 12336, 20923};
   convTest("float64", "int16", x, y);
}

void float64ToInt32Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 840558046.4931513, -1119152181.010918, -455756238.6323327, 1788410751.531635, -186796018.4341952, 1066959045.484518, -186423360.2554765};
   int32[[1]] y = {0, 0, 1, 840558046, -1119152181, -455756238, 1788410751, -186796018, 1066959045, -186423360};
   convTest("float64", "int32", x, y);
}

void float64ToInt64Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 5137005476335485, 3672613797350584, 7042690447228970, 6300228234817817, 187448944969007, 5040321966315490, -2725190092593780};
   int64[[1]] y = {0, 0, 1, 5137005476335485, 3672613797350584, 7042690447228970, 6300228234817817, 187448944969007, 5040321966315490, -2725190092593780};
   convTest("float64", "int64", x, y);
}

void float64ToUint8Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 51.50023523556684, 97.10746737180505, 75.69393812851449, 110.9694163130094, 120.4210109385665, 97.71788043511429, 20.39361399614753};
   uint8[[1]] y = {0, 0, 1, 51, 97, 75, 110, 120, 97, 20};
   convTest("float64", "uint8", x, y);
}

void float64ToUint16Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 32012.0958706886, 8287.874108749479, 4696.2343674202, 42472.92206052255, 45267.09157779862, 3088.513566138094, 55044.45803370675};
   uint16[[1]] y = {0, 0, 1, 32012, 8287, 4696, 42472, 45267, 3088, 55044};
   convTest("float64", "uint16", x, y);
}

void float64ToUint32Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 625558523.7176411, 1528921474.993432, 1505113109.446223, 3167971255.016057, 2585586096.961693, 3506780783.089148, 1761146669.710629};
   uint32[[1]] y = {0, 0, 1, 625558523, 1528921474, 1505113109, 3167971255, 2585586096, 3506780783, 1761146669};
   convTest("float64", "uint32", x, y);
}

void float64ToUint64Test() {
   pd_shared3p float64[[1]] x = {0.0, 2.22507e-308, 1.0, 590492966521176, 5916627273166019, 6161279570134107, 3579620507762477, 4977947794168105, 1556732877006147, 1516693833581697};
   uint64[[1]] y = {0, 0, 1, 590492966521176, 5916627273166019, 6161279570134107, 3579620507762477, 4977947794168105, 1556732877006147, 1516693833581697};
   convTest("float64", "uint64", x, y);
}

void int8ToFloat32Test() {
    pd_shared3p int8[[1]] x = {-128, -40, 40, 127, 77, 77, -46, -55, -33, -90};
    float32[[1]] y = {-128, -40, 40, 127, 77, 77, -46, -55, -33, -90};
    convTest("int8", "float32", x, y);
}

void int8ToFloat64Test() {
    pd_shared3p int8[[1]] x = {-128, -40, 40, 127, 8, 48, 122, -60, -59, 118};
    float64[[1]] y = {-128, -40, 40, 127, 8, 48, 122, -60, -59, 118};
    convTest("int8", "float64", x, y);
}

void int16ToFloat32Test() {
    pd_shared3p int16[[1]] x = {-32768, -16325, 12435, 32767, -9225, -5021, 21197, 6919, 21507, -17324};
    float32[[1]] y = {-32768, -16325, 12435, 32767, -9225, -5021, 21197, 6919, 21507, -17324};
    convTest("int16", "float32", x, y);
}

void int16ToFloat64Test() {
    pd_shared3p int16[[1]] x = {-32768, -16325, 12435, 32767, -22584, 10488, 26358, -9133, 6477, -12152};
    float64[[1]] y = {-32768, -16325, 12435, 32767, -22584, 10488, 26358, -9133, 6477, -12152};
    convTest("int16", "float64", x, y);
}

void int32ToFloat32Test() {
    pd_shared3p int32[[1]] x = {-483648, 2147483, 5114961, 4121316, -9030972, 7277922, 7810846, 6591096, 451077, -233345};
    float32[[1]] y = {-483648, 2147483, 5114961, 4121316, -9030972, 7277922, 7810846, 6591096, 451077, -233345};
    convTest("int32", "float32", x, y);
}

void int32ToFloat64Test() {
    pd_shared3p int32[[1]] x = {-2147483648, -483648, 2147483, 2147483647, 1553739963, 1909999442, -527530861, -1095817286, 1474821752, -573468176};
    float64[[1]] y = {-2147483648, -483648, 2147483, 2147483647, 1553739963, 1909999442, -527530861, -1095817286, 1474821752, -573468176};
    convTest("int32", "float64", x, y);
}

void int64ToFloat32Test() {
    pd_shared3p int64[[1]] x = {-6164537, -4242358, -5480797, 1946553, -8247488, 3510786, 207152, -9386566, 271576, -7643811};
    float32[[1]] y = {-6164537, -4242358, -5480797, 1946553, -8247488, 3510786, 207152, -9386566, 271576, -7643811};
    convTest("int64", "float32", x, y);
}

void int64ToFloat64Test() {
    pd_shared3p int64[[1]] x = {-7036854775808, 9223372036854, 9242640490324588, 6198843961452625, 8346019426791551, 5565847454532388, -5767200900243613, -2481935404808073, 9463908704046509, -2162408335404861};
    float64[[1]] y = {-7036854775808, 9223372036854, 9242640490324588, 6198843961452625, 8346019426791551, 5565847454532388, -5767200900243613, -2481935404808073, 9463908704046509, -2162408335404861};
    convTest("int64", "float64", x, y);
}

void uint8ToFloat32Test() {
    pd_shared3p uint8[[1]] x = {0, 100, 200, 255, 94, 180, 185, 56, 115, 158};
    float32[[1]] y = {0, 100, 200, 255, 94, 180, 185, 56, 115, 158};
    convTest("uint8", "float32", x, y);
}

void uint8ToFloat64Test() {
    pd_shared3p uint8[[1]] x = {0, 100, 200, 255, 117, 7, 33, 16, 72, 114};
    float64[[1]] y = {0, 100, 200, 255, 117, 7, 33, 16, 72, 114};
    convTest("uint8", "float64", x, y);
}

void uint16ToFloat32Test() {
    pd_shared3p uint16[[1]] x = {0, 15385, 38574, 65535, 40810, 43862, 24877, 57685, 13224, 36701};
    float32[[1]] y = {0, 15385, 38574, 65535, 40810, 43862, 24877, 57685, 13224, 36701};
    convTest("uint16", "float32", x, y);
}

void uint16ToFloat64Test() {
    pd_shared3p uint16[[1]] x = {0, 15385, 38574, 65535, 63165, 17281, 37859, 49301, 24388, 49222};
    float64[[1]] y = {0, 15385, 38574, 65535, 63165, 17281, 37859, 49301, 24388, 49222};
    convTest("uint16", "float64", x, y);
}

void uint32ToFloat32Test() {
    pd_shared3p uint32[[1]] x = {0, 21424, 6854552, 3517357, 3236537, 7434813, 3382427, 6618321, 4218189, 1273791};
    float32[[1]] y = {0, 21424, 6854552, 3517357, 3236537, 7434813, 3382427, 6618321, 4218189, 1273791};
    convTest("uint32", "float32", x, y);
}

void uint32ToFloat64Test() {
    pd_shared3p uint32[[1]] x = {0, 21424, 21525341, 4294967295, 2665066282, 958514403, 461390927, 1322057824, 4206433849, 3249482171};
    float64[[1]] y = {0, 21424, 21525341, 4294967295, 2665066282, 958514403, 461390927, 1322057824, 4206433849, 3249482171};
    convTest("uint32", "float64", x, y);
}

void uint64ToFloat32Test() {
    pd_shared3p uint64[[1]] x = {0, 4072056, 9873690, 7773594, 8526256, 8499975, 3520155, 8625357, 2898514, 3691276};
    float32[[1]] y = {0, 4072056, 9873690, 7773594, 8526256, 8499975, 3520155, 8625357, 2898514, 3691276};
    convTest("uint64", "float32", x, y);
}

void uint64ToFloat64Test() {
    pd_shared3p uint64[[1]] x = {0, 55161532, 142234215413552, 3421355526169115, 3899943139706544, 5049689017049132, 7566038149599303, 5132201580501001, 4237306890842267, 328513853958511};
    float64[[1]] y = {0, 55161532, 142234215413552, 3421355526169115, 3899943139706544, 5049689017049132, 7566038149599303, 5132201580501001, 4237306890842267, 328513853958511};
    convTest("uint64", "float64", x, y);
}

void main(){
    string test_prefix = "Casting values";
    {
        pd_shared3p bool a;
        { pd_shared3p uint8 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p uint16 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p uint32 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p uint64 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p int8 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p int16 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p int32 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p int64 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p xor_uint8 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p xor_uint16 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p xor_uint32 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p xor_uint64 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p float32 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p float64 b; test(test_prefix, cast_bool_to_type(b), a, b); }
        { pd_shared3p uint8 b; test(test_prefix, cast_type_to_bool(b), b, a); }
        { pd_shared3p uint16 b; test(test_prefix, cast_type_to_bool(b), b, a); }
        { pd_shared3p uint32 b; test(test_prefix, cast_type_to_bool(b), b, a); }
        { pd_shared3p uint64 b; test(test_prefix, cast_type_to_bool(b), b, a); }
        { pd_shared3p int8 b; test(test_prefix, cast_type_to_bool(b), b, a); }
        { pd_shared3p int16 b; test(test_prefix, cast_type_to_bool(b), b, a); }
        { pd_shared3p int32 b; test(test_prefix, cast_type_to_bool(b), b, a); }
        { pd_shared3p int64 b; test(test_prefix, cast_type_to_bool(b), b, a); }
//        { pd_shared3p xor_uint8 b; test(test_prefix, cast_type_to_bool(b), b, a); }
//        { pd_shared3p xor_uint16 b; test(test_prefix, cast_type_to_bool(b), b, a); }
//        { pd_shared3p xor_uint32 b; test(test_prefix, cast_type_to_bool(b), b, a); }
//        { pd_shared3p xor_uint64 b; test(test_prefix, cast_type_to_bool(b), b, a); }
        { pd_shared3p float32 b; test(test_prefix, cast_float_to_bool(b), b, a); }
        { pd_shared3p float64 b; test(test_prefix, cast_float_to_bool(b), b, a); }
    }

    {
        pd_shared3p uint8[[1]] a = {0, 100, 200, 255};
        {
            pd_shared3p uint16[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p uint16[[1]] a = {0,15385,38574,65535};
        {
            pd_shared3p uint8[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p uint32[[1]] a = {0,21424,21525341,4294967295};
        {
            pd_shared3p uint8[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p uint64[[1]] a = {0,55161532,142234215413552,18446744073709551615};
        {
            pd_shared3p uint8[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p int8[[1]] a = {-128,-40,40,127};
        {
            pd_shared3p uint8[[1]] b = {-128,-40,40,127};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {-128,-40,40,127};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {-128,-40,40,127};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {-128,-40,40,127};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {-128,-40,40,127};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {-128,-40,40,127};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {-128,-40,40,127};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {-128,-40,40,127};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {-128,-40,40,127};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {-128,-40,40,127};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {-128,-40,40,127};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p int16[[1]] a = {-32768,-16325,12435,32767};
        {
            pd_shared3p uint8[[1]] b = {-32768,-16325,12435,32767};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {-32768,-16325,12435,32767};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {-32768,-16325,12435,32767};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {-32768,-16325,12435,32767};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {-32768,-16325,12435,32767};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {-32768,-16325,12435,32767};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {-32768,-16325,12435,32767};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {-32768,-16325,12435,32767};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {-32768,-16325,12435,32767};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {-32768,-16325,12435,32767};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {-32768,-16325,12435,32767};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p int32[[1]] a = {-2147483648,-483648,2147483,2147483647};
        {
            pd_shared3p uint8[[1]] b = {-2147483648,-483648,2147483,2147483647};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {-2147483648,-483648,2147483,2147483647};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {-2147483648,-483648,2147483,2147483647};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {-2147483648,-483648,2147483,2147483647};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {-2147483648,-483648,2147483,2147483647};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {-2147483648,-483648,2147483,2147483647};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {-2147483648,-483648,2147483,2147483647};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {-2147483648,-483648,2147483,2147483647};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {-2147483648,-483648,2147483,2147483647};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {-2147483648,-483648,2147483,2147483647};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {-2147483648,-483648,2147483,2147483647};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p int64[[1]] a = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
        {
            pd_shared3p uint8[[1]] b =  {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b =  {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {-9223372036854775808,-7036854775808,9223372036854,9223372036854775807};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p xor_uint8[[1]] a = {0, 100, 200, 255};
        {
            pd_shared3p uint8[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {0, 100, 200, 255};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p float32[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p float64[[1]] b = {0, 100, 200, 255};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p xor_uint16[[1]] a = {0,15385,38574,65535};
        {
            pd_shared3p uint8[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {0,15385,38574,65535};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p float32[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p float64[[1]] b = {0,15385,38574,65535};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p xor_uint32[[1]] a = {0,21424,21525341,4294967295};
        {
            pd_shared3p uint8[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {0,21424,21525341,4294967295};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p float32[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p float64[[1]] b = {0,21424,21525341,4294967295};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p xor_uint64[[1]] a = {0,55161532,142234215413552,18446744073709551615};
        {
            pd_shared3p uint8[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint16[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint32[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p uint64[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int8[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int16[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int32[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p int64[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint8[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {0,55161532,142234215413552,18446744073709551615};
            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p float32[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p float64[[1]] b = {0,55161532,142234215413552,18446744073709551615};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p float32[[1]] a = {-3.40282e+38,0.0,1.17549e-38,1.0,3.40282e+38};
        {
            pd_shared3p xor_uint8[[1]] b = {UINT8_MIN,0,0,1,UINT8_MAX};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {UINT16_MIN,0,0,1,UINT16_MAX};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {UINT32_MIN,0,0,1,UINT32_MAX};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {UINT64_MIN,0,0,1,UINT64_MAX};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    {
        pd_shared3p float64[[1]] a = {-1.79769e+308,0.0,2.22507e-308,1.0,1.79769e+308};
        {
            pd_shared3p xor_uint8[[1]] b = {UINT8_MIN,0,0,1,UINT8_MAX};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint16[[1]] b = {UINT16_MIN,0,0,1,UINT16_MAX};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint32[[1]] b = {UINT32_MIN,0,0,1,UINT32_MAX};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
        {
            pd_shared3p xor_uint64[[1]] b = {UINT64_MIN,0,0,1,UINT64_MAX};
//            test(test_prefix, cast_type_to_type(a, b), a, b);
        }
    }
    float32ToFloat64Test();
    float32ToInt8Test();
    float32ToInt16Test();
    float32ToInt32Test();
    float32ToInt64Test();
    float32ToUint8Test();
    float32ToUint16Test();
    float32ToUint32Test();
    float32ToUint64Test();
    float64ToFloat32Test();
    float64ToInt8Test();
    float64ToInt16Test();
    float64ToInt32Test();
    float64ToInt64Test();
    float64ToUint8Test();
    float64ToUint16Test();
    float64ToUint32Test();
    float64ToUint64Test();
    int8ToFloat32Test();
    int8ToFloat64Test();
    int16ToFloat32Test();
    int16ToFloat64Test();
    int32ToFloat32Test();
    int32ToFloat64Test();
    int64ToFloat32Test();
    int64ToFloat64Test();
    uint8ToFloat32Test();
    uint8ToFloat64Test();
    uint16ToFloat32Test();
    uint16ToFloat64Test();
    uint32ToFloat32Test();
    uint32ToFloat64Test();
    uint64ToFloat32Test();
    uint64ToFloat64Test();
    test_report();
}

