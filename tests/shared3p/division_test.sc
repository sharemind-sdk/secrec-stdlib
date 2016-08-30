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

template<domain D, type T>
void divTest(string name, D T[[1]] x, D T[[1]] y, T[[1]] z) {
   test("[$name\] Division with two private values", all(declassify(x / y) == z));
}

void uint8DivTest() {
   pd_shared3p uint8[[1]] x (10) = {174, 0, UINT8_MAX - 1, 107, 43, 182, 13, 28, 184, 76};
   pd_shared3p uint8[[1]] y (10) = {15, 15, UINT8_MAX, 202, 42, 211, 107, 184, 45, 79};
   uint8[[1]] z (10) = {11, 0, 0, 0, 1, 0, 0, 0, 4, 0};
   divTest("uint8", x, y, z);
}

void uint16DivTest() {
   pd_shared3p uint16[[1]] x (10) = {45876, 0, UINT16_MAX - 1, 32580, 28143, 30398, 47283, 48041, 12278, 42202};
   pd_shared3p uint16[[1]] y (10) = {175, 175, UINT16_MAX, 14693, 22744, 34449, 58190, 25180, 882, 42797};
   uint16[[1]] z (10) = {262, 0, 0, 2, 1, 0, 0, 1, 13, 0};
   divTest("uint16", x, y, z);
}

void uint32DivTest() {
   pd_shared3p uint32[[1]] x (10) = {21798357, 0, UINT32_MAX - 1, 1079384478, 2261182555, 338606446, 775302438, 3003581665, 1646720950, 4082519954};
   pd_shared3p uint32[[1]] y (10) = {2398, 2398, UINT32_MAX, 2703159056, 131634797, 3374830731, 851673291, 3623133312, 2296214955, 3245316103};
   uint32[[1]] z (10) = {9090, 0, 0, 0, 17, 0, 0, 0, 0, 1};
   divTest("uint32", x, y, z);
}

void uint64DivTest() {
   pd_shared3p uint64[[1]] x (10) = {1872698523698, 0, UINT64_MAX - 1, 14153460087444749705, 12343329379638099274, 10897060826723379588, 17874006642868929947, 5299953365151909498, 14512684195513553964, 3430982392811976967};
   pd_shared3p uint64[[1]] y (10) = {2578953, 2578953, UINT64_MAX, 7909230950479169642, 15930648005381877304, 14128432587825544195, 200800327787477002, 1740196584730791021, 15702793034302958185, 13767204910012762350};
   uint64[[1]] z (10) = {726146, 0, 0, 1, 0, 0, 89, 3, 0, 0};
   divTest("uint64", x, y, z);
}

template<domain D, type T>
void modTest(string name, D T[[1]] x, D T[[1]] y, T[[1]] z) {
   test("[$name\] Modulo private values", all(declassify(x % y) == z));
}

void uint8ModTest() {
   pd_shared3p uint8[[1]] x (10) = {174, 0, 1, UINT8_MAX, 2, 105, 162, 84, 195, 196};
   pd_shared3p uint8[[1]] y (10) = {15, 1, 1, UINT8_MAX, 80, 84, 48, 198, 36, 171};
   uint8[[1]] z (10) = {9, 0, 0, 0, 2, 21, 18, 84, 15, 25};
   modTest("uint8", x, y, z);
}

void uint16ModTest() {
   pd_shared3p uint16[[1]] x (10) = {45876, 0, 1, UINT16_MAX, 42704, 21235, 53828, 47172, 53143, 20540};
   pd_shared3p uint16[[1]] y (10) = {175, 1, 1, UINT16_MAX, 8387, 53688, 43118, 25493, 45759, 19152};
   uint16[[1]] z (10) = {26, 0, 0, 0, 769, 21235, 10710, 21679, 7384, 1388};
   modTest("uint16", x, y, z);
}

void uint32ModTest() {
   pd_shared3p uint32[[1]] x (10) = {21798357, 0, 1, UINT32_MAX, 2671752757, 2729280231, 2054646346, 1296087542, 2513472559, 3827659690};
   pd_shared3p uint32[[1]] y (10) = {2398, 1, 1, UINT32_MAX, 2970657113, 1218519749, 322737441, 1034451649, 1245922416, 3220861984};
   uint32[[1]] z (10) = {537, 0, 0, 0, 2671752757, 292240733, 118221700, 261635893, 21627727, 606797706};
   modTest("uint32", x, y, z);
}

void uint64ModTest() {
   pd_shared3p uint64[[1]] x (10) = {1872698523698, 0, 1, UINT64_MAX, 2911197247836206231, 11779695373037731474, 12331135118952949625, 18423714812283352844, 4399035082396326458, 3170758563397194192};
   pd_shared3p uint64[[1]] y (10) = {2578953, 1, 1, UINT64_MAX, 15068055164583862637, 10491989979906224377, 7981981313118482348, 8428521183430222610, 16717383117636671846, 15670214433620879587};
   uint64[[1]] z (10) = {2118560, 0, 0, 0, 2911197247836206231, 1287705393131507097, 4349153805834467277, 1566672445422907624, 4399035082396326458, 3170758563397194192};
   modTest("uint64", x, y, z);
}

template<domain D, type T>
void pubDivTest(string name, D T[[1]] x, T[[1]] y, T[[1]] z) {
   test("[$name\] Division of private values by public values", all(declassify(x / y) == z));
}

