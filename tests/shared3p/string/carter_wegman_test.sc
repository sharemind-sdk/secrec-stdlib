/*
 * Copyright (C) 2018 Cybernetica
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

import shared3p;
import shared3p_string;
import stdlib;
import test_utility;

domain pd_shared3p shared3p;

bool cw_test() {
    uint rowLen = 3;
    pd_shared3p xor_uint8[[1]] data = {1, 1, 1, 2, 2, 2, 3, 3, 3};
    pd_shared3p xor_uint8[[1]] k = {233, 239, 73, 212, 164, 132, 188, 34, 168, 180, 117, 183, 238, 65, 118, 240, 249, 30, 121, 143, 231, 35, 252, 241, 21, 131, 100, 231, 114, 213, 188, 207, 99, 175, 1, 212, 2, 53, 231, 156, 97, 111, 10, 248, 110, 244, 226, 163, 248, 158, 85, 88, 102, 200, 10, 191, 173, 44, 67, 131, 172, 251, 194, 144, 217, 48, 69, 211, 177, 61, 11, 36, 55, 122, 50, 183, 2, 96, 131, 0, 148, 40, 92, 165, 198, 144, 60, 23, 22, 78, 170, 0, 53, 238, 236, 187, 85, 131, 190, 158, 160, 55, 55, 99, 240, 245, 189, 187, 137, 1, 155, 213, 192, 115, 137, 137, 0, 91, 156, 211, 91, 105, 146, 150, 184, 101, 234, 245, 60, 185, 230, 154, 161, 239, 217, 148, 209, 29, 195, 159, 38, 20, 118, 199, 187, 208, 43, 241, 75, 38, 183, 83, 199, 101, 173, 198, 48, 7, 35, 173, 238, 140, 153, 50, 136, 46, 115, 81, 30, 47, 186, 105, 106, 122, 109, 35, 210, 151, 203, 196, 234, 220, 81, 66, 189, 191, 234, 202, 34, 73, 50, 200, 33, 33, 18, 14, 136, 29, 190, 186, 226, 132, 217, 113, 40, 113, 247, 124, 64, 66, 138, 166, 86, 16, 115, 214, 147, 90, 140, 228, 125, 44, 138, 245, 80, 209, 78, 47, 12, 63, 90, 41, 78, 4, 254, 205, 22, 222, 141, 107, 17, 143, 18, 54, 140, 123, 41, 38, 127, 240, 85, 48, 69, 135, 5, 204, 28, 138, 146, 158, 182, 31, 79, 241, 128, 34, 222, 52, 236, 226, 35, 200, 116, 35, 226, 86, 81, 103, 122, 94, 119, 13, 48, 251, 84, 44, 125, 5, 87, 8, 221, 187, 80, 99, 85, 23, 193, 154, 37, 8, 123, 113, 15, 84, 224, 237, 100, 41, 82, 78, 27, 250, 127, 24, 54, 237, 76, 167, 230, 253, 110, 100, 36, 71, 144, 238, 91, 215, 184, 191, 236, 1, 41, 102, 172, 122, 136, 81, 208, 75, 104, 23, 204, 92, 10, 10, 243, 95, 206, 232, 112, 147, 19, 42, 189, 121, 175, 243, 127, 120, 143, 166, 71, 250, 164, 149, 157, 236, 99, 73, 23, 82, 193, 27, 181, 246, 51, 28, 14, 212, 148, 120, 93, 71};
    pd_shared3p xor_uint8[[1]] h = cw128Hash(data, k, rowLen);
    uint8[[1]] expected = {142, 1, 142, 247, 88, 121, 38, 201, 65, 238, 89, 50, 63, 184, 130, 76, 181, 148, 195, 174, 28, 106, 54, 139, 238, 36, 14, 114, 179, 80, 62, 235, 59, 149, 77, 89, 68, 19, 16, 66, 175, 202, 87, 64, 140, 232, 188, 167};
    return all(expected == declassify(h));
}

void main() {
    test("cw128Hash", cw_test());
    test_report();
}
