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
import matrix;
import shared3p_matrix;

domain pd_shared3p shared3p;

bool kl_str_test () {
	string a = "öö;;::";
	
	pd_shared3p xor_uint8[[1]] result = kl_str (a);
	pd_shared3p xor_uint8[[1]] expected_result = {195, 182, 195, 182, 59, 59, 58, 58};

	if (!all (declassify (result) == declassify (expected_result)))
		return false;
		
	return true;
}

bool kl_strCat_test () {
	string a = "õ";
	string b = "ö";
	
	pd_shared3p xor_uint8[[1]] result = kl_strCat (kl_str (a), kl_str (b));

	if (!all (declassify (result) == declassify (kl_str ("õö"))))
		return false;
	
	return true;
}

bool kl_strContains_test () {
	string a = "???!??";
	string b = "?!?";
	string c = "!?!";
	
	pd_shared3p bool result1 = kl_strContains (kl_str (a), kl_str (b));
	pd_shared3p bool result2 = kl_strContains (kl_str (a), kl_str (c));
	
	if (! declassify (result1) || declassify (result2))
		return false;
	
	return true;
}


bool kl_strDeclassify_test () {
	string a = "??!!??";
	
	pd_shared3p xor_uint8[[1]] b = kl_str (a);
	
	string result = kl_strDeclassify (b);

	return a == result;
}


bool kl_strEqPrefixes_test () {
	string a = "XXYXYX";
	string b = "XYX";
	
	pd_shared3p bool[[1]] result = kl_strEqPrefixes (kl_str (a), kl_str (b));
	bool[[1]] expected_result = {false, true, false, true};

	if (!all (declassify (result) == expected_result))
		return false;
		
	return true;
}


bool kl_strEquals_test () {
	string a = "XX";
	string b = "XY";

	return !declassify (kl_strEquals(kl_str (a), kl_str (b)));
}


bool kl_strHamming_test () {
	string a = "x";
	string b = "y";

	pd_shared3p uint result = kl_strHamming (kl_str (a), kl_str (b));
	
	return declassify (result) - 1 == 0;
}

bool kl_strIndexOf_test () {
	string a = "XXXYYX";
	string b = "YX";

	pd_shared3p uint result = kl_strIndexOf (kl_str (a), kl_str (b));
	
	return declassify (result) == 4;
}


bool kl_strIsLessThan_test () {
	string a = "aaab";
	string b = "AAb";

	pd_shared3p bool result = kl_strIsLessThan (kl_str (b), kl_str (a)); 

	return declassify(result);
}


bool kl_strLength_test () {
	string a = "aa";

	return kl_strLength (kl_str (a)) == 2;
}


bool kl_strLevenshtein_test () {
	string a = "ööõõ";
	string b = "ööõä";
	
	pd_shared3p uint result = kl_strLevenshtein (kl_str (a), kl_str(b));
	
	return declassify (result) == 1;
}


void main () {
	string test_prefix = "Kl_str";
	test (test_prefix, kl_str_test (), " ");
	
	test_prefix = "Kl_strCat";
	test (test_prefix, kl_strCat_test (), " ");
	
	test_prefix = "Kl_strContains";
	test (test_prefix, kl_strContains_test (), " ");
	
	test_prefix = "Kl_strDeclassify";
	test (test_prefix, kl_strDeclassify_test (), " ");
	
	test_prefix = "Kl_strEqPrefixes";
	test (test_prefix, kl_strEqPrefixes_test (), " ");
	
	test_prefix = "Kl_strEquals";
	test (test_prefix, kl_strEquals_test (), " ");
	
	test_prefix = "Kl_strHamming";
	test (test_prefix, kl_strHamming_test (), " ");
	
	test_prefix = "Kl_strIndexOf";
	test (test_prefix, kl_strIndexOf_test (), " ");
	
	test_prefix = "Kl_strIsLessThan";
	test (test_prefix, kl_strIsLessThan_test (), " ");
	
	test_prefix = "Kl_strLength";
	test (test_prefix, kl_strLength_test (), " ");
	
	test_prefix = "Kl_strLevenshtein";
	test (test_prefix, kl_strLevenshtein_test (), " ");
	
	test_report();
}