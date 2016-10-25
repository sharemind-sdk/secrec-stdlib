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
import shared3p_aes;
import test_utility;

domain pd_shared3p shared3p;

/*
 * Test vectors from: http://csrc.nist.gov/publications/fips/fips197/fips-197.pdf
 *                    http://csrc.nist.gov/groups/STM/cavp/documents/aes/AESAVS.pdf
 * Since there have been too few test cases that include the expansion key,
 * the key expansions for the encryption tests (and the third key expansion test in 128, 192, 256 bits)
 * had to be generated ourselves:
 * - 128 bit:       used independent semitrusted java implementation
 * - 192, 256 bits: self-assisted (verified only the final encryption result against trusted source)
 *
 * All the encryption tests are still coming from the official trusted sources
 */

void main(){
    string test_prefix = "aes128 key generation";
    {
        bool result = true;
        for(uint i = 50; i < 300; i = i + 50){
            pd_shared3p xor_uint32[[1]] key = aes128Genkey(i);

            if (size(key) != (i * 4)) {
                result = false;
                break;
            }
        }

        test(test_prefix, result);
    }

    test_prefix = "aes128 key expansion";
    {

        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, //expansion from trusted source
            0x2b7e1516, 0x28aed2a6, 0xabf71588, 0x09cf4f3c, //expansion from trusted source
            0xf42962ae, 0x7f08c3c6, 0x9c8d72a8, 0x50e76675  //expansion from semitrusted source
        };
        pd_shared3p xor_uint32[[1]] expandedKey1 = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x2b7e1516, 0x28aed2a6, 0xabf71588, 0x09cf4f3c, // R0
            0xf42962ae, 0x7f08c3c6, 0x9c8d72a8, 0x50e76675, // R0

            0xd6aa74fd, 0xd2af72fa, 0xdaa678f1, 0xd6ab76fe, // R1
            0xa0fafe17, 0x88542cb1, 0x23a33939, 0x2a6c7605, // R1
            0x611afffd, 0x1e123c3b, 0x829f4e93, 0xd27828e6, // R1

            0xb692cf0b, 0x643dbdf1, 0xbe9bc500, 0x6830b3fe, // R2
            0xf2c295f2, 0x7a96b943, 0x5935807a, 0x7359f67f, // R2
            0xdf2e7148, 0xc13c4d73, 0x43a303e0, 0x91db2b06, // R2

            0xb6ff744e, 0xd2c2c9bf, 0x6c590cbf, 0x0469bf41, // R3
            0x3d80477d, 0x4716fe3e, 0x1e237e44, 0x6d7a883b, // R3
            0x62df1ec9, 0xa3e353ba, 0xe040505a, 0x719b7b5c, // R3

            0x47f7f7bc, 0x95353e03, 0xf96c32bc, 0xfd058dfd, // R4
            0xef44a541, 0xa8525b7f, 0xb671253b, 0xdb0bad00, // R4
            0x7efe546a, 0xdd1d07d0, 0x3d5d578a, 0x4cc62cd6, // R4

            0x3caaa3e8, 0xa99f9deb, 0x50f3af57, 0xadf622aa, // R5
            0xd4d1c6f8, 0x7c839d87, 0xcaf2b8bc, 0x11f915bc, // R5
            0xda8fa243, 0x0792a593, 0x3acff219, 0x7609decf, // R5

            0x5e390f7d, 0xf7a69296, 0xa7553dc1, 0x0aa31f6b, // R6
            0x6d88a37a, 0x110b3efd, 0xdbf98641, 0xca0093fd, // R6
            0xfb92287b, 0xfc008de8, 0xc6cf7ff1, 0xb0c6a13e, // R6

            0x14f9701a, 0xe35fe28c, 0x440adf4d, 0x4ea9c026, // R7
            0x4e54f70e, 0x5f5fc9f3, 0x84a64fb2, 0x4ea6dc4f, // R7
            0x0fa09a9c, 0xf3a01774, 0x356f6885, 0x85a9c9bb, // R7

            0x47438735, 0xa41c65b9, 0xe016baf4, 0xaebf7ad2, // R8
            0xead27321, 0xb58dbad2, 0x312bf560, 0x7f8d292f, // R8
            0x5c7d700b, 0xafdd677f, 0x9ab20ffa, 0x1f1bc641, // R8

            0x549932d1, 0xf0855768, 0x1093ed9c, 0xbe2c974e, // R9
            0xac7766f3, 0x19fadc21, 0x28d12941, 0x575c006e, // R9
            0xe8c9f3cb, 0x471494b4, 0xdda69b4e, 0xc2bd5d0f, // R9

            0x13111d7f, 0xe3944a17, 0xf307a78b, 0x4d2b30c5, // R10
            0xd014f9a8, 0xc9ee2589, 0xe13f0cc8, 0xb6630ca6, // R10
            0xa48585ee, 0xe391115a, 0x3e378a14, 0xfc8ad71b  // R10
        };
        pd_shared3p xor_uint32[[1]] expandedKey2 = aes128ExpandKey(key);
        test(test_prefix, all(declassify(expandedKey1) == declassify(expandedKey2)));
    }

    test_prefix = "Encrypt with aes128";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff,
            0x00000000, 0x00000000, 0x00000000, 0x00000000,
            0xffffffff, 0xffffffe0, 0x00000000, 0x00000000
        };
        pd_shared3p xor_uint32[[1]] cipherText1 = {
            0x69c4e0d8, 0x6a7b0430, 0xd8cdb780, 0x70b4c55a,
            0x6d251e69, 0x44b051e0, 0x4eaa6fb4, 0xdbf78465,
            0xd451b8d6, 0xe1e1a0eb, 0xb155fbbf, 0x6e7b7dc3
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, //expansion from trusted source
            0x10a58869, 0xd74be5a3, 0x74cf867c, 0xfb473859, //expansion from semitrusted source
            0x00000000, 0x00000000, 0x00000000, 0x00000000  //expansion from semitrusted source
        };

        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x10a58869, 0xd74be5a3, 0x74cf867c, 0xfb473859, // R0
            0x00000000, 0x00000000, 0x00000000, 0x00000000, // R0

            0xd6aa74fd, 0xd2af72fa, 0xdaa678f1, 0xd6ab76fe, // R1
            0xb1a24366, 0x66e9a6c5, 0x122620b9, 0xe96118e0, // R1
            0x62636363, 0x62636363, 0x62636363, 0x62636363, // R1

            0xb692cf0b, 0x643dbdf1, 0xbe9bc500, 0x6830b3fe, // R2
            0x5c0fa278, 0x3ae604bd, 0x28c02404, 0xc1a13ce4, // R2
            0x9b9898c9, 0xf9fbfbaa, 0x9b9898c9, 0xf9fbfbaa, // R2

            0xb6ff744e, 0xd2c2c9bf, 0x6c590cbf, 0x0469bf41, // R3
            0x6ae4cb00, 0x5002cfbd, 0x78c2ebb9, 0xb963d75d, // R3
            0x90973450, 0x696ccffa, 0xf2f45733, 0x0b0fac99, // R3

            0x47f7f7bc, 0x95353e03, 0xf96c32bc, 0xfd058dfd, // R4
            0x99ea8756, 0xc9e848eb, 0xb12aa352, 0x0849740f, // R4
            0xee06da7b, 0x876a1581, 0x759e42b2, 0x7e91ee2b, // R4

            0x3caaa3e8, 0xa99f9deb, 0x50f3af57, 0xadf622aa, // R5
            0xb278f166, 0x7b90b98d, 0xcaba1adf, 0xc2f36ed0, // R5
            0x7f2e2b88, 0xf8443e09, 0x8dda7cbb, 0xf34b9290, // R5

            0x5e390f7d, 0xf7a69296, 0xa7553dc1, 0x0aa31f6b, // R6
            0x9fe78143, 0xe47738ce, 0x2ecd2211, 0xec3e4cc1, // R6
            0xec614b85, 0x1425758c, 0x99ff0937, 0x6ab49ba7, // R6

            0x14f9701a, 0xe35fe28c, 0x440adf4d, 0x4ea9c026, // R7
            0x6dcef98d, 0x89b9c143, 0xa774e352, 0x4b4aaf93, // R7
            0x21751787, 0x3550620b, 0xacaf6b3c, 0xc61bf09b, // R7

            0x47438735, 0xa41c65b9, 0xe016baf4, 0xaebf7ad2, // R8
            0x3bb7253e, 0xb20ee47d, 0x157a072f, 0x5e30a8bc, // R8
            0x0ef90333, 0x3ba96138, 0x97060a04, 0x511dfa9f, // R8

            0x549932d1, 0xf0855768, 0x1093ed9c, 0xbe2c974e, // R9
            0x24754066, 0x967ba41b, 0x8301a334, 0xdd310b88, // R9
            0xb1d4d8e2, 0x8a7db9da, 0x1d7bb3de, 0x4c664941, // R9

            0x13111d7f, 0xe3944a17, 0xf307a78b, 0x4d2b30c5, // R10
            0xd55e84a7, 0x432520bc, 0xc0248388, 0x1d158800, // R10
            0xb4ef5bcb, 0x3e92e211, 0x23e951cf, 0x6f8f188e  // R10
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes128EncryptEcb(expandedKey, plainText);
        test(test_prefix, all(declassify(cipherText1) == declassify(cipherText2)));
    }

    test_prefix = "Encrypt with single key aes128 (zero key)";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0xffff0000, 0x00000000, 0x00000000, 0x00000000,
            0xffffffff, 0xffffffff, 0xffffffff, 0xfff80000,
            0xffffffff, 0xffffffff, 0xffffffff, 0xfffffffe,
            0xffffffff, 0xffffffe0, 0x00000000, 0x00000000,
            0xf0000000, 0x00000000, 0x00000000, 0x00000000
        };

        pd_shared3p xor_uint32[[1]] cipherText1 = {
            0xd7e5dbd3, 0x324595f8, 0xfdc7d7c5, 0x71da6c2a,
            0x088c4b53, 0xf5ec0ff8, 0x14c19ada, 0xe7f6246c,
            0x5c005e72, 0xc1418c44, 0xf569f2ea, 0x33ba54f3,
            0xd451b8d6, 0xe1e1a0eb, 0xb155fbbf, 0x6e7b7dc3,
            0x96d9fd5c, 0xc4f07441, 0x727df0f3, 0x3e401a36
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x00000000, 0x00000000, 0x00000000, 0x00000000 //expansion from semitrusted source
        };

        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x00000000, 0x00000000, 0x00000000, 0x00000000, // R0
            0x62636363, 0x62636363, 0x62636363, 0x62636363, // R1
            0x9b9898c9, 0xf9fbfbaa, 0x9b9898c9, 0xf9fbfbaa, // R2
            0x90973450, 0x696ccffa, 0xf2f45733, 0x0b0fac99, // R3
            0xee06da7b, 0x876a1581, 0x759e42b2, 0x7e91ee2b, // R4
            0x7f2e2b88, 0xf8443e09, 0x8dda7cbb, 0xf34b9290, // R5
            0xec614b85, 0x1425758c, 0x99ff0937, 0x6ab49ba7, // R6
            0x21751787, 0x3550620b, 0xacaf6b3c, 0xc61bf09b, // R7
            0x0ef90333, 0x3ba96138, 0x97060a04, 0x511dfa9f, // R8
            0xb1d4d8e2, 0x8a7db9da, 0x1d7bb3de, 0x4c664941, // R9
            0xb4ef5bcb, 0x3e92e211, 0x23e951cf, 0x6f8f188e  // R10
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes128SingleKeyEncryptEcb(expandedKey, plainText);
        test(test_prefix, all(declassify(cipherText1) == declassify(cipherText2)));
    }

    test_prefix = "Encrypt with single key aes128 (random key)";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0xdeef6f56, 0x0acdfc8a, 0x4a205720, 0x757cd1bd
        };
        pd_shared3p xor_uint32[[1]] cipherText1 = {                                    //ciphertexts from semitrusted source
            0xc5b482d8, 0x0c0b802c, 0xf9f8c2d2, 0x8b251068
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x10a58869, 0xd74be5a3, 0x74cf867c, 0xfb473859 //expansion from trusted source
        };

        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x10a58869, 0xd74be5a3, 0x74cf867c, 0xfb473859, // R0
            0xb1a24366, 0x66e9a6c5, 0x122620b9, 0xe96118e0, // R1
            0x5c0fa278, 0x3ae604bd, 0x28c02404, 0xc1a13ce4, // R2
            0x6ae4cb00, 0x5002cfbd, 0x78c2ebb9, 0xb963d75d, // R3
            0x99ea8756, 0xc9e848eb, 0xb12aa352, 0x0849740f, // R4
            0xb278f166, 0x7b90b98d, 0xcaba1adf, 0xc2f36ed0, // R5
            0x9fe78143, 0xe47738ce, 0x2ecd2211, 0xec3e4cc1, // R6
            0x6dcef98d, 0x89b9c143, 0xa774e352, 0x4b4aaf93, // R7
            0x3bb7253e, 0xb20ee47d, 0x157a072f, 0x5e30a8bc, // R8
            0x24754066, 0x967ba41b, 0x8301a334, 0xdd310b88, // R9
            0xd55e84a7, 0x432520bc, 0xc0248388, 0x1d158800  // R10
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes128SingleKeyEncryptEcb(expandedKey, plainText);
        test(test_prefix, all(declassify(cipherText1) == declassify(cipherText2)));
    }

    test_prefix = "aes192 key generation";
    {
        bool result = true;
        for(uint i = 50; i < 300; i = i + 50){
            pd_shared3p xor_uint32[[1]] key = aes192Genkey(i);

            if (size(key) != (i * 6)) {
                result = false;
                break;
            }
        }

        test(test_prefix, result);
    }

    test_prefix = "aes192 key expansion";
    {
        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, //expansion from trusted source
            0x10111213, 0x14151617,
            0x8e73b0f7, 0xda0e6452, 0xc810f32b, 0x809079e5, //expansion from trusted source
            0x62f8ead2, 0x522c6b7b,
            0xe9f065d7, 0xc1357358, 0x7f787535, 0x7dfbb16c, //expansion self-assisted
            0x53489f6a, 0x4bd0f7cd
        };
        pd_shared3p xor_uint32[[1]] expandedKey1 = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x8e73b0f7, 0xda0e6452, 0xc810f32b, 0x809079e5, // R0
            0xe9f065d7, 0xc1357358, 0x7f787535, 0x7dfbb16c, // R0

            0x10111213, 0x14151617, 0x5846f2f9, 0x5c43f4fe, // R1
            0x62f8ead2, 0x522c6b7b, 0xfe0c91f7, 0x2402f5a5, // R1
            0x53489f6a, 0x4bd0f7cd, 0x9898d864, 0x59adab3c, // R1

            0x544afef5, 0x5847f0fa, 0x4856e2e9, 0x5c43f4fe, // R2
            0xec12068e, 0x6c827f6b, 0x0e7a95b9, 0x5c56fec2, // R2
            0x26d5de09, 0x5b2e6f65, 0x0866f00f, 0x43b607c2, // R2

            0x40f949b3, 0x1cbabd4d, 0x48f043b8, 0x10b7b342, // R3
            0x4db7b4bd, 0x69b54118, 0x85a74796, 0xe92538fd, // R3
            0xd45dfd7e, 0x8df05642, 0xab25884b, 0xf00be72e, // R3

            0x58e151ab, 0x04a2a555, 0x7effb541, 0x6245080c, // R4
            0xe75fad44, 0xbb095386, 0x485af057, 0x21efb14f, // R4
            0xf86d1721, 0xbbdb10e3, 0x6997ec94, 0xe467bad6, // R4

            0x2ab54bb4, 0x3a02f8f6, 0x62e3a95d, 0x66410c08, // R5
            0xa448f6d9, 0x4d6dce24, 0xaa326360, 0x113b30e6, // R5
            0x4f42329d, 0xbf49d5b3, 0x4724c292, 0xfcffd271, // R5

            0xf5018572, 0x97448d7e, 0xbdf1c6ca, 0x87f33e3c, // R6
            0xa25e7ed5, 0x83b1cf9a, 0x27f93943, 0x6a94f767, // R6
            0x77224f24, 0x9345f5f2, 0xdc07c76f, 0x634e12dc, // R6

            0xe5109761, 0x83519b69, 0x34157c9e, 0xa351f1e0, // R7
            0xc0a69407, 0xd19da4e1, 0xec1786eb, 0x6fa64971, // R7
            0x246ad04e, 0xd895023f, 0x4d553a45, 0xde10cfb7, // R7

            0x1ea0372a, 0x99530916, 0x7c439e77, 0xff12051e, // R8
            0x485f7032, 0x22cb8755, 0xe26d1352, 0x33f0b7b3, // R8
            0x021708d8, 0x61591a04, 0x4533ca4a, 0x9da6c875, // R8

            0xdd7e0e88, 0x7e2fff68, 0x608fc842, 0xf9dcc154, // R9
            0x40beeb28, 0x2f18a259, 0x6747d26b, 0x458c553e, // R9
            0x49bda71b, 0x97ad68ac, 0x95ba6074, 0xf4e37a70, // R9

            0x859f5f23, 0x7a8d5a3d, 0xc0c02952, 0xbeefd63a, // R10
            0xa7e1466c, 0x9411f1df, 0x821f750a, 0xad07d753, // R10
            0xb1d0b03a, 0x2c76784f, 0x3101236a, 0xa6ac4bc6, // R10

            0xde601e78, 0x27bcdf2c, 0xa223800f, 0xd8aeda32, // R11
            0xca400538, 0x8fcc5006, 0x282d166a, 0xbc3ce7b5, // R11
            0x33162bb2, 0xc7f551c2, 0x7625e1f8, 0x5a5399b7, // R11

            0xa4970a33, 0x1a78dc09, 0xc418c271, 0xe3a41d5d, // R12
            0xe98ba06f, 0x448c773c, 0x8ecc7204, 0x01002202, // R12
            0x5cef8ad4, 0xfa43c112, 0xc955eaa0, 0x0ea0bb62  // R12
        };

        pd_shared3p xor_uint32[[1]] expandedKey2 = aes192ExpandKey(key);
        test(test_prefix, all(declassify(expandedKey1) == declassify(expandedKey2)));

    }

    test_prefix = "Encrypt with aes192";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff,
            0x00000000, 0x00000000, 0x00000000, 0x00000000,
            0xffffffc0, 0x00000000, 0x00000000, 0x00000000
        };
        pd_shared3p xor_uint32[[1]] cipherText1 = {
            0xdda97ca4, 0x864cdfe0, 0x6eaf70a0, 0xec0d7191,
            0x0956259c, 0x9cd5cfd0, 0x181cca53, 0x380cde06,
            0x6c62f6bb, 0xcab7c3e8, 0x21c9290f, 0x08892dda
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, //expansion from trusted source
            0x10111213, 0x14151617,
            0xe9f065d7, 0xc1357358, 0x7f787535, 0x7dfbb16c, //expansion self-assisted
            0x53489f6a, 0x4bd0f7cd,
            0x00000000, 0x00000000, 0x00000000, 0x00000000, //expansion self-assisted
            0x00000000, 0x00000000
        };


        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0xe9f065d7, 0xc1357358, 0x7f787535, 0x7dfbb16c, // R0
            0x00000000, 0x00000000, 0x00000000, 0x00000000, // R0

            0x10111213, 0x14151617, 0x5846f2f9, 0x5c43f4fe, // R1
            0x53489f6a, 0x4bd0f7cd, 0x9898d864, 0x59adab3c, // R1
            0x00000000, 0x00000000, 0x62636363, 0x62636363, // R1

            0x544afef5, 0x5847f0fa, 0x4856e2e9, 0x5c43f4fe, // R2
            0x26d5de09, 0x5b2e6f65, 0x0866f00f, 0x43b607c2, // R2
            0x62636363, 0x62636363, 0x62636363, 0x62636363, // R2

            0x40f949b3, 0x1cbabd4d, 0x48f043b8, 0x10b7b342, // R3
            0xd45dfd7e, 0x8df05642, 0xab25884b, 0xf00be72e, // R3
            0x9b9898c9, 0xf9fbfbaa, 0x9b9898c9, 0xf9fbfbaa, // R3

            0x58e151ab, 0x04a2a555, 0x7effb541, 0x6245080c, // R4
            0xf86d1721, 0xbbdb10e3, 0x6997ec94, 0xe467bad6, // R4
            0x9b9898c9, 0xf9fbfbaa, 0x90973450, 0x696ccffa, // R4

            0x2ab54bb4, 0x3a02f8f6, 0x62e3a95d, 0x66410c08, // R5
            0x4f42329d, 0xbf49d5b3, 0x4724c292, 0xfcffd271, // R5
            0xf2f45733, 0x0b0fac99, 0x90973450, 0x696ccffa, // R5

            0xf5018572, 0x97448d7e, 0xbdf1c6ca, 0x87f33e3c, // R6
            0x77224f24, 0x9345f5f2, 0xdc07c76f, 0x634e12dc, // R6
            0xc81d19a9, 0xa171d653, 0x53858160, 0x588a2df9, // R6

            0xe5109761, 0x83519b69, 0x34157c9e, 0xa351f1e0, // R7
            0x246ad04e, 0xd895023f, 0x4d553a45, 0xde10cfb7, // R7
            0xc81d19a9, 0xa171d653, 0x7bebf49b, 0xda9a22c8, // R7

            0x1ea0372a, 0x99530916, 0x7c439e77, 0xff12051e, // R8
            0x021708d8, 0x61591a04, 0x4533ca4a, 0x9da6c875, // R8
            0x891fa3a8, 0xd1958e51, 0x198897f8, 0xb8f941ab, // R8

            0xdd7e0e88, 0x7e2fff68, 0x608fc842, 0xf9dcc154, // R9
            0x49bda71b, 0x97ad68ac, 0x95ba6074, 0xf4e37a70, // R9
            0xc26896f7, 0x18f2b43f, 0x91ed1797, 0x407899c6, // R9

            0x859f5f23, 0x7a8d5a3d, 0xc0c02952, 0xbeefd63a, // R10
            0xb1d0b03a, 0x2c76784f, 0x3101236a, 0xa6ac4bc6, // R10
            0x59f00e3e, 0xe1094f95, 0x83ecbc0f, 0x9b1e0830, // R10

            0xde601e78, 0x27bcdf2c, 0xa223800f, 0xd8aeda32, // R11
            0x33162bb2, 0xc7f551c2, 0x7625e1f8, 0x5a5399b7, // R11
            0x0af31fa7, 0x4a8b8661, 0x137b885f, 0xf272c7ca, // R11

            0xa4970a33, 0x1a78dc09, 0xc418c271, 0xe3a41d5d, // R12
            0x5cef8ad4, 0xfa43c112, 0xc955eaa0, 0x0ea0bb62, // R12
            0x432ac886, 0xd834c0b6, 0xd2c7df11, 0x984c5970  // R12
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes192EncryptEcb(expandedKey, plainText);
        test(test_prefix, all(declassify(cipherText1) == declassify(cipherText2)));
    }

