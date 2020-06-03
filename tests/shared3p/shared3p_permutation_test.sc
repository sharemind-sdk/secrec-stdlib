/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

import shared3p;
import shared3p_permutation;
import stdlib;
import test_utility;

domain pd_shared3p shared3p;

bool testGenPublicPermutation() {
    pd_shared3p uint proxy;
    uint n = 15;
    uint[[1]] p = publicRandomPermutation(proxy, n);
    uint[[1]] i = iota(n);
    return !all(i == p) && all(i == sort(p));
}

bool testGenPrivatePermutation() {
    uint n = 15;
    pd_shared3p uint[[1]] privPerm = privateRandomPermutation(n);
    uint[[1]] p = declassify(privPerm);
    uint[[1]] i = iota(n);
    return !all(i == p) && all(i == sort(p));
}

template<type T>
bool testApplyPublicPermutation(T proxy) {
    pd_shared3p T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    uint n = size(x);
    T[[1]] expected = (T) iota(n);
    T[[1]] res = declassify(applyPublicPermutation(x, p));
    return all(res == expected);
}

template<type T>
bool testApplyPublicPermutationRows(T proxy) {
    pd_shared3p T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    pd_shared3p T[[2]] X(n, 1);
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    T[[1]] expected = (T) iota(n);
    X[:, 0] = x;
    T[[2]] res = declassify(applyPublicPermutationRows(X, p));
    return all(res[:, 0] == expected);
}

template<type T>
bool testApplyPublicPermutationCols(T proxy) {
    pd_shared3p T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    pd_shared3p T[[2]] X(1, n);
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    T[[1]] expected = (T) iota(n);
    X[0, :] = x;
    T[[2]] res = declassify(applyPublicPermutationCols(X, p));
    return all(res[0, :] == expected);
}

template<type T>
bool testApplyPrivatePermutation(T proxy) {
    pd_shared3p T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    pd_shared3p uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    uint n = size(x);
    T[[1]] expected = (T) iota(n);
    T[[1]] res = declassify(applyPrivatePermutation(x, p));
    return all(res == expected);
}

void main() {
    string test_prefix = "publicRandomPermutation";
    test(test_prefix, testGenPublicPermutation(), 0u64);

    test_prefix = "privateRandomPermutation";
    test(test_prefix, testGenPrivatePermutation(), 0u64);

    test_prefix = "applyPublicPermutation";
    test(test_prefix, testApplyPublicPermutation(0u64), 0u64);

    test_prefix = "applyPublicPermutationRows";
    test(test_prefix, testApplyPublicPermutationRows(0i64), 0i64);

    test_prefix = "applyPublicPermutationCols";
    test(test_prefix, testApplyPublicPermutationCols(0f64), 0f64);

    test_prefix = "applyPrivatePermutation";
    test(test_prefix, testApplyPrivatePermutation(0u64), 0u64);

    test_report();
}
