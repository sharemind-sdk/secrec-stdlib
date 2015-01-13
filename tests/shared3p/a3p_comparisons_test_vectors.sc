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

module shared3p_comparisons_test_vectors;

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

template<domain D:shared3p,type T>
void larger_than(D T[[1]] vec, D T[[1]] vec2){
	if(any(declassify(vec2 > vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " > ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
	}
	else{
		succeeded_tests = succeeded_tests + 1;
	 	all_tests = all_tests +1;
	 	print("SUCCESS!");
	}
}

template<domain D:shared3p,type T>
void smaller_than(D T[[1]] vec, D T[[1]] vec2){
	if(any(declassify(vec < vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " < ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
	}
	else{
		succeeded_tests = succeeded_tests + 1;
		all_tests = all_tests +1;
		print("SUCCESS!");
	}
}

template<domain D:shared3p,type T>
void larger_than_equal(D T[[1]] vec, D T[[1]] vec2){
	if(any(declassify(vec2 >= vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " >= ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
	}
	else{
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
}

template<domain D:shared3p,type T>
void smaller_than_equal(D T[[1]] vec, D T[[1]] vec2){
	if(any(declassify(vec <= vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " <= ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
	}
	else{
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
}

template<domain D:shared3p,type T>
void equal_equal(D T[[1]] vec, D T[[1]] vec2){
	vec = randomize(vec);
	vec2 = vec;
	if(all(declassify(vec == vec2))){
		succeeded_tests = succeeded_tests + 1;
 		all_tests = all_tests +1;
 		print("SUCCESS!");
	}
	else{
		print("FAILURE! ", arrayToString(declassify(vec)) , " == ", arrayToString(declassify(vec2)));
 		all_tests = all_tests +1;
	}
}



void main(){
	print("Comparisons test: start");

	print("TEST 1: > operator");
	{
		print("uint8");
		pd_shared3p uint8[[1]] vec (6) = {5,55,105,155,205,255};
		pd_shared3p uint8[[1]] vec2 (6) = {4,40,55,2,175,0};
		larger_than(vec,vec2);
	}
	{
		print("uint16");
		pd_shared3p uint16[[1]] vec (7) = {5535,15535,25535,35535,45535,55535,65535};
		pd_shared3p uint16[[1]] vec2 (7) = {5534, 12546,24,35534,45535,39053,0};
		larger_than(vec,vec2);
	}
	{
		print("uint32");
		pd_shared3p uint32[[1]] vec (5) = {67295,1073792295,2147517294,3221242294,4294967294};
		pd_shared3p uint32[[1]] vec2 (5) = {67294,21432532,78635892,192468953,0};
		larger_than(vec,vec2);
	}
	{
		print("uint64/uint");
		pd_shared3p uint[[1]] vec (4) = {3689348814741910323,7378697629483820646,11068046444225730969,14757395258967641292};
		pd_shared3p uint[[1]] vec2 (4) = {3689348814741910322,35597629483820646,1,0};
		larger_than(vec,vec2);
	}
	{
		print("int8");
		pd_shared3p int8[[1]] vec (6) = {-127,-77,-27,23,73,123};
		pd_shared3p int8[[1]] vec2 (6) = {-128,-78,-27,-1,0,122};
		larger_than(vec,vec2);
	}
	{
		print("int16");
		pd_shared3p int16[[1]] vec (8) = {-32767,-24575,-16383,-8191,1,8193,16385,24577};
		pd_shared3p int16[[1]] vec2 (8) = {-32768,-25643,-17345,-8191,0,1,2153,21453};
		larger_than(vec,vec2);
	}
	{
		print("int32");
		pd_shared3p int32[[1]] vec (8) = {-2147483647,-1610612735,-1073741823,-536870911,1,536870913,1073741825,1610612737};
		pd_shared3p int32[[1]] vec2 (8) = {-2147483648,-1610612735,-1243259079,-9537127485,0,-1,1,1610612737};
		larger_than(vec,vec2);
	}
	{
		print("int64/int");
		pd_shared3p int[[1]] vec (6) = {-9223372036854775807,-5534023222112865484,-1844674407370955161,1844674407370955162,5534023222112865485,9223372036854775807};
		pd_shared3p int[[1]] vec2 (6) = {-9223372036854775808,-8735123222112865484,-6391824407370955161,0,-1,1};
		larger_than(vec,vec2);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8[[1]] vec (6) = {5,55,105,155,205,255};
		pd_shared3p xor_uint8[[1]] vec2 (6) = {4,40,55,2,175,0};
		if(any(declassify(vec2 > vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " > ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
		 	all_tests = all_tests +1;
		 	print("SUCCESS!");
		}
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16[[1]] vec (7) = {5535,15535,25535,35535,45535,55535,65535};
		pd_shared3p xor_uint16[[1]] vec2 (7) = {5534, 12546,24,35534,45535,39053,0};
		if(any(declassify(vec2 > vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " > ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
		 	all_tests = all_tests +1;
		 	print("SUCCESS!");
		}
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32[[1]] vec (5) = {67295,1073792295,2147517294,3221242294,4294967294};
		pd_shared3p xor_uint32[[1]] vec2 (5) = {67294,21432532,78635892,192468953,0};
		if(any(declassify(vec2 > vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " > ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
		 	all_tests = all_tests +1;
		 	print("SUCCESS!");
		}
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64[[1]] vec (4) = {3689348814741910323,7378697629483820646,11068046444225730969,14757395258967641292};
		pd_shared3p xor_uint64[[1]] vec2 (4) = {3689348814741910322,35597629483820646,1,0};
		if(any(declassify(vec2 > vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " > ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
		 	all_tests = all_tests +1;
		 	print("SUCCESS!");
		}
	}

	print("TEST 2: < operator");
	{
		print("uint8");
		pd_shared3p uint8[[1]] vec (6) = {5,55,105,155,205,255};
		pd_shared3p uint8[[1]] vec2 (6) = {4,40,55,2,175,0};
		smaller_than(vec,vec2);
	}
	{
		print("uint16");
		pd_shared3p uint16[[1]] vec (7) = {5535,15535,25535,35535,45535,55535,65535};
		pd_shared3p uint16[[1]] vec2 (7) = {5534, 12546,24,35534,45535,39053,0};
		smaller_than(vec,vec2);
	}
	{
		print("uint32");
		pd_shared3p uint32[[1]] vec (5) = {67295,1073792295,2147517294,3221242294,4294967294};
		pd_shared3p uint32[[1]] vec2 (5) = {67294,21432532,78635892,192468953,0};
		smaller_than(vec,vec2);
	}
	{
		print("uint64/uint");
		pd_shared3p uint[[1]] vec (4) = {3689348814741910323,7378697629483820646,11068046444225730969,14757395258967641292};
		pd_shared3p uint[[1]] vec2 (4) = {3689348814741910322,35597629483820646,1,0};
		smaller_than(vec,vec2);
	}
	{
		print("int8");
		pd_shared3p int8[[1]] vec (6) = {-127,-77,-27,23,73,123};
		pd_shared3p int8[[1]] vec2 (6) =  {-128,-78,-27,-1,0,122};
		smaller_than(vec,vec2);
	}
	{
		print("int16");
		pd_shared3p int16[[1]] vec (8) = {-32767,-24575,-16383,-8191,1,8193,16385,24577};
		pd_shared3p int16[[1]] vec2 (8) = {-32768,-25643,-17345,-8191,0,1,2153,21453};
		smaller_than(vec,vec2);
	}
	{
		print("int32");
		pd_shared3p int32[[1]] vec (8) = {-2147483647,-1610612735,-1073741823,-536870911,1,536870913,1073741825,1610612737};
		pd_shared3p int32[[1]] vec2 (8) = {-2147483648,-1610612735,-1243259079,-9537127485,0,-1,1,1610612737};
		smaller_than(vec,vec2);
	}
	{
		print("int64/int");
		pd_shared3p int[[1]] vec (6) = {-9223372036854775807,-5534023222112865484,-1844674407370955161,1844674407370955162,5534023222112865485,9223372036854775807};
		pd_shared3p int[[1]] vec2 (6) = {-9223372036854775808,-8735123222112865484,-6391824407370955161,0,-1,1};
		smaller_than(vec,vec2);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8[[1]] vec (6) = {5,55,105,155,205,255};
		pd_shared3p xor_uint8[[1]] vec2 (6) = {4,40,55,2,175,0};
		if(any(declassify(vec < vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " < ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16[[1]] vec (7) = {5535,15535,25535,35535,45535,55535,65535};
		pd_shared3p xor_uint16[[1]] vec2 (7) = {5534, 12546,24,35534,45535,39053,0};
		if(any(declassify(vec < vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " < ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32[[1]] vec (5) = {67295,1073792295,2147517294,3221242294,4294967294};
		pd_shared3p xor_uint32[[1]] vec2 (5) = {67294,21432532,78635892,192468953,0};
		if(any(declassify(vec < vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " < ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64[[1]] vec (4) = {3689348814741910323,7378697629483820646,11068046444225730969,14757395258967641292};
		pd_shared3p xor_uint64[[1]] vec2 (4) = {3689348814741910322,35597629483820646,1,0};
		if(any(declassify(vec < vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " < ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}

	print("TEST 3: >= operator");
	{
		print("uint8");
		pd_shared3p uint8[[1]] vec (6) = {5,55,105,155,205,255};
		pd_shared3p uint8[[1]] vec2 (6) = {4,40,55,2,175,0};
		larger_than_equal(vec,vec2);
	}
	{
		print("uint16");
		pd_shared3p uint16[[1]] vec (7) = {5535,15535,25535,35535,45535,55535,65535};
		pd_shared3p uint16[[1]] vec2 (7) = {5534, 12546,24,35534,45534,39053,0};
		larger_than_equal(vec,vec2);
	}
	{
		print("uint32");
		pd_shared3p uint32[[1]] vec (5) = {67295,1073792295,2147517294,3221242294,4294967294};
		pd_shared3p uint32[[1]] vec2 (5) = {67294,21432532,78635892,192468953,0};
		larger_than_equal(vec,vec2);
	}
	{
		print("uint64/uint");
		pd_shared3p uint[[1]] vec (4) = {3689348814741910323,7378697629483820646,11068046444225730969,14757395258967641292};
		pd_shared3p uint[[1]] vec2 (4) ={3689348814741910322,35597629483820646,1,0};
		larger_than_equal(vec,vec2);
	}
	{
		print("int8");
		pd_shared3p int8[[1]] vec (6) = {-127,-77,-27,23,73,123};
		pd_shared3p int8[[1]] vec2 (6) = {-128,-78,-28,-1,0,122};
		larger_than_equal(vec,vec2);
	}
	{
		print("int16");
		pd_shared3p int16[[1]] vec (8) = {-32767,-24575,-16383,-8191,1,8193,16385,24577};
		pd_shared3p int16[[1]] vec2 (8) = {-32768,-25643,-17345,-8192,0,1,2153,21453};
		larger_than_equal(vec,vec2);
	}
	{
		print("int32");
		pd_shared3p int32[[1]] vec (8) = {-2147483647,-1610612735,-1073741823,-536870911,1,536870913,1073741825,1610612737};
		pd_shared3p int32[[1]] vec2 (8) = {-2147483648,-1610612736,-1243259079,-9537127485,0,-1,1,1610612736};
		larger_than_equal(vec,vec2);
	}
	{
		print("int64/int");
		pd_shared3p int[[1]] vec (6) = {-9223372036854775807,-5534023222112865484,-1844674407370955161,1844674407370955162,5534023222112865485,9223372036854775807};
		pd_shared3p int[[1]] vec2 (6) = {-9223372036854775808,-8735123222112865484,-6391824407370955161,0,-1,1};
		larger_than_equal(vec,vec2);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8[[1]] vec (6) = {5,55,105,155,205,255};
		pd_shared3p xor_uint8[[1]] vec2 (6) = {4,40,55,2,175,0};
		if(any(declassify(vec2 >= vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " >= ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16[[1]] vec (7) = {5535,15535,25535,35535,45535,55535,65535};
		pd_shared3p xor_uint16[[1]] vec2 (7) = {5534, 12546,24,35534,45534,39053,0};
		if(any(declassify(vec2 >= vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " >= ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32[[1]] vec (5) = {67295,1073792295,2147517294,3221242294,4294967294};
		pd_shared3p xor_uint32[[1]] vec2 (5) = {67294,21432532,78635892,192468953,0};
		if(any(declassify(vec2 >= vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " >= ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64[[1]] vec (4) = {3689348814741910323,7378697629483820646,11068046444225730969,14757395258967641292};
		pd_shared3p xor_uint64[[1]] vec2 (4) = {3689348814741910322,35597629483820646,1,0};
		if(any(declassify(vec2 >= vec))){
			print("FAILURE! ", arrayToString(declassify(vec2)) , " >= ", arrayToString(declassify(vec)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	print("TEST 4: <= operator");
	{
		print("uint8");
		pd_shared3p uint8[[1]] vec (6) = {5,55,105,155,205,255};
		pd_shared3p uint8[[1]] vec2 (6) = {4,40,55,2,175,0};
		smaller_than_equal(vec,vec2);
	}
	{
		print("uint16");
		pd_shared3p uint16[[1]] vec (7) = {5535,15535,25535,35535,45535,55535,65535};
		pd_shared3p uint16[[1]] vec2 (7) = {5534, 12546,24,35534,45534,39053,0};
		smaller_than_equal(vec,vec2);
	}
	{
		print("uint32");
		pd_shared3p uint32[[1]] vec (5) = {67295,1073792295,2147517294,3221242294,4294967294};
		pd_shared3p uint32[[1]] vec2 (5) = {67294,21432532,78635892,192468953,0};
		smaller_than_equal(vec,vec2);
	}
	{
		print("uint64/uint");
		pd_shared3p uint[[1]] vec (4) = {3689348814741910323,7378697629483820646,11068046444225730969,14757395258967641292};
		pd_shared3p uint[[1]] vec2 (4) = {3689348814741910322,35597629483820646,1,0};
		smaller_than_equal(vec,vec2);
	}
	{
		print("int8");
		pd_shared3p int8[[1]] vec (6) = {-127,-77,-27,23,73,123};
		pd_shared3p int8[[1]] vec2 (6) = {-128,-78,-28,-1,0,122};
		smaller_than_equal(vec,vec2);
	}
	{
		print("int16");
		pd_shared3p int16[[1]] vec (8) = {-32767,-24575,-16383,-8191,1,8193,16385,24577};
		pd_shared3p int16[[1]] vec2 (8) = {-32768,-25643,-17345,-8192,0,1,2153,21453};
		smaller_than_equal(vec,vec2);
	}
	{
		print("int32");
		pd_shared3p int32[[1]] vec (8) = {-2147483647,-1610612735,-1073741823,-536870911,1,536870913,1073741825,1610612737};
		pd_shared3p int32[[1]] vec2 (8) = {-2147483648,-1610612736,-1243259079,-9537127485,0,-1,1,1610612736};
		smaller_than_equal(vec,vec2);
	}
	{
		print("int64/int");
		pd_shared3p int[[1]] vec (6) = {-9223372036854775807,-5534023222112865484,-1844674407370955161,1844674407370955162,5534023222112865485,9223372036854775807};
		pd_shared3p int[[1]] vec2 (6) =  {-9223372036854775808,-8735123222112865484,-6391824407370955161,0,-1,1};
		smaller_than_equal(vec,vec2);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8[[1]] vec (6) = {5,55,105,155,205,255};
		pd_shared3p xor_uint8[[1]] vec2 (6) = {4,40,55,2,175,0};
		if(any(declassify(vec <= vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " <= ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16[[1]] vec (7) = {5535,15535,25535,35535,45535,55535,65535};
		pd_shared3p xor_uint16[[1]] vec2 (7) = {5534, 12546,24,35534,45534,39053,0};
		if(any(declassify(vec <= vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " <= ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32[[1]] vec (5) = {67295,1073792295,2147517294,3221242294,4294967294};
		pd_shared3p xor_uint32[[1]] vec2 (5) = {67294,21432532,78635892,192468953,0};
		if(any(declassify(vec <= vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " <= ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64[[1]] vec (4) = {3689348814741910323,7378697629483820646,11068046444225730969,14757395258967641292};
		pd_shared3p xor_uint64[[1]] vec2 (4) = {3689348814741910322,35597629483820646,1,0};
		if(any(declassify(vec <= vec2))){
			print("FAILURE! ", arrayToString(declassify(vec)) , " <= ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
		else{
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
	}

	print("TEST 5: == operator");
	{
		print("bool");
		pd_shared3p bool[[1]] vec (6);
		pd_shared3p bool[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("uint8");
		pd_shared3p uint8[[1]] vec (6);
		pd_shared3p uint8[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("uint16");
		pd_shared3p uint16[[1]] vec (6);
		pd_shared3p uint16[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("uint32");
		pd_shared3p uint32[[1]] vec (6);
		pd_shared3p uint32[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("uint64/uint");
		pd_shared3p uint[[1]] vec (6);
		pd_shared3p uint[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("int8");
		pd_shared3p int8[[1]] vec (6);
		pd_shared3p int8[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("int16");
		pd_shared3p int16[[1]] vec (6);
		pd_shared3p int16[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("int32");
		pd_shared3p int32[[1]] vec (6);
		pd_shared3p int32[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("int64/int");
		pd_shared3p int[[1]] vec (6);
		pd_shared3p int[[1]] vec2 (6);
		equal_equal(vec,vec2);
	}
	{
		print("xor_uint8");
		pd_shared3p xor_uint8[[1]] vec (6);
		pd_shared3p xor_uint8[[1]] vec2 (6);
		vec = randomize(vec);
		vec2 = vec;
		if(all(declassify(vec == vec2))){
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
		else{
			print("FAILURE! ", arrayToString(declassify(vec)) , " == ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
	}
	{
		print("xor_uint16");
		pd_shared3p xor_uint16[[1]] vec (6);
		pd_shared3p xor_uint16[[1]] vec2 (6);
		vec = randomize(vec);
		vec2 = vec;
		if(all(declassify(vec == vec2))){
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
		else{
			print("FAILURE! ", arrayToString(declassify(vec)) , " == ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
	}
	{
		print("xor_uint32");
		pd_shared3p xor_uint32[[1]] vec (6);
		pd_shared3p xor_uint32[[1]] vec2 (6);
		vec = randomize(vec);
		vec2 = vec;
		if(all(declassify(vec == vec2))){
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
		else{
			print("FAILURE! ", arrayToString(declassify(vec)) , " == ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
	}
	{
		print("xor_uint64");
		pd_shared3p xor_uint64[[1]] vec (6);
		pd_shared3p xor_uint64[[1]] vec2 (6);
		vec = randomize(vec);
		vec2 = vec;
		if(all(declassify(vec == vec2))){
			succeeded_tests = succeeded_tests + 1;
	 		all_tests = all_tests +1;
	 		print("SUCCESS!");
		}
		else{
			print("FAILURE! ", arrayToString(declassify(vec)) , " == ", arrayToString(declassify(vec2)));
	 		all_tests = all_tests +1;
		}
	}

	print("Test finished!");
	print("Succeeded tests: ", succeeded_tests);
	print("Failed tests: ", all_tests - succeeded_tests);

    test_report(all_tests, succeeded_tests);
}
