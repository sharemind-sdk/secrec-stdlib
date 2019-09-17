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
import shared3p_string;
import test_utility;

domain pd_shared3p shared3p;

bool bl_str_test () {
    string a = "XXYYyy";

    pd_shared3p xor_uint8[[1]] result = bl_str (a);
    pd_shared3p xor_uint8[[1]] expected_result = {88, 88, 89, 89, 121, 121};

    if (!all (declassify (result) == declassify (expected_result)))
        return false;

    return true;
}

bool bl_strCat_test () {
    string a = "XX";
    string b = "YY";

    pd_shared3p xor_uint8[[1]] result = bl_strCat (bl_str (a) :: pd_shared3p, bl_str (b) :: pd_shared3p);

    if (!all (declassify (result) == declassify (bl_str ("XXYY") :: pd_shared3p)))
        return false;

    return true;
}

bool bl_strContains_test () {
    string a = "XXXYXXX";
    string b = "XYX";
    string c = "YXY";

    pd_shared3p bool result1 = bl_strContains (bl_str (a) :: pd_shared3p, bl_str (b) :: pd_shared3p);
    pd_shared3p bool result2 = bl_strContains (bl_str (a) :: pd_shared3p, bl_str (c) :: pd_shared3p);

    if (! declassify (result1) || declassify (result2))
        return false;

    return true;
}


bool bl_strDeclassify_test () {
    string a = "XXYYXX";

    pd_shared3p xor_uint8[[1]] b = bl_str (a);

    string result = bl_strDeclassify (b);

    return a == result;
}


bool bl_strEqPrefixes_test () {
    string a = "XXYXYX";
    string b = "XYX";

    pd_shared3p bool[[1]] result = bl_strEqPrefixes (bl_str (a) :: pd_shared3p, bl_str (b) :: pd_shared3p);
    bool[[1]] expected_result = {false, true, false, true, false, false};

    if (!all (declassify (result) == expected_result))
        return false;

    return true;
}


bool bl_strEquals_test () {
    string a = "XX";
    string b = "XY";

    return !declassify (bl_strEquals(bl_str (a) :: pd_shared3p, bl_str (b) :: pd_shared3p));
}


bool bl_strHamming_test () {
    string a = "x";
    string b = "y";

    pd_shared3p uint result = bl_strHamming (bl_str (a) :: pd_shared3p, bl_str (b) :: pd_shared3p);

    return declassify (result) - 1 == 0;
}

bool bl_strIndexOf_test () {
    string a = "XXXYYX";
    string b = "YX";

    pd_shared3p uint result = bl_strIndexOf (bl_str (a) :: pd_shared3p, bl_str (b) :: pd_shared3p);

    return declassify (result) == 4;
}


bool bl_strIsLessThan_test () {
    string a = "aaab";
    string b = "AAb";

    pd_shared3p bool result = bl_strIsLessThan (bl_str (b) :: pd_shared3p, bl_str (a) :: pd_shared3p);

    return declassify(result);
}


bool bl_strLength_test () {
    string a = "aa";

    return declassify (bl_strLength (bl_str (a) :: pd_shared3p)) == 2;
}


bool bl_strLevenshtein_test () {
    string a = "XXZY";
    string b = "XXZW";

    pd_shared3p uint result = bl_strLevenshtein (bl_str (a) :: pd_shared3p, bl_str(b) :: pd_shared3p);

    return declassify (result) == 1;
}


bool bl_strIsEmpty_test () {
    string a = "";

    return declassify (bl_strIsEmpty (bl_str (a) :: pd_shared3p));
}


bool bl_strTrim_test () {
    pd_shared3p xor_uint8[[1]] a = {35, 36, 37, 38, 0, 0, 0, 0};

    pd_shared3p xor_uint8[[1]] result = bl_strTrim (a);
    pd_shared3p xor_uint8[[1]] expected_result = {35, 36, 37, 38};

    if ( size (result) != size (expected_result))
        return false;

    return all (declassify (result) == declassify (expected_result));
}


bool countZeroes_test () {
    pd_shared3p xor_uint8[[1]] a = {35, 0, 0, 35};

    pd_shared3p uint result = countZeroes (a);

    return declassify (result) == 2;
}

bool zeroExtend_test () {
    string a = "XX";

    pd_shared3p xor_uint8[[1]] result = zeroExtend (bl_str (a) :: pd_shared3p, 5::uint);
    pd_shared3p xor_uint8[[1]] expected_result = {88, 88, 0, 0, 0};

    return all (declassify (result) == declassify (expected_result));
}

bool findTest() {
    string f = "foo";
    string fbr = "foo bar ";
    pd_shared3p xor_uint8[[1]] fb = __bytes_from_string(f);
    uint8[[1]] fbrb = __bytes_from_string(fbr);
    fbrb[3] = 0;
    fbrb[7] = 0;

    public BlStringVector<pd_shared3p> foobar;
    foobar.bound = 4;
    foobar.value = fbrb;

    return declassify(bl_strFind(fb, foobar) == 0);
}

void main () {
    string test_prefix = "bl_str";
    test (test_prefix, bl_str_test (), "  ");

    test_prefix = "bl_strCat";
    test (test_prefix, bl_strCat_test (), " ");

    test_prefix = "bl_strContains";
    test (test_prefix, bl_strContains_test (), " ");

    test_prefix = "bl_strDeclassify";
    test (test_prefix, bl_strDeclassify_test (), " ");

    test_prefix = "bl_strEqPrefixes";
    test (test_prefix, bl_strEqPrefixes_test (), " ");

    test_prefix = "bl_strEquals";
    test (test_prefix, bl_strEquals_test (), " ");

    test_prefix = "bl_strHamming";
    test (test_prefix, bl_strHamming_test (), " ");

    test_prefix = "bl_strIndexOf";
    test (test_prefix, bl_strIndexOf_test (), " ");

    test_prefix = "bl_strIsLessThan";
    test (test_prefix, bl_strIsLessThan_test (), " ");

    test_prefix = "bl_strLength";
    test (test_prefix, bl_strLength_test (), " ");

    test_prefix = "bl_strLevenshtein";
    test (test_prefix, bl_strLevenshtein_test (), " ");

    test_prefix = "bl_strIsEmpty";
    test (test_prefix, bl_strIsEmpty_test (), " ");

    test_prefix = "bl_strTrim";
    test (test_prefix, bl_strTrim_test (), " ");

    test_prefix = "zountZeroes";
    test (test_prefix, countZeroes_test (), " ");

    test_prefix = "zeroExtend";
    test (test_prefix, zeroExtend_test (), " ");

    test_prefix = "bl_strFind";
    test (test_prefix, findTest(), " ");

    test_report();
}