/*
    It turned out that there has been no such function!

    test_prefix = "Encrypt with single key aes192";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0xffffffff, 0xffffffff, 0xffff8000, 0x00000000,
            0x80000000, 0x00000000, 0x00000000, 0x00000000,
            0xffffffff, 0xffffffff, 0xffffff00, 0x00000000,
            0xffffff00, 0x00000000, 0x00000000, 0x00000000,
            0xffffffff, 0xff800000, 0x00000000, 0x00000000
        };

        pd_shared3p xor_uint32[[1]] cipherText1 = {
            0xd3427be7, 0xe4d27cd5, 0x4f5fe37b, 0x03cf0897,
            0x6cd02513, 0xe8d4dc98, 0x6b4afe08, 0x7a60bd0c,
            0x71dbf37e, 0x87a2e34d, 0x15b20e8f, 0x10e48924,
            0xa7876ec8, 0x7f5a09bf, 0xea42c77d, 0xa30fd50e,
            0x8e3558c1, 0x35252fb9, 0xc9f367ed, 0x609467a1
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x00000000, 0x00000000, 0x00000000, 0x00000000, //expansion self-assisted
            0x00000000, 0x00000000
        };
        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x00000000, 0x00000000, 0x00000000, 0x00000000, // R0
            0x00000000, 0x00000000, 0x62636363, 0x62636363, // R1
            0x62636363, 0x62636363, 0x62636363, 0x62636363, // R2
            0x9b9898c9, 0xf9fbfbaa, 0x9b9898c9, 0xf9fbfbaa, // R3
            0x9b9898c9, 0xf9fbfbaa, 0x90973450, 0x696ccffa, // R4
            0xf2f45733, 0x0b0fac99, 0x90973450, 0x696ccffa, // R5
            0xc81d19a9, 0xa171d653, 0x53858160, 0x588a2df9, // R6
            0xc81d19a9, 0xa171d653, 0x7bebf49b, 0xda9a22c8, // R7
            0x891fa3a8, 0xd1958e51, 0x198897f8, 0xb8f941ab, // R8
            0xc26896f7, 0x18f2b43f, 0x91ed1797, 0x407899c6, // R9
            0x59f00e3e, 0xe1094f95, 0x83ecbc0f, 0x9b1e0830, // R10
            0x0af31fa7, 0x4a8b8661, 0x137b885f, 0xf272c7ca, // R11
            0x432ac886, 0xd834c0b6, 0xd2c7df11, 0x984c5970  // R12
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes192SingleKeyEncryptEcb(expandedKey, plainText);
        test(test_prefix, all(declassify(cipherText1) == declassify(cipherText2)));
    }
*/

    test_prefix = "aes256 key generation";
    {
        bool result = true;
        for(uint i = 50; i < 300; i = i + 50){
            pd_shared3p xor_uint32[[1]] key = aes256Genkey(i);

            if (size(key) != (i * 8)) {
                result = false;
                break;
            }
        }

        test(test_prefix, result);
    }

    test_prefix = "aes256 key expansion";
    {
        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, //expansion from trusted source
            0x10111213, 0x14151617, 0x18191a1b, 0x1c1d1e1f,

            0x603deb10, 0x15ca71be, 0x2b73aef0, 0x857d7781, //expansion from trusted source
            0x1f352c07, 0x3b6108d7, 0x2d9810a3, 0x0914dff4,

            0x90143ae2, 0x0cd78c5d, 0x8ebdd6cb, 0x9dc17624, //expansion self-assisted
            0x27a96c78, 0xc639bccc, 0x41a61424, 0x564eafe1
        };

        pd_shared3p xor_uint32[[1]] expandedKey1 = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x603deb10, 0x15ca71be, 0x2b73aef0, 0x857d7781, // R0
            0x90143ae2, 0x0cd78c5d, 0x8ebdd6cb, 0x9dc17624, // R0

            0x10111213, 0x14151617, 0x18191a1b, 0x1c1d1e1f, // R1
            0x1f352c07, 0x3b6108d7, 0x2d9810a3, 0x0914dff4, // R1
            0x27a96c78, 0xc639bccc, 0x41a61424, 0x564eafe1, // R1

            0xa573c29f, 0xa176c498, 0xa97fce93, 0xa572c09c, // R2
            0x9ba35411, 0x8e6925af, 0xa51a8b5f, 0x2067fcde, // R2
            0xbe6dc253, 0xb2ba4e0e, 0x3c0798c5, 0xa1c6eee1, // R2

            0x1651a8cd, 0x0244beda, 0x1a5da4c1, 0x0640bade, // R3
            0xa8b09c1a, 0x93d194cd, 0xbe49846e, 0xb75d5b9a, // R3
            0x151d4480, 0xd324f84c, 0x9282ec68, 0xc4cc4389, // R3

            0xae87dff0, 0x0ff11b68, 0xa68ed5fb, 0x03fc1567, // R4
            0xd59aecb8, 0x5bf3c917, 0xfee94248, 0xde8ebe96, // R4
            0xf777654f, 0x45cd2b41, 0x79cab384, 0xd80c5d65, // R4

            0x6de1f148, 0x6fa54f92, 0x75f8eb53, 0x73b8518d, // R5
            0xb5a9328a, 0x2678a647, 0x98312229, 0x2f6c79b3, // R5
            0x74e308cd, 0xa7c7f081, 0x35451ce9, 0xf1895f60, // R5

            0xc656827f, 0xc9a79917, 0x6f294cec, 0x6cd5598b, // R6
            0x812c81ad, 0xdadf48ba, 0x24360af2, 0xfab8b464, // R6
            0x54b8b5ee, 0x11759eaf, 0x68bf2d2b, 0xb0b3704e, // R6

            0x3de23a75, 0x524775e7, 0x27bf9eb4, 0x5407cf39, // R7
            0x98c5bfc9, 0xbebd198e, 0x268c3ba7, 0x09e04214, // R7
            0x938e59e2, 0x3449a963, 0x010cb58a, 0xf085eaea, // R7

            0x0bdc905f, 0xc27b0948, 0xad5245a4, 0xc1871c2f, // R8
            0x68007bac, 0xb2df3316, 0x96e939e4, 0x6c518d80, // R8
            0xcb3f3262, 0xda4aaccd, 0xb2f581e6, 0x0246f1a8, // R8

            0x45f5a660, 0x17b2d387, 0x300d4d33, 0x640a820a, // R9
            0xc814e204, 0x76a9fb8a, 0x5025c02d, 0x59c58239, // R9
            0xe4d4f820, 0xd09d5143, 0xd191e4c9, 0x21140e23, // R9

            0x7ccff71c, 0xbeb4fe54, 0x13e6bbf0, 0xd261a7df, // R10
            0xde136967, 0x6ccc5a71, 0xfa256395, 0x9674ee15, // R10
            0x2194149f, 0xfbdeb852, 0x492b39b4, 0x4b6dc81c, // R10

            0xf01afafe, 0xe7a82979, 0xd7a5644a, 0xb3afe640, // R11
            0x5886ca5d, 0x2e2f31d7, 0x7e0af1fa, 0x27cf73c3, // R11
            0x57e810bc, 0x877541ff, 0x56e4a536, 0x77f0ab15, // R11

            0x2541fe71, 0x9bf50025, 0x8813bbd5, 0x5a721c0a, // R12
            0x749c47ab, 0x18501dda, 0xe2757e4f, 0x7401905a, // R12
            0x8df64d6a, 0x7628f538, 0x3f03cc8c, 0x746e0490, // R12

            0x4e5a6699, 0xa9f24fe0, 0x7e572baa, 0xcdf8cdea, // R13
            0xcafaaae3, 0xe4d59b34, 0x9adf6ace, 0xbd10190d, // R13
            0xc577e2dc, 0x4202a323, 0x14e60615, 0x6316ad00, // R13

            0x24fc79cc, 0xbf0979e9, 0x371ac23c, 0x6d68de36, // R14
            0xfe4890d1, 0xe6188d0b, 0x046df344, 0x706c631e, // R14
            0x8a632e91, 0xfc4bdba9, 0xc3481725, 0xb72613b5  // R14
        };

        pd_shared3p xor_uint32[[1]] expandedKey2 = aes256ExpandKey(key);
        test(test_prefix, all(declassify(expandedKey1) == declassify(expandedKey2)));

    }

    test_prefix = "Encrypt with aes256";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff,
            0x00000000, 0x00000000, 0x00000000, 0x00000000,
            0xffffffff, 0xffffffff, 0xe0000000, 0x00000000
        };
        pd_shared3p xor_uint32[[1]] cipherText1 = {
            0x8ea2b7ca, 0x516745bf, 0xeafc4990, 0x4b496089,
            0x798c7c00, 0x5dee432b, 0x2c8ea5df, 0xa381ecc3,
            0xa1b19bee, 0xe4e11713, 0x9f74b3c5, 0x3fdcb875
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, //expansion from trusted source
            0x10111213, 0x14151617, 0x18191a1b, 0x1c1d1e1f,

            0x90143ae2, 0x0cd78c5d, 0x8ebdd6cb, 0x9dc17624, //expansion self-assisted
            0x27a96c78, 0xc639bccc, 0x41a61424, 0x564eafe1,

            0x00000000, 0x00000000, 0x00000000, 0x00000000, //expansion self-assisted
            0x00000000, 0x00000000, 0x00000000, 0x00000000
        };
        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x90143ae2, 0x0cd78c5d, 0x8ebdd6cb, 0x9dc17624, // R0
            0x00000000, 0x00000000, 0x00000000, 0x00000000, // R0

            0x10111213, 0x14151617, 0x18191a1b, 0x1c1d1e1f, // R1
            0x27a96c78, 0xc639bccc, 0x41a61424, 0x564eafe1, // R1
            0x00000000, 0x00000000, 0x00000000, 0x00000000, // R1

            0xa573c29f, 0xa176c498, 0xa97fce93, 0xa572c09c, // R2
            0xbe6dc253, 0xb2ba4e0e, 0x3c0798c5, 0xa1c6eee1, // R2
            0x62636363, 0x62636363, 0x62636363, 0x62636363, // R2

            0x1651a8cd, 0x0244beda, 0x1a5da4c1, 0x0640bade, // R3
            0x151d4480, 0xd324f84c, 0x9282ec68, 0xc4cc4389, // R3
            0xaafbfbfb, 0xaafbfbfb, 0xaafbfbfb, 0xaafbfbfb, // R3

            0xae87dff0, 0x0ff11b68, 0xa68ed5fb, 0x03fc1567, // R4
            0xf777654f, 0x45cd2b41, 0x79cab384, 0xd80c5d65, // R4
            0x6f6c6ccf, 0x0d0f0fac, 0x6f6c6ccf, 0x0d0f0fac, // R4

            0x6de1f148, 0x6fa54f92, 0x75f8eb53, 0x73b8518d, // R5
            0x74e308cd, 0xa7c7f081, 0x35451ce9, 0xf1895f60, // R5
            0x7d8d8d6a, 0xd7767691, 0x7d8d8d6a, 0xd7767691, // R5

            0xc656827f, 0xc9a79917, 0x6f294cec, 0x6cd5598b, // R6
            0x54b8b5ee, 0x11759eaf, 0x68bf2d2b, 0xb0b3704e, // R6
            0x5354edc1, 0x5e5be26d, 0x31378ea2, 0x3c38810e, // R6

            0x3de23a75, 0x524775e7, 0x27bf9eb4, 0x5407cf39, // R7
            0x938e59e2, 0x3449a963, 0x010cb58a, 0xf085eaea, // R7
            0x968a81c1, 0x41fcf750, 0x3c717a3a, 0xeb070cab, // R7

            0x0bdc905f, 0xc27b0948, 0xad5245a4, 0xc1871c2f, // R8
            0xcb3f3262, 0xda4aaccd, 0xb2f581e6, 0x0246f1a8, // R8
            0x9eaa8f28, 0xc0f16d45, 0xf1c6e3e7, 0xcdfe62e9, // R8

            0x45f5a660, 0x17b2d387, 0x300d4d33, 0x640a820a, // R9
            0xe4d4f820, 0xd09d5143, 0xd191e4c9, 0x21140e23, // R9
            0x2b312bdf, 0x6acddc8f, 0x56bca6b5, 0xbdbbaa1e, // R9

            0x7ccff71c, 0xbeb4fe54, 0x13e6bbf0, 0xd261a7df, // R10
            0x2194149f, 0xfbdeb852, 0x492b39b4, 0x4b6dc81c, // R10
            0x6406fd52, 0xa4f79017, 0x553173f0, 0x98cf1119, // R10

            0xf01afafe, 0xe7a82979, 0xd7a5644a, 0xb3afe640, // R11
            0x57e810bc, 0x877541ff, 0x56e4a536, 0x77f0ab15, // R11
            0x6dbba90b, 0x07767584, 0x51cad331, 0xec71792f, // R11

            0x2541fe71, 0x9bf50025, 0x8813bbd5, 0x5a721c0a, // R12
            0x8df64d6a, 0x7628f538, 0x3f03cc8c, 0x746e0490, // R12
            0xe7b0e89c, 0x4347788b, 0x16760b7b, 0x8eb91a62, // R12

            0x4e5a6699, 0xa9f24fe0, 0x7e572baa, 0xcdf8cdea, // R13
            0xc577e2dc, 0x4202a323, 0x14e60615, 0x6316ad00, // R13
            0x74ed0ba1, 0x739b7e25, 0x2251ad14, 0xce20d43b, // R13

            0x24fc79cc, 0xbf0979e9, 0x371ac23c, 0x6d68de36, // R14
            0x8a632e91, 0xfc4bdba9, 0xc3481725, 0xb72613b5, // R14
            0x10f80a17, 0x53bf729c, 0x45c979e7, 0xcb706385  // R14
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes256EncryptEcb(expandedKey, plainText);
        test(test_prefix, all(declassify(cipherText1) == declassify(cipherText2)));
    }

    test_report();
}
