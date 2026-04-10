/*
 * Copyright (C) 2026 Cybernetica
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

import stdlib;
import shared3p;
import test_utility;

domain pd_shared3p shared3p;

void main() {
    pd_shared3p int32[[1]] pp(100) = 2;
    pd_shared3p int32[[1]] tt(33) = 4;
    test("cat", declassify(cat(pp, tt)[101]) == 4);
    test("cat", shape(cat(pp, tt))[0] == 133);

    pd_shared3p int32[[3]] a(10, 12, 14) = 5;
    a[1, 2, 0] = 15;
    pd_shared3p int32[[3]] b1(3, 12, 14) = 61;
    b1[1, 2, 0] = 161;
    pd_shared3p int32[[3]] b2(10, 3, 14) = 62;
    b2[1, 2, 0] = 162;
    pd_shared3p int32[[3]] b3(10, 12, 3) = 63;
    b3[1, 2, 0] = 163;

    pd_shared3p int32[[3]] c = cat(a, b1);
    test("cat", declassify(c[1, 2, 0]) == 15);
    test("cat", declassify(c[1 + 10, 2, 0]) == 161);
    test("cat", declassify(c[1 + 10, 2 + 1, 0]) == 61);

    c = cat(a, b1, 0);
    test("cat", declassify(c[1, 2, 0]) == 15);
    test("cat", declassify(c[1 + 10, 2, 0]) == 161);
    test("cat", declassify(c[1 + 10, 2 + 1, 0]) == 61);
    test("cat", shape(c)[0] == 10 + 3 && shape(c)[1] == 12 && shape(c)[2] == 14);

    c = cat(a, b2, 1);
    test("cat", declassify(c[1, 2, 0]) == 15);
    test("cat", declassify(c[1, 2 + 12, 0]) == 162);
    test("cat", shape(c)[0] == 10 && shape(c)[1] == 12 + 3 && shape(c)[2] == 14);

    c = cat(a, b3, 2);
    test("cat", declassify(c[1, 2, 0]) == 15);
    test("cat", declassify(c[1, 2, 0 + 14]) == 163);
    test("cat", shape(c)[0] == 10 && shape(c)[1] == 12 && shape(c)[2] == 14 + 3);

    int32[[1]] ppp(100) = 2;
    int32[[1]] ttp(33) = 4;
    test("cat", cat(ppp, ttp)[101] == 4);
    test("cat", shape(cat(ppp, ttp))[0] == 133);

    int32[[3]] ap(10, 12, 14) = 5;
    ap[1, 2, 0] = 15;
    int32[[3]] b1p(3, 12, 14) = 61;
    b1p[1, 2, 0] = 161;
    int32[[3]] cp = cat(ap, b1p);
    test("cat", cp[1, 2, 0] == 15);
    test("cat", cp[1 + 10, 2, 0] == 161);
    test("cat", cp[1 + 10, 2 + 1, 0] == 61);

    pd_shared3p int32[[1]] gg0(0) = 1;
    pd_shared3p int32[[1]] gg1(33) = 2;
    test("cat empty", shape(cat(gg0, gg1))[0] == 33);
    test("cat empty", declassify(cat(gg0, gg1)[0]) == 2);
    test("cat empty", declassify(cat(gg0, gg1)[32]) == 2);
    test("cat empty", shape(cat(gg1, gg0))[0] == 33);
    test("cat empty", declassify(cat(gg1, gg0)[0]) == 2);
    test("cat empty", declassify(cat(gg1, gg0)[32]) == 2);
    test("cat empty", shape(cat(gg0, gg0))[0] == 0);

    test_report();
}
