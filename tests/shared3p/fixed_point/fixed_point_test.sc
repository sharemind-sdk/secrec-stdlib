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
 
import shared3p;
import stdlib;
import test_utility;

domain pd_shared3p shared3p;


bool fix_conv_test (uint32 data) {
	pd_shared3p float32[[2]] a = reshape ({0.5, 0.3, 123.54,
										   0.2, 0.9, 55.45}, 2, 3);
	pd_shared3p uint32[[2]] b(2, 3);
	__syscall("shared3p::float32_to_fix32", __domainid (pd_shared3p), a, b, 16 :: uint);
	
	pd_shared3p float32[[2]] c = ((float32) b) / 65536;
	
	float32 relative_error = sum (flatten (declassify (c - a))) / sum (flatten (declassify (a)));

	//the relative error is around 1e-7
	return isNegligible (relative_error);
}


bool fix_conv_test (uint64 data) {
	pd_shared3p float64[[2]] a = reshape ({0.5, 0.3, 123.54,
										   0.2, 0.9, 55.45}, 2, 3);
	pd_shared3p uint64[[2]] b(2, 3);
	__syscall("shared3p::float64_to_fix64", __domainid (pd_shared3p), a, b, 32 :: uint);
	
	pd_shared3p float64[[2]] c = ((float64) b) / 4294967296;
	
	float64 relative_error = sum (flatten (declassify (c - a))) / sum (flatten (declassify (a)));

	//the relative error is around 1e-12
	return isNegligible (relative_error);
}
	

bool fix_mul_test (uint32 data) {
	pd_shared3p float32[[1]] a = {5, 4, 3, 2, 1};
	pd_shared3p uint32[[1]] a_fix(5);
	__syscall("shared3p::float32_to_fix32", __domainid (pd_shared3p), a, a_fix, 16 :: uint);
	
	pd_shared3p float32[[1]] b = {6, 5, 4, 3, 2};
	pd_shared3p uint32[[1]] b_fix(5);
	__syscall("shared3p::float32_to_fix32", __domainid (pd_shared3p), b, b_fix, 16 :: uint);
	
	pd_shared3p uint32[[1]] result_fix(5);
	__syscall("shared3p::mul_fix32_vec", __domainid (pd_shared3p), a_fix, b_fix, result_fix);
	
	pd_shared3p float32[[1]] result = (float32) result_fix / 65536;
	pd_shared3p float32[[1]] expected_result = a * b;
	
	float32 error = sum (declassify (result - expected_result));

	return isNegligible (error);
}


bool fix_mul_test (uint64 data) {
	pd_shared3p float64[[1]] a = {5, 4, 3, 2, 1};
	pd_shared3p uint64[[1]] a_fix(5);
	__syscall("shared3p::float64_to_fix64", __domainid (pd_shared3p), a, a_fix, 32 :: uint);
	
	pd_shared3p float64[[1]] b = {6, 5, 4, 3, 2};
	pd_shared3p uint64[[1]] b_fix(5);
	__syscall("shared3p::float64_to_fix64", __domainid (pd_shared3p), b, b_fix, 32 :: uint);
	
	pd_shared3p uint64[[1]] result_fix(5);
	__syscall("shared3p::mul_fix64_vec", __domainid (pd_shared3p), a_fix, b_fix, result_fix);
	
	pd_shared3p float64[[1]] result = (float64) result_fix / 4294967296;
	pd_shared3p float64[[1]] expected_result = a * b;
	
	float64 error = sum (declassify (result - expected_result));

	return isNegligible (error);
}


bool fix_sqrt_test (uint32 data) {
	pd_shared3p float32[[1]] a = {16, 25, 36, 49, 64};
	pd_shared3p uint32[[1]] a_fix(5);
	__syscall("shared3p::float32_to_fix32", __domainid (pd_shared3p), a, a_fix, 16 :: uint);
	
	pd_shared3p float32 b = 2;
	pd_shared3p uint32 b_fix;
	__syscall("shared3p::float32_to_fix32", __domainid (pd_shared3p), b, b_fix, 16 :: uint);
	
	pd_shared3p float32[[1]] expected_result1 = {4, 5, 6, 7, 8};
	pd_shared3p float32 expected_result2 = 1.414213562373095;

	pd_shared3p uint32[[1]] result1_fix(5);
	pd_shared3p uint32 result2_fix;
	
	__syscall("shared3p::sqrt_fix32_vec", __domainid (pd_shared3p), a_fix, result1_fix);
	__syscall("shared3p::sqrt_fix32_vec", __domainid (pd_shared3p), b_fix, result2_fix);

	pd_shared3p float32[[1]] result1 = (float32) result1_fix / 65536;
	pd_shared3p float32 result2 = (float32) result2_fix / 65536;
	
	float32 relative_error1 = sum (declassify ((expected_result1 - result1) / expected_result1));
	float32 relative_error2 = declassify ((expected_result2 - result2) / expected_result2);

	//the relative error is around 1e-7
	return isNegligible (relative_error1) && isNegligible (relative_error2);
}


