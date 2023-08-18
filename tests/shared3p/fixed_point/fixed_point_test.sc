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

import shared3p;
import stdlib;
import test_utility;

domain pd_shared3p shared3p;


template<domain D : shared3p, type Fix, type Float>
bool fix_conv_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[2]] a = reshape ({0.5, 0.3, 123.54,
                               0.2, 0.9, 55.45}, 2, 3);
    D Fix[[2]] b(2, 3) = (Fix) a;
    D Float[[2]] c = (Float) b;

    Float relative_error = sum (flatten (declassify (c - a))) / sum (flatten (declassify (a)));

    //the relative error is around 1e-7
    return isNegligible (relative_error);
}

float64[[1]] aconst = {46.1031757062301, 0.662917271256447, -10.5627957265824, 17.7343259332702, -10.5071143712848, -36.2001691944897, -26.2601210037246, 17.2664207406342, -15.5826088041067, -43.2740949559957};
float64[[1]] bconst = {24.8451678548008, -42.5837906310335, 26.0335614671931, -39.3047024030238, 0.727065955288708, -5.81115016248077, 26.5707450453192, -30.5834022816271, -42.3886028584093, 44.1291213734075};

float32 fix_err (pd_shared3p fix32 x) {
    return 1e-2;
}

float64 fix_err (pd_shared3p fix64 x) {
    return 1e-4;
}

template<domain D : shared3p, type Fix, type Float>
bool fix_mul_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Fix[[1]] a = (Fix) af;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] b = (Fix) bf;
    D Fix[[1]] result_fix = a * b;
    D Float[[1]] result = (Float) result_fix;
    D Float[[1]] expected_result = af * bf;

    Float error = sum (declassify (abs (result - expected_result)));

    return error < fix_err (fixProxy);
}

template<domain D : shared3p, type Fix, type Float>
bool fix_add_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Fix[[1]] a = (Fix) af;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] b = (Fix) bf;
    D Fix[[1]] result_fix = a + b;
    D Float[[1]] result = (Float) result_fix;
    D Float[[1]] expected_result = af + bf;

    Float error = sum (declassify (abs (result - expected_result)));

    return error < fix_err (fixProxy);
}

template<domain D : shared3p, type Fix, type Float>
bool fix_sub_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Fix[[1]] a = (Fix) af;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] b = (Fix) bf;
    D Fix[[1]] result_fix = a - b;
    D Float[[1]] result = (Float) result_fix;
    D Float[[1]] expected_result = af - bf;

    Float error = sum (declassify (abs (result - expected_result)));

    return error < fix_err (fixProxy);
}

template<domain D : shared3p, type Fix, type Float>
bool fix_div_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] a = (Fix) af;
    D Fix[[1]] b = (Fix) bf;
    D Float[[1]] result = (Float) (a / b);
    D Float[[1]] expected_result = af / bf;

    Float error = sum (declassify (abs (result - expected_result)));

    return error < fix_err (fixProxy);
}

template<domain D : shared3p, type Fix, type Float>
bool fix_eq_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] a = (Fix) af;
    D Fix[[1]] b = (Fix) bf;
    D bool[[1]] result = a == b;
    D bool[[1]] expected_result = af == bf;

    return declassify (all (expected_result == result));
}

template<domain D : shared3p, type Fix, type Float>
bool fix_lt_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] a = (Fix) af;
    D Fix[[1]] b = (Fix) bf;
    D bool[[1]] result = a < b;
    D bool[[1]] expected_result = af < bf;

    return declassify (all (expected_result == result));
}

template<domain D : shared3p, type Fix, type Float>
bool fix_gt_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] a = (Fix) af;
    D Fix[[1]] b = (Fix) bf;
    D bool[[1]] result = a > b;
    D bool[[1]] expected_result = af > bf;

    return declassify (all (expected_result == result));
}

