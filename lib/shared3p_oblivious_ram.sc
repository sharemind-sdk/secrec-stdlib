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

/**
* \cond
*/
module shared3p_oblivious_ram;

import shared3p;
import stdlib;
import shared3p_random;
import shared3p_sort;
/**
* \endcond
*/

/**
* @file
* \defgroup shared3p_oblivious_ram shared3p_oblivious_ram.sc
* \defgroup shared3p_oramPrepareRead oramPrepareRead
* \defgroup shared3p_oramPerformRead oramPerformRead
* \defgroup shared3p_oramPrepareWrite oramPrepareWrite
* \defgroup shared3p_oramPerformWrite oramPerformWrite
*/

/** \addtogroup shared3p_oblivious_ram
*@{
* @brief Module with functions for oblivious array lookup and write (ORAM)
* 
* These functions are useful for obliviously reading or writing by several secret indices or when reading from or writing 
to several vectors by secret indices.\n
* Note that both reading and writing consist of two functions: "prepare" (\ref shared3p_oramPrepareRead "oramPrepareRead" / 
* \ref shared3p_oramPrepareWrite "oramPrepareWrite") and the actual action (\ref shared3p_oramPerformRead "oramPerformRead" / 
* \ref shared3p_oramPerformWrite "oramPerformWrite").
* The `prepare*` functions are slower, but only require the secret indices vector, not the actual data vector to read from or 
* write to. Therefore, a single `prepare*` function can be used to invoke several read/write functions (using the same seed).
*
* If you want to obliviously read/write only a few values, use `*Lookup` and `*Update` functions from the 
* \ref oblivious "oblivious" module instead.
*/


/** \addtogroup shared3p_oramPrepareRead
 *  @{
 *  @brief Prepares a sort permutation for oblivious read function \ref shared3p_oramPerformRead "oramPerformRead".
 *  @note **D** - shared3p protection domain
 *  @note See supported types for \ref shared3p_oramPerformRead "oramPerformRead"
 *  @param srcLen - length of the vector to read from
 *  @param indices - indices (in the src vector) to read from
 *  @param shuffleSeed - random seed (32 bytes)
 *  @return sorting permutation, used as `sortperm` in \ref shared3p_oramPerformRead "oramPerformRead"
 *  @note Note that the output sorting permutation is a public value. Based on its input data, \ref shared3p_oramPrepareRead 
 *        "oramPrepareRead" computes a secret permutation that is represented as combination of a public output permutation 
 *        and a secret `shuffleSeed`. Therefore, the public permutation alone does not leak anything as it is masked by the 
 *        secret permutation. This works similarly to one-time-pad. However, for the same reason the `shuffleSeed` value must 
 *        not be reused for other `prepare*` (or \ref shuffle "shuffle") calls.
 *  @leakage{None}
 */

template <domain D : shared3p>
uint [[1]] oramPrepareRead(uint srcLen, D uint [[1]] indices, D uint8 [[1]] shuffleSeed) {
    uint[[1]] iota_vec = iota(srcLen+size(indices));
    D uint [[1]] t = cat(iota_vec[:srcLen], indices);
    D xor_uint64 [[1]] indc = iota_vec;
    D uint [[1]] shufT = shuffle(t, shuffleSeed);
    D xor_uint64 [[1]] shufIndc = shuffle(indc, shuffleSeed);
    return unsafeSort(shufT, shufIndc, true);
}
/** @}*/

/** \addtogroup shared3p_oramPerformRead
 *  @{
 *  @brief Obliviously reads values from source vector indicated by the indices vector.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint64" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int64"
 *  @param src - vector to read from
 *  @param shuffleSeed - random seed (32 bytes), has to be the same as used for \ref shared3p_oramPrepareRead "oramPrepareRead"
 *  @param sortperm - sorting permutation, output of \ref shared3p_oramPrepareRead "oramPrepareRead"
 *  @return Returns a new vector `result`, where `result[i] = src[indices[i]]`
 *  @note Given that <var>size(src) = m</var> and <var>size(indices) = n</var> then 
 *        \ref shared3p_oramPrepareRead "oramPrepareRead" works in <var>&Omicron;((m+n)log(m+n))</var>
 *        and \ref shared3p_oramPerformRead "oramPerformRead" in <var>&Omicron;(m+n)</var> time.
 *  @leakage{None}
 * */

