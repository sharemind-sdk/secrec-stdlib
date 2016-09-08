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
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f
        };
        pd_shared3p xor_uint32[[1]] expandedKey1 = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0xd6aa74fd, 0xd2af72fa, 0xdaa678f1, 0xd6ab76fe, // R1
            0xb692cf0b, 0x643dbdf1, 0xbe9bc500, 0x6830b3fe, // R2
            0xb6ff744e, 0xd2c2c9bf, 0x6c590cbf, 0x0469bf41, // R3
            0x47f7f7bc, 0x95353e03, 0xf96c32bc, 0xfd058dfd, // R4
            0x3caaa3e8, 0xa99f9deb, 0x50f3af57, 0xadf622aa, // R5
            0x5e390f7d, 0xf7a69296, 0xa7553dc1, 0x0aa31f6b, // R6
            0x14f9701a, 0xe35fe28c, 0x440adf4d, 0x4ea9c026, // R7
            0x47438735, 0xa41c65b9, 0xe016baf4, 0xaebf7ad2, // R8
            0x549932d1, 0xf0855768, 0x1093ed9c, 0xbe2c974e, // R9
            0x13111d7f, 0xe3944a17, 0xf307a78b, 0x4d2b30c5  // R10
        };

        pd_shared3p xor_uint32[[1]] expandedKey2 = aes128ExpandKey(key);
        test(test_prefix, all(declassify(expandedKey1) == declassify(expandedKey2)));
    }

    test_prefix = "Encrypt with aes128";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff
        };
        pd_shared3p xor_uint32[[1]] cipherText1 = {
            0x69c4e0d8, 0x6a7b0430, 0xd8cdb780, 0x70b4c55a
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f
        };
        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0xd6aa74fd, 0xd2af72fa, 0xdaa678f1, 0xd6ab76fe, // R1
            0xb692cf0b, 0x643dbdf1, 0xbe9bc500, 0x6830b3fe, // R2
            0xb6ff744e, 0xd2c2c9bf, 0x6c590cbf, 0x0469bf41, // R3
            0x47f7f7bc, 0x95353e03, 0xf96c32bc, 0xfd058dfd, // R4
            0x3caaa3e8, 0xa99f9deb, 0x50f3af57, 0xadf622aa, // R5
            0x5e390f7d, 0xf7a69296, 0xa7553dc1, 0x0aa31f6b, // R6
            0x14f9701a, 0xe35fe28c, 0x440adf4d, 0x4ea9c026, // R7
            0x47438735, 0xa41c65b9, 0xe016baf4, 0xaebf7ad2, // R8
            0x549932d1, 0xf0855768, 0x1093ed9c, 0xbe2c974e, // R9
            0x13111d7f, 0xe3944a17, 0xf307a78b, 0x4d2b30c5  // R10
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes128EncryptEcb(expandedKey, plainText);
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
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f,
            0x10111213, 0x14151617
        };
        pd_shared3p xor_uint32[[1]] expandedKey1 = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x10111213, 0x14151617, 0x5846f2f9, 0x5c43f4fe, // R1
            0x544afef5, 0x5847f0fa, 0x4856e2e9, 0x5c43f4fe, // R2
            0x40f949b3, 0x1cbabd4d, 0x48f043b8, 0x10b7b342, // R3
            0x58e151ab, 0x04a2a555, 0x7effb541, 0x6245080c, // R4
            0x2ab54bb4, 0x3a02f8f6, 0x62e3a95d, 0x66410c08, // R5
            0xf5018572, 0x97448d7e, 0xbdf1c6ca, 0x87f33e3c, // R6
            0xe5109761, 0x83519b69, 0x34157c9e, 0xa351f1e0, // R7
            0x1ea0372a, 0x99530916, 0x7c439e77, 0xff12051e, // R8
            0xdd7e0e88, 0x7e2fff68, 0x608fc842, 0xf9dcc154, // R9
            0x859f5f23, 0x7a8d5a3d, 0xc0c02952, 0xbeefd63a, // R10
            0xde601e78, 0x27bcdf2c, 0xa223800f, 0xd8aeda32, // R11
            0xa4970a33, 0x1a78dc09, 0xc418c271, 0xe3a41d5d  // R12
        };

        pd_shared3p xor_uint32[[1]] expandedKey2 = aes192ExpandKey(key);
        test(test_prefix, all(declassify(expandedKey1) == declassify(expandedKey2)));
    }

    test_prefix = "Encrypt with aes192";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff
        };
        pd_shared3p xor_uint32[[1]] cipherText1 = {
            0xdda97ca4, 0x864cdfe0, 0x6eaf70a0, 0xec0d7191
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f,
            0x10111213, 0x14151617
        };
        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x10111213, 0x14151617, 0x5846f2f9, 0x5c43f4fe, // R1
            0x544afef5, 0x5847f0fa, 0x4856e2e9, 0x5c43f4fe, // R2
            0x40f949b3, 0x1cbabd4d, 0x48f043b8, 0x10b7b342, // R3
            0x58e151ab, 0x04a2a555, 0x7effb541, 0x6245080c, // R4
            0x2ab54bb4, 0x3a02f8f6, 0x62e3a95d, 0x66410c08, // R5
            0xf5018572, 0x97448d7e, 0xbdf1c6ca, 0x87f33e3c, // R6
            0xe5109761, 0x83519b69, 0x34157c9e, 0xa351f1e0, // R7
            0x1ea0372a, 0x99530916, 0x7c439e77, 0xff12051e, // R8
            0xdd7e0e88, 0x7e2fff68, 0x608fc842, 0xf9dcc154, // R9
            0x859f5f23, 0x7a8d5a3d, 0xc0c02952, 0xbeefd63a, // R10
            0xde601e78, 0x27bcdf2c, 0xa223800f, 0xd8aeda32, // R11
            0xa4970a33, 0x1a78dc09, 0xc418c271, 0xe3a41d5d  // R12
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes192EncryptEcb(expandedKey, plainText);
        test(test_prefix, all(declassify(cipherText1) == declassify(cipherText2)));
    }

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
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f,
            0x10111213, 0x14151617, 0x18191a1b, 0x1c1d1e1f
        };
        pd_shared3p xor_uint32[[1]] expandedKey1 = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x10111213, 0x14151617, 0x18191a1b, 0x1c1d1e1f, // R1
            0xa573c29f, 0xa176c498, 0xa97fce93, 0xa572c09c, // R2
            0x1651a8cd, 0x0244beda, 0x1a5da4c1, 0x0640bade, // R3
            0xae87dff0, 0x0ff11b68, 0xa68ed5fb, 0x03fc1567, // R4
            0x6de1f148, 0x6fa54f92, 0x75f8eb53, 0x73b8518d, // R5
            0xc656827f, 0xc9a79917, 0x6f294cec, 0x6cd5598b, // R6
            0x3de23a75, 0x524775e7, 0x27bf9eb4, 0x5407cf39, // R7
            0x0bdc905f, 0xc27b0948, 0xad5245a4, 0xc1871c2f, // R8
            0x45f5a660, 0x17b2d387, 0x300d4d33, 0x640a820a, // R9
            0x7ccff71c, 0xbeb4fe54, 0x13e6bbf0, 0xd261a7df, // R10
            0xf01afafe, 0xe7a82979, 0xd7a5644a, 0xb3afe640, // R11
            0x2541fe71, 0x9bf50025, 0x8813bbd5, 0x5a721c0a, // R12
            0x4e5a6699, 0xa9f24fe0, 0x7e572baa, 0xcdf8cdea, // R13
            0x24fc79cc, 0xbf0979e9, 0x371ac23c, 0x6d68de36  // R14
        };

        pd_shared3p xor_uint32[[1]] expandedKey2 = aes256ExpandKey(key);
        test(test_prefix, all(declassify(expandedKey1) == declassify(expandedKey2)));
    }

    test_prefix = "Encrypt with aes256";
    {
        pd_shared3p xor_uint32[[1]] plainText = {
            0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff
        };
        pd_shared3p xor_uint32[[1]] cipherText1 = {
            0x8ea2b7ca, 0x516745bf, 0xeafc4990, 0x4b496089
        };
        pd_shared3p xor_uint32[[1]] key = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f,
            0x10111213, 0x14151617, 0x18191a1b, 0x1c1d1e1f
        };
        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x00010203, 0x04050607, 0x08090a0b, 0x0c0d0e0f, // R0
            0x10111213, 0x14151617, 0x18191a1b, 0x1c1d1e1f, // R1
            0xa573c29f, 0xa176c498, 0xa97fce93, 0xa572c09c, // R2
            0x1651a8cd, 0x0244beda, 0x1a5da4c1, 0x0640bade, // R3
            0xae87dff0, 0x0ff11b68, 0xa68ed5fb, 0x03fc1567, // R4
            0x6de1f148, 0x6fa54f92, 0x75f8eb53, 0x73b8518d, // R5
            0xc656827f, 0xc9a79917, 0x6f294cec, 0x6cd5598b, // R6
            0x3de23a75, 0x524775e7, 0x27bf9eb4, 0x5407cf39, // R7
            0x0bdc905f, 0xc27b0948, 0xad5245a4, 0xc1871c2f, // R8
            0x45f5a660, 0x17b2d387, 0x300d4d33, 0x640a820a, // R9
            0x7ccff71c, 0xbeb4fe54, 0x13e6bbf0, 0xd261a7df, // R10
            0xf01afafe, 0xe7a82979, 0xd7a5644a, 0xb3afe640, // R11
            0x2541fe71, 0x9bf50025, 0x8813bbd5, 0x5a721c0a, // R12
            0x4e5a6699, 0xa9f24fe0, 0x7e572baa, 0xcdf8cdea, // R13
            0x24fc79cc, 0xbf0979e9, 0x371ac23c, 0x6d68de36  // R14
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes256EncryptEcb(expandedKey, plainText);
        test(test_prefix, all(declassify(cipherText1) == declassify(cipherText2)));
    }

    test_report();
}
