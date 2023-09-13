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

import stdlib;
import shared3p;
import shared3p_oblivious;
import oblivious;
import shared3p_random;
import shared3p_oblivious_ram;
import test_utility;

domain pd_shared3p shared3p;

// Read out all indices in randomised order
template<domain D, type T>
bool oram_read_test (D T data) {
    D uint8[[1]] seed(32); seed = randomize(seed);

    uint test_size = 10;
    D T[[1]] v(test_size); v = randomize(v);
    D uint[[1]] z(test_size);

    // Make z a indices vector from 0..test_size:
    for (uint i = 0; i < test_size; i++) z[i] = i;
    z = shuffle(z);

    uint[[1]] out = oramPrepareRead(test_size, z, seed);
    D T[[1]] result = oramPerformRead(v, seed, out);

    // Verify result:
    T[[1]] pub_v = declassify(v);
    uint[[1]] pub_z = declassify(z);
    T[[1]] pub_result = declassify(result);
    for (uint i; i < test_size; i++) {
        if (pub_result[i] != pub_v[(uint) pub_z[i]]) {
            return false;
        }
    }
    return true;
}

// Read out the same index `test_size` times
template<domain D, type T>
bool oram_read_test2 (D T data) {
    D uint8[[1]] seed(32); seed = randomize(seed);

    uint test_size = 10;
    D T[[1]] v(test_size); v = randomize(v);
    D uint[[1]] z(test_size); z = 1;

    uint[[1]] out = oramPrepareRead(test_size, z, seed);
    D T[[1]] result = oramPerformRead(v, seed, out);

    // Verify result:
    T[[1]] pub_v = declassify(v);
    uint[[1]] pub_z = declassify(z);
    T[[1]] pub_result = declassify(result);
    for (uint i; i < test_size; i++) {
        if (pub_result[i] != pub_v[1]) {
            return false;
        }
    }
    return true;
}

template<domain D, type T>
bool oram_write_test (D T data) {
    D uint8[[1]] seed1(32); seed1 = randomize(seed1);
    D uint8[[1]] seed2(32); seed2 = randomize(seed2);

    uint test_size = 10;
    D T[[1]] vals(test_size); vals = randomize(vals);
    D T[[1]] v(test_size);
    D uint64[[1]] z(test_size);

    // Make z a indices vector from 0..test_size:
    for (uint i = 0; i < test_size; i++) {
        // vals[i] = 10*i;
        z[i] = i;
    }
    z = shuffle(z);

    uint[[1]] out = oramPrepareWrite(test_size, z, seed1, seed2);
    D T[[1]] result = oramPerformWrite(v, vals, seed1, seed2, out);

    // Verify result:
    T[[1]] pub_vals = declassify(vals);
    uint64[[1]] pub_z = declassify(z);
    T[[1]] pub_result = declassify(result);
    for (uint i; i < test_size; i++) {
        if (pub_result[(uint) pub_z[i]] != pub_vals[i]) {
            return false;
        }
    }
    return true;
}

// Test priorities
template<domain D, type T>
bool oram_write_test2 (D T data) {
    D uint8[[1]] seed1(32); seed1 = randomize(seed1);
    D uint8[[1]] seed2(32); seed2 = randomize(seed2);

    uint test_size = 10;
    D T[[1]] v(test_size);

    D T[[1]] vals = {10, 20, 30, 40}; 
    D uint64[[1]] z = {0, 1, 0, 1};

    uint[[1]] out = oramPrepareWrite(test_size, z, seed1, seed2);
    D T[[1]] result = oramPerformWrite(v, vals, seed1, seed2, out);

    // Verify result:
    T[[1]] pub_vals = declassify(vals);
    uint64[[1]] pub_z = declassify(z);
    T[[1]] pub_result = declassify(result);

    // Expect: {10, 20, 0, 0, ...}
    if (pub_result[0] != 10) return false;
    if (pub_result[1] != 20) return false;
    for (uint i = 2; i < size(z); i++) {
        if (pub_result[i] != 0) return false;
    }
    return true;
}

void main () {
    string test_prefix = "ORAM read";
    { pd_shared3p uint8 t; test(test_prefix, oram_read_test(t), t); }
    { pd_shared3p uint16 t; test(test_prefix, oram_read_test(t), t); }
    { pd_shared3p uint32 t; test(test_prefix, oram_read_test(t), t); }
    { pd_shared3p uint64 t; test(test_prefix, oram_read_test(t), t); }
    { pd_shared3p int8 t; test(test_prefix, oram_read_test(t), t); }
    { pd_shared3p int16 t; test(test_prefix, oram_read_test(t), t); }
    { pd_shared3p int32 t; test(test_prefix, oram_read_test(t), t); }
    { pd_shared3p int64 t; test(test_prefix, oram_read_test(t), t); }

    test_prefix = "ORAM read (reading same index)";
    { pd_shared3p uint8 t; test(test_prefix, oram_read_test2(t), t); }
    { pd_shared3p uint16 t; test(test_prefix, oram_read_test2(t), t); }
    { pd_shared3p uint32 t; test(test_prefix, oram_read_test2(t), t); }
    { pd_shared3p uint64 t; test(test_prefix, oram_read_test2(t), t); }
    { pd_shared3p int8 t; test(test_prefix, oram_read_test2(t), t); }
    { pd_shared3p int16 t; test(test_prefix, oram_read_test2(t), t); }
    { pd_shared3p int32 t; test(test_prefix, oram_read_test2(t), t); }
    { pd_shared3p int64 t; test(test_prefix, oram_read_test2(t), t); }

    test_prefix = "ORAM write";
    { pd_shared3p uint8 t; test(test_prefix, oram_write_test(t), t); }
    { pd_shared3p uint16 t; test(test_prefix, oram_write_test(t), t); }
    { pd_shared3p uint32 t; test(test_prefix, oram_write_test(t), t); }
    { pd_shared3p uint64 t; test(test_prefix, oram_write_test(t), t); }
    { pd_shared3p int8 t; test(test_prefix, oram_write_test(t), t); }
    { pd_shared3p int16 t; test(test_prefix, oram_write_test(t), t); }
    { pd_shared3p int32 t; test(test_prefix, oram_write_test(t), t); }
    { pd_shared3p int64 t; test(test_prefix, oram_write_test(t), t); }

    test_prefix = "ORAM write (writing same positions)";
    { pd_shared3p uint8 t; test(test_prefix, oram_write_test2(t), t); }
    { pd_shared3p uint16 t; test(test_prefix, oram_write_test2(t), t); }
    { pd_shared3p uint32 t; test(test_prefix, oram_write_test2(t), t); }
    { pd_shared3p uint64 t; test(test_prefix, oram_write_test2(t), t); }
    { pd_shared3p int8 t; test(test_prefix, oram_write_test2(t), t); }
    { pd_shared3p int16 t; test(test_prefix, oram_write_test2(t), t); }
    { pd_shared3p int32 t; test(test_prefix, oram_write_test2(t), t); }
    { pd_shared3p int64 t; test(test_prefix, oram_write_test2(t), t); }

    test_report();
}
