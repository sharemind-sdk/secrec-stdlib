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

module shared2p;

import stdlib;

kind shared2p {
    type bool;
    type uint8;
    type uint16;
    type uint32;
    type uint64;
    type int8;
    type int16;
    type int32;
    type int64;
    type xor_uint8 { public = uint8 };
    type xor_uint16 { public = uint16 };
    type xor_uint32 { public = uint32 };
    type xor_uint64 { public = uint64 };
}
/**
* \endcond
*/
/**
* @file
* \defgroup shared2p shared2p.sc
* \defgroup shared2p_sign sign
* \defgroup shared2p_abs abs
* \defgroup shared2p_sum sum
* \defgroup shared2p_sum_vec sum
* \defgroup shared2p_sum_k sum(parts)
* \defgroup shared2p_product product
* \defgroup shared2p_product_vec product
* \defgroup shared2p_product_k product(parts)
* \defgroup shared2p_any any
* \defgroup shared2p_all all
* \defgroup shared2p_trueprefixlength truePrefixLength
* \defgroup shared2p_min min
* \defgroup shared2p_min_vec min
* \defgroup shared2p_min_k min(parts)
* \defgroup shared2p_min_2 min(2 vectors)
* \defgroup shared2p_max max
* \defgroup shared2p_max_vec max
* \defgroup shared2p_max_k max(parts)
* \defgroup shared2p_max_2 max(2 vectors)
* \defgroup shared2p_argument argument
* \defgroup shared2p_publish publish
* \defgroup shared2p_bit_extract bit extraction
* \defgroup shared2p_reshare reshare
* \defgroup shared2p_choose choose
*/

/** \addtogroup shared2p
*@{
* @brief Module with shared2p protection domain functions
*/



/*******************************
    classify
********************************/
/**
* \cond
*/
template <domain D : shared2p, type T, dim N>
D T[[N]] classify(T[[N]] value) {
    D T[[N]] out;
    __syscall("shared2p::classify_$T\_vec", __domainid(D), __cref value, out);
    return out;
}
/**
* \endcond
*/

/*******************************
    sign, abs
********************************/

/** \addtogroup shared2p_sign
 *  @{
 *  @brief Function for determining the sign of values
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param x - an array of any dimension
 *  @return returns an array of equal shape, size and dimension, where -1 signifies that, in the input array at that position was a negative number and 1  that it was a positive number
 */

