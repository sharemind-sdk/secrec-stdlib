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


template<type T, type G>
bool covariance_test(T data, G data2) {
	//the function may overflow if the input is too big
	pd_shared3p T[[1]] a (10) = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	pd_shared3p T[[1]] b (10) = {10, 40, 60, 80, 100, 120, 140, 160, 180, 200};

	//find the covariance
	pd_shared3p G result = covariance(a, b);

	//covariance calculated with R
	G expected_result = 1883.333333;

	//the relative error is around 1e-8 for 32 and 1e-14 for 64 bit inputs
	G relative_error = abs (declassify (result) - expected_result) / expected_result;

	//if the error is creater than 1e-5 then the test fails
	if (!isNegligible (relative_error))
		return false;

	return true;

}


template<type T, type G>
bool covariance_test_filter(T data, G data2) {
	//the function may overflow if the input is too big
	pd_shared3p T[[1]] a (10) = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	pd_shared3p T[[1]] b (10) = {10, 40, 60, 80, 100, 120, 140, 160, 180, 200};

	//the mask determines which values in the input vectors are available
	pd_shared3p bool[[1]] mask (10) = true;
	mask [9] = false;

	//find the covariance
	pd_shared3p G result = covariance(a, b, mask);

	//covariance calculated with R
	G expected_result = 1550;

	G relative_error = abs (declassify (result) - expected_result) / expected_result;

	//if the error is creater than 1e-5 then the test fails
	if (!isNegligible (relative_error))
		return false;

	return true;

}


void main() {
	string test_prefix = "Covariance";
	test (test_prefix, covariance_test (0::int32, 0::float32), 0::int32);
	test (test_prefix, covariance_test (0::int64, 0::float64), 0::int64);

	test_prefix = "Covariance(filter)";
	test (test_prefix, covariance_test_filter (0::int32, 0::float32), 0::int32);
	test (test_prefix, covariance_test_filter (0::int64, 0::float64), 0::int64);

	test_report();

}