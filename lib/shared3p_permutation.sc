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
 * @param domainProxy - a value used to indicate the shared3p domain
 * used for generating shared randomness by the procedure. The value
 * of this parameter is not important.
 * @param n - length of the permutation
 * @return a random permutation of length n
 */
template<domain D : shared3p>
uint[[1]] publicRandomPermutation(D uint domainProxy, uint n) {
    uint32[[1]] pi(n);
    __syscall("shared3p::gen_rand_pub_perm", __domainid(D), __ref pi);
    return (uint) pi;
}
/** @} */

/**
 * \addtogroup shared3p_private_random_permutation
 * @{
 * @brief Generates a random private permutation
 * @param n - length of the permutation
 * @return a random permutation of length n
 */
template<domain D : shared3p>
D uint[[1]] privateRandomPermutation(uint n) {
    D uint[[1]] pi = iota(n);
    return shuffle(pi);
}
/** @} */

/** \cond */
template<domain D : shared3p, type T, dim N>
D T[[N]] _partialRearrange(D T[[N]] a, D T[[N]] b, uint[[1]] source, uint[[1]] target) {
    assert(size(source) == size(target));
    D T[[1]] temp(size(source));
    __syscall("shared3p::gather_$T\_vec",  __domainid(D), a, temp, __cref source);
    __syscall("shared3p::scatter_$T\_vec", __domainid(D), temp, b, __cref target);
    return b;
}
/** \endcond */

/**
 * \addtogroup shared3p_apply_public_permutation
 * @{
 * @brief Permute a vector according to a public permutation
 * @param x - vector to be permuted
 * @param p - permutation. Output at index i will be x[p[i]].
 * @return x permuted according to permutation p
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
 * @param X - matrix to be permuted
 * @param p - permutation. Output row at index i will be x[p[i], :].
 * @return X where rows have been permuted according to permutation p
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
    D T[[2]] Y(m,n);
    Y = _partialRearrange(X, Y, source, target);
    return Y;
}
/** @} */

/** @} */
