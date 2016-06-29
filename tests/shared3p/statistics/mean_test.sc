/*
 * Copyright  (C) 2015 Cybernetica
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


bool mean_test (int32 data) {
	pd_shared3p int32[[1]] a  (8) = {1, 4, 5, 10, 5, 5, 5, 6};
	pd_shared3p float32 result = mean (a);
	
	float32 relative_error =  (abs (declassify (result) - 5.125)) / 5.125;
	
	//the relative error is around 1e-8
	if  (!isNegligible (relative_error)) 
		return false;
	
	return true;
}


bool mean_test (int64 data) {
	pd_shared3p int64[[1]] a  (2) = {INT64_MAX, INT64_MIN};
	pd_shared3p float64 result = mean (a);
	
	float64 relative_error =  (abs (declassify (result) + 0.5)/0.5);
	
	//the relative error is around 1e-16
	if  (!isNegligible (relative_error)) 
		return false;
	
	return true;
}

bool mean_test_filter (int32 data) {
	pd_shared3p int32[[1]] a  (3) = {INT32_MAX, INT32_MIN, 1000};
	pd_shared3p bool[[1]] mask  (3) = {true, true, false};
	
	pd_shared3p float32 result = mean (a, mask);
	
	float32 relative_error =  (abs (declassify (result) + 0.5)/0.5);
	
	if  (!isNegligible (relative_error)) 
		return false;
	
	return true;
}


bool mean_test_filter (int64 data) {
	pd_shared3p int64[[1]] a  (3) = {INT64_MIN, INT64_MAX, 1000};
	pd_shared3p bool[[1]] mask  (3) = {true, true, false};
	
	pd_shared3p float64 result = mean (a, mask);
	
	float64 relative_error =  (abs (declassify (result) + 0.5)/0.5);

	if  (!isNegligible (relative_error)) 
		return false;
	
	return true;
}


void main () {
	string test_prefix = "Mean";
	test (test_prefix, mean_test (0::int32), 0::int32);
	test (test_prefix, mean_test (0::int64), 0::int64);
	
	test_prefix = "Mean (filter)";
	test (test_prefix, mean_test_filter (0::int32), 0::int32);
	test (test_prefix, mean_test_filter (0::int64), 0::int64);
	
	test_report ();	
}