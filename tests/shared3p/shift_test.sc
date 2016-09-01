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

import shared3p;
import stdlib;
import test_utility;

domain pd_shared3p shared3p;

template<domain D, type T, type U, type V>
void privateShiftLeftTest(string name, D T[[1]] x, D U[[1]] y, V[[1]] z) {
    test("[$name\] Private shift left", all(declassify(x << y) == z));}

void uint8PrivateShiftLeftTest() {
   pd_shared3p uint8[[1]] x (10) = {119, 200, 132, 40, 3, 91, 153, 61, 143, 242};
   pd_shared3p uint8[[1]] y (10) = {5, 0, 7, 0, 0, 5, 5, 6, 0, 2};
   uint8[[1]] z (10) = {224, 200, 0, 40, 3, 96, 32, 64, 143, 200};
   privateShiftLeftTest("uint8", x, y, z);
}

void uint16PrivateShiftLeftTest() {
   pd_shared3p uint16[[1]] x (10) = {45804, 4619, 22516, 17603, 14350, 4811, 16107, 63529, 7547, 18630};
   pd_shared3p uint16[[1]] y (10) = {11, 6, 11, 4, 8, 5, 14, 4, 3, 12};
   uint16[[1]] z (10) = {24576, 33472, 40960, 19504, 3584, 22880, 49152, 33424, 60376, 24576};
   privateShiftLeftTest("uint16", x, y, z);
}

void uint32PrivateShiftLeftTest() {
   pd_shared3p uint32[[1]] x (10) = {1598427622, 2462133449, 355855966, 3378332026, 3959410530, 618216816, 3362383115, 4098423289, 3278762041, 2352333380};
   pd_shared3p uint32[[1]] y (10) = {29, 10, 26, 29, 2, 17, 29, 15, 3, 13};
   uint32[[1]] z (10) = {3221225472, 78849024, 2013265920, 1073741824, 2952740232, 2061500416, 1610612736, 2096922624, 460292552, 3091759104};
   privateShiftLeftTest("uint32", x, y, z);
}

void uint64PrivateShiftLeftTest() {
   pd_shared3p uint64[[1]] x (10) = {3179661517711218390, 12010087570709087000, 15129418226277625224, 8321309575218175151, 16793294943264365446, 10292375396211072240, 5813613352212860358, 16129725819322733305, 4125192519648779967, 9708571385251476180};
   pd_shared3p uint64[[1]] y (10) = {53, 62, 27, 34, 61, 40, 3, 23, 52, 33};
   uint64[[1]] z (10) = {15762598695796736000, 0, 13681031234812116992, 14104837234750390272, 13835058055282163712, 2422074582409150464, 9615418670283779632, 1602321359558410240, 17001088593323622400, 3233414829704216576};
   privateShiftLeftTest("uint64", x, y, z);
}

void main() {
    uint8PrivateShiftLeftTest();
    uint16PrivateShiftLeftTest();
    uint32PrivateShiftLeftTest();
    uint64PrivateShiftLeftTest();
    test_report();
}

