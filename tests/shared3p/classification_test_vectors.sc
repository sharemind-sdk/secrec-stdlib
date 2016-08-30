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

import stdlib;
import shared3p;
import shared3p_random;
import test_utility;

domain pd_shared3p shared3p;

template <type T, dim N>
bool test_classification(T[[N]] value){
    public T[[N]] a; a = value;
    pd_shared3p T[[N]] b; b = a;
    a = declassify(b);
    public bool result;
    result = all(a == value);
    return result;
}

template<domain D : shared3p ,type T, type T2, dim N>
bool test_classification_xor(D T[[N]] priv, T2[[N]] pub){
    T2[[N]] pub2 = declassify(priv);
    return all(declassify(priv) == pub);
}

template <type T, dim N>
T[[N]] randomize(T[[N]] pub){
    pd_shared3p T[[N]] priv = pub;
    priv = randomize(priv);
    pub = declassify(priv);
    return pub;
}

void main() {
    string test_prefix = "PUBLIC -> PRIVATE -> PUBLIC conversion with MAX values";
    {
        uint8[[1]] pub (5) = UINT8_MAX;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        uint16[[1]] pub (5) = UINT16_MAX;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        uint32[[1]] pub (5) = UINT32_MAX;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        uint[[1]] pub (5) = UINT64_MAX;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        int8[[1]] pub (5) = INT8_MAX;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        int16[[1]] pub (5) = INT16_MAX;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        int32[[1]] pub (5) = INT32_MAX;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        int64[[1]] pub (5) = INT64_MAX;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        pd_shared3p xor_uint8[[1]] priv (5) = UINT8_MAX;
        uint8[[1]] pub (5) = UINT8_MAX;
        test(test_prefix, test_classification_xor(priv, pub), priv);
    }
    {
        pd_shared3p xor_uint16[[1]] priv (5) = UINT16_MAX;
        uint16[[1]] pub (5) = UINT16_MAX;
        test(test_prefix, test_classification_xor(priv, pub), priv);
    }
    {
        pd_shared3p xor_uint32[[1]] priv (5) = UINT32_MAX;
        uint32[[1]] pub (5) = UINT32_MAX;
        test(test_prefix, test_classification_xor(priv, pub), priv);
    }
    {
        pd_shared3p xor_uint64[[1]] priv (5) = UINT64_MAX;
        uint64[[1]] pub (5) = UINT64_MAX;
        test(test_prefix, test_classification_xor(priv, pub), priv);
    }

    test_prefix = "PUBLIC -> PRIVATE -> PUBLIC conversion with MIN values";
    {
        int8[[1]] pub (5) = INT8_MIN;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        int16[[1]] pub (5) = INT16_MIN;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        int32[[1]] pub (5) = INT32_MIN;
        test(test_prefix, test_classification(pub), pub);
    }
    {
        int64[[1]] pub (5) = INT64_MIN;
        test(test_prefix, test_classification(pub), pub);
    }

    test_prefix = "PUBLIC -> PRIVATE -> PUBLIC conversion with randomized values over 1-10 element vectors";
    for(uint i = 1; i < 11; ++i){
        {
            bool[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
        {
            uint8[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
        {
            uint16[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
        {
            uint32[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
        {
            uint[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
        {
            int8[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
        {
            int16[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
        {
            int32[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
        {
            int64[[1]] pub (i);
            pub = randomize(pub);
            test(test_prefix, test_classification(pub), pub);
        }
    }

    test_report();
}
