/*
 * Copyright (C) 2018 Cybernetica
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
@page tdb_vmap Vector map
@brief Vector maps in SecreC

@section tdb_vmap_section Vector map

The table database module includes a data structure called vector map which can be used for heterogeneous vectors, for dynamically typed data or as an associative data structure (similar to Python dictionaries). The \link table_database table database \endlink page describes how vector maps are used for working with databases.

A vector in a vector map contains SecreC vectors. Vector map string vectors are an exception as they can only contain SecreC strings since SecreC doesn't support string vectors.

Listing 1: Examples

\code

import shared3p;
import shared3p_table_database;
import stdlib;
import table_database;

domain pd_shared3p shared3p;

void main() {
    uint vmap = tdbVmapNew();

    // We can add values of different type to the same vector so this
    // can be used as a heterogeneous collection.
    uint[[1]] x = {1, 2};
    tdbVmapAddValue(vmap, "key", x);
    float[[1]] y = {30};
    tdbVmapAddValue(vmap, "key", y);

    uint[[1]] xres = tdbVmapGetValue(vmap, "key", 0 :: uint);
    print("First element of 'key':");
    printVector(xres);

    float[[1]] yres = tdbVmapGetValue(vmap, "key", 1 :: uint);
    print("Second element of 'key':");
    printVector(yres);

    // We have different functions for string types. Strings and
    // primitive types can't be in the same vector so we use a
    // different key in the vector map.
    tdbVmapAddString(vmap, "strings", "foobar");
    print("First element of 'strings': ", tdbVmapGetString(vmap, "strings", 0 :: uint));

    // Count the number of elements in a vector
    print("Number of elements of 'key': ", tostring(tdbVmapValueVectorSize(vmap, "key")));

    // Count the number of vectors in a vector map with a specific
    // key. This is useful to test if the map contains the key.
    print("Map contains 'baz'? ", tdbVmapCount(vmap, "baz") > 0);

    tdbVmapDelete(vmap);
}

\endcode
**/
