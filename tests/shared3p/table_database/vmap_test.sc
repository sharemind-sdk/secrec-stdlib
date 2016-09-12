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
import shared3p_table_database;
import table_database;
import test_utility;

domain pd_shared3p shared3p;


//do multiple tests on one value map and return the results as an array
template<domain D : shared3p, type T>
bool[[1]] vmap_test (D T data) {
	uint vmap_id = tdbVmapNew ();

	{
		D T data_type;
		tdbVmapAddType (vmap_id, "types", data_type);
		tdbVmapAddString (vmap_id, "names", "value1");
	}

	{
		D T data_type;
		tdbVmapAddType (vmap_id, "types", data_type);
		tdbVmapAddString (vmap_id, "names", "value2");
	}


	bool[[1]] test_results (7) = false;

	test_results[1] = vmap_getValue_test 	(data, vmap_id);
	test_results[2] = vmap_getType_test 	(data, vmap_id);
	test_results[3] = vmap_getString_test 	(data, vmap_id);
	test_results[4] = vmap_getIndex_test 	(data, vmap_id);
	test_results[5] = vmap_count_test 		(data, vmap_id);
	test_results[6] = vmap_erase_test 		(data, vmap_id);

	test_results[0] = all (test_results[1:]);

	return test_results;
}



template<domain D : shared3p, type T>
bool vmap_getValue_test (D T data, uint vmap_id) {
	tdbVmapDelete (vmap_id);
	vmap_id = tdbVmapNew();

	D T[[1]] value1 (5) = 0;
	D T[[1]] value2 (5) = 0;

	for (uint i = 0; i < 5; i++) {
		value1 [i] = ((T) i) * 10;
		value2 [i] = ((T) i) + 1;
	}

	tdbVmapAddValue(vmap_id, "values", value1);
	tdbVmapAddValue(vmap_id, "values", value2);

	value1 = tdbVmapGetValue (vmap_id, "values", 0::uint);
	value2 = tdbVmapGetValue (vmap_id, "values", 1::uint);

	D T[[1]] expected_value1 = {0, 10, 20, 30, 40};
	D T[[1]] expected_value2 = {1, 2, 3, 4, 5};

	bool result1 = all (declassify (value1) == declassify (expected_value1));
	bool result2 = all (declassify (value2) == declassify (expected_value2));

	return result1 && result2;
}


template<domain D : shared3p, type T>
bool vmap_getType_test (D T data, uint vmap_id) {
	tdbVmapDelete (vmap_id);
	uint vmap_id = tdbVmapNew ();

	{
		D T data_type;
		tdbVmapAddType (vmap_id, "types", data_type);
		tdbVmapAddString (vmap_id, "names", "value1");
	}

	{
		D T data_type;
		tdbVmapAddType (vmap_id, "types", data_type);
		tdbVmapAddString (vmap_id, "names", "value2");
	}

	string result = tdbVmapGetTypeName (vmap_id, "types", 0::uint);
	string expected_result = "$T";
	return result == expected_result;
}


template<domain D : shared3p, type T>
bool vmap_getString_test (D T data, uint vmap_id) {
	string result = tdbVmapGetString (vmap_id, "names", 0::uint);
	string expected_result = "value1";

	return expected_result == result;
}


template<domain D : shared3p, type T>
bool vmap_getIndex_test (D T data, uint vmap_id) {
	tdbVmapDelete (vmap_id);
	uint vmap_id = tdbVmapNew ();

	tdbVmapAddIndex (vmap_id, "indexes", 50::uint);
	tdbVmapAddIndex (vmap_id, "indexes", 51::uint);

	uint result1 = tdbVmapGetIndex (vmap_id, "indexes", 0::uint);
	uint result2 = tdbVmapGetIndex (vmap_id, "indexes", 1::uint);

	uint expected_result1 = 50;
	uint expected_result2 = 51;

	return result1 == expected_result1 && result2 == expected_result2;
}


template<domain D : shared3p, type T>
bool vmap_count_test (D T data, uint vmap_id) {
	uint result = tdbVmapCount (vmap_id, "indexes");
	uint expected_result = 1;

	return result == expected_result;
}


template<domain D : shared3p, type T>
bool vmap_erase_test (D T data, uint vmap_id) {
	tdbVmapErase (vmap_id, "indexes");
	uint result = tdbVmapCount (vmap_id, "indexes");
	uint expected_result = 0;

	return result == expected_result;

}


void main () {
	{	pd_shared3p int8 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p int16 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p int32 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p int64 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p uint8 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p uint16 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p uint32 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p uint64 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p float32 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	{	pd_shared3p float64 a;
		bool[[1]] test_results = vmap_test(a);

		test ("TdbVmapNew", 		test_results[0], a);
		test ("TdbVmapGetValue", 	test_results[1], a);
		test ("TdbVmapGetType", 	test_results[2], a);
		test ("TdbVmapGetString", 	test_results[3], a);
		test ("TdbVmapGetIndex", 	test_results[4], a);
		test ("TdbVmapCount",	 	test_results[5], a);
		test ("TdbVmapErase",	 	test_results[6], a); }

	test_report();
}
