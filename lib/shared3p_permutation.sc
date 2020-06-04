/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

/** \cond */
module shared3p_permutation;

import shared3p;
import shared3p_matrix;
import shared3p_random;
import stdlib;
/** \endcond */

/**
 * @file
 * \defgroup shared3p_permutation shared3p_permutation.sc
 * \defgroup shared3p_public_random_permutation publicRandomPermutation
 * \defgroup shared3p_private_random_permutation privateRandomPermutation
 * \defgroup shared3p_apply_public_permutation applyPublicPermutation
 * \defgroup shared3p_apply_public_permutation_rows applyPublicPermutationRows
 * \defgroup shared3p_apply_public_permutation_cols applyPublicPermutationCols
 * \defgroup shared3p_apply_private_permutation applyPrivatePermutation
 * \defgroup shared3p_apply_private_permutation_rows applyPrivatePermutationRows
 * \defgroup shared3p_apply_private_permutation_cols applyPrivatePermutationCols
 * \defgroup shared3p_unapply_public_permutation unapplyPublicPermutation
 * \defgroup shared3p_unapply_public_permutation_rows unapplyPublicPermutationRows
 */

/**
 * \addtogroup shared3p_permutation
 * @{
 * @brief Module with procedures for permuting vectors and matrices.
 */

/**
 * \addtogroup shared3p_public_random_permutation
 * @{
 * @brief Generates a random public permutation
 * @note **D** - shared3p protection domain
 * @note This procedure actually generates a `uint32` permutation
 * vector which is converted to `uint64`. It does not work when `n >
 * UINT32_MAX`.
 * @param domainProxy - a value used to indicate the shared3p domain
 * used for generating shared randomness by the procedure. The value
 * of this parameter is not important.
 * @param n - length of the permutation
 * @return a random permutation of length `n`
 */
template<domain D : shared3p>
uint[[1]] publicRandomPermutation(D uint domainProxy, uint n) {
    assert(n <= (uint) UINT32_MAX);
    uint32[[1]] pi(n);
    __syscall("shared3p::gen_rand_pub_perm", __domainid(D), __ref pi);
    return (uint) pi;
}
/** @} */

/**
 * \addtogroup shared3p_private_random_permutation
 * @{
 * @brief Generates a random private permutation
 * @note **D** - shared3p protection domain
 * @param n - length of the permutation
 * @return a random permutation of length n
 */
template<domain D : shared3p>
D uint[[1]] privateRandomPermutation(uint n) {
    D uint[[1]] pi = iota(n);
    return shuffle(pi);
}
/** @} */

/**
 * \addtogroup shared3p_apply_public_permutation
 * @{
 * @brief Permute a vector according to a public permutation
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref bool "bool" / \ref uint8 "uint8" /
 * \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" /
 * \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref
 * int64 "int" / \ref float32 "float32" / \ref float64 "float64" /
 * \ref fix32 "fix32" / \ref fix64 "fix64"
 * @param x - vector to be permuted
 * @param p - permutation. Output at index `i` will be `x[p[i]]`.
 * @return `x` permuted according to permutation `p`
 * @leakage{None}
 */
template<domain D : shared3p, type T>
D T[[1]] applyPublicPermutation(D T[[1]] x, uint[[1]] p) {
    assert(size(x) == size(p));
    uint n = size(p);
    uint[[1]] source = p;
    uint[[1]] target = iota(n);
    D T[[1]] y(n);
    y = _partialRearrange(x, y, source, target);
    return y;
}
/** @} */

/**
 * \addtogroup shared3p_apply_public_permutation_rows
 * @{
 * @brief Permute matrix rows according to a public permutation
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref bool "bool" / \ref uint8 "uint8" /
 * \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" /
 * \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref
 * int64 "int" / \ref float32 "float32" / \ref float64 "float64" /
 * \ref fix32 "fix32" / \ref fix64 "fix64"
 * @param X - matrix to be permuted
 * @param p - permutation. Output row at index `i` will be `X[p[i], :]`.
 * @return `X` where rows have been permuted according to permutation `p`
 * @leakage{None}
 */
template<domain D : shared3p, type T>
D T[[2]] applyPublicPermutationRows(D T[[2]] X, uint[[1]] p) {
    uint m = shape(p)[0];
    uint n = shape(X)[1];
    uint[[1]] source(m * n);
    uint[[1]] target = iota(m * n);
    for (uint i = 0; i < m; ++i) {
        for (uint j = 0; j < n; ++j) {
            source[i * n + j] = p[i] * shape(X)[1] + j;
        }
    }
    D T[[2]] Y(m, n);
    Y = _partialRearrange(X, Y, source, target);
    return Y;
}
/** @} */

/**
 * \addtogroup shared3p_apply_public_permutation_cols
 * @{
 * @brief Permute matrix columns according to a public permutation
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref bool "bool" / \ref uint8 "uint8" /
 * \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" /
 * \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref
 * int64 "int" / \ref float32 "float32" / \ref float64 "float64" /
 * \ref fix32 "fix32" / \ref fix64 "fix64"
 * @param X - matrix to be permuted
 * @param p - permutation. Output column at index `i` will be `X[:, p[i]]`.
 * @return `X` where columns have been permuted according to permutation `p`
 * @leakage{None}
 */
