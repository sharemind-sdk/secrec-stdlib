/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

import stdlib;
import test_utility;

template<type T>
T[[1]] reverse(T[[1]] x) {
    uint n = size(x);
    T[[1]] res(n);

    for (uint i = 0; i < n; ++i) {
        res[i] = x[n - i - 1];
    }

    return res;
}

bool bool_sort_test(bool ascending) {
    bool[[1]] x = {true, false, false};
    bool[[1]] expected = {false, false, true};
    bool[[1]] res = sort(x, ascending);

    if (!ascending) {
        expected = reverse(expected);
    }

    return all(res == expected);
}

template<type T>
bool sort_test(T proxy, bool ascending) {
    T[[1]] x = {1, 5, 6, 7, 0, 4, 3, 2, 9, 8};
    T[[1]] res = sort(x, ascending);
    T[[1]] expected = (T) iota(size(x));

    if (!ascending) {
        expected = reverse(expected);
    }

    return all(res == expected);
}

void main() {
    string test_prefix = "Public sort";
    test(test_prefix, bool_sort_test(true), true);
    test(test_prefix, sort_test(0u8, true), 0u8);
    test(test_prefix, sort_test(0u16, true), 0u16);
    test(test_prefix, sort_test(0u32, true), 0u32);
    test(test_prefix, sort_test(0u64, true), 0u64);
    test(test_prefix, sort_test(0i8, true), 0i8);
    test(test_prefix, sort_test(0i16, true), 0i16);
    test(test_prefix, sort_test(0i32, true), 0i32);
    test(test_prefix, sort_test(0i64, true), 0i64);
    test(test_prefix, sort_test(0f32, true), 0f32);
    test(test_prefix, sort_test(0f64, true), 0f64);

    test_prefix = "Public sort (descending)";
    test(test_prefix, bool_sort_test(false), true);
    test(test_prefix, sort_test(0u8, false), 0u8);
    test(test_prefix, sort_test(0u16, false), 0u16);
    test(test_prefix, sort_test(0u32, false), 0u32);
    test(test_prefix, sort_test(0u64, false), 0u64);
    test(test_prefix, sort_test(0i8, false), 0i8);
    test(test_prefix, sort_test(0i16, false), 0i16);
    test(test_prefix, sort_test(0i32, false), 0i32);
    test(test_prefix, sort_test(0i64, false), 0i64);
    test(test_prefix, sort_test(0f32, false), 0f32);
    test(test_prefix, sort_test(0f64, false), 0f64);

    test_report();
}