template<domain D : shared3p, type Fix, type Float>
bool fix_lte_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] a = (Fix) af;
    D Fix[[1]] b = (Fix) bf;
    D bool[[1]] result = a <= b;
    D bool[[1]] expected_result = af <= bf;

    return declassify (all (expected_result == result));
}

template<domain D : shared3p, type Fix, type Float>
bool fix_gte_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] af = (Float) aconst;
    D Float[[1]] bf = (Float) bconst;
    D Fix[[1]] a = (Fix) af;
    D Fix[[1]] b = (Fix) bf;
    D bool[[1]] result = a >= b;
    D bool[[1]] expected_result = af >= bf;

    return declassify (all (expected_result == result));
}

template<domain D : shared3p, type Fix, type Float>
bool fix_sqrt_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] a = {16, 25, 36, 49, 64};
    D Fix[[1]] a_fix = (Fix) a;

    D Float b = 2;
    D Fix b_fix = (Fix) b;

    D Float[[1]] expected_result1 = {4, 5, 6, 7, 8};
    D Float expected_result2 = 1.414213562373095;

    D Fix[[1]] result1_fix = sqrt(a_fix);
    D Fix result2_fix = sqrt(b_fix);

    D Float[[1]] result1 = (Float) result1_fix;
    D Float result2 = (Float) result2_fix;

    Float relative_error1 = sum (declassify ((expected_result1 - result1) / expected_result1));
    Float relative_error2 = declassify ((expected_result2 - result2) / expected_result2);

    //the relative error is around 1e-7
    return isNegligible (relative_error1) && isNegligible (relative_error2);
}

template<domain D : shared3p, type Fix, type Float>
bool fix_inv_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] a = {1, 10, 0.5, 7};
    D Fix[[1]] a_fix = (Fix) a;

    D Float[[1]] expected_result = {1, 0.1, 2, 0.1428571428571429};
    D Fix[[1]] result_fix = inv(a_fix);

    D Float[[1]] result = (Float) result_fix;
    Float relative_error = abs (sum (declassify ((result - expected_result) / expected_result)));

    //the relative error is around 1e-4
    return relative_error < 0.0002;
}

template<domain D : shared3p, type Fix, type Float>
bool fix_exp_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] a = {0, 1, 2.302585092994046, 4.158883083359672, 4.605170185988091};
    D Fix[[1]] a_fix = (Fix) a;

    D Float b = 0.693147180559945;
    D Fix b_fix = (Fix) b;

    D Float[[1]] expected_result1 = {1, 2.718281828459045, 10, 64, 99.999999999999957};
    D Float expected_result2 = 1.999999999999999;

    D Fix[[1]] result1_fix = exp(a_fix);
    D Fix result2_fix = exp(b_fix);

    D Float[[1]] result1 = (Float) result1_fix;
    D Float result2 = (Float) result2_fix;

    Float relative_error1 = sum (declassify ((expected_result1 - result1) / expected_result1));
    Float relative_error2 = declassify ((expected_result2 - result2) / expected_result2);

    //the relative error is around
    return isNegligible (relative_error1) && isNegligible (relative_error2);
}

template<domain D : shared3p, type Fix, type Float>
bool fix_ln_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] a = {1, 2.71828, 10, 64, 100};
    D Fix[[1]] a_fix = (Fix) a;

    D Float b = 2;
    D Fix b_fix = (Fix) b;

    D Float[[1]] expected_result1 = {0, 0.999999327347282, 2.302585092994046, 4.158883083359671, 4.605170185988092};
    D Float expected_result2 = 0.693147180559945;

    D Fix[[1]] result1_fix = ln(a_fix);
    D Fix result2_fix = ln(b_fix);

    D Float[[1]] result1 = (Float) result1_fix;
    D Float result2 = (Float) result2_fix;

    Float relative_error1 = sum (declassify ((expected_result1 - result1) / expected_result1));
    Float relative_error2 = declassify ((expected_result2 - result2) / expected_result2);

    //the relative error is around
    return isNegligible (relative_error1) && isNegligible (relative_error2);
}

