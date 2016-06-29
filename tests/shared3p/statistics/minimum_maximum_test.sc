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
 * packaging of this file. Please review the following information to
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

//templates were not used in order to test the maximum and minimum values of each data type
bool test_maximum (int32 data) {
	//all values are zero
	pd_shared3p int32[[1]] a (100) = 0;
	
	//test the maximum negative int32 value
	pd_shared3p int32[[1]] b (100) = 0;
	a [50] = INT32_MIN;
	
	//test the maximum positive int32 value
	pd_shared3p int32[[1]] c (100) = 0;
	c [50] = INT32_MAX;
	c [51] = INT32_MIN;
	
	//test maximum value when the maximum value is not availabile
	pd_shared3p int32[[1]] d (100) = -10;
	d [1] = 50;
	
	//availability mask that makes every value in the input available
	pd_shared3p bool[[1]] available1 (100) = true;
	
	//availability mask that makes only the first value of the input available
	pd_shared3p bool[[1]] available2 (100) = false;
	available2 [0] = true;
	
	pd_shared3p int32 max_in_a = maximum (a, available1);
	pd_shared3p int32 max_in_b = maximum (b, available1);
	pd_shared3p int32 max_in_c = maximum (c, available1);
	pd_shared3p int32 max_in_d = maximum (d, available2);
	
	//test for correct values
	if (declassify (max_in_a) != 0)
		return false;

	if (declassify (max_in_b) != 0)
		return false;
	
	if (declassify (max_in_c) != INT32_MAX)
		return false;
	
	if (declassify (max_in_d) != -10)
		return false;
	
	return true;
}


bool test_maximum (int64 data) {
	//all values are zero
	pd_shared3p int64[[1]] a (100) = 0;
	
	//test the maximum negative int64 value
	pd_shared3p int64[[1]] b (100) = 0;
	a [50] = INT64_MIN;
	
	//test the maximum positive int64 value
	pd_shared3p int64[[1]] c (100) = 0;
	c [50] = INT64_MAX;
	
	//test maximum value when the maximum value is not availabile
	pd_shared3p int64[[1]] d (100) = -10;
	d [1] = 50;
	
	//availability mask that makes every value in the input available
	pd_shared3p bool[[1]] available1 (100) = true;
	
	//availability mask that makes only the first value of the input available
	pd_shared3p bool[[1]] available2 (100) = false;
	available2 [0] = true;
	
	pd_shared3p int64 max_in_a = maximum (a, available1);
	pd_shared3p int64 max_in_b = maximum (b, available1);
	pd_shared3p int64 max_in_c = maximum (c, available1);
	pd_shared3p int64 max_in_d = maximum (d, available2);
	
	//test for correct values
	if (declassify (max_in_a) != 0)
		return false;

	if (declassify (max_in_b) != 0)
		return false;
	
	if (declassify (max_in_c) != INT64_MAX)
		return false;
	
	if (declassify (max_in_d) != -10)
		return false;
	
	return true;
}


bool test_minimum (int32 data) {
	//all values are zero
	pd_shared3p int32[[1]] a (100) = 0;
	
	//test the maximum negative int32 value
	pd_shared3p int32[[1]] b (100) = 0;
	b [50] = INT32_MIN;
	b [51] = INT32_MAX;
	
	//test the maximum positive int32 value
	pd_shared3p int32[[1]] c (100) = 0;
	c [50] = INT32_MAX;
	
	//test maximum value when the maximum value is not availabile
	pd_shared3p int32[[1]] d (100) = -10;
	d [1] = -50;
	
	//availability mask that makes every value in the input available
	pd_shared3p bool[[1]] available1 (100) = true;
	
	//availability mask that makes only the first value of the input available
	pd_shared3p bool[[1]] available2 (100) = false;
	available2 [0] = true;
	
	pd_shared3p int32 min_in_a = minimum (a, available1);
	pd_shared3p int32 min_in_b = minimum (b, available1);
	pd_shared3p int32 min_in_c = minimum (c, available1);
	pd_shared3p int32 min_in_d = minimum (d, available2);
	
	//test for correct values
	if (declassify (min_in_a) != 0)
		return false;

	if (declassify (min_in_b) != INT32_MIN)
		return false;
	
	if (declassify (min_in_c) != 0)
		return false;
	
	if (declassify (min_in_d) != -10)
		return false;
	
	return true;
}


bool test_minimum (int64 data) {
	//all values are zero
	pd_shared3p int64[[1]] a (100) = 0;
	
	//test the maximum negative int64 value
	pd_shared3p int64[[1]] b (100) = 0;
	b [50] = INT64_MIN;
	b [51] = INT64_MAX;
	
	//test the maximum positive int32 value
	pd_shared3p int64[[1]] c (100) = 0;
	c [50] = INT64_MAX;
	
	//test maximum value when the maximum value is not availabile
	pd_shared3p int64[[1]] d (100) = -10;
	d [1] = -50;
	
	//availability mask that makes every value in the input available
	pd_shared3p bool[[1]] available1 (100) = true;
	
	//availability mask that makes only the first value of the input available
	pd_shared3p bool[[1]] available2 (100) = false;
	available2 [0] = true;
	
	pd_shared3p int64 min_in_a = minimum (a, available1);
	pd_shared3p int64 min_in_b = minimum (b, available1);
	pd_shared3p int64 min_in_c = minimum (c, available1);
	pd_shared3p int64 min_in_d = minimum (d, available2);
	
	//test for correct values
	if (declassify (min_in_a) != 0)
		return false;

	if (declassify (min_in_b) != INT64_MIN)
		return false;
	
	if (declassify (min_in_c) != 0)
		return false;
	
	if (declassify (min_in_d) != -10)
		return false;
	
	return true;
}


void main () {
	string test_prefix = "Maximum";
	test (test_prefix, test_maximum (0::int32), 0::int32);
	test (test_prefix, test_maximum (0::int64), 0::int64);
	
	test_prefix = "Minimum";
	test (test_prefix, test_minimum (0::int32), 0::int32);
	test (test_prefix, test_minimum (0::int64), 0::int64);
	
	test_report ();
}