void uint8PubDivTest() {
   pd_shared3p uint8[[1]] x (10) = {15, 226, 246, 102, 123, 191, 161, 69, 197, 188};
   uint8[[1]] y (10) = {174, 252, 215, 127, 208, 16, 35, 116, 94, 98};
   uint8[[1]] z (10) = {0, 0, 1, 0, 0, 11, 4, 0, 2, 1};
   pubDivTest("uint8", x, y, z);
}

void uint16PubDivTest() {
   pd_shared3p uint16[[1]] x (1) = {175};
   uint16[[1]] y (1) = {45876};
   uint16[[1]] z (1) = {0};
   pubDivTest("uint16", x, y, z);
}

void uint32PubDivTest() {
   pd_shared3p uint32[[1]] x (1) = {2398};
   uint32[[1]] y (1) = {21798357};
   uint32[[1]] z (1) = {0};
   pubDivTest("uint32", x, y, z);
}

void uint64PubDivTest() {
   pd_shared3p uint64[[1]] x (1) = {2578953};
   uint64[[1]] y (1) = {1872698523698};
   uint64[[1]] z (1) = {0};
   pubDivTest("uint64", x, y, z);
}

template<domain D, type T>
void pubDiv2Test(string name, T[[1]] x, D T[[1]] y, T[[1]] z) {
   test("[$name\] Division of public values by private values", all(declassify(x / y) == z));
}

void uint8PubDiv2Test() {
   uint8[[1]] x (1) = {174};
   pd_shared3p uint8[[1]] y (1) = {15};
   uint8[[1]] z (1) = {11};
   pubDiv2Test("uint8", x, y, z);
}

void uint16PubDiv2Test() {
   uint16[[1]] x (1) = {45876};
   pd_shared3p uint16[[1]] y (1) = {175};
   uint16[[1]] z (1) = {262};
   pubDiv2Test("uint16", x, y, z);
}

void uint32PubDiv2Test() {
   uint32[[1]] x (1) = {21798357};
   pd_shared3p uint32[[1]] y (1) = {2398};
   uint32[[1]] z (1) = {9090};
   pubDiv2Test("uint32", x, y, z);
}

void uint64PubDiv2Test() {
   uint64[[1]] x (1) = {1872698523698};
   pd_shared3p uint64[[1]] y (1) = {2578953};
   uint64[[1]] z (1) = {726146};
   pubDiv2Test("uint64", x, y, z);
}

template<domain D, type T>
void pubModTest(string name, D T[[1]] x, T[[1]] y, T[[1]] z) {
   test("[$name\] Modulo private by public", all(declassify(x % y) == z));
}

void uint8PubModTest() {
   pd_shared3p uint8[[1]] x (1) = {174};
   uint8[[1]] y (1) = {15};
   uint8[[1]] z (1) = {9};
   pubModTest("uint8", x, y, z);
}

void uint16PubModTest() {
   pd_shared3p uint16[[1]] x (1) = {45876};
   uint16[[1]] y (1) = {175};
   uint16[[1]] z (1) = {26};
   pubModTest("uint16", x, y, z);
}

void uint32PubModTest() {
   pd_shared3p uint32[[1]] x (1) = {21798357};
   uint32[[1]] y (1) = {2398};
   uint32[[1]] z (1) = {537};
   pubModTest("uint32", x, y, z);
}

void uint64PubModTest() {
   pd_shared3p uint64[[1]] x (1) = {1872698523698};
   uint64[[1]] y (1) = {2578953};
   uint64[[1]] z (1) = {2118560};
   pubModTest("uint64", x, y, z);
}

template<domain D, type T>
void pubMod2Test(string name, T[[1]] x, D T[[1]] y, T[[1]] z) {
   test("[$name\] Modulo public by private", all(declassify(x % y) == z));
}

void uint8PubMod2Test() {
   uint8[[1]] x (1) = {174};
   pd_shared3p uint8[[1]] y (1) = {15};
   uint8[[1]] z (1) = {9};
   pubMod2Test("uint8", x, y, z);
}

void uint16PubMod2Test() {
   uint16[[1]] x (1) = {45876};
   pd_shared3p uint16[[1]] y (1) = {175};
   uint16[[1]] z (1) = {26};
   pubMod2Test("uint16", x, y, z);
}

void uint32PubMod2Test() {
   uint32[[1]] x (1) = {21798357};
   pd_shared3p uint32[[1]] y (1) = {2398};
   uint32[[1]] z (1) = {537};
   pubMod2Test("uint32", x, y, z);
}

void uint64PubMod2Test() {
   uint64[[1]] x (1) = {1872698523698};
   pd_shared3p uint64[[1]] y (1) = {2578953};
   uint64[[1]] z (1) = {2118560};
   pubMod2Test("uint64", x, y, z);
}

void main() {
    uint8DivTest();
    uint16DivTest();
    uint32DivTest();
    uint64DivTest();
    uint8ModTest();
    uint16ModTest();
    uint32ModTest();
    uint64ModTest();
    uint8PubDivTest();
    uint16PubDivTest();
    uint32PubDivTest();
    uint64PubDivTest();
    uint8PubDiv2Test();
    uint16PubDiv2Test();
    uint32PubDiv2Test();
    uint64PubDiv2Test();
    uint8PubModTest();
    uint16PubModTest();
    uint32PubModTest();
    uint64PubModTest();
    uint8PubMod2Test();
    uint16PubMod2Test();
    uint32PubMod2Test();
    uint64PubMod2Test();

    test_report();
}