/** \cond */
template <domain D : shared3p, type T>
D T [[1]] _oramPerformRead(D T [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    uint m = size(src);
    uint n = size(sortperm) - m;
    D T [[1]] srcPrime = invPrefixSum(src);
    D T [[1]] zeroes(n) = 0;
    D T [[1]] sortPermed(m+n);
    D T [[1]] afterFstShuffle = shuffle(cat(srcPrime, zeroes), shuffleSeed);
    __syscall("shared3p::gather_$T\_vec", __domainid(D), afterFstShuffle, sortPermed, __cref sortperm);
    D T [[1]] invSortPermed(m+n);
    D T [[1]] spps = prefixSum(sortPermed);
    __syscall("shared3p::scatter_$T\_vec", __domainid(D), spps, invSortPermed, __cref sortperm);
    D T [[1]] ivspps = inverseShuffle(invSortPermed, shuffleSeed);
    D T [[1]] res = _mkSlice(ivspps, m, m+n);
    return res;
}
/** \endcond */


template <domain D : shared3p>
D uint8 [[1]] oramPerformRead(D uint8 [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    return _oramPerformRead(src, shuffleSeed, sortperm);
}

template <domain D : shared3p>
D uint16 [[1]] oramPerformRead(D uint16 [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    return _oramPerformRead(src, shuffleSeed, sortperm);
}

template <domain D : shared3p>
D uint32 [[1]] oramPerformRead(D uint32 [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    return _oramPerformRead(src, shuffleSeed, sortperm);
}

template <domain D : shared3p>
D uint64 [[1]] oramPerformRead(D uint64 [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    return _oramPerformRead(src, shuffleSeed, sortperm);
}

template <domain D : shared3p>
D int8 [[1]] oramPerformRead(D int8 [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    return _oramPerformRead(src, shuffleSeed, sortperm);
}

template <domain D : shared3p>
D int16 [[1]] oramPerformRead(D int16 [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    return _oramPerformRead(src, shuffleSeed, sortperm);
}

template <domain D : shared3p>
D int32 [[1]] oramPerformRead(D int32 [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    return _oramPerformRead(src, shuffleSeed, sortperm);
}

template <domain D : shared3p>
D int64 [[1]] oramPerformRead(D int64 [[1]] src, D uint8 [[1]] shuffleSeed, uint [[1]] sortperm) {
    return _oramPerformRead(src, shuffleSeed, sortperm);
}
/** @}*/

/** \cond */
template <domain D : shared3p, type T>
D T [[1]] _mkSlice(D T [[1]] src, uint b, uint e) {
    D T [[1]] res(e-b);
    uint [[1]] inds = iota(e-b) + b;
    __syscall("shared3p::gather_$T\_vec", __domainid(D), src, res, __cref inds);
    return res;
}
/** \endcond */

/** \addtogroup shared3p_oramPrepareWrite
 *  @{
 *  @brief Prepares a sort permutation for oblivious write function \ref shared3p_oramPerformWrite "oramPerformWrite".
 *  @note **D** - shared3p protection domain
 *  @note See supported types for \ref shared3p_oramPerformWrite "oramPerformWrite"
 *  @param arrLen - length of the original values vector (`arr` in \ref shared3p_oramPerformWrite "oramPerformWrite")
 *  @param indices - vector specifying where to write the corresponding value from the replacement values vector (`vals` in \ref shared3p_oramPerformWrite "oramPerformWrite")
 *  @param shuffleSeed1 - random seed (32 bytes)
 *  @param shuffleSeed2 - random seed (32 bytes)
 *  @return sorting permutation, used as `sigmatau` in \ref shared3p_oramPerformWrite "oramPerformWrite"
 *  @note Note that the output sorting permutation is a public value. Based on its input data, \ref shared3p_oramPrepareWrite 
 *        "oramPrepareWrite" computes a secret permutation that is represented as combination of a public output permutation 
 *        and secret `shuffleSeed1` and `shuffleSeed2`. Therefore, the public permutation alone does not leak anything as it 
 *        is masked by the secret permutation. This works similarly to one-time-pad. However, for the same reason `shuffleSeed1`
 *        `shuffleSeed2` values must not be reused for other `prepare*` (or \ref shuffle "shuffle") calls.
 *  @leakage{None}
 */

template<domain D : shared3p>
uint [[1]] oramPrepareWrite(uint arrLen, D uint [[1]] indices, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2) {
    uint[[1]] iota_vec = iota(arrLen+size(indices));
    D uint [[1]] t = cat(indices, iota_vec[:arrLen]);  // Put indices before originals, so the priority of indices will be higher
    D xor_uint64 [[1]] indc = iota_vec;
    D uint [[1]] shufT = shuffle(t, shuffleSeed1);
    D xor_uint64 [[1]] shufIndc = shuffle(indc, shuffleSeed1);
    uint [[1]] sigma = unsafeSort(shufT, shufIndc, true);
    D uint [[1]] tprime(size(t));
    __syscall("shared3p::gather_uint64_vec", __domainid(D), shufT, tprime, __cref sigma);
    //uint [[1]] collleft = iota[0 : size(t)-1];
    //uint [[1]] collright = iota[1 : size(t)];
    D uint [[1]] t_is_diff = 1 - invPrefixSum(tprime);  // elements of t_is_diff are 0-s and 1-s
    t_is_diff[0] = 0;
    // now t_is_diff[i] == 0, if tprime[i] != tprime[i-1]. Otherwise, t_is_diff[i] == 1.
    uint [[1]] tau = _countsort(t_is_diff, shuffleSeed2);
    return cat(sigma, tau);
}
/** @}*/

/** \cond */
// Array v contains 0-s and 1-s. The sort will put 0-s before 1-s, stably
template<domain D : shared3p, type T>
T [[1]] _countsort(D T [[1]] v, D uint8 [[1]] shuffleSeed) {
    D T [[1]] w = 1 - v;
    D T [[1]] ps = prefixSum(cat(w, v));
    D T [[1]] psw = _mkSlice(ps, 0u64, size(v));
    D T [[1]] psv = _mkSlice(ps, size(v), 2*size(v));
    D T [[1]] pos = psw + v * (psv - psw);
    return declassify(shuffle(pos, shuffleSeed))-1;
}
/** \endcond */

/** \addtogroup shared3p_oramPerformWrite
 *  @{
 *  @brief Obliviously replaces (some) values in the original values vector with those contained in the replacement values vector.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint64" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int64"
 *  @param arr - original values vector
 *  @param vals - replacement values vector
 *  @param shuffleSeed1 - random seed (32 bytes), has to be the same as used for \ref shared3p_oramPrepareWrite "oramPrepareWrite"
 *  @param shuffleSeed2 - random seed (32 bytes), has to be the same as used for \ref shared3p_oramPrepareWrite "oramPrepareWrite"
 *  @param sigmatau - sorting permutation, output of \ref shared3p_oramPrepareWrite "oramPrepareWrite"
 *  @return Returns a copy of `arr` , where `arr[indices[i]] = vals[i]`
 *  @note Given that <var>size(arr) = m</var> and <var>size(indices) = n</var> then 
 *        \ref shared3p_oramPrepareWrite "oramPrepareWrite" works in <var>&Omicron;((m+n)log(m+n))</var>
 *        and \ref shared3p_oramPerformWrite "oramPerformWrite" in <var>&Omicron;(m+n)</var> time.
 *  @leakage{None}
 */

/** \cond */
template<domain D : shared3p, type T>
D T [[1]] _oramPerformWrite(D T [[1]] arr, D T [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    uint m = size(arr);
    uint n = size(vals);
    uint [[1]] sigma = sigmatau[0:(m+n)];
    uint [[1]] tau = sigmatau[(m+n) : 2*(m+n)];
    D T [[1]] bt1 = shuffle(cat(vals, arr), shuffleSeed1);
    D T [[1]] bt2(m+n);
    __syscall("shared3p::gather_$T\_vec", __domainid(D), bt1, bt2, __cref sigma);
    D T [[1]] bt3 = shuffle(bt2, shuffleSeed2);
    D T [[1]] bt4(m+n);
    __syscall("shared3p::scatter_$T\_vec", __domainid(D), bt3, bt4, __cref tau);
    return _mkSlice(bt4, 0u64, m);
}
/** \endcond */

template<domain D : shared3p>
D uint8 [[1]] oramPerformWrite(D uint8 [[1]] arr, D uint8 [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    return _oramPerformWrite(arr, vals, shuffleSeed1, shuffleSeed2, sigmatau);
}

template<domain D : shared3p>
D uint16 [[1]] oramPerformWrite(D uint16 [[1]] arr, D uint16 [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    return _oramPerformWrite(arr, vals, shuffleSeed1, shuffleSeed2, sigmatau);
}

template<domain D : shared3p>
D uint32 [[1]] oramPerformWrite(D uint32 [[1]] arr, D uint32 [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    return _oramPerformWrite(arr, vals, shuffleSeed1, shuffleSeed2, sigmatau);
}

template<domain D : shared3p>
D uint64 [[1]] oramPerformWrite(D uint64 [[1]] arr, D uint64 [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    return _oramPerformWrite(arr, vals, shuffleSeed1, shuffleSeed2, sigmatau);
}

template<domain D : shared3p>
D int8 [[1]] oramPerformWrite(D int8 [[1]] arr, D int8 [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    return _oramPerformWrite(arr, vals, shuffleSeed1, shuffleSeed2, sigmatau);
}

template<domain D : shared3p>
D int16 [[1]] oramPerformWrite(D int16 [[1]] arr, D int16 [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    return _oramPerformWrite(arr, vals, shuffleSeed1, shuffleSeed2, sigmatau);
}

template<domain D : shared3p>
D int32 [[1]] oramPerformWrite(D int32 [[1]] arr, D int32 [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    return _oramPerformWrite(arr, vals, shuffleSeed1, shuffleSeed2, sigmatau);
}

template<domain D : shared3p>
D int64 [[1]] oramPerformWrite(D int64 [[1]] arr, D int64 [[1]] vals, D uint8 [[1]] shuffleSeed1, D uint8 [[1]] shuffleSeed2, uint [[1]] sigmatau) {
    return _oramPerformWrite(arr, vals, shuffleSeed1, shuffleSeed2, sigmatau);
}
/** @}*/

/** @}*/
