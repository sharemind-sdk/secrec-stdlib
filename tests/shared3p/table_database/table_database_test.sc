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


//test for all the functions and ways to manipulate a shared3p table
//returns the results of every test in a single array
template<domain D : shared3p, type T>
bool[[1]] shared3p_table_test (D T data) {
	string data_source = "DS1";
	string table_name = "TestTable";
	D T table_data_type;  //determines the type of data stored in the table

	tdbOpenConnection(data_source);

	//test if the table already exists, if so, delete the table
	if (tdbTableExists(data_source, table_name)) {
		print("Deleting existing table: ", table_name);
		tdbTableDelete(data_source, table_name);
	}

	//create a table with 5 columns
	uint64 columns = 5;
	tdbTableCreate(data_source, table_name, table_data_type, columns);

	//array containing the results of every test done on the current table
	//the first test is table creation (this function)
	bool[[1]] test_results (8) = false;

	//complete the individual tests
	test_results[1] = shared3p_table_insertRow_test	 (data, data_source, table_name);
	test_results[2] = shared3p_table_readColumn_test (data, data_source, table_name);
	test_results[3] = table_getColumnCount_test		 (data, data_source, table_name);
	test_results[4] = table_getRowCount_test 		 (data, data_source, table_name);
	test_results[5] = table_exists_test 			 (data, data_source, table_name);
	test_results[6] = table_insertRow_test_vmap		 (data, data_source, table_name);
	test_results[7] = value_as_column_test			 (data, data_source, table_name);

	//table creation passes only if all other tests pass
	test_results[0] = all (test_results[1:]);

	tdbTableDelete (data_source, table_name);
	tdbCloseConnection (data_source);

	return test_results;
}


template<domain D : shared3p, type T>
bool shared3p_table_insertRow_test (D T data, string data_source, string table_name) {
	D T[[1]] a = {1, 3, 7, 15, 31};

	//add data to the table row by row
	for (uint i = 0; i < 5; i++) {
		tdbInsertRow (data_source, table_name, a);
	}

	//expected results are the sums of all columns
	D T[[1]] expected_results = a * 5;
	bool[[1]] test_results (5) = false;

	//calculate the sum of every column and compare them to the expected results
	for (uint i = 0; i < 5; i++) {
		D T[[1]] column_data = tdbReadColumn (data_source, table_name, i);

		//compare the results
		test_results[i] = sum (declassify (column_data)) == declassify (expected_results[i]);
	}

	//if all columns had the correct values the test passes
	return all (test_results);
}


template<domain D : shared3p, type T>
bool table_insertRow_test_vmap (D T data, string data_source, string table_name) {
	tdbTableDelete(data_source, table_name);

	uint vmap_id = tdbVmapNew();

	D T data_type;
	tdbVmapAddType (vmap_id, "types", data_type);
	tdbVmapAddString (vmap_id, "names", "value1");

	tdbVmapAddType (vmap_id, "types", data_type);
	tdbVmapAddString (vmap_id, "names", "value2");


	tdbTableCreate(data_source, table_name, vmap_id);

	for (int i = 1; i < 6; i++) {
		pd_shared3p int value1_temp = i * 10;
		pd_shared3p int value2_temp = i + 1;

		D T value1 = (T) value1_temp;
		D T value2 = (T) value2_temp;

		if (i != 1) {
			tdbVmapAddBatch(vmap_id);
		}

		tdbVmapAddValue(vmap_id, "values", value1);
		tdbVmapAddValue(vmap_id, "values", value2);
	}

	tdbInsertRow (data_source, table_name, vmap_id);

	D T[[1]] value1 = tdbReadColumn(data_source, table_name, "value1");
	D T[[1]] value2 = tdbReadColumn(data_source, table_name, 1::uint);

	D T[[1]] expected_value1 = {10, 20, 30, 40, 50};
	D T[[1]] expected_value2 = {2, 3, 4, 5, 6};

	bool result1 = all (declassify (value1) == declassify (expected_value1));
	bool result2 = all (declassify (value2) == declassify (expected_value2));

	tdbVmapDelete (vmap_id);

	return result1 && result2;
}

template<domain D : shared3p, type T>
bool shared3p_table_readColumn_test (D T data, string data_source, string table_name) {
	//expected results are the sums of all columns
	D T[[1]] expected_results = {1, 3, 7, 15, 31} * 5;
	bool[[1]] test_results (5) = false;

	//calculate the sum of every column and compare them to the expected results
	for (uint i = 0; i < 5; i++) {
		D T[[1]] column_data = tdbReadColumn (data_source, table_name, i);

		//compare the results
		test_results[i] = sum (declassify (column_data)) == declassify (expected_results[i]);
	}

	//if all columns had the correct values the test passes
	return all (test_results);
}

template<domain D : shared3p, type T>
bool table_getColumnCount_test (D T data, string data_source, string table_name) {
	uint columns = tdbGetColumnCount (data_source, table_name);

	return columns == 5;
}


template<domain D : shared3p, type T>
bool table_getRowCount_test (D T data, string data_source, string table_name) {
	uint rows = tdbGetRowCount (data_source, table_name);

	return rows == 5;
}


template<domain D : shared3p, type T>
bool table_exists_test (D T data, string data_source, string table_name) {
	return tdbTableExists (data_source, table_name);
}


template<domain D : shared3p, type T>
bool value_as_column_test (D T data, string data_source, string table_name) {
	uint vmap_id = tdbVmapNew();
	tdbTableDelete(data_source, table_name);

	{
		D T vType;
		tdbVmapAddType (vmap_id, "types", vType);
		tdbVmapAddString (vmap_id, "names", "value1");
	}

	{
		D T vType;
		tdbVmapAddType (vmap_id, "types", vType);
		tdbVmapAddString (vmap_id, "names", "value2");
	}

	tdbTableCreate(data_source, table_name, vmap_id);

	__syscall("tdb_vmap_push_back_index", vmap_id, __cref "valueAsColumn", 1 :: uint64);

	D T[[1]] data1 = {50, 60, 70, 80, 90};
	D T[[1]] data2 = {5, 6, 7, 8, 9};

	tdbVmapAddValue(vmap_id, "values", data1);
	tdbVmapAddValue(vmap_id, "values", data2);
	tdbInsertRow (data_source, table_name, vmap_id);

	D T[[1]] result1 = tdbReadColumn (data_source, table_name, 0::uint64);
	D T[[1]] result2 = tdbReadColumn (data_source, table_name, 1::uint64);

	bool a = all (declassify (result1) == declassify (data1));
	bool b = all (declassify (result2) == declassify (data2));

	return a && b;
}


void main () {
	{pd_shared3p uint8 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p uint16 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p uint32 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p uint64 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p int8 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p int16 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p int32 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p int64 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p float32 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	{pd_shared3p float64 a;
	 bool[[1]] results = shared3p_table_test (a);

	 test ("tdbCreateTable",	  results[0], a);
	 test ("tdbInsertRow",  	  results[1], a);
	 test ("tdbReadColumn", 	  results[2], a);
	 test ("tdbGetColumnCount",   results[3], a);
	 test ("tdbGetRowCount",	  results[4], a);
	 test ("tdbTableDelete",	  results[5], a);
	 test ("tdbInsertRow (vmap)", results[6], a);
	 test ("ValueAsColumn",		  results[7], a);
	}

	test_report ();
}