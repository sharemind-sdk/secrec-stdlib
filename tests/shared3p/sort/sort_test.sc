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
import test_utility;
import shared3p_sort;

domain pd_shared3p shared3p;


template<domain D, type T>
bool radix_test (D T data) {
	D T[[1]] a = {1, 0, 2, 9, 3, 8, 4, 7, 5, 6};
	D T[[1]] b = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	D T[[1]] c = {4, 5, 6, 7, 8, 9, 10, 11, 12};
	
	D T[[1]] result1 = radixSort (a);
	D T[[1]] result2 = radixSort (b);
	D T[[1]] result3 = radixSort (c);
	
	D T[[1]] expected_result1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	D T[[1]] expected_result2 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	D T[[1]] expected_result3 = {4, 5, 6, 7, 8, 9, 10, 11, 12};
	
	bool x = all (declassify (result1 == expected_result1));
	bool y = all (declassify (result2 == expected_result2));
	bool z = all (declassify (result3 == expected_result3));
	
	return x && y && z;	
}


template<domain D, type T>
bool quick_test (D T data) {
	D T[[1]] a = {1, 0, 2, 9, 3, 8, 4, 7, 5, 6};
	D T[[1]] b = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	D T[[1]] c = {4, 5, 6, 7, 8, 9, 10, 11, 12};
	
	D T[[1]] result1 = quicksort (a);
	D T[[1]] result2 = quicksort (b);
	D T[[1]] result3 = quicksort (c);
	
	D T[[1]] expected_result1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	D T[[1]] expected_result2 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	D T[[1]] expected_result3 = {4, 5, 6, 7, 8, 9, 10, 11, 12};
	
	bool x = all (declassify (result1 == expected_result1));
	bool y = all (declassify (result2 == expected_result2));
	bool z = all (declassify (result3 == expected_result3));
	
	return x && y && z;	
}


template<domain D, type T>
bool quickquick_test (D T data) {
	D T[[1]] a = {1, 0, 2, 9, 3, 8, 4, 7, 5, 6};
	D T[[1]] b = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	D T[[1]] c = {4, 5, 6, 7, 8, 9, 10, 11, 12};
	
	D T[[1]] result1 = quickquicksort (a);
	D T[[1]] result2 = quickquicksort (b);
	D T[[1]] result3 = quickquicksort (c);
	
	D T[[1]] expected_result1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	D T[[1]] expected_result2 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	D T[[1]] expected_result3 = {4, 5, 6, 7, 8, 9, 10, 11, 12};
	
	bool x = all (declassify (result1 == expected_result1));
	bool y = all (declassify (result2 == expected_result2));
	bool z = all (declassify (result3 == expected_result3));
	
	return x && y && z;	
}

template<domain D, type T>
bool radix_matrix_test (D T data) {
	D T[[2]] a = reshape({2, 2, 20,
						  4, 4, 40,
						  3, 3, 30,
						  1, 1, 10,
						  5, 5, 50}, 5, 3);

	D T[[2]] expected_result = reshape({1, 1, 10,
										2, 2, 20,
										3, 3, 30,
						  				4, 4, 40,
						  				5, 5, 50}, 5, 3);
	
	D T[[2]] result = radixSort (a, 2::uint);	
	bool x = all (declassify (result == expected_result));

	return x;	
}


template<domain D, type T>
bool quick_matrix_test (D T data) {
	D T[[2]] a = reshape({2, 2, 20,
						  4, 4, 40,
						  3, 3, 30,
						  1, 1, 10,
						  5, 5, 50}, 5, 3);

	D T[[2]] expected_result = reshape({1, 1, 10,
										2, 2, 20,
										3, 3, 30,
						  				4, 4, 40,
						  				5, 5, 50}, 5, 3);
	
	D T[[2]] result = quicksort (a, 2::uint);	
	bool x = all (declassify (result == expected_result));

	return x;	
}


template<domain D, type T>
bool quickquick_matrix_test (D T data) {
	D T[[2]] a = reshape({2, 2, 20,
						  4, 4, 40,
						  3, 3, 30,
						  1, 1, 10,
						  5, 5, 50}, 5, 3);

	D T[[2]] expected_result = reshape({1, 1, 10,
										2, 2, 20,
										3, 3, 30,
						  				4, 4, 40,
						  				5, 5, 50}, 5, 3);
	
	D T[[2]] result = quickquicksort (a, 2::uint);	
	bool x = all (declassify (result == expected_result));

	return x;	
}


template<domain D, type T>
bool radix_index_test (D T data) {
	D T[[1]] a = {10, 0, 20, 90, 30, 80, 40, 70, 50, 60};
	D uint[[1]] index_vec = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	
	D uint[[1]] result = radixSortWithIndex (a, index_vec);
	
	D uint[[1]] expected_result = {1, 0, 2, 4, 6, 8, 9, 7, 5, 3};

	printVector (declassify (result));
	
	bool x = all (declassify (result == expected_result));

	return x ;	
}


void main () {
	{pd_shared3p uint64 a; 		test ("RadixSort(vector)", radix_test (a), a);}
	{pd_shared3p xor_uint64 a; 	test ("RadixSort(vector)", radix_test (a), a);}

	{pd_shared3p xor_uint64 a;	test ("Quicksort(vector)", quick_test (a), a);}
	
	{pd_shared3p xor_uint64 a;	test ("Quickquicksort(vector)", quickquick_test (a), a);}
	
	{pd_shared3p uint64 a; 		test ("RadixSort(matrix)", radix_matrix_test (a), a);}
	{pd_shared3p xor_uint64 a;	test ("RadixSort(matrix)", radix_matrix_test (a), a);}
	
	{pd_shared3p xor_uint64 a;	test ("Quickquickqort(matrix)", quick_matrix_test (a), a);}
	{pd_shared3p xor_uint64 a;	test ("Quickquicksort(matrix)", quickquick_matrix_test (a), a);}
	
	//{pd_shared3p uint64 a; 		test ("RadixSortWithIndex", radix_index_test (a), a);}
	{pd_shared3p xor_uint64 a; 	test ("RadixSortWithIndex", radix_index_test (a), a);}
	
	test_report();
}