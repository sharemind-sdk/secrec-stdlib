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
	bool[[1]] test_results (6) = false;
	
	//complete the individual tests
	test_results[1] = shared3p_table_insertRow_test	 (data, data_source, table_name);
	test_results[2] = shared3p_table_readColumn_test (data, data_source, table_name);
	test_results[3] = table_getColumnCount_test		 (data, data_source, table_name);
	test_results[4] = table_getRowCount_test (data, data_source, table_name);
	test_results[5] = table_exists_test (data, data_source, table_name);
	
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


void main () {	
	{pd_shared3p uint8 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}

	{pd_shared3p uint16 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p uint32 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p uint64 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p int8 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p int16 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p int32 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p int64 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p float32 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p float64 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}

	/*
	{pd_shared3p xor_uint8 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p xor_uint16 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p xor_uint32 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	
	{pd_shared3p xor_uint64 a; 
	 bool[[1]] results = shared3p_table_test (a);
	 
	 test ("tbdCreateTable",	results[0], a);
	 test ("tbdInsertRow",  	results[1], a);
	 test ("tbdReadColumn", 	results[2], a);
	 test ("tbdGetColumnCount", results[3], a);
	 test ("tbdGetRowCount",	results[4], a);
	 test ("tbdTableDelete",	results[5], a);
	}
	*/
	test_report ();
}