bool fix_sqrt_test (uint64 data) {
	pd_shared3p float64[[1]] a = {16, 25, 36, 49, 64};
	pd_shared3p uint64[[1]] a_fix(5);
	__syscall("shared3p::float64_to_fix64", __domainid (pd_shared3p), a, a_fix, 32 :: uint);
	
	pd_shared3p float64 b = 2;
	pd_shared3p uint64 b_fix;
	__syscall("shared3p::float64_to_fix64", __domainid (pd_shared3p), b, b_fix, 32 :: uint);
	
	pd_shared3p float64[[1]] expected_result1 = {4, 5, 6, 7, 8};
	pd_shared3p float64 expected_result2 = 1.414213562373095;

	pd_shared3p uint64[[1]] result1_fix(5);
	pd_shared3p uint64 result2_fix;
	
	__syscall("shared3p::sqrt_fix64_vec", __domainid (pd_shared3p), a_fix, result1_fix);
	__syscall("shared3p::sqrt_fix64_vec", __domainid (pd_shared3p), b_fix, result2_fix);

	pd_shared3p float64[[1]] result1 = (float64) result1_fix / 4294967296;
	pd_shared3p float64 result2 = (float64) result2_fix / 4294967296;
	
	float64 relative_error1 = sum (declassify ((expected_result1 - result1) / expected_result1));
	float64 relative_error2 = declassify ((expected_result2 - result2) / expected_result2);

	//the relative error is around 1e-10
	return isNegligible (relative_error1) && isNegligible (relative_error2);
}


bool fix_inv_test (uint32 data) {
	pd_shared3p float32[[1]] a = {1, 10, 0.5, 7};
	pd_shared3p uint32[[1]] a_fix(4);
	__syscall("shared3p::float32_to_fix32", __domainid (pd_shared3p), a, a_fix, 16 :: uint);

	pd_shared3p float32[[1]] expected_result = {1, 0.1, 2, 0.1428571428571429};
	pd_shared3p uint32[[1]] result_fix(4);
	 __syscall("shared3p::inv_fix32_vec", __domainid (pd_shared3p), a_fix, result_fix);
	
	pd_shared3p float32[[1]] result = (float32) result_fix / 65536;	
	float32 relative_error = abs (sum (declassify ((result - expected_result) / expected_result)));
	
	//the relative error is around 1e-4
	return relative_error < 0.0002;
}


bool fix_inv_test (uint64 data) {
	pd_shared3p float64[[1]] a = {1, 10, 0.5, 7};
	pd_shared3p uint64[[1]] a_fix(4);
	__syscall("shared3p::float64_to_fix64", __domainid (pd_shared3p), a, a_fix, 32 :: uint);

	pd_shared3p float64[[1]] expected_result = {1, 0.1, 2, 0.1428571428571429};
	pd_shared3p uint64[[1]] result_fix(4);
	 __syscall("shared3p::inv_fix64_vec", __domainid (pd_shared3p), a_fix, result_fix);
	
	pd_shared3p float64[[1]] result = (float64) result_fix / 4294967296;	
	float64 relative_error = abs (sum (declassify ((result - expected_result) / expected_result)));

	//the relative error is around 1e-9
	return isNegligible(relative_error);
}


void main () {	
	test ("Fixed point conversion", fix_conv_test (0::uint32), 0::uint32);
	test ("Fixed point conversion", fix_conv_test (0::uint64), 0::uint64);
	
	test ("Fixed point multiplication", fix_mul_test (0::uint32), 0::uint32);
	test ("Fixed point multiplication", fix_mul_test (0::uint64), 0::uint64);
	
	test ("Fixed point square root", fix_sqrt_test (0::uint32), 0::uint32);
	test ("Fixed point square root", fix_sqrt_test (0::uint64), 0::uint64);
	
	test ("Fixed point invert", fix_inv_test (0::uint32), 0::uint32);
	test ("Fixed point invert", fix_inv_test (0::uint64), 0::uint64);
	
	test_report();
}