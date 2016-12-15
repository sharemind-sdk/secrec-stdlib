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

/**
@page table_database Table database
@brief Table databases in SecreC

@section table_database Table database

SecreC supports a basic file-based table database system intended for storing and organizing large amounts of data to be privately processed in SecreC. 

Before creating a table a connection must be opened to the data source with the function \link table_database::tdbOpenConnection tdbOpenConnection\endlink. The name of the data source is set in Sharemind's configuration. Before opening a new connection to a different data source make sure the old connection has been closed with \link table_database::tdbCloseConnection tdbCloseConnection\endlink. A simple table with a uniform data type can be created with the function \link shared3p_table_database::tdbTableCreate tdbTableCreate\endlink. 

Listing 1: Creating an empty table

\code
	//creating an empty table with 3 columns of type int32 and protection domain shared3p
	
	import shared3p;
	import shared3p_table_database;
	import stdlib;
	import table_database;

	domain pd_shared3p shared3p;

	void main () {
		string datasource = "DS1";
		string table_name = "SimpleTable";
	
		tdbOpenConnection (datasource);
		pd_shared3p int32 data_type;
		
		tdbTableCreate (datasource, table_name, data_type);
	}
\endcode

Data is added to the table one row at a time with the function \link shared3p_table_database::tdbInsertRow tdbInsertRow\endlink. Each row is a vector with every element corresponding to a column in the table. Data is read from the table one column at a time with the function \link shared3p_table_database::tdbReadColumn tdbReadColumn\endlink. The identifier of the column can be the column's index or the name of the column. 

@subsection vmap Vector Map

A vector map (also referred to as a value map or a vmap) is a data structure for making more complicated tables with column identifiers and multiple data types. A vmap can contain four different types of information: types, strings, values and indexes. A vmap is similar to a Python dictionary as the vmap contains parameters and data associated to those parameters.

Values added to the vmap can be either fixed lenght or variable length. Variable length means that the size of values must not be uniform in one column e.g. strings or vectors with variable length. Values are stored in the vmap as batches. Each batch covers all columns but one row. Data can be added one entry at a time with<b> tdbVmapAdd{Type/String/Value/Index} </b>for fixed length data or with<b> tdbVmapAddVlen{Type/Value}</b> for variable length data.

@subsection vmap_table Creating a table from a vector map

When creating a table instead of specifing a data type and the number of columns a vmap can be used. The vmap must contain a type and string for every column with the parameters "types" and "names" respectively. Data can be inserted to the table with tdbInsertRow but instead of a vector a vmap can be used. The vmap must have values with the parameter "values" that are the same type as their respective column in the table. Every batch in the vmap corresponds to a single row in the table. 

Listing 2: Creating a table with a vector map

\code
	import shared3p;
	import shared3p_table_database;
	import stdlib;
	import table_database;

	domain pd_shared3p shared3p;

	void main() {
		string ds = "DS1"; // Data source name
		string tbl = "table"; // Table name

		// Open database before running operations on it
		tdbOpenConnection(ds);


		// Check if a table exists
		if (tdbTableExists(ds, tbl)) {
			// Delete existing table
			tdbTableDelete(ds, tbl);
		}

		// We want to create a simple table with three columns:
		//
		//       -----------------------------------------------------------
		// Name: |       "index" |      "measurement" | "have_measurement" |
		// Type: | public uint64 | pd_shared3p uint64 |   pd_shared3p bool |
		//       -----------------------------------------------------------
		//       |             0 |                  0 |               true |
		//       |             1 |                 10 |               true |
		//       |             2 |                 20 |               true |
		//       |             3 |                 30 |               true |
		//       |             4 |                 40 |               true |
		//       -----------------------------------------------------------

		// Create a new "vector map/value map" for storing arguments to the table
		// creation call.
		uint params = tdbVmapNew();

		// Column 0, name "index", type public uint64
		{
			uint64 vtype;
			tdbVmapAddType(params, "types", vtype);
			tdbVmapAddString(params, "names", "index");
		}

		// Column 1, name "measurement", type pd_shared3p uint64
		{
			pd_shared3p uint64 vtype;
			tdbVmapAddType(params, "types", vtype);
			tdbVmapAddString(params, "names", "measurement");
		}

		// Column 2, name "have_measurement", type pd_shared3p bool
		{
			pd_shared3p bool vtype;
			tdbVmapAddType(params, "types", vtype);
			tdbVmapAddString(params, "names", "have_measurement");
		}

		// Create the table
		tdbTableCreate(ds, tbl, params);

		// Free the parameter map
		tdbVmapDelete(params);

		// Insert some data
		uint nrows = 5;
		params = tdbVmapNew();

		for (uint i = 0; i < nrows; ++i) {
			uint64 index = i;
			pd_shared3p uint64 measurement = i * 10;
			pd_shared3p bool have_measurement = true;
			
			if (i != 0) {
				// This has to be called in-between rows
				tdbVmapAddBatch(params);
			}
			
			tdbVmapAddValue(params, "values", index);
			tdbVmapAddValue(params, "values", measurement);
			tdbVmapAddValue(params, "values", have_measurement);
    		}
		
    		tdbInsertRow(ds, tbl, params);
			
	}
\endcode

@subsection clean_up Erasing tables

Before creating a new table it is recommended to check if a table with the same name already exists and delete it if it does. A table can be deleted with \link table_database::tdbTableDelete tdbTableDelete\endlink. After the program finishes SecreC automatically deletes all existing vector maps but for safety concerns it is advised to do this manually.

**/
