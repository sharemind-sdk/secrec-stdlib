/*
 * Copyright (C) 2016 Cybernetica
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

/**
* \cond
*/
module shared3p;

import matrix; // for log gamma
import stdlib;

kind shared3p {
    type bool;
    type uint8;
    type uint16;
    type uint32;
    type uint64;
    type int8;
    type int16;
    type int32;
    type int64;
    type float32;
    type float64;
    type xor_uint8 { public = uint8 };
    type xor_uint16 { public = uint16 };
    type xor_uint32 { public = uint32 };
    type xor_uint64 { public = uint64 };
    type fix32 { public = float32 };
    type fix64 { public = float64 };
}
/**
* \endcond
*/

/**
* @file
* \defgroup shared3p shared3p.sc
* \defgroup sign sign
* \defgroup shared3p_abs abs
* \defgroup shared3p_sum sum
* \defgroup shared3p_sum_vec sum
* \defgroup shared3p_sum_k sum(parts)
* \defgroup shared3p_prefix_sum prefixSum
* \defgroup shared3p_inv_prefix_sum invPrefixSum
* \defgroup shared3p_product product
* \defgroup shared3p_product_vec product
* \defgroup shared3p_product_k product(parts)
* \defgroup shared3p_any any
* \defgroup shared3p_all all
* \defgroup shared3p_trueprefixlength truePrefixLength
* \defgroup shared3p_inv inv
* \defgroup shared3p_sqrt sqrt
* \defgroup shared3p_sin sin
* \defgroup shared3p_ln ln
* \defgroup shared3p_log log
* \defgroup shared3p_log10 log10
* \defgroup shared3p_exp exp
* \defgroup shared3p_erf erf
* \defgroup shared3p_pow pow
* \defgroup shared3p_isnegligible isNegligible
* \defgroup shared3p_min min
* \defgroup shared3p_min_vec min
* \defgroup shared3p_min_k min(parts)
* \defgroup shared3p_min_2 min(2 vectors)
* \defgroup shared3p_max max
* \defgroup shared3p_max_vec max
* \defgroup shared3p_max_k max(parts)
* \defgroup shared3p_max_2 max(2 vectors)
* \defgroup shared3p_floor floor
* \defgroup shared3p_ceiling ceiling
* \defgroup shared3p_argument argument
* \defgroup shared3p_publish publish
* \defgroup shared3p_bit_extract bit extraction
* \defgroup shared3p_reshare reshare
* \defgroup shared3p_choose1 choose(single condition)
* \defgroup shared3p_choose2 choose(multiple conditions)
* \defgroup shared3p_log_gamma logGamma
*/

/** \addtogroup shared3p
*@{
* @brief Module with shared3p protection domain functions
*/


/**
 * \cond
 */

// Useful for simplifying gather/scatter usage
template<domain D : shared3p, type T, dim N>
D T[[N]] _partialRearrange(D T[[N]] a, D T[[N]] b, uint[[1]] source, uint[[1]] target) {
    assert(size(source) == size(target));
    D T[[1]] temp(size(source));
    __syscall("shared3p::gather_$T\_vec",  __domainid(D), a, temp, __cref source);
    __syscall("shared3p::scatter_$T\_vec", __domainid(D), temp, b, __cref target);
    return b;
}

template <domain D : shared3p, type T>
D fix32[[1]] _toFixCastHelper (D T[[1]] x) {
    D int32[[1]] xint (size (x));
    D uint32[[1]] xuint (size (x));
    __syscall ("shared3p::conv_$T\_to_int32_vec", __domainid (D), x, xint);
    __syscall ("shared3p::conv_int32_to_uint32_vec", __domainid (D), xint, xuint);
    D uint32[[1]] shifts (size (x)) = 16;
    D fix32[[1]] res (size (x));
    __syscall ("shared3p::shift_left_uint32_vec", __domainid (D), xuint, shifts, res);
    return res;
}

template <domain D : shared3p, type T>
D fix64[[1]] _toFixCastHelper (D T[[1]] x) {
    D int64[[1]] xint (size (x));
    D uint64[[1]] xuint (size (x));
    __syscall ("shared3p::conv_$T\_to_int64_vec", __domainid (D), x, xint);
    __syscall ("shared3p::conv_int64_to_uint64_vec", __domainid (D), xint, xuint);
    D uint64[[1]] shifts (size (x)) = 32;
    D fix64[[1]] res (size (x));
    __syscall ("shared3p::shift_left_uint64_vec", __domainid (D), xuint, shifts, res);
    return res;
}
/**
 * \endcond
 */


/*******************************
    classify
********************************/
/**
* \cond
*/
template <domain D : shared3p, type T, dim N>
D T[[N]] classify(T[[N]] value) {
    D T[[N]] out = value;
    return out;
}
/**
* \endcond
*/

/*******************************
    sign, abs
********************************/

/** \addtogroup sign
 *  @{
 *  @brief Function for determining the sign of values
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param x - an array of any dimension
 *  @return returns an array of equal shape, size and dimension, where -1 signifies that, in the input array at that position was a negative number and 1  that it was a positive number
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D int8[[N]] sign (D int8[[N]] x) {
    __syscall ("shared3p::sign_int8_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D int16[[N]] sign (D int16[[N]] x) {
    __syscall ("shared3p::sign_int16_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D int32[[N]] sign (D int32[[N]] x) {
    __syscall ("shared3p::sign_int32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D int[[N]] sign (D int[[N]] x) {
    __syscall ("shared3p::sign_int64_vec", __domainid (D), x, x);
    return x;
}

/** @}*/
/** \addtogroup shared3p_abs
 *  @{
 *  @brief Function for finding absolute values
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref float32 "float32" / \ref float64 "float64" / \ref fix32 "fix32" / \ref fix64 "fix64"
 *  @param x - an array of any dimension
 *  @return returns an array of equal shape, size and dimension, where all values are the absolute values of the input array at that position
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D uint8[[N]] abs (D int8[[N]] x) {
    D uint8[[N]] y;
    y = (uint8) x;
    __syscall ("shared3p::abs_int8_vec", __domainid (D), x, y);
    return y;
}

template <domain D : shared3p, dim N>
D uint16[[N]] abs (D int16[[N]] x) {
    D uint16[[N]] y;
    y = (uint16) x;
    __syscall ("shared3p::abs_int16_vec", __domainid (D), x, y);
    return y;
}

template <domain D : shared3p, dim N>
D uint32[[N]] abs (D int32[[N]] x) {
    D uint32[[N]] y;
    y = (uint32) x;
    __syscall ("shared3p::abs_int32_vec", __domainid (D), x, y);
    return y;
}

template <domain D : shared3p, dim N>
D uint[[N]] abs (D int[[N]] x) {
    D uint[[N]] y;
    y = (uint) x;
    __syscall ("shared3p::abs_int64_vec", __domainid (D), x, y);
    return y;
}

template<domain D : shared3p, dim N>
D float32[[N]] abs (D float32[[N]] value) {
    D float32[[N]] out = value;
    __syscall("shared3p::abs_float32_vec", __domainid (D), value, out);
    return out;
}

template<domain D : shared3p, dim N>
D float64[[N]] abs (D float64[[N]] value) {
    D float64[[N]] out = value;
    __syscall("shared3p::abs_float64_vec", __domainid (D), value, out);
    return out;
}

template<domain D : shared3p, dim N>
D fix32[[N]] abs (D fix32[[N]] value) {
    D fix32[[N]] cond = (fix32) (value > 0);
    return cond * (value + value) - value;
}

template<domain D : shared3p, dim N>
D fix64[[N]] abs (D fix64[[N]] value) {
    D fix64[[N]] cond = (fix64) (value > 0);
    return cond * (value + value) - value;
}
/** @}*/

/*******************************
    sum
********************************/
/** \addtogroup shared3p_sum
 *  @{
 *  @brief Functions for finding sums
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref bool "bool" / \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref float32 "float32" / \ref float64 "float64"
 */

/** \addtogroup shared3p_sum_vec
 *  @{
 *  @brief Function for finding the sum of all the elements in a vector
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref bool "bool" / \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref float32 "float32" / \ref float64 "float64"
 *  @note We are using a system call for summing vectors as it's very common
 *  operation, and the performance overhead of manually summing is in the
 *  range of 100 to 200 times slower.
 *  @param vec - a 1-dimensional array
 *  @returns the sum of all input vector elements
 *  @leakage{None}
 */


