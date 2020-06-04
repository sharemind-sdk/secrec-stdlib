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

template<domain D, type T>
bool testApplyPublicPermutation(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    uint n = size(x);
    D T[[1]] expected = (T) iota(n);
    D T[[1]] res = applyPublicPermutation(x, p);
    return declassify(all(res == expected));
}

template<domain D, type T>
bool testApplyPublicPermutationRows(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    D T[[2]] X(n, 1);
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    D T[[1]] expected = (T) iota(n);
    X[:, 0] = x;
    D T[[2]] res = applyPublicPermutationRows(X, p);
    return declassify(all(res[:, 0] == expected));
}

template<domain D, type T>
bool testApplyPublicPermutationCols(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    D T[[2]] X(1, n);
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    D T[[1]] expected = (T) iota(n);
    X[0, :] = x;
    D T[[2]] res = applyPublicPermutationCols(X, p);
    return declassify(all(res[0, :] == expected));
}

template<domain D, type T>
bool testApplyPrivatePermutation(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    D uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    uint n = size(x);
    D T[[1]] expected = (T) iota(n);
    D T[[1]] res = applyPrivatePermutation(x, p);
    return declassify(all(res == expected));
}

template<domain D, type T>
bool testApplyPrivatePermutationRows(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    D T[[2]] X(n, 1);
    D uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    D T[[1]] expected = (T) iota(n);
    X[:, 0] = x;
    D T[[2]] res = applyPrivatePermutationRows(X, p);
    return declassify(all(res[:, 0] == expected));
}

template<domain D, type T>
bool testApplyPrivatePermutationCols(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    D T[[2]] X(1, n);
    D uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    D T[[1]] expected = (T) iota(n);
    X[0, :] = x;
    D T[[2]] res = applyPrivatePermutationCols(X, p);
    return declassify(all(res[0, :] == expected));
}

template<domain D, type T>
bool testUnapplyPublicPermutation(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    D T[[1]] res = unapplyPublicPermutation(applyPublicPermutation(x, p), p);
    return declassify(all(res == x));
}

template<domain D, type T>
bool testUnapplyPublicPermutationRows(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    D T[[2]] X(n, 1);
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    X[:, 0] = x;
    D T[[2]] res = unapplyPublicPermutationRows(applyPublicPermutationRows(X, p), p);
    return declassify(all(res == X));
}

template<domain D, type T>
bool testUnapplyPublicPermutationCols(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    D T[[2]] X(1, n);
    uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    X[0, :] = x;
    D T[[2]] res = unapplyPublicPermutationCols(applyPublicPermutationCols(X, p), p);
    return declassify(all(res == X));
}

template<domain D, type T>
bool testUnapplyPrivatePermutation(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    D uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    D T[[1]] res = unapplyPrivatePermutation(applyPrivatePermutation(x, p), p);
    return declassify(all(res == x));
}

template<domain D, type T>
bool testUnapplyPrivatePermutationRows(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    D T[[2]] X(n, 1);
    D uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    X[:, 0] = x;
    D T[[2]] res = unapplyPrivatePermutationRows(applyPrivatePermutationRows(X, p), p);
    return declassify(all(res == X));
}

template<domain D, type T>
bool testUnapplyPrivatePermutationCols(D T proxy) {
    D T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    uint n = size(x);
    D T[[2]] X(1, n);
    D uint[[1]] p = {4, 0, 7, 6, 5, 1, 2, 3, 9, 8};
    X[0, :] = x;
    D T[[2]] res = unapplyPrivatePermutationCols(applyPrivatePermutationCols(X, p), p);
    return declassify(all(res == X));
}

void main() {
    string test_prefix = "publicRandomPermutation";
    test(test_prefix, testGenPublicPermutation(), 0u64);

    test_prefix = "privateRandomPermutation";
    test(test_prefix, testGenPrivatePermutation(), 0u64);

    test_prefix = "applyPublicPermutation";
    { pd_shared3p int32 proxy; test(test_prefix, testApplyPublicPermutation(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testApplyPublicPermutation(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testApplyPublicPermutation(proxy), proxy); }

    test_prefix = "applyPublicPermutationRows";
    { pd_shared3p int32 proxy; test(test_prefix, testApplyPublicPermutationRows(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testApplyPublicPermutationRows(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testApplyPublicPermutationRows(proxy), proxy); }

    test_prefix = "applyPublicPermutationCols";
    { pd_shared3p int32 proxy; test(test_prefix, testApplyPublicPermutationCols(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testApplyPublicPermutationCols(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testApplyPublicPermutationCols(proxy), proxy); }

    test_prefix = "applyPrivatePermutation";
    { pd_shared3p int32 proxy; test(test_prefix, testApplyPrivatePermutation(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testApplyPrivatePermutation(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testApplyPrivatePermutation(proxy), proxy); }

    test_prefix = "applyPrivatePermutationRows";
    { pd_shared3p int32 proxy; test(test_prefix, testApplyPrivatePermutationRows(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testApplyPrivatePermutationRows(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testApplyPrivatePermutationRows(proxy), proxy); }

    test_prefix = "applyPrivatePermutationCols";
    { pd_shared3p int32 proxy; test(test_prefix, testApplyPrivatePermutationCols(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testApplyPrivatePermutationCols(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testApplyPrivatePermutationCols(proxy), proxy); }

    test_prefix = "unapplyPublicPermutation";
    { pd_shared3p int32 proxy; test(test_prefix, testUnapplyPublicPermutation(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testUnapplyPublicPermutation(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testUnapplyPublicPermutation(proxy), proxy); }

    test_prefix = "unapplyPublicPermutationRows";
    { pd_shared3p int32 proxy; test(test_prefix, testUnapplyPublicPermutationRows(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testUnapplyPublicPermutationRows(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testUnapplyPublicPermutationRows(proxy), proxy); }

    test_prefix = "unapplyPublicPermutationCols";
    { pd_shared3p int32 proxy; test(test_prefix, testUnapplyPublicPermutationCols(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testUnapplyPublicPermutationCols(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testUnapplyPublicPermutationCols(proxy), proxy); }

    test_prefix = "unapplyPrivatePermutation";
    { pd_shared3p int32 proxy; test(test_prefix, testUnapplyPrivatePermutation(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testUnapplyPrivatePermutation(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testUnapplyPrivatePermutation(proxy), proxy); }

    test_prefix = "unapplyPrivatePermutationRows";
    { pd_shared3p int32 proxy; test(test_prefix, testUnapplyPrivatePermutationRows(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testUnapplyPrivatePermutationRows(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testUnapplyPrivatePermutationRows(proxy), proxy); }

    test_prefix = "unapplyPrivatePermutationCols";
    { pd_shared3p int32 proxy; test(test_prefix, testUnapplyPrivatePermutationCols(proxy), proxy); }
    { pd_shared3p fix32 proxy; test(test_prefix, testUnapplyPrivatePermutationCols(proxy), proxy); }
    { pd_shared3p float32 proxy; test(test_prefix, testUnapplyPrivatePermutationCols(proxy), proxy); }

    test_report();
}