template <domain D : shared2p, dim N>
D int8[[N]] sign (D int8[[N]] x) {
    __syscall ("shared2p::sign_int8_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared2p, dim N>
D int16[[N]] sign (D int16[[N]] x) {
    __syscall ("shared2p::sign_int16_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared2p, dim N>
D int32[[N]] sign (D int32[[N]] x) {
    __syscall ("shared2p::sign_int32_vec", __domainid (D), x, x);
    return x;
}

template <domain D : shared2p, dim N>
D int[[N]] sign (D int[[N]] x) {
    __syscall ("shared2p::sign_int64_vec", __domainid (D), x, x);
    return x;
}

/** @}*/
/** \addtogroup shared2p_abs
 *  @{
 *  @brief Function for finding absolute values
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param x - an array of any dimension
 *  @return returns an array of equal shape, size and dimension, where all values are the absolute values of the input array at that position
 */

template <domain D : shared2p, dim N>
D uint8[[N]] abs (D int8[[N]] x) {
    D uint8[[N]] y;
    y = (uint8) x;
    __syscall ("shared2p::abs_int8_vec", __domainid (D), x, y);
    return y;
}

template <domain D : shared2p, dim N>
D uint16[[N]] abs (D int16[[N]] x) {
    D uint16[[N]] y;
    y = (uint16) x;
    __syscall ("shared2p::abs_int16_vec", __domainid (D), x, y);
    return y;
}

template <domain D : shared2p, dim N>
D uint32[[N]] abs (D int32[[N]] x) {
    D uint32[[N]] y;
    y = (uint32) x;
    __syscall ("shared2p::abs_int32_vec", __domainid (D), x, y);
    return y;
}

template <domain D : shared2p, dim N>
D uint[[N]] abs (D int[[N]] x) {
    D uint[[N]] y;
    y = (uint) x;
    __syscall ("shared2p::abs_int64_vec", __domainid (D), x, y);
    return y;
}

/** @}*/

/*******************************
    sum
********************************/
/** \addtogroup shared2p_sum
 *  @{
 *  @brief Functions for finding sums
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref bool "bool" / \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 */

/** \addtogroup shared2p_sum_vec
 *  @{
 *  @brief Function for finding the sum of all the elements in a vector
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref bool "bool" / \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @note We are using a system call for summing vectors as it's very common
 *  operation, and the performance overhead of manually summing is in the
 *  range of 100 to 200 times slower.
 *  @param vec - a 1-dimensional array
 *  @returns the sum of all input vector elements
 */


/**
* @note boolean values are converted to numerical values and then added, for more info click \ref bool "here"
* @return returns the sum of all the elements in the input vector as an \ref uint "uint64" type value
*/
template <domain D : shared2p>
D uint sum (D bool[[1]] vec) {
    D uint out;
    __syscall ("shared2p::sum_bool_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint8 sum (D uint8[[1]] vec) {
    D uint8 out;
    __syscall ("shared2p::sum_uint8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint16 sum (D uint16[[1]] vec) {
    D uint16 out;
    __syscall ("shared2p::sum_uint16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint32 sum (D uint32[[1]] vec) {
    D uint32 out;
    __syscall ("shared2p::sum_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint sum (D uint[[1]] vec) {
    D uint out;
    __syscall ("shared2p::sum_uint64_vec", __domainid (D), vec, out);
    return out;
}


template <domain D : shared2p>
D int8 sum (D int8[[1]] vec) {
    D int8 out;
    __syscall ("shared2p::sum_int8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int16 sum (D int16[[1]] vec) {
    D int16 out;
    __syscall ("shared2p::sum_int16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int32 sum (D int32[[1]] vec) {
    D int32 out;
    __syscall ("shared2p::sum_int32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int sum (D int[[1]] vec) {
    D int out;
    __syscall ("shared2p::sum_int64_vec", __domainid (D), vec, out);
    return out;
}

/** @}*/

/** \addtogroup shared2p_sum_k
 *  @{
 *  @brief Function for finding the sum of all elements in the input vector in specified parts.
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref bool "bool" / \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @pre the length of the input array must be a multiple of **k**
 *  @param vec - The input array of subarrays to sum. The subarrays are stacked one after another and are all of the same size.
 *  @param k - The number of subarrays in the input array.
 *  @return The array of subarrayCount number of sums, each corresponding to respective subarray in the input array **vec**.
 */


template <domain D : shared2p>
D uint[[1]] sum (D bool[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared2p::sum_bool_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint8[[1]] sum (D uint8[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint8[[1]] out (k);
    __syscall ("shared2p::sum_uint8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint16[[1]] sum (D uint16[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint16[[1]] out (k);
    __syscall ("shared2p::sum_uint16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint32[[1]] sum (D uint32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint32[[1]] out (k);
    __syscall ("shared2p::sum_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint[[1]] sum (D uint[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared2p::sum_uint64_vec", __domainid (D), vec, out);
    return out;
}


template <domain D : shared2p>
D int8[[1]] sum (D int8[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int8[[1]] out (k);
    __syscall ("shared2p::sum_int8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int16[[1]] sum (D int16[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int16[[1]] out (k);
    __syscall ("shared2p::sum_int16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int32[[1]] sum (D int32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int32[[1]] out (k);
    __syscall ("shared2p::sum_int32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int[[1]] sum (D int[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int[[1]] out (k);
    __syscall ("shared2p::sum_int64_vec", __domainid (D), vec, out);
    return out;
}

/** @}*/
/** @}*/

/*******************************
    product
********************************/
/** \addtogroup shared2p_product
 *  @{
 *  @brief Functions for finding products
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 */


/** \addtogroup shared2p_product_vec
 *  @{
 *  @brief Function for finding the product of the input vector
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param x - input of supported type
 *  @return The product of the input vector
 */

template <domain D : shared2p, type T>
D T product (D T x) {
    return x;
}

template <domain D : shared2p>
D uint8 product (D uint8[[1]] x) {
    D uint8 out;
    __syscall ("shared2p::product_uint8_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared2p>
D uint16 product (D uint16[[1]] x) {
    D uint16 out;
    __syscall ("shared2p::product_uint16_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared2p>
D uint32 product (D uint32[[1]] x) {
    D uint32 out;
    __syscall ("shared2p::product_uint32_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared2p>
D uint product (D uint[[1]] x) {
    D uint out;
    __syscall ("shared2p::product_uint64_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared2p>
D int8 product (D int8[[1]] x) {
    D int8 out;
    __syscall ("shared2p::product_int8_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared2p>
D int16 product (D int16[[1]] x) {
    D int16 out;
    __syscall ("shared2p::product_int16_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared2p>
D int32 product (D int32[[1]] x) {
    D int32 out;
    __syscall ("shared2p::product_int32_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared2p>
D int product (D int[[1]] x) {
    D int out;
    __syscall ("shared2p::product_int64_vec", __domainid (D), x, out);
    return out;
}

/** @}*/
/** \addtogroup shared2p_product_k
 *  @{
 *  @brief Function for finding the product of all elements in the input vector in specified parts.
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @pre the length of the input array must be a multiple of **k**
 *  @param vec - The input array of subarrays to find the product of. The subarrays are stacked one after another and are all of the same size.
 *  @param k - The number of subarrays in the input array.
 *  @return The array of subarrayCount number of products, each corresponding to respective subarray in the input array **vec**.
 */

template <domain D : shared2p>
D uint8[[1]] product (D uint8[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint8[[1]] out (k);
    __syscall ("shared2p::product_uint8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint16[[1]] product (D uint16[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint16[[1]] out (k);
    __syscall ("shared2p::product_uint16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint32[[1]] product (D uint32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint32[[1]] out (k);
    __syscall ("shared2p::product_uint32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D uint[[1]] product (D uint[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared2p::product_uint64_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int8[[1]] product (D int8[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int8[[1]] out (k);
    __syscall ("shared2p::product_int8_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int16[[1]] product (D int16[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int16[[1]] out (k);
    __syscall ("shared2p::product_int16_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int32[[1]] product (D int32[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int32[[1]] out (k);
    __syscall ("shared2p::product_int32_vec", __domainid (D), vec, out);
    return out;
}

template <domain D : shared2p>
D int[[1]] product (D int[[1]] vec, uint k) {
    assert(k > 0 && size(vec) % k == 0);
    D int[[1]] out (k);
    __syscall ("shared2p::product_int64_vec", __domainid (D), vec, out);
    return out;
}

/** @}*/
/** @}*/


/*******************************
    any, all
********************************/


/** \addtogroup shared2p_any
 *  @{
 *  @brief Function for checking if any value of the input vector is true
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref bool "bool"
 *  @return true if any of the input bits is set
 *  @return false if all input bits are not set
 *  @note performs one vectorized cast, and one comparison against zero
 */

/**
* @param b - scalar boolean
*/
template <domain D : shared2p>
D bool any (D bool b) {
    return b;
}

/**
* @param vec - boolean 1-dimensional vector
*/
template <domain D : shared2p>
D bool any (D bool[[1]] vec) {
    return sum (vec) != 0;
}

/**
* @param vec - boolean 1-dimensional vector
* @param k - an \ref uint64 "uint" type value that shows in how many subarrays must **any** be found
*/
template <domain D : shared2p>
D bool[[1]] any (D bool[[1]] vec, uint k) {
    return sum (vec, k) != 0;
}

/**
* @param arr - boolean any dimension vector
*/
template <domain D : shared2p, dim N>
D bool any (D bool[[N]] arr) {
    return any(flatten(arr));
}
/** @}*/

/** \addtogroup shared2p_all
 *  @{
 *  @brief Function for checking if all values of the input vector are true
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref bool "bool"
 *  @return true if all of the input bits are set
 *  @return false if any input bit is not set
 *  @note performs one vectorized cast, and one comparison against length of the vector
 */

/**
* @param b - scalar boolean
*/
template <domain D : shared2p>
D bool all (D bool b) {
    return b;
}

/**
* @param arr - boolean 1-dimensional vector
*/
template <domain D : shared2p>
D bool all (D bool [[1]] arr) {
    uint n = size (arr);
    return sum (arr) == n;
}

/**
* @param vec - boolean 1-dimensional vector
* @param k - an \ref uint64 "uint" type value that shows in how many subarrays must **all** be found
*/
template <domain D : shared2p>
D bool[[1]] all (D bool[[1]] vec, uint k) {
    uint n = size(vec);
    assert(k > 0 && n % k == 0);
    uint len = n / k;
    return sum (vec, k) == len;
}

/**
* @param arr - boolean any dimension vector
*/
template <domain D : shared2p, dim N>
D bool all (D bool[[N]] arr) {
    return all(flatten(arr));
}
/** @}*/

/*******************************
    truePrefixLength
********************************/

/** \addtogroup shared2p_trueprefixlength
 *  @{
 *  @brief Function for finding how many elements from the start of a vector are true
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref bool "bool"
 *  @returns the number of set bits in the longest constant true prefix of the input
 *  @note this function performs log n multiplications on vectors of at most size n this is more efficient than performing n multiplications on scalars
 * \todo i think this can be further optimized
 */

/**
* @param arr - boolean 1-dimensional vector
*/
template <domain D : shared2p>
D uint truePrefixLength (D bool [[1]] arr) {
    for (uint shift = 1, n = size (arr); shift < n; shift *= 2) {
        arr[shift:] = arr[shift:] & arr[:n-shift];
    }

    return sum (arr);
}

/** @}*/

/*******************************
    Min, max
********************************/

/** \addtogroup shared2p_min
 *  @{
 *  @brief Functions for finding the minimum value
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 */

/** \addtogroup shared2p_min_vec
 *  @{
 *  @brief Function for finding the minimum element of the input vector.
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param x - input
 *  @returns minimum element of the input vector.
 *  @pre input vector is not empty
 */

template <domain D : shared2p, type T>
D T min (D T x) {
    return x;
}

template <domain D : shared2p>
D uint8 min (D uint8[[1]] x) {
    assert (size(x) > 0);
    D uint8 out;
    __syscall ("shared2p::vecmin_uint8_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D uint16 min (D uint16[[1]] x) {
    assert (size(x) > 0);
    D uint16 out;
    __syscall ("shared2p::vecmin_uint16_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D uint32 min (D uint32[[1]] x) {
    assert (size(x) > 0);
    D uint32 out;
    __syscall ("shared2p::vecmin_uint32_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D uint min (D uint[[1]] x) {
    assert (size(x) > 0);
    D uint out;
    __syscall ("shared2p::vecmin_uint64_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D int8 min (D int8[[1]] x) {
    assert (size(x) > 0);
    D uint8[[1]] in = (uint8) x + 128;
    D uint8 out;
    __syscall ("shared2p::vecmin_uint8_vec", __domainid (D), in, out);
    out -= 128;
    return (int8) out;
}
template <domain D : shared2p>
D int16 min (D int16[[1]] x) {
    assert (size(x) > 0);
    D uint16[[1]] in = (uint16) x + 32768;
    D uint16 out;
    __syscall ("shared2p::vecmin_uint16_vec", __domainid (D), in, out);
    out -= 32768;
    return (int16)out;
}
template <domain D : shared2p>
D int32 min (D int32[[1]] x) {
    assert (size(x) > 0);
    D uint32[[1]] in = (uint32) x + 2147483648;
    D uint32 out;
    __syscall ("shared2p::vecmin_uint32_vec", __domainid (D), in, out);
    out -= 2147483648;
    return (int32)out;
}
template <domain D : shared2p>
D int min (D int[[1]] x) {
    assert (size(x) > 0);
    D uint[[1]] in = (uint) x + 9223372036854775808;
    D uint out;
    __syscall ("shared2p::vecmin_uint64_vec", __domainid (D), in, out);
    out -= 9223372036854775808;
    return (int)out;
}

template <domain D : shared2p>
D xor_uint8 min (D xor_uint8[[1]] x) {
    assert (size(x) > 0);
    D xor_uint8 out;
    __syscall ("shared2p::vecmin_xor_uint8_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D xor_uint16 min (D xor_uint16[[1]] x) {
    assert (size(x) > 0);
    D xor_uint16 out;
    __syscall ("shared2p::vecmin_xor_uint16_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D xor_uint32 min (D xor_uint32[[1]] x) {
    assert (size(x) > 0);
    D xor_uint32 out;
    __syscall ("shared2p::vecmin_xor_uint32_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D xor_uint64 min (D xor_uint64[[1]] x) {
    assert (size(x) > 0);
    D xor_uint64 out;
    __syscall ("shared2p::vecmin_xor_uint64_vec", __domainid (D), x, out);
    return out;
}

/** @}*/
/** \addtogroup shared2p_min_k
 *  @{
 *  @brief Function for finding the minimum element in the specified parts of the vector.
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param x - input vector
 *  @param k - an \ref uint64 "uint" type value, which specifies into how many subarrays must the input array be divided
 *  @returns a vector with all the minimum elements of all the subarrays specified by k
 *  @pre input vector is not empty
 *  @pre the size of the input array is dividable by **k**
 */


template <domain D : shared2p>
D uint8[[1]] min (D uint8[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint8[[1]] out (k);
    __syscall ("shared2p::vecmin_uint8_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared2p>
D uint16[[1]] min (D uint16[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint16[[1]] out (k);
    __syscall ("shared2p::vecmin_uint16_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared2p>
D uint32[[1]] min (D uint32[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint32[[1]] out (k);
    __syscall ("shared2p::vecmin_uint32_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared2p>
D uint[[1]] min (D uint[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared2p::vecmin_uint64_vec", __domainid (D), x, out);
    return out;
}


template <domain D : shared2p>
D int8[[1]] min (D int8[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint8[[1]] in = (uint8) x + 128;
    D uint8[[1]] out (k);
    __syscall ("shared2p::vecmin_uint8_vec", __domainid (D), in, out);
    out -= 128;
    return (int8) out;
}
template <domain D : shared2p>
D int16[[1]] min (D int16[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint16[[1]] in = (uint16) x + 32768;
    D uint16[[1]] out (k);
    __syscall ("shared2p::vecmin_uint16_vec", __domainid (D), in, out);
    out -= 32768;
    return (int16)out;
}
template <domain D : shared2p>
D int32[[1]] min (D int32[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint32[[1]] in = (uint32) x + 2147483648;
    D uint32[[1]] out (k);
    __syscall ("shared2p::vecmin_uint32_vec", __domainid (D), in, out);
    out -= 2147483648;
    return (int32)out;
}
template <domain D : shared2p>
D int[[1]] min (D int[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint[[1]] in = (uint) x + 9223372036854775808;
    D uint[[1]] out (k);
    __syscall ("shared2p::vecmin_uint64_vec", __domainid (D), in, out);
    out -= 9223372036854775808;
    return (int)out;
}

/** @}*/
/** \addtogroup shared2p_min_2
 *  @{
 *  @brief Function for finding the pointwise minimum of 2 arrays
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param x - first argument
 *  @param y - second argument
 *  @returns an array with the pointwise minimum of each element in the two input vectors
 *  @pre both input vectors are of equal length
 */

template <domain D : shared2p>
D uint8 min (D uint8 x, D uint8 y) {
    __syscall ("shared2p::min_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint16 min (D uint16 x, D uint16 y) {
    __syscall ("shared2p::min_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint32 min (D uint32 x, D uint32 y) {
    __syscall ("shared2p::min_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint min (D uint x, D uint y) {
    __syscall ("shared2p::min_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D int8 min (D int8 x, D int8 y) {
    D uint8 in1 = (uint8) x + 128;
    D uint8 in2 = (uint8) y + 128;
    __syscall ("shared2p::min_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared2p>
D int16 min (D int16 x, D int16 y) {
    D uint16 in1 = (uint16) x + 32768;
    D uint16 in2 = (uint16) y + 32768;
    __syscall ("shared2p::min_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared2p>
D int32 min (D int32 x, D int32 y) {
    D uint32 in1 = (uint32) x + 2147483648;
    D uint32 in2 = (uint32) y + 2147483648;
    __syscall ("shared2p::min_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared2p>
D int min (D int x, D int y) {
    D uint in1 = (uint) x + 9223372036854775808;
    D uint in2 = (uint) y + 9223372036854775808;
    __syscall ("shared2p::min_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}

template <domain D : shared2p>
D uint8[[1]] min (D uint8[[1]] x, D uint8[[1]] y) {
    assert (size(x) == size(y));
    __syscall ("shared2p::min_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint16[[1]] min (D uint16[[1]] x, D uint16[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::min_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint32[[1]] min (D uint32[[1]] x, D uint32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::min_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint[[1]] min (D uint[[1]] x, D uint[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::min_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D int8[[1]] min (D int8[[1]] x, D int8[[1]] y) {
    assert (size (x) == size (y));
    D uint8[[1]] in1 = (uint8) x + 128;
    D uint8[[1]] in2 = (uint8) y + 128;
    __syscall ("shared2p::min_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared2p>
D int16[[1]] min (D int16[[1]] x, D int16[[1]] y) {
    assert (size (x) == size (y));
    D uint16[[1]] in1 = (uint16) x + 32768;
    D uint16[[1]] in2 = (uint16) y + 32768;
    __syscall ("shared2p::min_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared2p>
D int32[[1]] min (D int32[[1]] x, D int32[[1]] y) {
    assert (size (x) == size (y));
    D uint32[[1]] in1 = (uint32) x + 2147483648;
    D uint32[[1]] in2 = (uint32) y + 2147483648;
    __syscall ("shared2p::min_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared2p>
D int[[1]] min (D int[[1]] x, D int[[1]] y) {
    assert (size (x) == size (y));
    D uint[[1]] in1 = (uint) x + 9223372036854775808;
    D uint[[1]] in2 = (uint) y + 9223372036854775808;
    __syscall ("shared2p::min_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}

template <domain D : shared2p, dim N>
D uint8[[N]] min (D uint8[[N]] x, D uint8[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::min_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D uint16[[N]] min (D uint16[[N]] x, D uint16[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::min_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D uint32[[N]] min (D uint32[[N]] x, D uint32[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::min_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D uint[[N]] min (D uint[[N]] x, D uint[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::min_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D int8[[N]] min (D int8[[N]] x, D int8[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint8[[N]] in1 = (uint8) x + 128;
    D uint8[[N]] in2 = (uint8) y + 128;
    __syscall ("shared2p::min_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared2p, dim N>
D int16[[N]] min (D int16[[N]] x, D int16[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint16[[N]] in1 = (uint16) x + 32768;
    D uint16[[N]] in2 = (uint16) y + 32768;
    __syscall ("shared2p::min_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared2p, dim N>
D int32[[N]] min (D int32[[N]] x, D int32[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint32[[N]] in1 = (uint32) x + 2147483648;
    D uint32[[N]] in2 = (uint32) y + 2147483648;
    __syscall ("shared2p::min_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared2p, dim N>
D int[[N]] min (D int[[N]] x, D int[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint[[N]] in1 = (uint) x + 9223372036854775808;
    D uint[[N]] in2 = (uint) y + 9223372036854775808;
    __syscall ("shared2p::min_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}

template <domain D : shared2p>
D xor_uint8 min (D xor_uint8 x, D xor_uint8 y) {
    __syscall ("shared2p::min_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint16 min (D xor_uint16 x, D xor_uint16 y) {
    __syscall ("shared2p::min_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint32 min (D xor_uint32 x, D xor_uint32 y) {
    __syscall ("shared2p::min_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint64 min (D xor_uint64 x, D xor_uint64 y) {
    __syscall ("shared2p::min_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared2p>
D xor_uint8[[1]] min (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    assert (size(x) == size(y));
    __syscall ("shared2p::min_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint16[[1]] min (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::min_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint32[[1]] min (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::min_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint64[[1]] min (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::min_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared2p, dim N>
D xor_uint8[[N]] min (D xor_uint8[[N]] x, D xor_uint8[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::min_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D xor_uint16[[N]] min (D xor_uint16[[N]] x, D xor_uint16[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::min_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D xor_uint32[[N]] min (D xor_uint32[[N]] x, D xor_uint32[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::min_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D xor_uint64[[N]] min (D xor_uint64[[N]] x, D xor_uint64[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::min_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

/** @}*/
/** @}*/


/** \addtogroup shared2p_max
 *  @{
 *  @brief Functions for finding the maximum value
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 */

/** \addtogroup shared2p_max_vec
 *  @{
 *  @brief Function for finding the maximum element of the input vector.
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param x - input
 *  @returns maximum element of the input vector.
 *  @pre input vector is not empty
 */

template <domain D : shared2p, type T>
D T max (D T x) {
    return x;
}
template <domain D : shared2p>
D uint8 max (D uint8[[1]] x) {
    assert (size(x) > 0);
    D uint8 out;
    __syscall ("shared2p::vecmax_uint8_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D uint16 max (D uint16[[1]] x) {
    assert (size(x) > 0);
    D uint16 out;
    __syscall ("shared2p::vecmax_uint16_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D uint32 max (D uint32[[1]] x) {
    assert (size(x) > 0);
    D uint32 out;
    __syscall ("shared2p::vecmax_uint32_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D uint max (D uint[[1]] x) {
    assert (size(x) > 0);
    D uint out;
    __syscall ("shared2p::vecmax_uint64_vec", __domainid (D), x, out);
    return out;
}

template <domain D : shared2p>
D int8 max (D int8[[1]] x) {
    assert (size(x) > 0);
    D uint8[[1]] in = (uint8) x + 128;
    D uint8 out;
    __syscall ("shared2p::vecmax_uint8_vec", __domainid (D), in, out);
    out -= 128;
    return (int8) out;
}
template <domain D : shared2p>
D int16 max (D int16[[1]] x) {
    assert (size(x) > 0);
    D uint16[[1]] in = (uint16) x + 32768;
    D uint16 out;
    __syscall ("shared2p::vecmax_uint16_vec", __domainid (D), in, out);
    out -= 32768;
    return (int16)out;
}
template <domain D : shared2p>
D int32 max (D int32[[1]] x) {
    assert (size(x) > 0);
    D uint32[[1]] in = (uint32) x + 2147483648;
    D uint32 out;
    __syscall ("shared2p::vecmax_uint32_vec", __domainid (D), in, out);
    out -= 2147483648;
    return (int32)out;
}
template <domain D : shared2p>
D int max (D int[[1]] x) {
    assert (size(x) > 0);
    D uint[[1]] in = (uint) x + 9223372036854775808;
    D uint out;
    __syscall ("shared2p::vecmax_uint64_vec", __domainid (D), in, out);
    out -= 9223372036854775808;
    return (int)out;
}

template <domain D : shared2p>
D xor_uint8 max (D xor_uint8[[1]] x) {
    assert (size(x) > 0);
    D xor_uint8 out;
    __syscall ("shared2p::vecmax_xor_uint8_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D xor_uint16 max (D xor_uint16[[1]] x) {
    assert (size(x) > 0);
    D xor_uint16 out;
    __syscall ("shared2p::vecmax_xor_uint16_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D xor_uint32 max (D xor_uint32[[1]] x) {
    assert (size(x) > 0);
    D xor_uint32 out;
    __syscall ("shared2p::vecmax_xor_uint32_vec", __domainid (D), x, out);
    return out;
}
template <domain D : shared2p>
D xor_uint64 max (D xor_uint64[[1]] x) {
    assert (size(x) > 0);
    D xor_uint64 out;
    __syscall ("shared2p::vecmax_xor_uint64_vec", __domainid (D), x, out);
    return out;
}

/** @}*/
/** \addtogroup shared2p_max_k
 *  @{
 *  @brief Function for finding the maximum element in the specified parts of the vector.
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int"
 *  @param x - input vector
 *  @param k - an \ref uint64 "uint" type value, which specifies into how many subarrays must the input array be divided
 *  @returns a vector with all the maximum elements of all the subarrays specified by k
 *  @pre input vector is not empty
 *  @pre the size of the input array is dividable by **k**
 */



template <domain D : shared2p>
D uint8[[1]] max (D uint8[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint8[[1]] out (k);
    __syscall ("shared2p::vecmax_uint8_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared2p>
D uint16[[1]] max (D uint16[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint16[[1]] out (k);
    __syscall ("shared2p::vecmax_uint16_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared2p>
D uint32[[1]] max (D uint32[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint32[[1]] out (k);
    __syscall ("shared2p::vecmax_uint32_vec", __domainid (D), x, out);
    return out;
}



template <domain D : shared2p>
D uint[[1]] max (D uint[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint[[1]] out (k);
    __syscall ("shared2p::vecmax_uint64_vec", __domainid (D), x, out);
    return out;
}


template <domain D : shared2p>
D int8[[1]] max (D int8[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint8[[1]] in = (uint8) x + 128;
    D uint8[[1]] out (k);
    __syscall ("shared2p::vecmax_uint8_vec", __domainid (D), in, out);
    out -= 128;
    return (int8) out;
}
template <domain D : shared2p>
D int16[[1]] max (D int16[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint16[[1]] in = (uint16) x + 32768;
    D uint16[[1]] out (k);
    __syscall ("shared2p::vecmax_uint16_vec", __domainid (D), in, out);
    out -= 32768;
    return (int16)out;
}
template <domain D : shared2p>
D int32[[1]] max (D int32[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint32[[1]] in = (uint32) x + 2147483648;
    D uint32[[1]] out (k);
    __syscall ("shared2p::vecmax_uint32_vec", __domainid (D), in, out);
    out -= 2147483648;
    return (int32)out;
}
template <domain D : shared2p>
D int[[1]] max (D int[[1]] x, uint k) {
    uint n = size(x);
    assert(n > 0 && k > 0 && n % k == 0);
    D uint[[1]] in = (uint) x + 9223372036854775808;
    D uint[[1]] out (k);
    __syscall ("shared2p::vecmax_uint64_vec", __domainid (D), in, out);
    out -= 9223372036854775808;
    return (int)out;
}

/** @}*/
/** \addtogroup shared2p_max_2
 *  @{
 *  @brief Function for finding the pointwise maximum of 2 arrays
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param x - first argument
 *  @param y - second argument
 *  @returns an array with the pointwise maximum of each element in the two input vectors
 *  @pre both input vectors are of equal length
 */

template <domain D : shared2p>
D uint8 max (D uint8 x, D uint8 y) {
    __syscall ("shared2p::max_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint16 max (D uint16 x, D uint16 y) {
    __syscall ("shared2p::max_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint32 max (D uint32 x, D uint32 y) {
    __syscall ("shared2p::max_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint max (D uint x, D uint y) {
    __syscall ("shared2p::max_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D int8 max (D int8 x, D int8 y) {
    D uint8 in1 = (uint8) x + 128;
    D uint8 in2 = (uint8) y + 128;
    __syscall ("shared2p::max_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared2p>
D int16 max (D int16 x, D int16 y) {
    D uint16 in1 = (uint16) x + 32768;
    D uint16 in2 = (uint16) y + 32768;
    __syscall ("shared2p::max_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared2p>
D int32 max (D int32 x, D int32 y) {
    D uint32 in1 = (uint32) x + 2147483648;
    D uint32 in2 = (uint32) y + 2147483648;
    __syscall ("shared2p::max_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared2p>
D int max (D int x, D int y) {
    D uint in1 = (uint) x + 9223372036854775808;
    D uint in2 = (uint) y + 9223372036854775808;
    __syscall ("shared2p::max_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}

template <domain D : shared2p>
D uint8[[1]] max (D uint8[[1]] x, D uint8[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::max_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint16[[1]] max (D uint16[[1]] x, D uint16[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::max_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint32[[1]] max (D uint32[[1]] x, D uint32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::max_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D uint[[1]] max (D uint[[1]] x, D uint[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::max_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D int8[[1]] max (D int8[[1]] x, D int8[[1]] y) {
    assert (size (x) == size (y));
    D uint8[[1]] in1 = (uint8) x + 128;
    D uint8[[1]] in2 = (uint8) y + 128;
    __syscall ("shared2p::max_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared2p>
D int16[[1]] max (D int16[[1]] x, D int16[[1]] y) {
    assert (size (x) == size (y));
    D uint16[[1]] in1 = (uint16) x + 32768;
    D uint16[[1]] in2 = (uint16) y + 32768;
    __syscall ("shared2p::max_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared2p>
D int32[[1]] max (D int32[[1]] x, D int32[[1]] y) {
    assert (size (x) == size (y));
    D uint32[[1]] in1 = (uint32) x + 2147483648;
    D uint32[[1]] in2 = (uint32) y + 2147483648;
    __syscall ("shared2p::max_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared2p>
D int[[1]] max (D int[[1]] x, D int[[1]] y) {
    assert (size (x) == size (y));
    D uint[[1]] in1 = (uint) x + 9223372036854775808;
    D uint[[1]] in2 = (uint) y + 9223372036854775808;
    __syscall ("shared2p::max_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}

template <domain D : shared2p, dim N>
D uint8[[N]] max (D uint8[[N]] x, D uint8[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::max_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D uint16[[N]] max (D uint16[[N]] x, D uint16[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::max_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D uint32[[N]] max (D uint32[[N]] x, D uint32[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::max_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D uint[[N]] max (D uint[[N]] x, D uint[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::max_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D int8[[N]] max (D int8[[N]] x, D int8[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint8[[N]] in1 = (uint8) x + 128;
    D uint8[[N]] in2 = (uint8) y + 128;
    __syscall ("shared2p::max_uint8_vec", __domainid (D), in1, in2, in1);
    in1 -= 128;
    return (int8)in1;
}
template <domain D : shared2p, dim N>
D int16[[N]] max (D int16[[N]] x, D int16[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint16[[N]] in1 = (uint16) x + 32768;
    D uint16[[N]] in2 = (uint16) y + 32768;
    __syscall ("shared2p::max_uint16_vec", __domainid (D), in1, in2, in1);
    in1 -= 32768;
    return (int16)in1;
}
template <domain D : shared2p, dim N>
D int32[[N]] max (D int32[[N]] x, D int32[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint32[[N]] in1 = (uint32) x + 2147483648;
    D uint32[[N]] in2 = (uint32) y + 2147483648;
    __syscall ("shared2p::max_uint32_vec", __domainid (D), in1, in2, in1);
    in1 -= 2147483648;
    return (int32)in1;
}
template <domain D : shared2p, dim N>
D int[[N]] max (D int[[N]] x, D int[[N]] y) {
    assert(shapesAreEqual(x,y));
    D uint[[N]] in1 = (uint) x + 9223372036854775808;
    D uint[[N]] in2 = (uint) y + 9223372036854775808;
    __syscall ("shared2p::max_uint64_vec", __domainid (D), in1, in2, in1);
    in1 -= 9223372036854775808;
    return (int)in1;
}

template <domain D : shared2p>
D xor_uint8 max (D xor_uint8 x, D xor_uint8 y) {
    __syscall ("shared2p::max_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint16 max (D xor_uint16 x, D xor_uint16 y) {
    __syscall ("shared2p::max_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint32 max (D xor_uint32 x, D xor_uint32 y) {
    __syscall ("shared2p::max_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint64 max (D xor_uint64 x, D xor_uint64 y) {
    __syscall ("shared2p::max_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

template <domain D : shared2p>
D xor_uint8[[1]] max (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::max_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint16[[1]] max (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::max_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint32[[1]] max (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::max_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p>
D xor_uint64[[1]] max (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    assert (size (x) == size (y));
    __syscall ("shared2p::max_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D xor_uint8[[N]] max (D xor_uint8[[N]] x, D xor_uint8[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::max_xor_uint8_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D xor_uint16[[N]] max (D xor_uint16[[N]] x, D xor_uint16[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::max_xor_uint16_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D xor_uint32[[N]] max (D xor_uint32[[N]] x, D xor_uint32[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::max_xor_uint32_vec", __domainid (D), x, y, x);
    return x;
}
template <domain D : shared2p, dim N>
D xor_uint64[[N]] max (D xor_uint64[[N]] x, D xor_uint64[[N]] y) {
    assert(shapesAreEqual(x,y));
    __syscall ("shared2p::max_xor_uint64_vec", __domainid (D), x, y, x);
    return x;
}

/** @}*/
/** @}*/

/** \addtogroup shared2p_argument
 *  @{
 *  @brief Function for accessing the named program arguments of shared2p types.
 *  @note **T** - any \ref data_types "data" type
 *  @param name The name of the argument.
 *  @return returns the value associated with the argument specified by parameter name.
 */

/**
*  @return An argument of scalar type.
*/
template <domain D : shared2p, type T>
D T argument (string name) {
    uint num_bytes;
    __syscall("Process_argument", __cref name, __return num_bytes);
    uint8 [[1]] bytes (num_bytes);
    D T out;
    __syscall("Process_argument", __cref name, __ref bytes, __return num_bytes);
    __syscall ("shared2p::set_shares_$T\_vec", __domainid(D), out, __cref bytes);
    return out;
}

/**
*  @return An argument of 1-dimensional array type.
*/
template <domain D : shared2p, type T>
D T[[1]] argument (string name) {
    uint num_bytes, vector_size;
    __syscall("Process_argument", __cref name, __return num_bytes);
    uint8 [[1]] bytes (num_bytes);
    __syscall("Process_argument", __cref name, __ref bytes, __return num_bytes);
    __syscall ("shared2p::set_shares_$T\_vec", __domainid(D), __cref bytes, __return vector_size);
    D T [[1]] out (vector_size);
    __syscall ("shared2p::set_shares_$T\_vec", __domainid(D), out, __cref bytes);
    return out;
}

/** @}*/
/** \addtogroup shared2p_publish
 *  @{
 *  @brief Function for publishing the named values of shared2p types.
 *  @note **N** - any array size of any dimension
 *  @note **T** - any \ref data_types "data" type
 *  @param name The name of the argument.
 *  @param val the value to publish under the given name. Accepts scalars as well as arrays.
 */

template <domain D : shared2p, type T, dim N>
void publish (string name, D T[[N]] val) {
    uint num_bytes;
    __syscall ("shared2p::get_shares_$T\_vec", __domainid(D), val, __return num_bytes);
    uint8 [[1]] bytes (num_bytes);
    __syscall ("shared2p::get_shares_$T\_vec", __domainid(D), val, __ref bytes);
    __syscall("Process_setResult", __cref name, __cref "$D", __cref "$T", __cref bytes, 0::uint, num_bytes);
}
/** @}*/

/**
 * \addtogroup shared2p_bit_extract
 * @{
 * @brief Function for converting xor_uint(X) type value to the bit representation.
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @note The input is arbitrary dimensional array, output is flattened to one boolean vector. Reshape the result to get appropriate dimensionality.
 *  @param input - the input value to convert
 *  @return returns filattened vector of extracted bits
 */

template <domain D : shared2p, dim N>
D bool[[1]] bit_extract (D xor_uint8[[N]] input) {
    D bool[[1]] out (8 * size (input));
    __syscall ("shared2p::bit_extract_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p, dim N>
D bool[[1]] bit_extract (D xor_uint16[[N]] input) {
    D bool[[1]] out (16 * size (input));
    __syscall ("shared2p::bit_extract_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p, dim N>
D bool[[1]] bit_extract (D xor_uint32[[N]] input) {
    D bool[[1]] out (32 * size (input));
    __syscall ("shared2p::bit_extract_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p, dim N>
D bool[[1]] bit_extract (D xor_uint64[[N]] input) {
    D bool[[1]] out (64 * size (input));
    __syscall ("shared2p::bit_extract_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

/**
 * @}
 * \addtogroup shared2p_reshare
 * @{
 * @brief Function for converting uint(X) type values to xor_uint(X) and the other way around
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param input - the input value to convert
 *  @return returns a converted value from uint(X) -> xor_uint(X) or xor_uint(X) -> uint(X)
 */

template <domain D : shared2p>
D xor_uint8 reshare (D uint8 input) {
    D xor_uint8 out;
    __syscall ("shared2p::reshare_uint8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint8 reshare (D xor_uint8 input) {
    D uint8 out;
    __syscall ("shared2p::reshare_xor_uint8_to_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D xor_uint8 [[1]] reshare (D uint8[[1]] input) {
    D xor_uint8[[1]] out (size (input));
    __syscall ("shared2p::reshare_uint8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint8 [[1]] reshare (D xor_uint8[[1]] input) {
    D uint8[[1]] out (size (input));
    __syscall ("shared2p::reshare_xor_uint8_to_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D xor_uint8 [[2]] reshare (D uint8[[2]] input) {
    D xor_uint8[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared2p::reshare_uint8_to_xor_uint8_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint8 [[2]] reshare (D xor_uint8[[2]] input) {
    D uint8[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared2p::reshare_xor_uint8_to_uint8_vec", __domainid (D), input, out);
    return out;
}

/*****************************
*****************************/

template <domain D : shared2p>
D xor_uint16 reshare (D uint16 input) {
    D xor_uint16 out;
    __syscall ("shared2p::reshare_uint16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint16 reshare (D xor_uint16 input) {
    D uint16 out;
    __syscall ("shared2p::reshare_xor_uint16_to_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D xor_uint16 [[1]] reshare (D uint16[[1]] input) {
    D xor_uint16[[1]] out (size (input));
    __syscall ("shared2p::reshare_uint16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint16 [[1]] reshare (D xor_uint16[[1]] input) {
    D uint16[[1]] out (size (input));
    __syscall ("shared2p::reshare_xor_uint16_to_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D xor_uint16 [[2]] reshare (D uint16[[2]] input) {
    D xor_uint16[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared2p::reshare_uint16_to_xor_uint16_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint16 [[2]] reshare (D xor_uint16[[2]] input) {
    D uint16[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared2p::reshare_xor_uint16_to_uint16_vec", __domainid (D), input, out);
    return out;
}

/*****************************
*****************************/

template <domain D : shared2p>
D xor_uint32 reshare (D uint32 input) {
    D xor_uint32 out;
    __syscall ("shared2p::reshare_uint32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint32 reshare (D xor_uint32 input) {
    D uint32 out;
    __syscall ("shared2p::reshare_xor_uint32_to_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D xor_uint32 [[1]] reshare (D uint32[[1]] input) {
    D xor_uint32[[1]] out (size (input));
    __syscall ("shared2p::reshare_uint32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint32 [[1]] reshare (D xor_uint32[[1]] input) {
    D uint32[[1]] out (size (input));
    __syscall ("shared2p::reshare_xor_uint32_to_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D xor_uint32 [[2]] reshare (D uint32[[2]] input) {
    D xor_uint32[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared2p::reshare_uint32_to_xor_uint32_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint32 [[2]] reshare (D xor_uint32[[2]] input) {
    D uint32[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared2p::reshare_xor_uint32_to_uint32_vec", __domainid (D), input, out);
    return out;
}

/*****************************
*****************************/

template <domain D : shared2p>
D xor_uint64 reshare (D uint64 input) {
    D xor_uint64 out;
    __syscall ("shared2p::reshare_uint64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint64 reshare (D xor_uint64 input) {
    D uint64 out;
    __syscall ("shared2p::reshare_xor_uint64_to_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D xor_uint64 [[1]] reshare (D uint64[[1]] input) {
    D xor_uint64[[1]] out (size (input));
    __syscall ("shared2p::reshare_uint64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint64 [[1]] reshare (D xor_uint64[[1]] input) {
    D uint64[[1]] out (size (input));
    __syscall ("shared2p::reshare_xor_uint64_to_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D xor_uint64 [[2]] reshare (D uint64[[2]] input) {
    D xor_uint64[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared2p::reshare_uint64_to_xor_uint64_vec", __domainid (D), input, out);
    return out;
}

template <domain D : shared2p>
D uint64 [[2]] reshare (D xor_uint64[[2]] input) {
    D uint64[[2]] out (shape(input)[0],shape(input)[1]);
    __syscall ("shared2p::reshare_xor_uint64_to_uint64_vec", __domainid (D), input, out);
    return out;
}


/**
 * @}
 * \addtogroup shared2p_choose
 *  @{
 *  @brief Function for obliviously choosing pointwise from the inputs
 *  @note **D** - shared2p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param cond - a boolean vector
 *  @param first - values for **true** case
 *  @param second - values for **false** case
 *  @return pointwise check if **cond** at a certain position is **true** or **false**. if **true** the element of **first** at that position is returned else the element of **second** at that position is returned
 */

template <domain D : shared2p, dim N>
D xor_uint8[[N]] choose(D bool[[N]] cond, D xor_uint8[[N]] first, D xor_uint8[[N]] second) {
    D xor_uint8[[N]] out = first;
    __syscall ("shared2p::choose_xor_uint8_vec", __domainid (D), cond, first, second, out);
    return out;
}

template <domain D : shared2p, dim N>
D xor_uint16[[N]] choose(D bool[[N]] cond, D xor_uint16[[N]] first, D xor_uint16[[N]] second) {
    D xor_uint16[[N]] out = first;
    __syscall ("shared2p::choose_xor_uint16_vec", __domainid (D), cond, first, second, out);
    return out;
}

template <domain D : shared2p, dim N>
D xor_uint32[[N]] choose(D bool[[N]] cond, D xor_uint32[[N]] first, D xor_uint32[[N]] second) {
    D xor_uint32[[N]] out = first;
    __syscall ("shared2p::choose_xor_uint32_vec", __domainid (D), cond, first, second, out);
    return out;
}

template <domain D : shared2p, dim N>
D xor_uint64[[N]] choose(D bool[[N]] cond, D xor_uint64[[N]] first, D xor_uint64[[N]] second) {
    D xor_uint64[[N]] out = first;
    __syscall ("shared2p::choose_xor_uint64_vec", __domainid (D), cond, first, second, out);
    return out;
}

/** @}*/

/** \cond */

/**********************************************************************
 OPERATOR DEFINITIONS
**********************************************************************/

template <domain D : shared2p>
D bool[[1]] operator ! (D bool[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::not_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p, type T>
D bool[[1]] operator == (D T[[1]] x, D T[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::eq_$T\_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p, type T>
D bool[[1]] operator != (D T[[1]] x, D T[[1]] y) {
    return !(x == y);
}

template <domain D : shared2p>
D bool[[1]] operator < (D uint8[[1]] x, D uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D uint16[[1]] x, D uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D uint32[[1]] x, D uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D uint64[[1]] x, D uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D int8[[1]] x, D int8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D int16[[1]] x, D int16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D int32[[1]] x, D int32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D int64[[1]] x, D int64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator < (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lt_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D uint8[[1]] x, D uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D uint16[[1]] x, D uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D uint32[[1]] x, D uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D uint64[[1]] x, D uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D int8[[1]] x, D int8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D int16[[1]] x, D int16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D int32[[1]] x, D int32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D int64[[1]] x, D int64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator > (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gt_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D uint8[[1]] x, D uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D uint16[[1]] x, D uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D uint32[[1]] x, D uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D uint64[[1]] x, D uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D int8[[1]] x, D int8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D int16[[1]] x, D int16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D int32[[1]] x, D int32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D int64[[1]] x, D int64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator <= (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::lte_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D uint8[[1]] x, D uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D uint16[[1]] x, D uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D uint32[[1]] x, D uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D uint64[[1]] x, D uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D int8[[1]] x, D int8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D int16[[1]] x, D int16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D int32[[1]] x, D int32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D int64[[1]] x, D int64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator >= (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::gte_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] operator + (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::add_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] operator + (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::add_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] operator + (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::add_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] operator + (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::add_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] operator + (D int8[[1]] x, D int8[[1]] y) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::add_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] operator + (D int16[[1]] x, D int16[[1]] y) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::add_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] operator + (D int32[[1]] x, D int32[[1]] y) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::add_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] operator + (D int64[[1]] x, D int64[[1]] y) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::add_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] operator - (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::sub_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] operator - (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::sub_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] operator - (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::sub_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] operator - (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::sub_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] operator - (D int8[[1]] x, D int8[[1]] y) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::sub_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] operator - (D int16[[1]] x, D int16[[1]] y) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::sub_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] operator - (D int32[[1]] x, D int32[[1]] y) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::sub_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] operator - (D int64[[1]] x, D int64[[1]] y) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::sub_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] operator * (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::mul_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] operator * (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::mul_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] operator * (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::mul_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] operator * (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::mul_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] operator * (D int8[[1]] x, D int8[[1]] y) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::mul_int8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] operator * (D int16[[1]] x, D int16[[1]] y) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::mul_int16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] operator * (D int32[[1]] x, D int32[[1]] y) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::mul_int32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] operator * (D int64[[1]] x, D int64[[1]] y) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::mul_int64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] operator % (D uint8[[1]] x, D uint8[[1]] y) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::mod_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] operator % (D uint16[[1]] x, D uint16[[1]] y) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::mod_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] operator % (D uint32[[1]] x, D uint32[[1]] y) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::mod_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] operator % (D uint64[[1]] x, D uint64[[1]] y) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::mod_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator | (D bool[[1]] x, D bool[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::or_bool_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] operator | (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::or_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] operator | (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::or_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] operator | (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::or_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] operator | (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::or_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator & (D bool[[1]] x, D bool[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::and_bool_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] operator & (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::and_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] operator & (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::and_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] operator & (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::and_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] operator & (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::and_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] operator ^ (D bool[[1]] x, D bool[[1]] y) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::xor_bool_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] operator ^ (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::xor_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] operator ^ (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::xor_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] operator ^ (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::xor_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] operator ^ (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::xor_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] operator << (D xor_uint8[[1]] x, D xor_uint8[[1]] y) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::shift_left_xor_uint8_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] operator << (D xor_uint16[[1]] x, D xor_uint16[[1]] y) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::shift_left_xor_uint16_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] operator << (D xor_uint32[[1]] x, D xor_uint32[[1]] y) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::shift_left_xor_uint32_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] operator << (D xor_uint64[[1]] x, D xor_uint64[[1]] y) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::shift_left_xor_uint64_vec", __domainid (D), x, y, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] operator * (D uint8[[1]] x, uint8[[1]] y) {
    __syscall ("shared2p::mulc_uint8_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared2p>
D uint16[[1]] operator * (D uint16[[1]] x, uint16[[1]] y) {
    __syscall ("shared2p::mulc_uint16_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared2p>
D uint32[[1]] operator * (D uint32[[1]] x, uint32[[1]] y) {
    __syscall ("shared2p::mulc_uint32_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared2p>
D uint64[[1]] operator * (D uint64[[1]] x, uint64[[1]] y) {
    __syscall ("shared2p::mulc_uint64_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared2p>
D int8[[1]] operator * (D int8[[1]] x, int8[[1]] y) {
    __syscall ("shared2p::mulc_int8_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared2p>
D int16[[1]] operator * (D int16[[1]] x, int16[[1]] y) {
    __syscall ("shared2p::mulc_int16_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared2p>
D int32[[1]] operator * (D int32[[1]] x, int32[[1]] y) {
    __syscall ("shared2p::mulc_int32_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared2p>
D int64[[1]] operator * (D int64[[1]] x, int64[[1]] y) {
    __syscall ("shared2p::mulc_int64_vec", __domainid (D), x, __cref y, x);
    return x;
}

template <domain D : shared2p>
D uint8[[1]] operator * (uint8[[1]] x, D uint8[[1]] y) {
    __syscall ("shared2p::mulc_uint8_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared2p>
D uint16[[1]] operator * (uint16[[1]] x, D uint16[[1]] y) {
    __syscall ("shared2p::mulc_uint16_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared2p>
D uint32[[1]] operator * (uint32[[1]] x, D uint32[[1]] y) {
    __syscall ("shared2p::mulc_uint32_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared2p>
D uint64[[1]] operator * (uint64[[1]] x, D uint64[[1]] y) {
    __syscall ("shared2p::mulc_uint64_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared2p>
D int8[[1]] operator * (int8[[1]] x, D int8[[1]] y) {
    __syscall ("shared2p::mulc_int8_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared2p>
D int16[[1]] operator * (int16[[1]] x, D int16[[1]] y) {
    __syscall ("shared2p::mulc_int16_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared2p>
D int32[[1]] operator * (int32[[1]] x, D int32[[1]] y) {
    __syscall ("shared2p::mulc_int32_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared2p>
D int64[[1]] operator * (int64[[1]] x, D int64[[1]] y) {
    __syscall ("shared2p::mulc_int64_vec", __domainid (D), __cref x, y, y);
    return y;
}

template <domain D : shared2p>
D uint8[[1]] operator - (D uint8[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::neg_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] operator - (D uint16[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::neg_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] operator - (D uint32[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::neg_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] operator - (D uint64[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::neg_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] operator - (D int8[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::neg_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] operator - (D int16[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::neg_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] operator - (D int32[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::neg_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] operator - (D int64[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::neg_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] operator ~ (D xor_uint8[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::inv_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] operator ~ (D xor_uint16[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::inv_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] operator ~ (D xor_uint32[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::inv_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] operator ~ (D xor_uint64[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::inv_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] cast (D bool[[1]] x) { return x; }

template <domain D : shared2p>
D uint8[[1]] cast (D uint8[[1]] x) { return x; }

template <domain D : shared2p>
D uint16[[1]] cast (D uint16[[1]] x) { return x; }

template <domain D : shared2p>
D uint32[[1]] cast (D uint32[[1]] x) { return x; }

template <domain D : shared2p>
D uint64[[1]] cast (D uint64[[1]] x) { return x; }

template <domain D : shared2p>
D int8[[1]] cast (D int8[[1]] x) { return x; }

template <domain D : shared2p>
D int16[[1]] cast (D int16[[1]] x) { return x; }

template <domain D : shared2p>
D int32[[1]] cast (D int32[[1]] x) { return x; }

template <domain D : shared2p>
D int64[[1]] cast (D int64[[1]] x) { return x; }

template <domain D : shared2p>
D xor_uint8[[1]] cast (D xor_uint8[[1]] x) { return x; }

template <domain D : shared2p>
D xor_uint16[[1]] cast (D xor_uint16[[1]] x) { return x; }

template <domain D : shared2p>
D xor_uint32[[1]] cast (D xor_uint32[[1]] x) { return x; }

template <domain D : shared2p>
D xor_uint64[[1]] cast (D xor_uint64[[1]] x) { return x; }

template <domain D : shared2p>
D bool[[1]] cast (D uint8[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::conv_uint8_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] cast (D uint16[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::conv_uint16_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] cast (D uint32[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::conv_uint32_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] cast (D uint64[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::conv_uint64_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] cast (D int8[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::conv_int8_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] cast (D int16[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::conv_int16_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] cast (D int32[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::conv_int32_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D bool[[1]] cast (D int64[[1]] x) {
    D bool[[1]] res (size (x));
    __syscall ("shared2p::conv_int64_to_bool_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D bool[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D bool[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D bool[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D bool[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] cast (D bool[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] cast (D bool[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] cast (D bool[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] cast (D bool[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D uint8[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_uint8_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D uint8[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_uint8_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D uint8[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_uint8_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] cast (D uint8[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::conv_uint8_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] cast (D uint8[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::conv_uint8_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] cast (D uint8[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::conv_uint8_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] cast (D uint8[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::conv_uint8_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D uint16[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_uint16_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D uint16[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_uint16_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D uint16[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_uint16_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] cast (D uint16[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::conv_uint16_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] cast (D uint16[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::conv_uint16_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] cast (D uint16[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::conv_uint16_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] cast (D uint16[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::conv_uint16_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D uint32[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_uint32_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D uint32[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_uint32_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D uint32[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_uint32_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] cast (D uint32[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::conv_uint32_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] cast (D uint32[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::conv_uint32_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] cast (D uint32[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::conv_uint32_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] cast (D uint32[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::conv_uint32_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D uint64[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_uint64_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D uint64[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_uint64_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D uint64[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_uint64_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] cast (D uint64[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::conv_uint64_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] cast (D uint64[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::conv_uint64_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] cast (D uint64[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::conv_uint64_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] cast (D uint64[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::conv_uint64_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D int8[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_int8_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D int8[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_int8_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D int8[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_int8_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D int8[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_int8_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] cast (D int8[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::conv_int8_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] cast (D int8[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::conv_int8_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] cast (D int8[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::conv_int8_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D int16[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_int16_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D int16[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_int16_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D int16[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_int16_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D int16[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_int16_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] cast (D int16[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::conv_int16_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] cast (D int16[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::conv_int16_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] cast (D int16[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::conv_int16_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D int32[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_int32_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D int32[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_int32_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D int32[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_int32_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D int32[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_int32_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] cast (D int32[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::conv_int32_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] cast (D int32[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::conv_int32_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int64[[1]] cast (D int32[[1]] x) {
    D int64[[1]] res (size (x));
    __syscall ("shared2p::conv_int32_to_int64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D int64[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_int64_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D int64[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_int64_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D int64[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_int64_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D int64[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_int64_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int8[[1]] cast (D int64[[1]] x) {
    D int8[[1]] res (size (x));
    __syscall ("shared2p::conv_int64_to_int8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int16[[1]] cast (D int64[[1]] x) {
    D int16[[1]] res (size (x));
    __syscall ("shared2p::conv_int64_to_int16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D int32[[1]] cast (D int64[[1]] x) {
    D int32[[1]] res (size (x));
    __syscall ("shared2p::conv_int64_to_int32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] cast (D bool[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] cast (D bool[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] cast (D bool[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] cast (D bool[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_bool_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint8[[1]] cast (D xor_uint8[[1]] x) {
    D uint8[[1]] res (size (x));
    __syscall ("shared2p::reshare_xor_uint8_to_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint16[[1]] cast (D xor_uint16[[1]] x) {
    D uint16[[1]] res (size (x));
    __syscall ("shared2p::reshare_xor_uint16_to_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint32[[1]] cast (D xor_uint32[[1]] x) {
    D uint32[[1]] res (size (x));
    __syscall ("shared2p::reshare_xor_uint32_to_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D uint64[[1]] cast (D xor_uint64[[1]] x) {
    D uint64[[1]] res (size (x));
    __syscall ("shared2p::reshare_xor_uint64_to_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] cast (D uint8[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::reshare_uint8_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] cast (D uint16[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::reshare_uint16_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] cast (D uint32[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::reshare_uint32_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] cast (D uint64[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::reshare_uint64_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] cast (D xor_uint8[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint8_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] cast (D xor_uint8[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint8_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] cast (D xor_uint8[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint8_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] cast (D xor_uint16[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint16_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] cast (D xor_uint16[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint16_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] cast (D xor_uint16[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint16_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] cast (D xor_uint32[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint32_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] cast (D xor_uint32[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint32_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint64[[1]] cast (D xor_uint32[[1]] x) {
    D xor_uint64[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint32_to_xor_uint64_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint8[[1]] cast (D xor_uint64[[1]] x) {
    D xor_uint8[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint64_to_xor_uint8_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint16[[1]] cast (D xor_uint64[[1]] x) {
    D xor_uint16[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint64_to_xor_uint16_vec", __domainid (D), x, res);
    return res;
}

template <domain D : shared2p>
D xor_uint32[[1]] cast (D xor_uint64[[1]] x) {
    D xor_uint32[[1]] res (size (x));
    __syscall ("shared2p::conv_xor_uint64_to_xor_uint32_vec", __domainid (D), x, res);
    return res;
}

/** \endcond */

/** @}*/
/** @}*/