template<domain D : shared3p, type Fix, type Float>
bool fix_log_test (D Fix fixProxy, D Float floatProxy) {
    D Float[[1]] a = {1, 50, 100, 1000, 25};
    D Float[[1]] a_base = {10,10,10,10,10};
    D Fix[[1]] a_fix = (Fix) a;
    D Fix[[1]] a_base_fix = (Fix) a_base;

    D Float b = 54;
    D Float b_base = 10;
    D Fix b_fix = (Fix) b;
    D Fix b_fix_base = (Fix) b_base;

    D Float[[1]] expected_result1 = {0, 1.698970004336019, 2, 3, 1.397940008672038};
    D Float expected_result2 = 1.732393759822969;

    D Fix[[1]] result1_fix = log(a_fix, a_base_fix);
    D Fix result2_fix = log(b_fix, b_base_fix);

    D Float[[1]] result1 = (Float) result1_fix;
    D Float result2 = (Float) result2_fix;

    Float relative_error1 = sum (declassify ((expected_result1 - result1) / expected_result1));
    Float relative_error2 = declassify ((expected_result2 - result2) / expected_result2);

    //the relative error is around
    return isNegligible (relative_error1) && isNegligible (relative_error2);
}

void main () {
    pd_shared3p fix32 fi32;
    pd_shared3p float32 fl32;
    pd_shared3p fix64 fi64;
    pd_shared3p float64 fl64;

    test ("Fixed point conversion", fix_conv_test (fi32, fl32), fi32);
    test ("Fixed point conversion", fix_conv_test (fi64, fl64), fi64);

    test ("Fixed point multiplication", fix_mul_test (fi32, fl32), fi32);
    test ("Fixed point multiplication", fix_mul_test (fi64, fl64), fi64);

    test ("Fixed point addition", fix_add_test (fi32, fl32), fi32);
    test ("Fixed point addition", fix_add_test (fi64, fl64), fi64);

    test ("Fixed point division", fix_div_test (fi32, fl32), fi32);
    test ("Fixed point division", fix_div_test (fi64, fl64), fi64);

    test ("Fixed point subtraction", fix_sub_test (fi32, fl32), fi32);
    test ("Fixed point subtraction", fix_sub_test (fi64, fl64), fi64);

    test ("Fixed point equality", fix_eq_test (fi32, fl32), fi32);
    test ("Fixed point equality", fix_eq_test (fi64, fl64), fi64);

    test ("Fixed point less-than", fix_lt_test (fi32, fl32), fi32);
    test ("Fixed point less-than", fix_lt_test (fi64, fl64), fi64);

    test ("Fixed point greater-than", fix_gt_test (fi32, fl32), fi32);
    test ("Fixed point greater-than", fix_gt_test (fi64, fl64), fi64);

    test ("Fixed point less-than-equal", fix_lte_test (fi32, fl32), fi32);
    test ("Fixed point less-than-equal", fix_lte_test (fi64, fl64), fi64);

    test ("Fixed point greater-than-equal", fix_gte_test (fi32, fl32), fi32);
    test ("Fixed point greater-than-equal", fix_gte_test (fi64, fl64), fi64);

    test ("Fixed point square root", fix_sqrt_test (fi32, fl32), fi32);
    test ("Fixed point square root", fix_sqrt_test (fi64, fl64), fi64);

    test ("Fixed point invert", fix_inv_test (fi32, fl32), fi32);
    test ("Fixed point invert", fix_inv_test (fi64, fl64), fi64);

    test ("Fixed point exponent", fix_exp_test (fi32, fl32), fi32);
    test ("Fixed point exponent", fix_exp_test (fi64, fl64), fi64);

    test ("Fixed point natural log", fix_ln_test (fi32, fl32), fi32);
    test ("Fixed point natural log", fix_ln_test (fi64, fl64), fi64);

    test ("Fixed point log base 10", fix_log_test (fi32, fl32), fi32);
    test ("Fixed point log base 10", fix_log_test (fi64, fl64), fi64);

    test_report();
}
