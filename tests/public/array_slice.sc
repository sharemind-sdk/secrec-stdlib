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

import stdlib;
import test_utility;

void main() {
    uint64[[1]] ap = {100, 101, 102, 103, 104, 105, 106, 107};
    test("rvalue slicing", ap[3] == 103);
    test("rvalue slicing", ap[3:7] == {103, 104, 105, 106});
    uint64[[2]] app(4, 3);
    app[0,:] = {100, 101, 102}; app[1,:] = {110, 111, 112}; app[2,:] = {120, 121, 122}; app[3,:] = {130, 131, 132};
    test("rvalue slicing", app[3, 1] == 131);
    test("rvalue slicing", app[2:4, 1] == {121, 131});
    test("rvalue slicing", app[0, 1:3] == {101, 102});
    test("rvalue slicing", flatten(app[2:4, 1:3]) == {121, 122, 131, 132});

    uint64[[2]] tp(5, 10); tp = 80;
    test("assignment; array = scalar", tp[2,5] == 80);
    tp[1:4, 2:6] = 81;
    test("assignment; array_slice = scalar (alt 1)", tp[2,5] == 81);
    tp[1:4, 8] = 812;
    test("assignment; array_slice = scalar (alt 2)", tp[2,8] == 812);
    tp[4, 6] = 82;
    test("assignment; array_element = scalar", tp[4, 6] == 82);
    uint64[[2]] tpp(3, 4); tpp = app;
    test("assignment; array = array", tpp[3, 1] == 131);
    uint64[[2]] tppp(3, 4); tppp = app[:,:];
    test("assignment; array = array_slice", tppp[3, 1] == 131);
    tp[3, 1:9] = ap;
    test("assignment; array_slice = array", tp[3, 1] == 100);
    tp[3:5, 4:7] = app[:2, :3];
    test("assignment; array_slice = array_slice", tp[4, 6] == 112);

    uint64[[2]] op(5, 10); op = 80; op += 30;
    test("assignment; array += scalar", op[2,5] == 110);
    op[0:1, 1:9] += 10;
    test("assignment; array_slice += scalar (alt 1)", op[0, 4] == 120);
    op[0, 9:10] += 11;
    test("assignment; array_slice += scalar (alt 2)", op[0, 9] == 121);
    op[1, 4] += 20;
    test("assignment; array_element += scalar", op[1, 4] == 130);
    uint64[[2]] opc(5, 10); opc = 80; opc += op;
    test("assignment; array += array", opc[1, 4] == 80 + 130);
    uint64[[2]] opcc(5, 9); opcc = 80; opcc += op[:,:9];
    test("assignment; array += array_slice", opcc[1, 4] == 80 + 130);
    op[3, 1:9] += ap;
    test("assignment; array_slice += array", op[3, 2] == 110 + 101);
    op[4, 1:9] += ap[:];
    test("assignment; array_slice += array_slice (alt 1)", op[4, 3] == 110 + 102);
    op[3:5, 1:4] += app[:2, :3];
    test("assignment; array_slice += array_slice (alt 2)", op[4, 3] == 110 + 102 + 112);

    uint64[[1]] nope(0) = 2;
    test("empty", shape(nope[0:0])[0] == 0);

    test_report();
}
