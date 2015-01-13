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

module shuffling_test;

import stdlib;
import matrix;
import shared3p;
import shared3p_matrix;
import oblivious;
import shared3p_random;
import shared3p_sort;
import shared3p_bloom;
import shared3p_string;
import shared3p_aes;
import shared3p_join;
import profiling;
import test_utility;

domain pd_shared3p shared3p;

public uint all_tests;
public uint succeeded_tests;
public bool test_result;

template<type T>
void vector_shuffle_test(T data){
	{
		pd_shared3p T[[1]] vec (0);
		vec = shuffle(vec);
	}
	pd_shared3p T[[1]] vec (50);
	vec = randomize(vec);
	T[[1]] vec2 = declassify(vec);
	vec = shuffle(shuffle(vec));
	T[[1]] vec3 = declassify(vec);
	bool[[1]] result = (vec2 == vec3);
	if(all(result)){
 		print("FAILURE! double shuffling resulted in the same vector");
 		all_tests = all_tests +1;
 	}
 	else{
 		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
}

template<domain D : shared3p, type T>
void vector_shuffle_test_xor_uint(D T data){
	{
		pd_shared3p T[[1]] vec (0);
		vec = shuffle(vec);
	}
	pd_shared3p T[[1]] vec (50);
	vec = randomize(vec);
	pd_shared3p T[[1]] vec2 = vec;
	vec = shuffle(shuffle(vec));
	bool[[1]] result = declassify(vec == vec2);
	if(all(result)){
 		print("FAILURE! double shuffling resulted in the same vector");
 		all_tests = all_tests +1;
 	}
 	else{
 		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
}

template<type T>
void vector_shuffle_test_key(T data){
	{
		pd_shared3p T[[1]] vec (0);
		pd_shared3p uint8[[1]] key (32);
		vec = shuffle(vec,key);
	}
	pd_shared3p uint8[[1]] key (32); 
	pd_shared3p T[[1]] vec (50);
	pd_shared3p T[[1]] vec2 (50);
	key = randomize(key);
	vec = randomize(vec);
	vec2 = vec;
	vec = shuffle(vec,key);
	vec2 = shuffle(vec2,key);
	T[[1]] control = declassify(vec);
	T[[1]] control2 = declassify(vec2);
	bool[[1]] result = (control == control2);
	if(all(result)){
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
 	}
 	else{
 		print("FAILURE! shuffling same vectors with same key resulted in different results");
 		all_tests = all_tests +1;
	}
}

template<domain D : shared3p, type T>
void vector_shuffle_test_xor_uint_key(D T data){
	{
		pd_shared3p T[[1]] vec (0);
		pd_shared3p uint8[[1]] key (32);
		vec = shuffle(vec,key);
	}
	pd_shared3p uint8[[1]] key (32);
	pd_shared3p T[[1]] vec (50);
	pd_shared3p T[[1]] vec2 = vec;
	key = randomize(key);
	vec = randomize(vec);
	vec2 = vec;
	vec = shuffle(vec,key);
	vec2 = shuffle(vec2,key);
	bool[[1]] result = declassify(vec == vec2);
	if(all(result)){
 		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
 	}
 	else{
 		print("FAILURE! double shuffling resulted in the same vector");
 		all_tests = all_tests +1;
	}
}

template<type T>
void matrix_shuffle_test(T data){
	{
		pd_shared3p T[[2]] mat (0,0);
		mat = shuffleRows(mat);
		pd_shared3p T[[2]] mat2 (3,0);
		mat2 = shuffleRows(mat2);
		pd_shared3p T[[2]] mat3 (0,3);
		mat3 = shuffleRows(mat3);
	}
	pd_shared3p T[[2]] mat (50,6);
	mat = randomize(mat);
	T[[2]] mat2 = declassify(mat);
	mat = shuffleRows(mat);
	T[[2]] mat3 = declassify(mat);
	bool[[2]] result = (mat2 == mat3);
	if(all(result)){
 		print("FAILURE! shuffling rows resulted in the same matrix");
 		all_tests = all_tests +1;
 	}
 	else{
 		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
}

template<domain D : shared3p, type T>
void matrix_shuffle_test_xor_uint(D T data){
	{
		pd_shared3p T[[2]] mat (0,0);
		mat = shuffleRows(mat);
		pd_shared3p T[[2]] mat2 (3,0);
		mat2 = shuffleRows(mat2);
		pd_shared3p T[[2]] mat3 (0,3);
		mat3 = shuffleRows(mat3);
	}
	pd_shared3p T[[2]] mat (50,6);
	mat = randomize(mat);
	pd_shared3p T[[2]] mat2 = mat;
	mat = shuffleRows(mat);
	bool[[2]] result = declassify(mat == mat2);
	if(all(result)){
 		print("FAILURE! shuffling rows resulted in the same matrix");
 		all_tests = all_tests +1;
 	}
 	else{
 		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
}

template<type T>
void matrix_shuffle_test_key(T data){
	{
		pd_shared3p uint8[[1]] key (32); 
		pd_shared3p T[[2]] mat (0,0);
		mat = shuffleRows(mat,key);
		pd_shared3p T[[2]] mat2 (3,0);
		mat2 = shuffleRows(mat2,key);
		pd_shared3p T[[2]] mat3 (0,3);
		mat3 = shuffleRows(mat3,key);
	}
	pd_shared3p uint8[[1]] key (32); 
	pd_shared3p T[[2]] mat (50,6);
	pd_shared3p T[[2]] mat2 (50,6);
	key = randomize(key);
	mat = randomize(mat);
	mat2 = mat;
	mat = shuffleRows(mat,key);
	mat2 = shuffleRows(mat2,key);
	T[[2]] control = declassify(mat);
	T[[2]] control2 = declassify(mat2);
	bool[[2]] result = (control == control2);
	if(all(result)){
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
 	}
 	else{
 		print("FAILURE! shuffling same matrices with same key resulted in different matrices");
 		all_tests = all_tests +1;
	}
}

template<domain D : shared3p, type T>
void matrix_shuffle_test_xor_uint_key(D T data){
	{
		pd_shared3p uint8[[1]] key (32); 
		pd_shared3p T[[2]] mat (0,0);
		mat = shuffleRows(mat,key);
		pd_shared3p T[[2]] mat2 (3,0);
		mat2 = shuffleRows(mat2,key);
		pd_shared3p T[[2]] mat3 (0,3);
		mat3 = shuffleRows(mat3,key);
	}
	pd_shared3p uint8[[1]] key (32);
	pd_shared3p T[[2]] mat (50,6);
	pd_shared3p T[[2]] mat2 = mat;
	key = randomize(key);
	mat = randomize(mat);
	mat2 = mat;
	mat = shuffleRows(mat,key);
	mat2 = shuffleRows(mat2,key);
	bool[[2]] result = declassify(mat == mat2);
	if(all(result)){
 		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
 	}
 	else{
 		print("FAILURE! double shuffling resulted in the same vector");
 		all_tests = all_tests +1;
	}
}

template<type T>
void random_test(T data){
	pd_shared3p T scalar;
	pd_shared3p T scalar2 = randomize(randomize(scalar));
	if(declassify(scalar) != declassify(scalar2)){
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
 	}
 	else{
 		print("FAILURE! Randomizing failed");
 		all_tests = all_tests +1;
	}
	{
		pd_shared3p T[[1]] vec (0);
		vec = randomize(vec);
	}
	pd_shared3p T[[1]] vec (25);
	pd_shared3p T[[1]] vec2 = randomize(randomize(vec));
	if(all(declassify(vec) == declassify(vec2))){
		print("FAILURE! Randomizing failed");
 		all_tests = all_tests +1;
	}
	else{
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
}

template<domain D:shared3p,type T>
void random_test_xor(D T data){
	D T scalar;
	D T scalar2 = randomize(randomize(scalar));
	if(declassify(scalar) != declassify(scalar2)){
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
 	}
 	else{
 		print("FAILURE! Randomizing failed");
 		all_tests = all_tests +1;
	}
	{
		pd_shared3p T[[1]] vec (0);
		vec = randomize(vec);
	}
	D T[[1]] vec (25);
	D T[[1]] vec2 = randomize(randomize(vec));
	if(all(declassify(vec) == declassify(vec2))){
		print("FAILURE! Randomizing failed");
 		all_tests = all_tests +1;
	}
	else{
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
}

void main(){
	public int8 INT8_MAX = 127;
	public int8 INT8_MIN = -128;
	public int16 INT16_MAX = 32767;
	public int16 INT16_MIN = -32768;
	public int32 INT32_MAX = 2147483647;
	public int32 INT32_MIN = -2147483648;
	public int64 INT64_MAX = 9223372036854775807;
	public int64 INT64_MIN = -9223372036854775808;
 
 	public uint8 UINT8_MAX = 255; //2^8 - 1
 	public uint16 UINT16_MAX = 65535; // 2^16 - 1
 	public uint32 UINT32_MAX = 4294967295; // 2^32 - 1
 	public uint64 UINT64_MAX = 18446744073709551615; //2^64 - 1


	print("Shuffling test: start");

	print("Test 1: Shuffling vectors with 50 elements without key");
	{
		print("bool");
		vector_shuffle_test(false);
	}
	{
		print("uint8");
		vector_shuffle_test(0::uint8);
	}
	{
		print("uint16");
		vector_shuffle_test(0::uint16);
	}
	{
		print("uint32");
		vector_shuffle_test(0::uint32);
	}
	{
		print("uint64/uint");
		vector_shuffle_test(0::uint);
	}
	{
		print("int8");
		vector_shuffle_test(0::int8);
	}
	{
		print("int16");
		vector_shuffle_test(0::int16);
	}
	{
		print("int32");
		vector_shuffle_test(0::int32);
	}
	{
		print("int64/int");
		vector_shuffle_test(0::int);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8 data = 0;
		vector_shuffle_test_xor_uint(data);
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16 data = 0;
		vector_shuffle_test_xor_uint(data);
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32 data = 0;
		vector_shuffle_test_xor_uint(data);
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64 data = 0;
		vector_shuffle_test_xor_uint(data);
	}
	print("Test 2: Shuffling vectors with 50 elements with key");
	{
		print("bool");
		vector_shuffle_test_key(false);
	}
	{
		print("uint8");
		vector_shuffle_test_key(0::uint8);
	}
	{
		print("uint16");
		vector_shuffle_test_key(0::uint16);
	}
	{
		print("uint32");
		vector_shuffle_test_key(0::uint32);
	}
	{
		print("uint64/uint");
		vector_shuffle_test_key(0::uint);
	}
	{
		print("int8");
		vector_shuffle_test_key(0::int8);
	}
	{
		print("int16");
		vector_shuffle_test_key(0::int16);
	}
	{
		print("int32");
		vector_shuffle_test_key(0::int32);
	}
	{
		print("int64/int");
		vector_shuffle_test_key(0::int);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8 data = 0;
		vector_shuffle_test_xor_uint_key(data);
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16 data = 0;
		vector_shuffle_test_xor_uint_key(data);
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32 data = 0;
		vector_shuffle_test_xor_uint_key(data);
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64 data = 0;
		vector_shuffle_test_xor_uint_key(data);
	}
	print("Test 3: Shuffling rows of (50,6) matrix without key");
	{
		print("bool");
		matrix_shuffle_test(false);
	}
	{
		print("uint8");
		matrix_shuffle_test(0::uint8);
	}
	{
		print("uint16");
		matrix_shuffle_test(0::uint16);
	}
	{
		print("uint32");
		matrix_shuffle_test(0::uint32);
	}
	{
		print("uint64/uint");
		matrix_shuffle_test(0::uint);
	}
	{
		print("int8");
		matrix_shuffle_test(0::int8);
	}
	{
		print("int16");
		matrix_shuffle_test(0::int16);
	}
	{
		print("int32");
		matrix_shuffle_test(0::int32);
	}
	{
		print("int64/int");
		matrix_shuffle_test(0::int);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8 data = 0;
		matrix_shuffle_test_xor_uint(data);
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16 data = 0;
		matrix_shuffle_test_xor_uint(data);
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32 data = 0;
		matrix_shuffle_test_xor_uint(data);
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64 data = 0;
		matrix_shuffle_test_xor_uint(data);
	}
	print("Test 4: Shuffling rows of (50,6) matrix with key");
	{
		print("bool");
		matrix_shuffle_test_key(false);
	}
	{
		print("uint8");
		matrix_shuffle_test_key(0::uint8);
	}
	{
		print("uint16");
		matrix_shuffle_test_key(0::uint16);
	}
	{
		print("uint32");
		matrix_shuffle_test_key(0::uint32);
	}
	{
		print("uint64/uint");
		matrix_shuffle_test_key(0::uint);
	}
	{
		print("int8");
		matrix_shuffle_test_key(0::int8);
	}
	{
		print("int16");
		matrix_shuffle_test_key(0::int16);
	}
	{
		print("int32");
		matrix_shuffle_test_key(0::int32);
	}
	{
		print("int64/int");
		matrix_shuffle_test_key(0::int);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8 data = 0;
		matrix_shuffle_test_xor_uint_key(data);
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16 data = 0;
		matrix_shuffle_test_xor_uint_key(data);
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32 data = 0;
		matrix_shuffle_test_xor_uint_key(data);
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64 data = 0;
		matrix_shuffle_test_xor_uint_key(data);
	}
	print("TEST 5: Random test");
	{
		print("bool");
		random_test(false);
	}
	{
		print("uint8");
		random_test(0::uint8);
	}
	{
		print("uint16");
		random_test(0::uint16);
	}
	{
		print("uint32");
		random_test(0::uint32);
	}
	{
		print("uint64/uint");
		random_test(0::uint);
	}
	{
		print("int8");
		random_test(0::int8);
	}
	{
		print("int16");
		random_test(0::int16);
	}
	{
		print("int32");
		random_test(0::int32);
	}
	{
		print("int64/int");
		random_test(0::int);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8 data = 0;
		random_test_xor(data);
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16 data = 0;
		random_test_xor(data);
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32 data = 0;
		random_test_xor(data);
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64 data = 0;
		random_test_xor(data);
	}

	print("Test finished!");
	print("Succeeded tests: ", succeeded_tests);
	print("Failed tests: ", all_tests - succeeded_tests);

    test_report(all_tests, succeeded_tests);
}