/**
* @note boolean values are converted to numerical values and then added, for more info click \ref bool "here"
* @return returns the sum of all the elements in the input vector as an \ref uint "uint64" type value
*/
template <domain D : shared3p>
D uint sum (D bool[[1]] vec) {
    D uint out;
    __syscall ("shared3p::sum_bool_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint8 sum (D uint8[[1]] vec) {
    D uint8 out;
    __syscall ("shared3p::sum_uint8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint16 sum (D uint16[[1]] vec) {
    D uint16 out;
    __syscall ("shared3p::sum_uint16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint32 sum (D uint32[[1]] vec) {
    D uint32 out;
    __syscall ("shared3p::sum_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint sum (D uint[[1]] vec) {
    D uint out;
    __syscall ("shared3p::sum_uint64_vec", __domainid (D), vec, out);
    return out;
}


template <domain D : shared3p>
D int8 sum (D int8[[1]] vec) {
    D int8 out;
    __syscall ("shared3p::sum_int8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int16 sum (D int16[[1]] vec) {
    D int16 out;
    __syscall ("shared3p::sum_int16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int32 sum (D int32[[1]] vec) {
    D int32 out;
    __syscall ("shared3p::sum_int32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int sum (D int[[1]] vec) {
    D int out;
    __syscall ("shared3p::sum_int64_vec", __domainid (D), vec, out);
    return out;
}


template <domain D : shared3p>
D float32 sum (D float32[[1]] vec) {
    D float32 out;
    __syscall ("shared3p::sum_float32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D float64 sum (D float64[[1]] vec) {
    D float64 out;
    __syscall ("shared3p::sum_float64_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D fix32 sum (D fix32[[1]] vec) {
    D fix32 out;
    __syscall ("shared3p::sum_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D fix64 sum (D fix64[[1]] vec) {
    D fix64 out;
    __syscall ("shared3p::sum_uint64_vec", __domainid (D), vec, out);
    return out;
}

/** @}*/

/** \addtogroup shared3p_sum_k
 *  @{
 *  @brief Function for finding the sum of all elements in the input vector in specified parts.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref bool "bool" / \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref float32 "float32" / \ref float64 "float64"
 *  @pre the length of the input array must be a multiple of **k**
 *  @param vec - The input array of subarrays to sum. The subarrays are stacked one after another and are all of the same size.
 *  @param k - The number of subarrays in the input array.
 *  @return The array of subarrayCount number of sums, each corresponding to respective subarray in the input array **vec**.
 *  @leakage{None}
 */


template <domain D : shared3p>
D uint[[1]] sum (D bool[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared3p::sum_bool_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint8[[1]] sum (D uint8[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint8[[1]] out (k);
    __syscall ("shared3p::sum_uint8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint16[[1]] sum (D uint16[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint16[[1]] out (k);
    __syscall ("shared3p::sum_uint16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint32[[1]] sum (D uint32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint32[[1]] out (k);
    __syscall ("shared3p::sum_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint[[1]] sum (D uint[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared3p::sum_uint64_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int8[[1]] sum (D int8[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int8[[1]] out (k);
    __syscall ("shared3p::sum_int8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int16[[1]] sum (D int16[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int16[[1]] out (k);
    __syscall ("shared3p::sum_int16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int32[[1]] sum (D int32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int32[[1]] out (k);
    __syscall ("shared3p::sum_int32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int[[1]] sum (D int[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int[[1]] out (k);
    __syscall ("shared3p::sum_int64_vec", __domainid (D), vec, out);
    return out;
}


template <domain D : shared3p>
D float32[[1]] sum (D float32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D float32[[1]] out (k);
    __syscall ("shared3p::sum_float32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D float64[[1]] sum (D float64[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D float64[[1]] out (k);
    __syscall ("shared3p::sum_float64_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D fix32[[1]] sum (D fix32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D fix32[[1]] out (k);
    __syscall ("shared3p::sum_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D fix64[[1]] sum (D fix64[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D fix64[[1]] out (k);
    __syscall ("shared3p::sum_uint64_vec", __domainid (D), vec, out);
    return out;
}

/** @}*/
/** @}*/

/** \cond */
template<domain D : shared3p, type T>
D T[[1]] _prefixSum(D T [[1]] a) {
    D T[[1]] res(size(a));
    __syscall("shared3p::prefix_sum_$T\_vec", __domainid(D), a, res);
    return res;
}
/** \endcond */

/** \addtogroup shared3p_prefix_sum
 *  @{
 *  @brief Fast prefix sum
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param vec - a vector of supported type
 *  @return Turns the input vector [x1, x2, ..., xn] to [x1, x1 + x2, ... , x1 + ... + xn]
 *  @leakage{None}
 */
template<domain D : shared3p>
D uint8[[1]] prefixSum(D uint8[[1]] vec) {
    return _prefixSum(vec);
}
template<domain D : shared3p>
D uint16[[1]] prefixSum(D uint16[[1]] vec) {
    return _prefixSum(vec);
}
template<domain D : shared3p>
D uint32[[1]] prefixSum(D uint32[[1]] vec) {
    return _prefixSum(vec);
}
template<domain D : shared3p>
D uint64[[1]] prefixSum(D uint64[[1]] vec) {
    return _prefixSum(vec);
}
template<domain D : shared3p>
D int8[[1]] prefixSum(D int8[[1]] vec) {
    return _prefixSum(vec);
}
template<domain D : shared3p>
D int16[[1]] prefixSum(D int16[[1]] vec) {
    return _prefixSum(vec);
}
template<domain D : shared3p>
D int32[[1]] prefixSum(D int32[[1]] vec) {
    return _prefixSum(vec);
}
template<domain D : shared3p>
D int64[[1]] prefixSum(D int64[[1]] vec) {
    return _prefixSum(vec);
}
/** @} */

/** \cond */
template<domain D : shared3p, type T>
D T[[1]] _invPrefixSum(D T[[1]] a) {
    D T[[1]] res(size(a));
    __syscall("shared3p::inv_prefix_sum_$T\_vec", __domainid(D), a, res);
    return res;
}
/** \endcond */

/** \addtogroup shared3p_inv_prefix_sum
 *  @{
 *  @brief Fast inverse prefix sum
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param vec - a vector of supported type
 *  @return Reverses prefixSum: subtracts the preceding element from each element of vec.
 *  @leakage{None}
 */
template<domain D : shared3p>
D uint8[[1]] invPrefixSum(D uint8[[1]] vec) {
    return _invPrefixSum(vec);
}
template<domain D : shared3p>
D uint16[[1]] invPrefixSum(D uint16[[1]] vec) {
    return _invPrefixSum(vec);
}
template<domain D : shared3p>
D uint32[[1]] invPrefixSum(D uint32[[1]] vec) {
    return _invPrefixSum(vec);
}
template<domain D : shared3p>
D uint64[[1]] invPrefixSum(D uint64[[1]] vec) {
    return _invPrefixSum(vec);
}
template<domain D : shared3p>
D int8[[1]] invPrefixSum(D int8[[1]] vec) {
    return _invPrefixSum(vec);
}
template<domain D : shared3p>
D int16[[1]] invPrefixSum(D int16[[1]] vec) {
    return _invPrefixSum(vec);
}
template<domain D : shared3p>
D int32[[1]] invPrefixSum(D int32[[1]] vec) {
    return _invPrefixSum(vec);
}
template<domain D : shared3p>
D int64[[1]] invPrefixSum(D int64[[1]] vec) {
    return _invPrefixSum(vec);
}
/** @} */

/*******************************
    product
********************************/
/** \addtogroup shared3p_product
 *  @{
 *  @brief Functions for finding products
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 */


/** \addtogroup shared3p_product_vec
 *  @{
 *  @brief Function for finding the product of the input vector
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param vec - a vector of supported type
 *  @return The product of the input vector
 *  @leakage{None}
 */

template <domain D : shared3p, type T>
D T product (D T scalar) {
    return scalar;
}

template <domain D : shared3p>
D uint8 product (D uint8[[1]] vec) {
    D uint8 out;
    __syscall ("shared3p::product_uint8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint16 product (D uint16[[1]] vec) {
    D uint16 out;
    __syscall ("shared3p::product_uint16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint32 product (D uint32[[1]] vec) {
    D uint32 out;
    __syscall ("shared3p::product_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint product (D uint[[1]] vec) {
    D uint out;
    __syscall ("shared3p::product_uint64_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int8 product (D int8[[1]] vec) {
    D int8 out;
    __syscall ("shared3p::product_int8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int16 product (D int16[[1]] vec) {
    D int16 out;
    __syscall ("shared3p::product_int16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int32 product (D int32[[1]] vec) {
    D int32 out;
    __syscall ("shared3p::product_int32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int product (D int[[1]] vec) {
    D int out;
    __syscall ("shared3p::product_int64_vec", __domainid (D), vec, out);
    return out;
}

/** @}*/
/** \addtogroup shared3p_product_k
 *  @{
 *  @brief Function for finding the product of all elements in the input vector in specified parts.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @pre the length of the input array must be a multiple of **k**
 *  @param vec - The input array of subarrays to find the product of. The subarrays are stacked one after another and are all of the same size.
 *  @param k - The number of subarrays in the input array.
 *  @return The array of subarrayCount number of products, each corresponding to respective subarray in the input array **vec**.
 *  @leakage{None}
 */

template <domain D : shared3p>
D uint8[[1]] product (D uint8[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint8[[1]] out (k);
    __syscall ("shared3p::product_uint8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint16[[1]] product (D uint16[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint16[[1]] out (k);
    __syscall ("shared3p::product_uint16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint32[[1]] product (D uint32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint32[[1]] out (k);
    __syscall ("shared3p::product_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D uint[[1]] product (D uint[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared3p::product_uint64_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int8[[1]] product (D int8[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int8[[1]] out (k);
    __syscall ("shared3p::product_int8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int16[[1]] product (D int16[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int16[[1]] out (k);
    __syscall ("shared3p::product_int16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int32[[1]] product (D int32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int32[[1]] out (k);
    __syscall ("shared3p::product_int32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared3p>
D int[[1]] product (D int[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int[[1]] out (k);
    __syscall ("shared3p::product_int64_vec", __domainid (D), vec, out);
    return out;
}

/** @}*/
/** @}*/


/*******************************
    any, all
********************************/


/** \addtogroup shared3p_any
 *  @{
 *  @brief Function for checking if any value of the input vector is true
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref bool "bool"
 *  @return true if any of the input bits is set
 *  @return false if all input bits are not set
 *  @note performs one vectorized cast, and one comparison against zero
 *  @leakage{None}
 */

/**
* @param b - scalar boolean
*/
template <domain D : shared3p>
D bool any (D bool b) {
    return b;
}

/**
* @param vec - boolean 1-dimensional vector
*/
template <domain D : shared3p>
D bool any (D bool[[1]] vec) {
    uint n = size(vec);

    if (n <= (uint) UINT8_MAX)
        return sum((uint8) vec) != 0;

    if (n <= (uint) UINT16_MAX)
        return sum((uint16) vec) != 0;

    if (n <= (uint) UINT32_MAX)
        return sum((uint32) vec) != 0;

    return sum(vec) != 0;
}

/**
* @param vec - boolean 1-dimensional vector
* @param k - an \ref uint64 "uint" type value that shows in how many subarrays must **any** be found
*/
template <domain D : shared3p>
D bool[[1]] any (D bool[[1]] vec, uint k) {
    uint n = size(vec);
    assert(k > 0 && n % k == 0);
    uint groupLen = n / k;

    if (groupLen <= (uint) UINT8_MAX)
        return sum((uint8) vec, k) != 0;

    if (groupLen <= (uint) UINT16_MAX)
        return sum((uint16) vec, k) != 0;

    if (groupLen <= (uint) UINT32_MAX)
        return sum((uint32) vec, k) != 0;

    return sum(vec, k) != 0;
}

/**
* @param arr - boolean any dimension vector
*/
template <domain D : shared3p, dim N>
D bool any (D bool[[N]] arr) {
    return any(flatten(arr));
}
/** @}*/

/** \addtogroup shared3p_all
 *  @{
 *  @brief Function for checking if all values of the input vector are true
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref bool "bool"
 *  @return true if all of the input bits are set
 *  @return false if any input bit is not set
 *  @note performs one vectorized cast, and one comparison against length of the vector
 *  @leakage{None}
 */

/**
* @param b - scalar boolean
*/
template <domain D : shared3p>
D bool all (D bool b) {
    return b;
}

/**
* @param vec - boolean 1-dimensional vector
*/
template <domain D : shared3p>
D bool all (D bool [[1]] vec) {
    uint n = size (vec);

    /*
     * NOTE: These special cases are useful because we are some times
     * working with very long boolean arrays. Converting
     * every bit into 64-bit integer can easily exeed memory limitations.
     * Especially so because mostly the group sizes are small (less than 256).
     */

    if (n <= (uint) UINT8_MAX)
        return sum((uint8) vec) == (uint8) n;

    if (n <= (uint) UINT16_MAX)
        return sum((uint16) vec) == (uint16) n;

    if (n <= (uint) UINT32_MAX)
        return sum((uint32) vec) == (uint32) n;

    return sum(vec) == n;
}

/**
* @param vec - boolean 1-dimensional vector
* @param k - an \ref uint64 "uint" type value that shows in how many subarrays must **all** be found
*/
template <domain D : shared3p>
D bool[[1]] all (D bool[[1]] vec, uint k) {
    uint n = size(vec);
    assert(k > 0 && n % k == 0);
    uint groupLen = n / k;

    if (groupLen <= (uint) UINT8_MAX)
        return sum((uint8) vec, k) == (uint8) groupLen;

    if (groupLen <= (uint) UINT16_MAX)
        return sum((uint16) vec, k) == (uint16) groupLen;

    if (groupLen <= (uint) UINT32_MAX)
        return sum((uint32) vec, k) == (uint32) groupLen;

    return sum(vec, k) == groupLen;
}

/**
* @param arr - boolean any dimension vector
*/
template <domain D : shared3p, dim N>
D bool all (D bool[[N]] arr) {
    return all(flatten(arr));
}
/** @}*/

/*******************************
    truePrefixLength
********************************/

/** \addtogroup shared3p_trueprefixlength
 *  @{
 *  @brief Function for finding how many elements from the start of a vector are true
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref bool "bool"
 *  @returns the number of set bits in the longest constant true prefix of the input
 *  @note this function performs log n multiplications on vectors of at most size n this is more efficient than performing n multiplications on scalars
 *  @leakage{None}
 * \todo i think this can be further optimized
 */

/**
* @param arr - boolean 1-dimensional vector
*/
template <domain D : shared3p>
D uint truePrefixLength (D bool [[1]] arr) {
    for (uint shift = 1, n = size (arr); shift < n; shift *= 2) {
        arr[shift:] = arr[shift:] & arr[:n-shift];
    }

    return sum (arr);
}
/** @}*/

/*****************************************************
    inv, sqrt, sin, ln, exp, erf, isNegligible
*****************************************************/

/** \addtogroup shared3p_inv
 *  @{
 *  @brief Function for inversing a value
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64" / \ref fix32 "fix32" / \ref fix64 "fix64"
 *  @return returns the inversed values of the input array
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D float32[[N]] inv (D float32[[N]] x) {
    __syscall ("shared3p::inv_float32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D float64[[N]] inv (D float64[[N]] x) {
    __syscall ("shared3p::inv_float64_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D fix32[[N]] inv (D fix32[[N]] x) {
    __syscall ("shared3p::inv_fix32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D fix64[[N]] inv (D fix64[[N]] x) {
    __syscall ("shared3p::inv_fix64_vec", __domainid (D), x, x);
    return x;
}

/** @}*/
/** \addtogroup shared3p_sqrt
 *  @{
 *  @brief Function for finding the square root of a value
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64" / \ref fix32 "fix32" / \ref fix64 "fix64"
 *  @param x - input
 *  @return the square roots of the input array
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D float32[[N]] sqrt (D float32[[N]] x) {
    __syscall ("shared3p::sqrt_float32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D float64[[N]] sqrt (D float64[[N]] x) {
    __syscall ("shared3p::sqrt_float64_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D fix32[[N]] sqrt (D fix32[[N]] x) {
    __syscall ("shared3p::sqrt_fix32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D fix64[[N]] sqrt (D fix64[[N]] x) {
    __syscall ("shared3p::sqrt_fix64_vec", __domainid (D), x, x);
    return x;
}

/** @}*/
/** \addtogroup shared3p_sin
 *  @{
 *  @brief Function for finding the sine of a value
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 *  @param x - input
 *  @return the sines of the input array
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D float32[[N]] sin (D float32[[N]] x) {
    __syscall ("shared3p::sin_float32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D float64[[N]] sin (D float64[[N]] x) {
    __syscall ("shared3p::sin_float64_vec", __domainid (D), x, x);
    return x;
}
/** @}*/

/** \addtogroup shared3p_ln
 *  @{
 *  @brief Function for finding the natural logarithm of a value
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 *  @return returns the natural logarithms of the input array
 *  @leakage{None}
 */
template <domain D : shared3p, dim N>
D float32[[N]] ln (D float32[[N]] x) {
    __syscall ("shared3p::ln_float32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D float64[[N]] ln (D float64[[N]] x) {
    __syscall ("shared3p::ln_float64_vec", __domainid (D), x, x);
    return x;
}
/** @}*/

/** \addtogroup shared3p_log
 *  @{
 *  @brief Function for finding the logarithm of a value
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 *  @return x input
 *  @return b logarithm base
 *  @return returns the logarithms of the input array
 *  @leakage{None}
 */
template <domain D : shared3p, dim N>
D float32[[N]] log (D float32[[N]] x, D float32[[N]] b) {
    return ln (x) / ln (b);
}

template <domain D : shared3p, dim N>
D float64[[N]] log (D float64[[N]] x, D float64[[N]] b) {
    return ln (x) / ln (b);
}
/** @} */

/** \addtogroup shared3p_log10
 *  @{
 *  @brief Function for finding the base 10 logarithm of a value
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 *  @return x input
 *  @return returns the base 10 logarithms of the input array
 *  @leakage{None}
 */
template <domain D : shared3p, dim N>
D float32[[N]] log10 (D float32[[N]] x) {
    return ln (x) / 2.302585092994046;
}

template <domain D : shared3p, dim N>
D float64[[N]] log10 (D float64[[N]] x) {
    return ln (x) / 2.302585092994046;
}
/** @} */

/** \addtogroup shared3p_exp
 *  @{
 *  @brief Function for finding exp(x)
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 *  @param x - input array
 *  @return exponential function applied to elements of the input array
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D float32[[N]] exp (D float32[[N]] x) {
    __syscall ("shared3p::exp_float32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D float64[[N]] exp (D float64[[N]] x) {
    __syscall ("shared3p::exp_float64_vec", __domainid (D), x, x);
    return x;
}
/** @}*/

/** \addtogroup shared3p_erf
 *  @{
 *  @brief Function for finding the value of error function
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 *  @param x - input array
 *  @return error function applied to elements of the input array
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D float32[[N]] erf (D float32[[N]] x) {
    __syscall ("shared3p::erf_float32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared3p, dim N>
D float64[[N]] erf (D float64[[N]] x) {
    __syscall ("shared3p::erf_float64_vec", __domainid (D), x, x);
    return x;
}
/** @}*/

/** \addtogroup shared3p_pow
 *  @{
 *  @brief Function for computing values of the power function
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 *  @param a base
 *  @param b exponent
 *  @return returns the b-th powers of vector a
 *  @leakage{None}
 */
template <domain D : shared3p, dim N>
D float32[[N]] pow (D float32[[N]] a, D float32[[N]] b) {
    return exp (ln (a) * b);
}

template <domain D : shared3p, dim N>
D float64[[N]] pow (D float64[[N]] a, D float64[[N]] b) {
    return exp (ln (a) * b);
}
/** @} */

/** \addtogroup shared3p_isnegligible
 *  @{
 *  @brief Function for finding if the error is small enough to neglect
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 *  @return returns **true** if the error is small enough to neglect
 *  @return returns **false** if the error is not small enough
 *  @note isNegligible checks up to the 5th place after the comma
 *  @note this does not quite match public isNegligible
 *  @leakage{None}
 */

/**
* @param a - a scalar of supported type
* @return returns **true** if the error is small enough to neglect
* @return returns **false** if the error is not small enough
*/
template <domain D : shared3p>
D bool isNegligible (D float32 a) {
    D bool out;
    __syscall ("shared3p::isnegligible_float32_vec", __domainid (D), a, out);
    return out;
}

/**
* @param a - a scalar of supported type
* @return returns **true** if the error is small enough to neglect
* @return returns **false** if the error is not small enough
*/
template <domain D : shared3p>
D bool isNegligible (D float64 a) {
    D bool out;
    __syscall ("shared3p::isnegligible_float64_vec", __domainid (D), a, out);
    return out;
}

/**
* @param a - a vector of supported type
* @return returns a vector where each element of the input vector has been evaluated, whether the error is small enough to neglect or not
*/
template <domain D : shared3p>
D bool[[1]] isNegligible (D float32[[1]] a) {
    D bool[[1]] out (size (a));
    __syscall ("shared3p::isnegligible_float32_vec", __domainid (D), a, out);
    return out;
}

/**
* @param a - a vector of supported type
* @return returns a vector where each element of the input vector has been evaluated, whether the error is small enough to neglect or not
*/
template <domain D : shared3p>
D bool[[1]] isNegligible (D float64[[1]] a) {
    D bool[[1]] out (size (a));
    __syscall ("shared3p::isnegligible_float64_vec", __domainid (D), a, out);
    return out;
}
/** @}*/

/*******************************
    Min, max
********************************/

/** \addtogroup shared3p_min
 *  @{
 *  @brief Functions for finding the minimum value
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 */

/** \addtogroup shared3p_min_vec
 *  @{
 *  @brief Function for finding the minimum element of the input vector.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param x - input
 *  @returns minimum element of the input vector.
 *  @pre input vector is not empty
 *  @leakage{None}
 */

template <domain D : shared3p, type T>
D T min (D T x) {
    return x;
}

template <domain D : shared3p>
D uint8 min (D uint8[[1]] x) {
    assert (size(x) > 0);
    D uint8 out;
    __syscall ("shared3p::vecmin_uint8_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D uint16 min (D uint16[[1]] x) {
    assert (size(x) > 0);
    D uint16 out;
    __syscall ("shared3p::vecmin_uint16_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D uint32 min (D uint32[[1]] x) {
    assert (size(x) > 0);
    D uint32 out;
    __syscall ("shared3p::vecmin_uint32_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D uint min (D uint[[1]] x) {
    assert (size(x) > 0);
    D uint out;
    __syscall ("shared3p::vecmin_uint64_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D int8 min (D int8[[1]] x) {
    assert (size(x) > 0);
    D uint8[[1]] in = (uint8) x + 128;
    D uint8 out;
    __syscall ("shared3p::vecmin_uint8_vec", __domainid (D), in, out);
    out -= 128;
    return (int8) out;
}
template <domain D : shared3p>
D int16 min (D int16[[1]] x) {
    assert (size(x) > 0);
    D uint16[[1]] in = (uint16) x + 32768;
    D uint16 out;
    __syscall ("shared3p::vecmin_uint16_vec", __domainid (D), in, out);
    out -= 32768;
    return (int16)out;
}
template <domain D : shared3p>
D int32 min (D int32[[1]] x) {
    assert (size(x) > 0);
    D uint32[[1]] in = (uint32) x + 2147483648;
    D uint32 out;
    __syscall ("shared3p::vecmin_uint32_vec", __domainid (D), in, out);
    out -= 2147483648;
    return (int32)out;
}
template <domain D : shared3p>
D int min (D int[[1]] x) {
    assert (size(x) > 0);
    D uint[[1]] in = (uint) x + 9223372036854775808;
    D uint out;
    __syscall ("shared3p::vecmin_uint64_vec", __domainid (D), in, out);
    out -= 9223372036854775808;
    return (int)out;
}
template<domain D : shared3p>
D float32 min (D float32[[1]] x) {
    assert (size (x) > 0);
    D float32 out;
    __syscall ("shared3p::vecmin_float32_vec", __domainid (D), x, out);
    return out;
}
template<domain D : shared3p>
D float64 min (D float64[[1]] x) {
    assert (size (x) > 0);
    D float64 out;
    __syscall ("shared3p::vecmin_float64_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared3p>
D xor_uint8 min (D xor_uint8[[1]] x) {
    assert (size(x) > 0);
    D xor_uint8 out;
    __syscall ("shared3p::vecmin_xor_uint8_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D xor_uint16 min (D xor_uint16[[1]] x) {
    assert (size(x) > 0);
    D xor_uint16 out;
    __syscall ("shared3p::vecmin_xor_uint16_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D xor_uint32 min (D xor_uint32[[1]] x) {
    assert (size(x) > 0);
    D xor_uint32 out;
    __syscall ("shared3p::vecmin_xor_uint32_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D xor_uint64 min (D xor_uint64[[1]] x) {
    assert (size(x) > 0);
    D xor_uint64 out;
    __syscall ("shared3p::vecmin_xor_uint64_vec", __domainid (D), x, out);
    return out;
}

/** @}*/
/** \addtogroup shared3p_min_k
 *  @{
 *  @brief Function for finding the minimum element in the specified parts of the vector.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param x - input vector
 *  @param k - an \ref uint64 "uint" type value, which specifies into how many subarrays must the input array be divided
 *  @returns a vector with all the minimum elements of all the subarrays specified by k
 *  @pre input vector is not empty
 *  @pre the size of the input array is dividable by **k**
 *  @leakage{None}
 */


template <domain D : shared3p>
D uint8[[1]] min (D uint8[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint8[[1]] out (k);
    __syscall ("shared3p::vecmin_uint8_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared3p>
D uint16[[1]] min (D uint16[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint16[[1]] out (k);
    __syscall ("shared3p::vecmin_uint16_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared3p>
D uint32[[1]] min (D uint32[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint32[[1]] out (k);
    __syscall ("shared3p::vecmin_uint32_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared3p>
D uint[[1]] min (D uint[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared3p::vecmin_uint64_vec", __domainid (D), x, out);
    return out;
}


template <domain D : shared3p>
D int8[[1]] min (D int8[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint8[[1]] in = (uint8) x + 128;
    D uint8[[1]] out (k);
    __syscall ("shared3p::vecmin_uint8_vec", __domainid (D), in, out);
    out -= 128;
    return (int8) out;
}
template <domain D : shared3p>
D int16[[1]] min (D int16[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint16[[1]] in = (uint16) x + 32768;
    D uint16[[1]] out (k);
    __syscall ("shared3p::vecmin_uint16_vec", __domainid (D), in, out);
    out -= 32768;
    return (int16)out;
}
template <domain D : shared3p>
D int32[[1]] min (D int32[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint32[[1]] in = (uint32) x + 2147483648;
    D uint32[[1]] out (k);
    __syscall ("shared3p::vecmin_uint32_vec", __domainid (D), in, out);
    out -= 2147483648;
    return (int32)out;
}
template <domain D : shared3p>
D int[[1]] min (D int[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint[[1]] in = (uint) x + 9223372036854775808;
    D uint[[1]] out (k);
    __syscall ("shared3p::vecmin_uint64_vec", __domainid (D), in, out);
    out -= 9223372036854775808;
    return (int)out;
}

/** @}*/
/** \addtogroup shared3p_min_2
 *  @{
 *  @brief Function for finding the pointwise minimum of 2 arrays
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref float32 "float32" / \ref float64 "float64" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param x - first argument
 *  @param y - second argument
 *  @returns an array with the pointwise minimum of each element in the two input vectors
 *  @pre both input vectors are of equal length
 *  @leakage{None}
 */

template <domain D : shared3p>
D uint8 min (D uint8 x, D uint8 y) {
    __syscall ("shared3p::min_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint16 min (D uint16 x, D uint16 y) {
    __syscall ("shared3p::min_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint32 min (D uint32 x, D uint32 y) {
    __syscall ("shared3p::min_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint min (D uint x, D uint y) {
    __syscall ("shared3p::min_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D int8 min (D int8 x, D int8 y) {
    D uint8 in1 = (uint8) x + 128;
    D uint8 in2 = (uint8) y + 128;
    __syscall ("shared3p::min_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared3p>
D int16 min (D int16 x, D int16 y) {
    D uint16 in1 = (uint16) x + 32768;
    D uint16 in2 = (uint16) y + 32768;
    __syscall ("shared3p::min_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared3p>
D int32 min (D int32 x, D int32 y) {
    D uint32 in1 = (uint32) x + 2147483648;
    D uint32 in2 = (uint32) y + 2147483648;
    __syscall ("shared3p::min_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared3p>
D int min (D int x, D int y) {
    D uint in1 = (uint) x + 9223372036854775808;
    D uint in2 = (uint) y + 9223372036854775808;
    __syscall ("shared3p::min_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}
template <domain D : shared3p>
D float32 min (D float32 x, D float32 y) {
    __syscall ("shared3p:min_float32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D float64 min (D float64 x, D float64 y) {
    __syscall ("shared3p:min_float64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared3p>
D uint8[[1]] min (D uint8[[1]] x, D uint8[[1]] y) {
    assert (size(x) == size(y));
    __syscall ("shared3p::min_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint16[[1]] min (D uint16[[1]] x, D uint16[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::min_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint32[[1]] min (D uint32[[1]] x, D uint32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::min_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint[[1]] min (D uint[[1]] x, D uint[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::min_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D int8[[1]] min (D int8[[1]] x, D int8[[1]] y) {
    assert (size (x) == size (y));
    D uint8[[1]] in1 = (uint8) x + 128;
    D uint8[[1]] in2 = (uint8) y + 128;
    __syscall ("shared3p::min_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared3p>
D int16[[1]] min (D int16[[1]] x, D int16[[1]] y) {
    assert (size (x) == size (y));
    D uint16[[1]] in1 = (uint16) x + 32768;
    D uint16[[1]] in2 = (uint16) y + 32768;
    __syscall ("shared3p::min_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared3p>
D int32[[1]] min (D int32[[1]] x, D int32[[1]] y) {
    assert (size (x) == size (y));
    D uint32[[1]] in1 = (uint32) x + 2147483648;
    D uint32[[1]] in2 = (uint32) y + 2147483648;
    __syscall ("shared3p::min_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared3p>
D int[[1]] min (D int[[1]] x, D int[[1]] y) {
    assert (size (x) == size (y));
    D uint[[1]] in1 = (uint) x + 9223372036854775808;
    D uint[[1]] in2 = (uint) y + 9223372036854775808;
    __syscall ("shared3p::min_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}
template<domain D : shared3p>
D float32[[1]] min(D float32[[1]] x, D float32[[1]] y) {
    assert(size(x) == size(y));
    __syscall ("shared3p::min_float32_vec", __domainid (D), x, y, x);
    return x;
}

template<domain D : shared3p>
D float64[[1]] min(D float64[[1]] x, D float64[[1]] y) {
    assert(size(x) == size(y));
    __syscall ("shared3p::min_float64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared3p, dim N>
D uint8[[N]] min (D uint8[[N]] x, D uint8[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::min_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D uint16[[N]] min (D uint16[[N]] x, D uint16[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::min_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D uint32[[N]] min (D uint32[[N]] x, D uint32[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::min_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D uint[[N]] min (D uint[[N]] x, D uint[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::min_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D int8[[N]] min (D int8[[N]] x, D int8[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint8[[N]] in1 = (uint8) x + 128;
    D uint8[[N]] in2 = (uint8) y + 128;
    __syscall ("shared3p::min_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared3p, dim N>
D int16[[N]] min (D int16[[N]] x, D int16[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint16[[N]] in1 = (uint16) x + 32768;
    D uint16[[N]] in2 = (uint16) y + 32768;
    __syscall ("shared3p::min_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared3p, dim N>
D int32[[N]] min (D int32[[N]] x, D int32[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint32[[N]] in1 = (uint32) x + 2147483648;
    D uint32[[N]] in2 = (uint32) y + 2147483648;
    __syscall ("shared3p::min_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared3p, dim N>
D int[[N]] min (D int[[N]] x, D int[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint[[N]] in1 = (uint) x + 9223372036854775808;
    D uint[[N]] in2 = (uint) y + 9223372036854775808;
    __syscall ("shared3p::min_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}
template<domain D : shared3p, dim N>
D float32[[N]] min(D float32[[N]] x, D float32[[N]] y) {
    assert(size(x) == size(y));
    __syscall ("shared3p::min_float32_vec", __domainid (D), x, y, x);
    return x;
}
template<domain D : shared3p, dim N>
D float64[[N]] min(D float64[[N]] x, D float64[[N]] y) {
    assert(size(x) == size(y));
    __syscall ("shared3p::min_float64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint8 min (D xor_uint8 x, D xor_uint8 y) {
    __syscall ("shared3p::min_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint16 min (D xor_uint16 x, D xor_uint16 y) {
    __syscall ("shared3p::min_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint32 min (D xor_uint32 x, D xor_uint32 y) {
    __syscall ("shared3p::min_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint64 min (D xor_uint64 x, D xor_uint64 y) {
    __syscall ("shared3p::min_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared3p>
D xor_uint8[[1]] min (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    assert (size(x) == size(y));
    __syscall ("shared3p::min_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint16[[1]] min (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::min_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint32[[1]] min (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::min_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint64[[1]] min (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::min_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared3p, dim N>
D xor_uint8[[N]] min (D xor_uint8[[N]] x, D xor_uint8[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::min_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D xor_uint16[[N]] min (D xor_uint16[[N]] x, D xor_uint16[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::min_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D xor_uint32[[N]] min (D xor_uint32[[N]] x, D xor_uint32[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::min_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D xor_uint64[[N]] min (D xor_uint64[[N]] x, D xor_uint64[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::min_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}
/** @}*/
/** @}*/


/** \addtogroup shared3p_max
 *  @{
 *  @brief Functions for finding the maximum value
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 */

/** \addtogroup shared3p_max_vec
 *  @{
 *  @brief Function for finding the maximum element of the input vector.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param x - input
 *  @returns maximum element of the input vector
 *  @pre input vector is not empty
 *  @leakage{None}
 */

template <domain D : shared3p, type T>
D T max (D T x) {
    return x;
}
template <domain D : shared3p>
D uint8 max (D uint8[[1]] x) {
    assert (size(x) > 0);
    D uint8 out;
    __syscall ("shared3p::vecmax_uint8_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D uint16 max (D uint16[[1]] x) {
    assert (size(x) > 0);
    D uint16 out;
    __syscall ("shared3p::vecmax_uint16_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D uint32 max (D uint32[[1]] x) {
    assert (size(x) > 0);
    D uint32 out;
    __syscall ("shared3p::vecmax_uint32_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D uint max (D uint[[1]] x) {
    assert (size(x) > 0);
    D uint out;
    __syscall ("shared3p::vecmax_uint64_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared3p>
D int8 max (D int8[[1]] x) {
    assert (size(x) > 0);
    D uint8[[1]] in = (uint8) x + 128;
    D uint8 out;
    __syscall ("shared3p::vecmax_uint8_vec", __domainid (D), in, out);
    out -= 128;
    return (int8) out;
}
template <domain D : shared3p>
D int16 max (D int16[[1]] x) {
    assert (size(x) > 0);
    D uint16[[1]] in = (uint16) x + 32768;
    D uint16 out;
    __syscall ("shared3p::vecmax_uint16_vec", __domainid (D), in, out);
    out -= 32768;
    return (int16)out;
}
template <domain D : shared3p>
D int32 max (D int32[[1]] x) {
    assert (size(x) > 0);
    D uint32[[1]] in = (uint32) x + 2147483648;
    D uint32 out;
    __syscall ("shared3p::vecmax_uint32_vec", __domainid (D), in, out);
    out -= 2147483648;
    return (int32)out;
}
template <domain D : shared3p>
D int max (D int[[1]] x) {
    assert (size(x) > 0);
    D uint[[1]] in = (uint) x + 9223372036854775808;
    D uint out;
    __syscall ("shared3p::vecmax_uint64_vec", __domainid (D), in, out);
    out -= 9223372036854775808;
    return (int)out;
}
template<domain D : shared3p>
D float32 max(D float32[[1]] x) {
    assert (size (x) > 0);
    D float32 out;
    __syscall ("shared3p::vecmax_float32_vec", __domainid (D), x, out);
    return out;
}
template<domain D : shared3p>
D float64 max(D float64[[1]] x) {
    assert (size (x) > 0);
    D float64 out;
    __syscall ("shared3p::vecmax_float64_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D xor_uint8 max (D xor_uint8[[1]] x) {
    assert (size(x) > 0);
    D xor_uint8 out;
    __syscall ("shared3p::vecmax_xor_uint8_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D xor_uint16 max (D xor_uint16[[1]] x) {
    assert (size(x) > 0);
    D xor_uint16 out;
    __syscall ("shared3p::vecmax_xor_uint16_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D xor_uint32 max (D xor_uint32[[1]] x) {
    assert (size(x) > 0);
    D xor_uint32 out;
    __syscall ("shared3p::vecmax_xor_uint32_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared3p>
D xor_uint64 max (D xor_uint64[[1]] x) {
    assert (size(x) > 0);
    D xor_uint64 out;
    __syscall ("shared3p::vecmax_xor_uint64_vec", __domainid (D), x, out);
    return out;
}

/** @}*/
/** \addtogroup shared3p_max_k
 *  @{
 *  @brief Function for finding the maximum element in the specified parts of the vector.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param x - input vector
 *  @param k - an \ref uint64 "uint" type value, which specifies into how many subarrays must the input array be divided
 *  @returns a vector with all the maximum elements of all the subarrays specified by k
 *  @pre input vector is not empty
 *  @pre the size of the input array is dividable by **k**
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint8[[1]] max (D uint8[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint8[[1]] out (k);
    __syscall ("shared3p::vecmax_uint8_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared3p>
D uint16[[1]] max (D uint16[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint16[[1]] out (k);
    __syscall ("shared3p::vecmax_uint16_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared3p>
D uint32[[1]] max (D uint32[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint32[[1]] out (k);
    __syscall ("shared3p::vecmax_uint32_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared3p>
D uint[[1]] max (D uint[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared3p::vecmax_uint64_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared3p>
D int8[[1]] max (D int8[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint8[[1]] in = (uint8) x + 128;
    D uint8[[1]] out (k);
    __syscall ("shared3p::vecmax_uint8_vec", __domainid (D), in, out);
    out -= 128;
    return (int8) out;
}
template <domain D : shared3p>
D int16[[1]] max (D int16[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint16[[1]] in = (uint16) x + 32768;
    D uint16[[1]] out (k);
    __syscall ("shared3p::vecmax_uint16_vec", __domainid (D), in, out);
    out -= 32768;
    return (int16)out;
}
template <domain D : shared3p>
D int32[[1]] max (D int32[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint32[[1]] in = (uint32) x + 2147483648;
    D uint32[[1]] out (k);
    __syscall ("shared3p::vecmax_uint32_vec", __domainid (D), in, out);
    out -= 2147483648;
    return (int32)out;
}
template <domain D : shared3p>
D int[[1]] max (D int[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint[[1]] in = (uint) x + 9223372036854775808;
    D uint[[1]] out (k);
    __syscall ("shared3p::vecmax_uint64_vec", __domainid (D), in, out);
    out -= 9223372036854775808;
    return (int)out;
}

/** @}*/
/** \addtogroup shared3p_max_2
 *  @{
 *  @brief Function for finding the pointwise maximum of 2 arrays
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref float32 "float32" / \ref float64 "float64" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @returns an array with the pointwise maximum of each element in the two input vectors
 *  @pre both input vectors are of equal length
 *  @leakage{None}
 */

template <domain D : shared3p>
D uint8 max (D uint8 x, D uint8 y) {
    __syscall ("shared3p::max_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint16 max (D uint16 x, D uint16 y) {
    __syscall ("shared3p::max_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint32 max (D uint32 x, D uint32 y) {
    __syscall ("shared3p::max_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint max (D uint x, D uint y) {
    __syscall ("shared3p::max_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D int8 max (D int8 x, D int8 y) {
    D uint8 in1 = (uint8) x + 128;
    D uint8 in2 = (uint8) y + 128;
    __syscall ("shared3p::max_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared3p>
D int16 max (D int16 x, D int16 y) {
    D uint16 in1 = (uint16) x + 32768;
    D uint16 in2 = (uint16) y + 32768;
    __syscall ("shared3p::max_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared3p>
D int32 max (D int32 x, D int32 y) {
    D uint32 in1 = (uint32) x + 2147483648;
    D uint32 in2 = (uint32) y + 2147483648;
    __syscall ("shared3p::max_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared3p>
D int max (D int x, D int y) {
    D uint in1 = (uint) x + 9223372036854775808;
    D uint in2 = (uint) y + 9223372036854775808;
    __syscall ("shared3p::max_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}
template <domain D : shared3p>
D float32 max (D float32 x, D float32 y) {
    __syscall ("shared3p::max_float32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D float64 max (D float64 x, D float64 y) {
    __syscall ("shared3p::max_float64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared3p>
D uint8[[1]] max (D uint8[[1]] x, D uint8[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint16[[1]] max (D uint16[[1]] x, D uint16[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint32[[1]] max (D uint32[[1]] x, D uint32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D uint[[1]] max (D uint[[1]] x, D uint[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D int8[[1]] max (D int8[[1]] x, D int8[[1]] y) {
    assert (size (x) == size (y));
    D uint8[[1]] in1 = (uint8) x + 128;
    D uint8[[1]] in2 = (uint8) y + 128;
    __syscall ("shared3p::max_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared3p>
D int16[[1]] max (D int16[[1]] x, D int16[[1]] y) {
    assert (size (x) == size (y));
    D uint16[[1]] in1 = (uint16) x + 32768;
    D uint16[[1]] in2 = (uint16) y + 32768;
    __syscall ("shared3p::max_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared3p>
D int32[[1]] max (D int32[[1]] x, D int32[[1]] y) {
    assert (size (x) == size (y));
    D uint32[[1]] in1 = (uint32) x + 2147483648;
    D uint32[[1]] in2 = (uint32) y + 2147483648;
    __syscall ("shared3p::max_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared3p>
D int[[1]] max (D int[[1]] x, D int[[1]] y) {
    assert (size (x) == size (y));
    D uint[[1]] in1 = (uint) x + 9223372036854775808;
    D uint[[1]] in2 = (uint) y + 9223372036854775808;
    __syscall ("shared3p::max_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}
template <domain D : shared3p>
D float32[[1]] max (D float32[[1]] x, D float32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_float32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D float64[[1]] max (D float64[[1]] x, D float64[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_float64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared3p, dim N>
D uint8[[N]] max (D uint8[[N]] x, D uint8[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::max_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D uint16[[N]] max (D uint16[[N]] x, D uint16[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::max_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D uint32[[N]] max (D uint32[[N]] x, D uint32[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::max_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D uint[[N]] max (D uint[[N]] x, D uint[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::max_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D int8[[N]] max (D int8[[N]] x, D int8[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint8[[N]] in1 = (uint8) x + 128;
    D uint8[[N]] in2 = (uint8) y + 128;
    __syscall ("shared3p::max_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared3p, dim N>
D int16[[N]] max (D int16[[N]] x, D int16[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint16[[N]] in1 = (uint16) x + 32768;
    D uint16[[N]] in2 = (uint16) y + 32768;
    __syscall ("shared3p::max_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared3p, dim N>
D int32[[N]] max (D int32[[N]] x, D int32[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint32[[N]] in1 = (uint32) x + 2147483648;
    D uint32[[N]] in2 = (uint32) y + 2147483648;
    __syscall ("shared3p::max_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared3p, dim N>
D int[[N]] max (D int[[N]] x, D int[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint[[N]] in1 = (uint) x + 9223372036854775808;
    D uint[[N]] in2 = (uint) y + 9223372036854775808;
    __syscall ("shared3p::max_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}
template <domain D : shared3p, dim N>
D float32[[N]] max (D float32[[N]] x, D float32[[N]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_float32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D float64[[N]] max (D float64[[N]] x, D float64[[N]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_float64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint8 max (D xor_uint8 x, D xor_uint8 y) {
    __syscall ("shared3p::max_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint16 max (D xor_uint16 x, D xor_uint16 y) {
    __syscall ("shared3p::max_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint32 max (D xor_uint32 x, D xor_uint32 y) {
    __syscall ("shared3p::max_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint64 max (D xor_uint64 x, D xor_uint64 y) {
    __syscall ("shared3p::max_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared3p>
D xor_uint8[[1]] max (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint16[[1]] max (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint32[[1]] max (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p>
D xor_uint64[[1]] max (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared3p::max_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D xor_uint8[[N]] max (D xor_uint8[[N]] x, D xor_uint8[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::max_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D xor_uint16[[N]] max (D xor_uint16[[N]] x, D xor_uint16[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::max_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D xor_uint32[[N]] max (D xor_uint32[[N]] x, D xor_uint32[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::max_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared3p, dim N>
D xor_uint64[[N]] max (D xor_uint64[[N]] x, D xor_uint64[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared3p::max_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

/** @}*/
/** @}*/
/** \addtogroup shared3p_floor
 *  @{
 *  @brief Functions for rounding a value downwards
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" \ref float64 "float64"
 *  @return returns the downwards rounded value of the input scalar/vector
 *  @leakage{None}
 */

/**
* @param value - input scalar of supported type
*/
template <domain D : shared3p>
D float32 floor (D float32 value) {
    D float32 out;
    __syscall("shared3p::floor_float32_vec", __domainid( D ), value, out);
    return out;
}

template <domain D : shared3p>
D float64 floor (D float64 value) {
    D float64 out;
    __syscall("shared3p::floor_float64_vec", __domainid( D ), value, out);
    return out;
}

/**
* @param arr - input vector of supported type
*/
template <domain D : shared3p>
D float32[[1]] floor (D float32[[1]] arr) {
    D float32[[1]] out (size (arr));
    __syscall("shared3p::floor_float32_vec", __domainid( D ), arr, out);
    return out;
}

template <domain D : shared3p>
D float64[[1]] floor (D float64[[1]] arr) {
    D float64[[1]] out (size (arr));
    __syscall("shared3p::floor_float64_vec", __domainid( D ), arr, out);
    return out;
}

/** @}*/
/** \addtogroup shared3p_ceiling
 *  @{
 *  @brief Functions for rounding a value upwards
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float32 "float32" \ref float64 "float64"
 *  @return returns the upwards rounded value of the input scalar/vector
 *  @leakage{None}
 */

/**
* @param value - input scalar of supported type
*/
template <domain D : shared3p>
D float32 ceiling (D float32 value) {
    D float32 out;
    __syscall("shared3p::ceiling_float32_vec", __domainid( D ), value, out);
    return out;
}

template <domain D : shared3p>
D float64 ceiling (D float64 value) {
    D float64 out;
    __syscall("shared3p::ceiling_float64_vec", __domainid( D ), value, out);
    return out;
}

/**
* @param arr - input vector of supported type
*/
template <domain D : shared3p>
D float32[[1]] ceiling (D float32[[1]] arr) {
    D float32[[1]] out (size (arr));
    __syscall("shared3p::ceiling_float32_vec", __domainid( D ), arr, out);
    return out;
}

template <domain D : shared3p>
D float64[[1]] ceiling (D float64[[1]] arr) {
    D float64[[1]] out (size (arr));
    __syscall("shared3p::ceiling_float64_vec", __domainid( D ), arr, out);
    return out;
}

/** @}*/
/** \addtogroup shared3p_argument
 *  @{
 *  @brief Function for accessing the named program arguments of shared3p types.
 *  @note **T** - any \ref data_types "data" type
 *  @param name The name of the argument.
 *  @return returns the value associated with the argument specified by parameter name.
 */

/**
*  @return An argument of scalar type.
*/
template <domain D : shared3p, type T>
D T argument (string name) {
    uint num_bytes;
    __syscall("Process_argument", __cref name, __return num_bytes);
    uint8 [[1]] bytes (num_bytes);
    D T out;
    __syscall("Process_argument", __cref name, __ref bytes, __return num_bytes);
    __syscall ("shared3p::set_shares_$T\_vec", __domainid(D), out, __cref bytes);
    return out;
}

/**
*  @return An argument of 1-dimensional array type.
*/
template <domain D : shared3p, type T>
D T[[1]] argument (string name) {
    uint num_bytes, vector_size;
    __syscall("Process_argument", __cref name, __return num_bytes);
    uint8 [[1]] bytes (num_bytes);
    __syscall("Process_argument", __cref name, __ref bytes, __return num_bytes);
    __syscall ("shared3p::set_shares_$T\_vec", __domainid(D), __cref bytes, __return vector_size);
    D T [[1]] out (vector_size);
    __syscall ("shared3p::set_shares_$T\_vec", __domainid(D), out, __cref bytes);
    return out;
}

/** @}*/
/** \addtogroup shared3p_publish
 *  @{
 *  @brief Function for publishing the named values of shared3p types.
 *  @note **N** - any array size of any dimension
 *  @note **T** - any \ref data_types "data" type
 *  @param name The name of the argument.
 *  @param val the value to publish under the given name. Accepts scalars as well as arrays.
 */

template <domain D : shared3p, type T, dim N>
void publish (string name, D T[[N]] val) {
    uint num_bytes;
    __syscall ("shared3p::get_shares_$T\_vec", __domainid(D), val, __return num_bytes);
    uint8 [[1]] bytes (num_bytes);
    __syscall ("shared3p::get_shares_$T\_vec", __domainid(D), val, __ref bytes);
    __syscall("Process_setResult", __cref name, __cref "$D", __cref "$T", __cref bytes, 0::uint, num_bytes);
}
/** @}*/

/**
 * \addtogroup shared3p_bit_extract
 * @{
 * @brief Function for converting xor_uint(X) type value to the bit representation.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @note The input is arbitrary dimensional array, output is flattened to one boolean vector. Reshape the result to get appropriate dimensionality.
 *  @param input - the input value to convert
 *  @return returns filattened vector of extracted bits
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D bool[[1]] bit_extract (D xor_uint8[[N]] input) {
    D bool[[1]] out (8 * size (input));
    __syscall ("shared3p::bit_extract_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p, dim N>
D bool[[1]] bit_extract (D xor_uint16[[N]] input) {
    D bool[[1]] out (16 * size (input));
    __syscall ("shared3p::bit_extract_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p, dim N>
D bool[[1]] bit_extract (D xor_uint32[[N]] input) {
    D bool[[1]] out (32 * size (input));
    __syscall ("shared3p::bit_extract_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p, dim N>
D bool[[1]] bit_extract (D xor_uint64[[N]] input) {
    D bool[[1]] out (64 * size (input));
    __syscall ("shared3p::bit_extract_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

/**
 * @}
 * \addtogroup shared3p_reshare
 * @{
 * @brief Function for converting uint(X) type values to xor_uint(X) and the other way around
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param input - the input value to convert
 *  @return returns a converted value from uint(X) -> xor_uint(X) or xor_uint(X) -> uint(X)
 *  @leakage{None}
 */

template <domain D : shared3p>
D xor_uint8 reshare (D uint8 input) {
    D xor_uint8 out;
    __syscall ("shared3p::reshare_uint8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint8 reshare (D int8 input) {
    D xor_uint8 out;
    __syscall ("shared3p::reshare_int8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint8 reshare (D xor_uint8 input) {
    D uint8 out;
    __syscall ("shared3p::reshare_xor_uint8_to_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint8 [[1]] reshare (D uint8[[1]] input) {
    D xor_uint8[[1]] out (size (input));
    __syscall ("shared3p::reshare_uint8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint8 [[1]] reshare (D int8[[1]] input) {
    D xor_uint8[[1]] out (size (input));
    __syscall ("shared3p::reshare_int8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint8 [[1]] reshare (D xor_uint8[[1]] input) {
    D uint8[[1]] out (size (input));
    __syscall ("shared3p::reshare_xor_uint8_to_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint8 [[2]] reshare (D uint8[[2]] input) {
    D xor_uint8[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_uint8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint8 [[2]] reshare (D int8[[2]] input) {
    D xor_uint8[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_int8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint8 [[2]] reshare (D xor_uint8[[2]] input) {
    D uint8[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_xor_uint8_to_uint8_vec", __domainid (D), input, out);
    return out;
}

/*****************************
*****************************/

template <domain D : shared3p>
D xor_uint16 reshare (D uint16 input) {
    D xor_uint16 out;
    __syscall ("shared3p::reshare_uint16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint16 reshare (D int16 input) {
    D xor_uint16 out;
    __syscall ("shared3p::reshare_int16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint16 reshare (D xor_uint16 input) {
    D uint16 out;
    __syscall ("shared3p::reshare_xor_uint16_to_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint16 [[1]] reshare (D uint16[[1]] input) {
    D xor_uint16[[1]] out (size (input));
    __syscall ("shared3p::reshare_uint16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint16 [[1]] reshare (D int16[[1]] input) {
    D xor_uint16[[1]] out (size (input));
    __syscall ("shared3p::reshare_int16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint16 [[1]] reshare (D xor_uint16[[1]] input) {
    D uint16[[1]] out (size (input));
    __syscall ("shared3p::reshare_xor_uint16_to_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint16 [[2]] reshare (D uint16[[2]] input) {
    D xor_uint16[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_uint16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint16 [[2]] reshare (D int16[[2]] input) {
    D xor_uint16[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_int16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint16 [[2]] reshare (D xor_uint16[[2]] input) {
    D uint16[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_xor_uint16_to_uint16_vec", __domainid (D), input, out);
    return out;
}

/*****************************
*****************************/

template <domain D : shared3p>
D xor_uint32 reshare (D uint32 input) {
    D xor_uint32 out;
    __syscall ("shared3p::reshare_uint32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint32 reshare (D int32 input) {
    D xor_uint32 out;
    __syscall ("shared3p::reshare_int32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint32 reshare (D xor_uint32 input) {
    D uint32 out;
    __syscall ("shared3p::reshare_xor_uint32_to_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint32 [[1]] reshare (D uint32[[1]] input) {
    D xor_uint32[[1]] out (size (input));
    __syscall ("shared3p::reshare_uint32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint32 [[1]] reshare (D int32[[1]] input) {
    D xor_uint32[[1]] out (size (input));
    __syscall ("shared3p::reshare_int32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint32 [[1]] reshare (D xor_uint32[[1]] input) {
    D uint32[[1]] out (size (input));
    __syscall ("shared3p::reshare_xor_uint32_to_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint32 [[2]] reshare (D uint32[[2]] input) {
    D xor_uint32[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_uint32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint32 [[2]] reshare (D int32[[2]] input) {
    D xor_uint32[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_int32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint32 [[2]] reshare (D xor_uint32[[2]] input) {
    D uint32[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_xor_uint32_to_uint32_vec", __domainid (D), input, out);
    return out;
}

/*****************************
*****************************/

template <domain D : shared3p>
D xor_uint64 reshare (D uint64 input) {
    D xor_uint64 out;
    __syscall ("shared3p::reshare_uint64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint64 reshare (D int64 input) {
    D xor_uint64 out;
    __syscall ("shared3p::reshare_int64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint64 reshare (D xor_uint64 input) {
    D uint64 out;
    __syscall ("shared3p::reshare_xor_uint64_to_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint64 [[1]] reshare (D uint64[[1]] input) {
    D xor_uint64[[1]] out (size (input));
    __syscall ("shared3p::reshare_uint64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint64 [[1]] reshare (D int64[[1]] input) {
    D xor_uint64[[1]] out (size (input));
    __syscall ("shared3p::reshare_int64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint64 [[1]] reshare (D xor_uint64[[1]] input) {
    D uint64[[1]] out (size (input));
    __syscall ("shared3p::reshare_xor_uint64_to_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint64 [[2]] reshare (D uint64[[2]] input) {
    D xor_uint64[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_uint64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D xor_uint64 [[2]] reshare (D int64[[2]] input) {
    D xor_uint64[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_int64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared3p>
D uint64 [[2]] reshare (D xor_uint64[[2]] input) {
    D uint64[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared3p::reshare_xor_uint64_to_uint64_vec", __domainid (D), input, out);
    return out;
}


/**
 * @}
 * \addtogroup shared3p_choose1
 *  @{
 *  @brief Function for obliviously choosing one of the inputs
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param cond - a boolean scalar
 *  @return returns one of the input arrays that was obliviously chosen with the condition. if **true**, array **first** is returned else **second** is returned
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D xor_uint8[[N]] choose(D bool cond, D xor_uint8[[N]] first, D xor_uint8[[N]] second) {
    D bool[[1]] cond2(size(first));
    cond2 = cond;
    D xor_uint8[[N]] out = first;
    __syscall ("shared3p::choose_xor_uint8_vec", __domainid (D), cond2, first, second, out);
    return out;
}

template <domain D : shared3p, dim N>
D xor_uint16[[N]] choose(D bool cond, D xor_uint16[[N]] first, D xor_uint16[[N]] second) {
    D bool[[1]] cond2(size(first));
    cond2 = cond;
    D xor_uint16[[N]] out = first;
    __syscall ("shared3p::choose_xor_uint16_vec", __domainid (D), cond2, first, second, out);
    return out;
}

template <domain D : shared3p, dim N>
D xor_uint32[[N]] choose(D bool cond, D xor_uint32[[N]] first, D xor_uint32[[N]] second) {
    D bool[[1]] cond2(size(first));
    cond2 = cond;
    D xor_uint32[[N]] out = first;
    __syscall ("shared3p::choose_xor_uint32_vec", __domainid (D), cond2, first, second, out);
    return out;
}

template <domain D : shared3p, dim N>
D xor_uint64[[N]] choose(D bool cond, D xor_uint64[[N]] first, D xor_uint64[[N]] second) {
    D bool[[1]] cond2(size(first));
    cond2 = cond;
    D xor_uint64[[N]] out = first;
    __syscall ("shared3p::choose_xor_uint64_vec", __domainid (D), cond2, first, second, out);
    return out;
}

/**
 * @}
 * \addtogroup shared3p_choose2
 *  @{
 *  @brief Function for obliviously choosing pointwise from the inputs
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param cond - a boolean vector
 *  @return pointwise check if **cond** at a certain position is **true** or **false**. if **true** the element of **first** at that position is returned else the element of **second** at that position is returned
 *  @leakage{None}
 */
template <domain D : shared3p, dim N>
D xor_uint8[[N]] choose(D bool[[N]] cond, D xor_uint8[[N]] first, D xor_uint8[[N]] second) {
    D xor_uint8[[N]] out = first;
    __syscall ("shared3p::choose_xor_uint8_vec", __domainid (D), cond, first, second, out);
    return out;
}

template <domain D : shared3p, dim N>
D xor_uint16[[N]] choose(D bool[[N]] cond, D xor_uint16[[N]] first, D xor_uint16[[N]] second) {
    D xor_uint16[[N]] out = first;
    __syscall ("shared3p::choose_xor_uint16_vec", __domainid (D), cond, first, second, out);
    return out;
}

template <domain D : shared3p, dim N>
D xor_uint32[[N]] choose(D bool[[N]] cond, D xor_uint32[[N]] first, D xor_uint32[[N]] second) {
    D xor_uint32[[N]] out = first;
    __syscall ("shared3p::choose_xor_uint32_vec", __domainid (D), cond, first, second, out);
    return out;
}

template <domain D : shared3p, dim N>
D xor_uint64[[N]] choose(D bool[[N]] cond, D xor_uint64[[N]] first, D xor_uint64[[N]] second) {
    D xor_uint64[[N]] out = first;
    __syscall ("shared3p::choose_xor_uint64_vec", __domainid (D), cond, first, second, out);
    return out;
}
/** @}*/

/** \cond */
float64[[1]] _logGammaCoeffs = {
    0.99999999999999709182,
    57.156235665862923517,
    -59.597960355475491248,
    14.136097974741747174,
    -0.49191381609762019978,
    0.33994649984811888699e-4,
    0.46523628927048575665e-4,
    -0.98374475304879564677e-4,
    0.15808870322491248884e-3,
    -0.21026444172410488319e-3,
    0.21743961811521264320e-3,
    -0.16431810653676389022e-3,
    0.84418223983852743293e-4,
    -0.26190838401581408670e-4,
    0.36899182659531622704e-5
};

float64 _g_plus_half = 607 / 128.0 + 0.5;

float64 _half_log_2_pi = 0.9189385332046727418;
/** \endcond */

/*
 * Lanczos approximation with coefficients from
 * http://my.fit.edu/~gabdo/gamma.txt
 */

/** \addtogroup shared3p_log_gamma
 *  @{
 *  @brief Compute the logarithm of the Gamma function.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref float64 "float64"
 *  @param x - input
 *  @return returns log(Gamma(x))
 *  @leakage{None}
 */
template<domain D : shared3p>
D float64[[1]] logGamma(D float64[[1]] x) {
    uint n = size(x);
    uint nCoeff = size(_logGammaCoeffs);

    float64[[2]] addL(n, nCoeff - 1);
    D float64[[2]] addR(n, nCoeff - 1);
    D float64[[2]] divL(n, nCoeff - 1);
    for (uint i = 1; i < nCoeff; ++i) {
        addL[:, i - 1] = (float64) i;
        addR[:, i - 1] = x;
        divL[:, i - 1] = _logGammaCoeffs[i];
    }

    D float64[[2]] addRes = addL + addR;
    D float64[[2]] divRes = divL / addRes;
    D float64[[1]] sums = rowSums(divRes) + _logGammaCoeffs[0];
    D float64[[1]] tmp = x + _g_plus_half;

    return ((x + 0.5) * ln(tmp)) - tmp + _half_log_2_pi + ln(sums / x);
}
/** @} */

/** \cond */

/**********************************************************************
 OPERATOR DEFINITIONS
**********************************************************************/

template <domain D : shared3p>
D bool[[1]] operator ! (D bool[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::not_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p, type T>
D bool[[1]] operator == (D T[[1]] x, D T[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::eq_$T\_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator == (D fix32[[1]] x, D fix32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::eq_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator == (D fix64[[1]] x, D fix64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::eq_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p, type T>
D bool[[1]] operator != (D T[[1]] x, D T[[1]] y) {
    return !(x == y);
}

template <domain D : shared3p>
D bool[[1]] operator < (D uint8[[1]] x, D uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D uint16[[1]] x, D uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D uint32[[1]] x, D uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D uint64[[1]] x, D uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D int8[[1]] x, D int8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D int16[[1]] x, D int16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D int32[[1]] x, D int32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D int64[[1]] x, D int64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D float32[[1]] x, D float32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_float32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D float64[[1]] x, D float64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_float64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lt_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D fix32[[1]] x, D fix32[[1]] y) {
    uint n = size (x);
    D bool[[1]] res (n);
    D int32[[1]] xi (n), yi (n);
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), x, xi);
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), y, yi);
    __syscall ("shared3p::lt_int32_vec", __domainid (D), xi, yi, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator < (D fix64[[1]] x, D fix64[[1]] y) {
    uint n = size (x);
    D bool[[1]] res (n);
    D int64[[1]] xi (n), yi (n);
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), x, xi);
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), y, yi);
    __syscall ("shared3p::lt_int64_vec", __domainid (D), xi, yi, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D uint8[[1]] x, D uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D uint16[[1]] x, D uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D uint32[[1]] x, D uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D uint64[[1]] x, D uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D int8[[1]] x, D int8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D int16[[1]] x, D int16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D int32[[1]] x, D int32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D int64[[1]] x, D int64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D float32[[1]] x, D float32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_float32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D float64[[1]] x, D float64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_float64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gt_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D fix32[[1]] x, D fix32[[1]] y) {
    uint n = size (x);
    D bool[[1]] res (n);
    D int32[[1]] xi (n), yi (n);
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), x, xi);
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), y, yi);
    __syscall ("shared3p::gt_int32_vec", __domainid (D), xi, yi, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator > (D fix64[[1]] x, D fix64[[1]] y) {
    uint n = size (x);
    D bool[[1]] res (n);
    D int64[[1]] xi (n), yi (n);
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), x, xi);
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), y, yi);
    __syscall ("shared3p::gt_int64_vec", __domainid (D), xi, yi, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D uint8[[1]] x, D uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D uint16[[1]] x, D uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D uint32[[1]] x, D uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D uint64[[1]] x, D uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D int8[[1]] x, D int8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D int16[[1]] x, D int16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D int32[[1]] x, D int32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D int64[[1]] x, D int64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D float32[[1]] x, D float32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_float32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D float64[[1]] x, D float64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_float64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::lte_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D fix32[[1]] x, D fix32[[1]] y) {
    uint n = size (x);
    D bool[[1]] res (n);
    D int32[[1]] xi (n), yi (n);
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), x, xi);
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), y, yi);
    __syscall ("shared3p::lte_int32_vec", __domainid (D), xi, yi, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator <= (D fix64[[1]] x, D fix64[[1]] y) {
    uint n = size (x);
    D bool[[1]] res (n);
    D int64[[1]] xi (n), yi (n);
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), x, xi);
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), y, yi);
    __syscall ("shared3p::lte_int64_vec", __domainid (D), xi, yi, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D uint8[[1]] x, D uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D uint16[[1]] x, D uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D uint32[[1]] x, D uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D uint64[[1]] x, D uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D int8[[1]] x, D int8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D int16[[1]] x, D int16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D int32[[1]] x, D int32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D int64[[1]] x, D int64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D float32[[1]] x, D float32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_float32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D float64[[1]] x, D float64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_float64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::gte_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D fix32[[1]] x, D fix32[[1]] y) {
    uint n = size (x);
    D bool[[1]] res (n);
    D int32[[1]] xi (n), yi (n);
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), x, xi);
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), y, yi);
    __syscall ("shared3p::gte_int32_vec", __domainid (D), xi, yi, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator >= (D fix64[[1]] x, D fix64[[1]] y) {
    uint n = size (x);
    D bool[[1]] res (n);
    D int64[[1]] xi (n), yi (n);
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), x, xi);
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), y, yi);
    __syscall ("shared3p::gte_int64_vec", __domainid (D), xi, yi, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] operator + (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::add_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] operator + (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::add_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] operator + (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::add_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] operator + (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::add_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] operator + (D int8[[1]] x, D int8[[1]] y) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::add_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] operator + (D int16[[1]] x, D int16[[1]] y) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::add_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] operator + (D int32[[1]] x, D int32[[1]] y) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::add_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] operator + (D int64[[1]] x, D int64[[1]] y) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::add_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] operator + (D float32[[1]] x, D float32[[1]] y) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::add_float32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] operator + (D float64[[1]] x, D float64[[1]] y) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::add_float64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] operator + (D fix32[[1]] x, D fix32[[1]] y) {
    D fix32[[1]] res (size (x));
    __syscall ("shared3p::add_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D fix64[[1]] operator + (D fix64[[1]] x, D fix64[[1]] y) {
    D fix64[[1]] res (size (x));
    __syscall ("shared3p::add_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] operator - (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::sub_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] operator - (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::sub_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] operator - (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::sub_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] operator - (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::sub_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] operator - (D int8[[1]] x, D int8[[1]] y) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::sub_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] operator - (D int16[[1]] x, D int16[[1]] y) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::sub_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] operator - (D int32[[1]] x, D int32[[1]] y) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::sub_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] operator - (D int64[[1]] x, D int64[[1]] y) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::sub_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] operator - (D float32[[1]] x, D float32[[1]] y) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::sub_float32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] operator - (D float64[[1]] x, D float64[[1]] y) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::sub_float64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] operator - (D fix32[[1]] x, D fix32[[1]] y) {
    D fix32[[1]] res (size (x));
    __syscall ("shared3p::sub_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D fix64[[1]] operator - (D fix64[[1]] x, D fix64[[1]] y) {
    D fix64[[1]] res (size (x));
    __syscall ("shared3p::sub_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] operator * (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::mul_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] operator * (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::mul_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] operator * (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::mul_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] operator * (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::mul_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] operator * (D int8[[1]] x, D int8[[1]] y) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::mul_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] operator * (D int16[[1]] x, D int16[[1]] y) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::mul_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] operator * (D int32[[1]] x, D int32[[1]] y) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::mul_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] operator * (D int64[[1]] x, D int64[[1]] y) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::mul_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] operator * (D float32[[1]] x, D float32[[1]] y) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::mul_float32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] operator * (D float64[[1]] x, D float64[[1]] y) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::mul_float64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] operator * (D fix32[[1]] x, D fix32[[1]] y) {
    D fix32[[1]] res (size (x));
    __syscall ("shared3p::mul_fix32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D fix64[[1]] operator * (D fix64[[1]] x, D fix64[[1]] y) {
    D fix64[[1]] res (size (x));
    __syscall ("shared3p::mul_fix64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] operator % (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::mod_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] operator % (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::mod_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] operator % (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::mod_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] operator % (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::mod_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] operator / (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::div_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] operator / (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::div_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] operator / (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::div_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] operator / (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::div_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] operator / (D float32[[1]] x, D float32[[1]] y) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::div_float32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] operator / (D float64[[1]] x, D float64[[1]] y) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::div_float64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] operator / (D fix32[[1]] x, D fix32[[1]] y) {
    return x * inv (y);
}

template <domain D : shared3p>
D fix64[[1]] operator / (D fix64[[1]] x, D fix64[[1]] y) {
    return x * inv (y);
}

template <domain D : shared3p>
D bool[[1]] operator | (D bool[[1]] x, D bool[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::or_bool_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] operator | (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::or_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] operator | (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::or_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] operator | (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::or_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] operator | (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::or_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator & (D bool[[1]] x, D bool[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::and_bool_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] operator & (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::and_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] operator & (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::and_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] operator & (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::and_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] operator & (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::and_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] operator ^ (D bool[[1]] x, D bool[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::xor_bool_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] operator ^ (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::xor_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] operator ^ (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::xor_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] operator ^ (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::xor_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] operator ^ (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::xor_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] operator << (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::shift_left_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] operator << (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::shift_left_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] operator << (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::shift_left_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] operator << (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::shift_left_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] operator << (D xor_uint8[[1]] x, uint8[[1]] k) {
    int[[1]] shifts = (int) k;
    __syscall("shared3p::shift_left_xor_uint8_vec", __domainid(D), x, __cref shifts, x);
    return x;
}

template <domain D : shared3p>
D xor_uint16[[1]] operator << (D xor_uint16[[1]] x, uint16[[1]] k) {
    int[[1]] shifts = (int) k;
    __syscall("shared3p::shift_left_xor_uint16_vec", __domainid(D), x, __cref shifts, x);
    return x;
}

template <domain D : shared3p>
D xor_uint32[[1]] operator << (D xor_uint32[[1]] x, uint32[[1]] k) {
    int[[1]] shifts = (int) k;
    __syscall("shared3p::shift_left_xor_uint32_vec", __domainid(D), x, __cref shifts, x);
    return x;
}

template <domain D : shared3p>
D xor_uint64[[1]] operator << (D xor_uint64[[1]] x, uint64[[1]] k) {
    int[[1]] shifts = (int) k;
    __syscall("shared3p::shift_left_xor_uint64_vec", __domainid(D), x, __cref shifts, x);
    return x;
}

template <domain D : shared3p>
D uint8[[1]] operator >> (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::shift_right_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] operator >> (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::shift_right_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] operator >> (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::shift_right_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] operator >> (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::shift_right_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] operator * (D uint8[[1]] x, uint8[[1]] y) {
    __syscall ("shared3p::mulc_uint8_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D uint16[[1]] operator * (D uint16[[1]] x, uint16[[1]] y) {
    __syscall ("shared3p::mulc_uint16_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D uint32[[1]] operator * (D uint32[[1]] x, uint32[[1]] y) {
    __syscall ("shared3p::mulc_uint32_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D uint64[[1]] operator * (D uint64[[1]] x, uint64[[1]] y) {
    __syscall ("shared3p::mulc_uint64_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D xor_uint8[[1]] operator >> (D xor_uint8[[1]] x, uint8[[1]] k) {
    int[[1]] shifts = - (int) k;
    __syscall("shared3p::shift_left_xor_uint8_vec", __domainid(D), x, __cref shifts, x);
    return x;
}

template <domain D : shared3p>
D xor_uint16[[1]] operator >> (D xor_uint16[[1]] x, uint16[[1]] k) {
    int[[1]] shifts = - (int) k;
    __syscall("shared3p::shift_left_xor_uint16_vec", __domainid(D), x, __cref shifts, x);
    return x;
}

template <domain D : shared3p>
D xor_uint32[[1]] operator >> (D xor_uint32[[1]] x, uint32[[1]] k) {
    int[[1]] shifts = - (int) k;
    __syscall("shared3p::shift_left_xor_uint32_vec", __domainid(D), x, __cref shifts, x);
    return x;
}

template <domain D : shared3p>
D xor_uint64[[1]] operator >> (D xor_uint64[[1]] x, uint64[[1]] k) {
    int[[1]] shifts = - (int) k;
    __syscall("shared3p::shift_left_xor_uint64_vec", __domainid(D), x, __cref shifts, x);
    return x;
}

template <domain D : shared3p>
D int8[[1]] operator * (D int8[[1]] x, int8[[1]] y) {
    __syscall ("shared3p::mulc_int8_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D int16[[1]] operator * (D int16[[1]] x, int16[[1]] y) {
    __syscall ("shared3p::mulc_int16_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D int32[[1]] operator * (D int32[[1]] x, int32[[1]] y) {
    __syscall ("shared3p::mulc_int32_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D int64[[1]] operator * (D int64[[1]] x, int64[[1]] y) {
    __syscall ("shared3p::mulc_int64_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D float32[[1]] operator * (D float32[[1]] x, float32[[1]] y) {
    __syscall ("shared3p::mulc_float32_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D float64[[1]] operator * (D float64[[1]] x, float64[[1]] y) {
    __syscall ("shared3p::mulc_float64_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D uint8[[1]] operator * (uint8[[1]] x, D uint8[[1]] y) {
    __syscall ("shared3p::mulc_uint8_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D uint16[[1]] operator * (uint16[[1]] x, D uint16[[1]] y) {
    __syscall ("shared3p::mulc_uint16_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D uint32[[1]] operator * (uint32[[1]] x, D uint32[[1]] y) {
    __syscall ("shared3p::mulc_uint32_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D uint64[[1]] operator * (uint64[[1]] x, D uint64[[1]] y) {
    __syscall ("shared3p::mulc_uint64_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D int8[[1]] operator * (int8[[1]] x, D int8[[1]] y) {
    __syscall ("shared3p::mulc_int8_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D int16[[1]] operator * (int16[[1]] x, D int16[[1]] y) {
    __syscall ("shared3p::mulc_int16_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D int32[[1]] operator * (int32[[1]] x, D int32[[1]] y) {
    __syscall ("shared3p::mulc_int32_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D int64[[1]] operator * (int64[[1]] x, D int64[[1]] y) {
    __syscall ("shared3p::mulc_int64_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D float32[[1]] operator * (float32[[1]] x, D float32[[1]] y) {
    __syscall ("shared3p::mulc_float32_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D float64[[1]] operator * (float64[[1]] x, D float64[[1]] y) {
    __syscall ("shared3p::mulc_float64_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared3p>
D uint8[[1]] operator / (D uint8[[1]] x, uint8[[1]] y) {
    __syscall ("shared3p::divc_uint8_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D uint16[[1]] operator / (D uint16[[1]] x, uint16[[1]] y) {
    __syscall ("shared3p::divc_uint16_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D uint32[[1]] operator / (D uint32[[1]] x, uint32[[1]] y) {
    __syscall ("shared3p::divc_uint32_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D uint64[[1]] operator / (D uint64[[1]] x, uint64[[1]] y) {
    __syscall ("shared3p::divc_uint64_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D float32[[1]] operator / (D float32[[1]] x, float32[[1]] y) {
    __syscall ("shared3p::divc_float32_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D float64[[1]] operator / (D float64[[1]] x, float64[[1]] y) {
    __syscall ("shared3p::divc_float64_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared3p>
D uint8[[1]] operator - (D uint8[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::neg_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] operator - (D uint16[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::neg_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] operator - (D uint32[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::neg_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] operator - (D uint64[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::neg_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] operator - (D int8[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::neg_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] operator - (D int16[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::neg_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] operator - (D int32[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::neg_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] operator - (D int64[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::neg_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] operator - (D float32[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::neg_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] operator - (D float64[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::neg_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] operator - (D fix32[[1]] x) {
    D fix32[[1]] res (size (x));
    __syscall ("shared3p::neg_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix64[[1]] operator - (D fix64[[1]] x) {
    D fix64[[1]] res (size (x));
    __syscall ("shared3p::neg_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] operator ~ (D xor_uint8[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::inv_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] operator ~ (D xor_uint16[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::inv_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] operator ~ (D xor_uint32[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::inv_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] operator ~ (D xor_uint64[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::inv_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D bool[[1]] x) { return x; }

template <domain D : shared3p>
D uint8[[1]] cast (D uint8[[1]] x) { return x; }

template <domain D : shared3p>
D uint16[[1]] cast (D uint16[[1]] x) { return x; }

template <domain D : shared3p>
D uint32[[1]] cast (D uint32[[1]] x) { return x; }

template <domain D : shared3p>
D uint64[[1]] cast (D uint64[[1]] x) { return x; }

template <domain D : shared3p>
D int8[[1]] cast (D int8[[1]] x) { return x; }

template <domain D : shared3p>
D int16[[1]] cast (D int16[[1]] x) { return x; }

template <domain D : shared3p>
D int32[[1]] cast (D int32[[1]] x) { return x; }

template <domain D : shared3p>
D int64[[1]] cast (D int64[[1]] x) { return x; }

template <domain D : shared3p>
D float32[[1]] cast (D float32[[1]] x) { return x; }

template <domain D : shared3p>
D float64[[1]] cast (D float64[[1]] x) { return x; }

template <domain D : shared3p>
D xor_uint8[[1]] cast (D xor_uint8[[1]] x) { return x; }

template <domain D : shared3p>
D xor_uint16[[1]] cast (D xor_uint16[[1]] x) { return x; }

template <domain D : shared3p>
D xor_uint32[[1]] cast (D xor_uint32[[1]] x) { return x; }

template <domain D : shared3p>
D xor_uint64[[1]] cast (D xor_uint64[[1]] x) { return x; }

template <domain D : shared3p>
D fix32[[1]] cast (D fix32[[1]] x) { return x; }

template <domain D : shared3p>
D fix64[[1]] cast (D fix64[[1]] x) { return x; }

template <domain D : shared3p>
D bool[[1]] cast (D uint8[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D uint16[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D uint32[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D uint64[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D int8[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D int16[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D int32[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D int64[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D float32[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D float64[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D bool[[1]] cast (D fix32[[1]] x) {
    return x != 0;
}

template <domain D : shared3p>
D bool[[1]] cast (D fix64[[1]] x) {
    return x != 0;
}

template <domain D : shared3p>
D uint8[[1]] cast (D bool[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D bool[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D bool[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D bool[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D bool[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D bool[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D bool[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D bool[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D bool[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D bool[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D bool[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D fix64[[1]] cast (D bool[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D uint16[[1]] cast (D uint8[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D uint8[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D uint8[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D uint8[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D uint8[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D uint8[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D uint8[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D uint8[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D uint8[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint8_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D uint8[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D fix64[[1]] cast (D uint8[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D uint8[[1]] cast (D uint16[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D uint16[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D uint16[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D uint16[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D uint16[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D uint16[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D uint16[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D uint16[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D uint16[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint16_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D uint16[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D fix64[[1]] cast (D uint16[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D uint8[[1]] cast (D uint32[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D uint32[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D uint32[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D uint32[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D uint32[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D uint32[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D uint32[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D uint32[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D uint32[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint32_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D uint32[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D fix64[[1]] cast (D uint32[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D uint8[[1]] cast (D uint64[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D uint64[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D uint64[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D uint64[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D uint64[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D uint64[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D uint64[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D uint64[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D uint64[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_uint64_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D uint64[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D fix64[[1]] cast (D uint64[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D uint8[[1]] cast (D int8[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D int8[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D int8[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D int8[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D int8[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D int8[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D int8[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D int8[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D int8[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_int8_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D int8[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D fix64[[1]] cast (D int8[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D uint8[[1]] cast (D int16[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D int16[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D int16[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D int16[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D int16[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D int16[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D int16[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D int16[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D int16[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_int16_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D int16[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D fix64[[1]] cast (D int16[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D uint8[[1]] cast (D int32[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D int32[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D int32[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D int32[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D int32[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D int32[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D int32[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D int32[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D int32[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_int32_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D int32[[1]] x) {
    D uint32[[1]] xuint (size (x));
    __syscall ("shared3p::conv_int32_to_uint32_vec", __domainid (D), x, xuint);
    D uint32[[1]] shifts (size (x)) = 16;
    D fix32[[1]] res (size (x));
    __syscall ("shared3p::shift_left_uint32_vec", __domainid (D), xuint, shifts, res);
    return res;
}

template <domain D : shared3p>
D fix64[[1]] cast (D int32[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D uint8[[1]] cast (D int64[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D int64[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D int64[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D int64[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D int64[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D int64[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D int64[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D int64[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D int64[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_int64_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D int64[[1]] x) {
    return _toFixCastHelper (x);
}

template <domain D : shared3p>
D fix64[[1]] cast (D int64[[1]] x) {
    D uint64[[1]] xuint (size (x));
    __syscall ("shared3p::conv_int64_to_uint64_vec", __domainid (D), x, xuint);
    D uint64[[1]] shifts (size (x)) = 32;
    D fix64[[1]] res (size (x));
    __syscall ("shared3p::shift_left_uint64_vec", __domainid (D), xuint, shifts, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] cast (D float32[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D float32[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D float32[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D float32[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D float32[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D float32[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D float32[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D float32[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D float32[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_float64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D float32[[1]] x) {
    D fix32[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_fix32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix64[[1]] cast (D float32[[1]] x) {
    D float64[[1]] fl64 = (float64) x;
    D fix64[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_fix64_vec", __domainid (D), fl64, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] cast (D float64[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D float64[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D float64[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D float64[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int8[[1]] cast (D float64[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int16[[1]] cast (D float64[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int32[[1]] cast (D float64[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D int64[[1]] cast (D float64[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D float64[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D fix32[[1]] cast (D float64[[1]] x) {
    D float32[[1]] fl32 = (float32) x;
    D fix32[[1]] res (size (x));
    __syscall ("shared3p::conv_float32_to_fix32_vec", __domainid (D), fl32, res);
    return res;
}

template <domain D : shared3p>
D fix64[[1]] cast (D float64[[1]] x) {
    D fix64[[1]] res (size (x));
    __syscall ("shared3p::conv_float64_to_fix64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] cast (D bool[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] cast (D bool[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] cast (D bool[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] cast (D bool[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_bool_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint8[[1]] cast (D xor_uint8[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared3p::reshare_xor_uint8_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint16[[1]] cast (D xor_uint16[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared3p::reshare_xor_uint16_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint32[[1]] cast (D xor_uint32[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared3p::reshare_xor_uint32_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D uint64[[1]] cast (D xor_uint64[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared3p::reshare_xor_uint64_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] cast (D uint8[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::reshare_uint8_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] cast (D uint16[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::reshare_uint16_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] cast (D uint32[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::reshare_uint32_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] cast (D uint64[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::reshare_uint64_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] cast (D xor_uint8[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint8_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] cast (D xor_uint8[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint8_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] cast (D xor_uint8[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint8_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] cast (D xor_uint16[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint16_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] cast (D xor_uint16[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint16_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] cast (D xor_uint16[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint16_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] cast (D xor_uint32[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint32_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] cast (D xor_uint32[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint32_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint64[[1]] cast (D xor_uint32[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint32_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint8[[1]] cast (D xor_uint64[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint64_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint16[[1]] cast (D xor_uint64[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint64_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D xor_uint32[[1]] cast (D xor_uint64[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared3p::conv_xor_uint64_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float32[[1]] cast (D fix32[[1]] x) {
    D float32[[1]] res (size (x));
    __syscall ("shared3p::conv_fix32_to_float32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared3p>
D float64[[1]] cast (D fix32[[1]] x) {
    D float32[[1]] tmp (size (x));
    __syscall ("shared3p::conv_fix32_to_float32_vec", __domainid (D), x, tmp);
    return (float64) tmp;
}

template <domain D : shared3p>
D float32[[1]] cast (D fix64[[1]] x) {
    D float64[[1]] tmp (size (x));
    __syscall ("shared3p::conv_fix64_to_float64_vec", __domainid (D), x, tmp);
    return (float32) tmp;
}

template <domain D : shared3p>
D float64[[1]] cast (D fix64[[1]] x) {
    D float64[[1]] res (size (x));
    __syscall ("shared3p::conv_fix64_to_float64_vec", __domainid (D), x, res);
    return res;
}
/** \endcond */

/** @}*/
/** @}*/

