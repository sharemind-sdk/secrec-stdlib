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
import shared3p_statistics_summary;
import test_utility;

domain pd_shared3p shared3p;


bool MAD_test (int32 data) {
	pd_shared3p int32[[1]] a (5) = {1, 1, 2, 2, 2147483647};
	pd_shared3p float32 result = MAD(a);
	
	float32 expected_result = 1.4826;
	float32 relative_error = abs (declassify (result) - expected_result) / expected_result;
	
	if (!isNegligible (relative_error))
		return false;

	return true;
}


bool MAD_test (int64 data) {
	pd_shared3p int64[[1]] a (5) = {1, 1, 2, 2, 2147483647};
	pd_shared3p float64 result = MAD (a);
	
	float64 expected_result = 1.4826;
	float64 relative_error = abs (declassify (result) - expected_result) / expected_result ;
	
	if (!isNegligible (relative_error))
		return false;

	return true;
}


bool MAD_test_const (int32 data, float32 data2) {
	pd_shared3p int32[[1]] a (5) = {1, 1, 2, 2, 2147483647};
	float32 scale = 1/3;
	pd_shared3p float32 result = MAD (a, scale);
	
	float32 relative_error = abs (declassify (result) - scale) / scale;

	if (!isNegligible (relative_error))
		return false;

	return true;
}


bool MAD_test_const (int64 data, float64 data2) {
	pd_shared3p int64[[1]] a (5) = {1, 1, 2, 2, 2147483647};
	float64 scale = 900000000000/7;
	pd_shared3p float64 result = MAD (a, scale);
	
	float64 relative_error = abs (declassify (result) - scale) / scale;
	
	if (!isNegligible (relative_error))
		return false;

	return true;
}


bool MAD_test_filter (int32 data) {
	pd_shared3p int32[[1]] a (8) = {50, 50, 100, 1, 1, 2, 2, 2147483647};
	pd_shared3p bool[[1]] mask (8) = true;
	mask[0:2] = false;
	
	pd_shared3p float32 result = MAD (a, mask);
	
	float32 expected_result = 1.4826;
	float32 relative_error = abs (declassify (result) - expected_result) / expected_result;
	
	if (!isNegligible (relative_error))
		return false;

	return true;
}


bool MAD_test_filter (int64 data) {
	pd_shared3p int64[[1]] a (8) = {50, 50, 100, 1, 1, 2, 2, 2147483647};
	pd_shared3p bool[[1]] mask (8) = true;
	mask[0:2] = false;
	
	pd_shared3p float64 result = MAD (a, mask);
	
	float64 expected_result = 1.4826;
	float64 relative_error = abs (declassify (result) - expected_result) / expected_result;
	
	if (!isNegligible (relative_error))
		return false;

	return true;
}


bool MAD_test_filter_constant(int32 data, float32 data2) {
	pd_shared3p int32[[1]] a (8) = {50, 50, 100, 1, 1, 2, 2, 2147483647};
	pd_shared3p bool[[1]] mask (8) = true;
	float32 scale = 1/3;
	mask[0:2] = false;
	
	pd_shared3p float32 result = MAD (a, mask, scale);
	
	float32 relative_error = abs (declassify (result) - scale)/scale;

	if (!isNegligible (relative_error))
		return false;

	return true;
}


bool MAD_test_filter_constant(int64 data, float64 data2) {
	pd_shared3p int64[[1]] a (8) = {50, 50, 100, 1, 1, 2, 2, 2147483647};
	pd_shared3p bool[[1]] mask (8) = true;
	float64 scale = 1/3;
	mask[0:2] = false;
	
	pd_shared3p float64 result = MAD(a, mask, scale);
	
	float64 relative_error = abs (declassify (result) - scale) / scale;

	if (!isNegligible (relative_error))
		return false;

	return true;
}


void main() {
	string test_prefix = "MAD";
	test (test_prefix, MAD_test (0::int32), 0::int32);
	test (test_prefix, MAD_test (0::int64), 0::int64);
	
	test_prefix = "MAD(constant)";
	test (test_prefix, MAD_test_const (0::int32, 0::float32), 0::int32, 0::float32);
	test (test_prefix, MAD_test_const (0::int64, 0::float64), 0::int64, 0::float64);
	
	test_prefix = "MAD(filter)";
	test (test_prefix, MAD_test_filter (0::int32), 0::int32);
	test (test_prefix, MAD_test_filter (0::int64), 0::int64);
	
	test_prefix = "MAD(filter, constant)";
	test (test_prefix, MAD_test_filter_constant (0::int32, 0::float32), 0::int32, 0::float32);
	test (test_prefix, MAD_test_filter_constant (0::int64, 0::float64), 0::int64, 0::float64);
	
	test_report();
}