template<domain D : shared3p, type T>
D T[[2]] applyPublicPermutationCols(D T[[2]] X, uint[[1]] p) {
    uint m = shape(X)[0];
    uint n = shape(p)[0];
    uint[[1]] source (m * n);
    uint[[1]] target = iota(m * n);
    for (uint i = 0; i < m; ++i) {
        for (uint j = 0; j < n; ++j) {
            source[i * n + j] = i * shape(X)[1] + p[j];
        }
    }
    D T[[2]] Y(m, n);
    Y = _partialRearrange(X, Y, source, target);
    return Y;
}
/** @} */

/**
 * \addtogroup shared3p_apply_private_permutation
 * @{
 * @brief Permute a vector according to a private permutation
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref bool "bool" / \ref uint8 "uint8" /
 * \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" /
 * \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref
 * int64 "int" / \ref float32 "float32" / \ref float64 "float64" /
 * \ref fix32 "fix32" / \ref fix64 "fix64"
 * @param x - vector to be permuted
 * @param p - permutation. Output at index `i` will be `x[p[i]]`.
 * @return `x` permuted according to permutation `p`
 * @leakage{None}
 */
template<domain D : shared3p, type T>
D T[[1]] applyPrivatePermutation(D T[[1]] data, D uint[[1]] p) {
    D uint8[[1]] key(32);
    key = randomize(key);
    uint[[1]] tau = declassify(shuffle(p, key));
    data = applyPublicPermutation(data, tau);
    return inverseShuffle(data, key);
}
/** @} */

/**
 * \addtogroup shared3p_apply_private_permutation_rows
 * @{
 * @brief Permute matrix rows according to a private permutation
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref bool "bool" / \ref uint8 "uint8" /
 * \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" /
 * \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref
 * int64 "int" / \ref float32 "float32" / \ref float64 "float64" /
 * \ref fix32 "fix32" / \ref fix64 "fix64"
 * @param X - matrix to be permuted
 * @param p - permutation. Output row at index `i` will be `X[p[i], :]`.
 * @return `X` where rows have been permuted according to permutation `p`
 * @leakage{None}
 */
template <domain D : shared3p, type T>
D T[[2]] applyPrivatePermutationRows(D T[[2]] data, D uint[[1]] p) {
    D uint8 [[1]] key(32);
    key = randomize(key);
    uint[[1]] tau = declassify(shuffle(p, key));
    data = applyPublicPermutationRows(data, tau);
    return inverseShuffleRows(data, key);
}
/** @} */

/**
 * \addtogroup shared3p_apply_private_permutation_cols
 * @{
 * @brief Permute matrix columns according to a private permutation
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref bool "bool" / \ref uint8 "uint8" /
 * \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" /
 * \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref
 * int64 "int" / \ref float32 "float32" / \ref float64 "float64" /
 * \ref fix32 "fix32" / \ref fix64 "fix64"
 * @param X - matrix to be permuted
 * @param p - permutation. Output column at index `i` will be `X[:, p[i]]`.
 * @return `X` where columns have been permuted according to permutation `p`
 * @leakage{None}
 */
template <domain D : shared3p, type T>
D T[[2]] applyPrivatePermutationCols(D T[[2]] data, D uint[[1]] p) {
    D uint8 [[1]] key(32);
    key = randomize(key);
    uint [[1]] tau = declassify(shuffle(p, key));
    data = applyPublicPermutationRows(transpose(data), tau);
    return transpose(inverseShuffleRows(data, key));
}
/** @} */

/**
 * \addtogroup shared3p_unapply_public_permutation
 * @{
 * @brief Permute a vector according to a public permutation. Reverses
 * the effect of `applyPublicPermutation`.
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref bool "bool" / \ref uint8 "uint8" /
 * \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" /
 * \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref
 * int64 "int" / \ref float32 "float32" / \ref float64 "float64" /
 * \ref fix32 "fix32" / \ref fix64 "fix64"
 * @param X - matrix to be permuted
 * @param p - permutation. Output at index `p[i]` will be `x[i]`.
 * @return `x` permuted according to permutation `p`
 * @leakage{None}
 */
template <domain D : shared3p, type T>
D T[[1]] unapplyPublicPermutation(D T[[1]] x, uint[[1]] p) {
    assert(size(x) == size(p));
    uint n = shape(p)[0];
    uint [[1]] source = iota(n);
    uint [[1]] target = p;
    D T[[1]] y(n);
    y = _partialRearrange(x, y, source, target);
    return y;
}
/** @} */

/**
 * \addtogroup shared3p_unapply_public_permutation_rows
 * @{
 * @brief Permute matrix rows according to a public
 * permutation. Reverses the effect of `applyPublicPermutationRows`.
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref bool "bool" / \ref uint8 "uint8" /
 * \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" /
 * \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref
 * int64 "int" / \ref float32 "float32" / \ref float64 "float64" /
 * \ref fix32 "fix32" / \ref fix64 "fix64"
 * @param X - matrix to be permuted
 * @param p - permutation. Output row at index `p[i]` will be `X[i, :]`.
 * @return `X` where rows have been permuted according to permutation `p`
 * @leakage{None}
 */
template <domain D : shared3p, type T>
D T[[2]] unapplyPublicPermutationRows(D T[[2]] X, uint[[1]] p) {
    assert(shape(X)[0] == size(p));
    uint m = shape(p)[0];
    uint n = shape(X)[1];
    uint[[1]] source = iota(m * n);
    uint[[1]] target(m * n);
    for (uint i = 0; i < m; ++i) {
        for (uint j = 0; j < n; ++j) {
            target[i * n + j] = p[i] * n + j;
        }
    }
    D T[[2]] Y(m, n);
    Y = _partialRearrange(X, Y, source, target);
    return Y;
}
/** @} */

/** @} */
