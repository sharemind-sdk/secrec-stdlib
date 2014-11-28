/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

module matrix_test;

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

template<domain D : shared3p,type T>
void test_transpose(D T data){
	{
		pd_shared3p T[[2]] mat (0,0);
		mat = transpose(mat);
		pd_shared3p T[[2]] mat2 (2,0);
		mat2 = transpose(mat2);
		pd_shared3p T[[2]] mat3 (0,2);
		mat3 = transpose(mat3);
	}
	bool result = true;
	pd_shared3p T[[2]] mat (6,6);
	mat = randomize(mat);
	pd_shared3p T[[2]] mat2 = transpose(mat);
	for(uint i = 0; i < 6; ++i){
		if(!all(declassify(mat[:,i]) == declassify(mat2[i,:]))){
			result = false;
		}	
	}
	if(result){
 		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
 	}
 	else{
 		print("FAILURE! transposing failed"); print("Got this : ");
 		printMatrix(declassify(mat2)); print(" From this: ");
 		printMatrix(declassify(mat));
 		all_tests = all_tests +1;
 	}
}

template<domain D : shared3p,type T>
void test_transpose2(D T data){
	{
		pd_shared3p T[[3]] mat (0,0,0);
		mat = transpose(mat);
		pd_shared3p T[[3]] mat2 (0,2,0);
		mat2 = transpose(mat2);
		pd_shared3p T[[3]] mat3 (0,0,2);
		mat3 = transpose(mat3);
		pd_shared3p T[[3]] mat4 (2,0,0);
		mat4 = transpose(mat4);
		pd_shared3p T[[3]] mat5 (2,0,2);
		mat5 = transpose(mat5);
		pd_shared3p T[[3]] mat6 (2,2,0);
		mat6 = transpose(mat6);
	}
	bool result = true;
	pd_shared3p T[[3]] mat (5,5,5);
	mat = randomize(mat);
	pd_shared3p T[[3]] mat2 = transpose(mat);
	for(uint i = 0; i < 5; ++i){
		if(!all(declassify(mat[:,i,:]) == declassify(mat2[:,:,i]))){
			result = false;
		}
	}
	if(result){
 		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
 	}
 	else{
 		print("FAILURE! transposing failed"); print("Got this : ");
 		printVector(flatten(declassify(mat2))); print(" From this: ");
 		printVector(flatten(declassify(mat)));
 		all_tests = all_tests +1;
 	}
}

void main(){
	print("Matrix test: start");

	print("TEST 1: Matrix transpose 2D and 3D");
	{
		print("bool");
		pd_shared3p bool data = false;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("uint8");
		pd_shared3p uint8 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("uint16");
		pd_shared3p uint16 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("uint32");
		pd_shared3p uint32 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("uint64/uint");
		pd_shared3p uint64 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("int8");
		pd_shared3p int8 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("int16");
		pd_shared3p int16 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("int32");
		pd_shared3p int32 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("int64/int");
		pd_shared3p int data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64 data = 0;
		test_transpose(data);
		test_transpose2(data);
	}

	print("Test finished!");
	print("Succeeded tests: ", succeeded_tests);
	print("Failed tests: ", all_tests - succeeded_tests);

    test_report(all_tests, succeeded_tests);
}
