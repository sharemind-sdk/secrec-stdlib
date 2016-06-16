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

//this function can overflow if the input get close to the maximum value of int
template<type T, type G>
bool covariance_test(T data, G data2) {
	pd_shared3p T[[1]] a (10) = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	pd_shared3p T[[1]] b (10) = {10, 40, 60, 80, 100, 120, 140, 160, 180, 200};
	
	pd_shared3p G c = covariance(a, b);

	//the inaccuaracy is present in other functions aswell
	//the relative error is around 1e-8 
	if (!isNegligible((abs(declassify(c) - 1883.3333))/1883.3333))
		return false;
	
	return true;
		
}


template<type T, type G>
bool covariance_test_filter(T data, G data2) {
	pd_shared3p T[[1]] a (10) = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	pd_shared3p T[[1]] b (10) = {10, 40, 60, 80, 100, 120, 140, 160, 180, 200};
	
	pd_shared3p bool[[1]] mask (10) = true;
	mask [9] = false;
	
	pd_shared3p G c = covariance(a, b, mask);
	
	if (!isNegligible((abs(declassify(c) - 1550))/1550))
		return false;
	
	return true;
		
}


void main() {
	string test_prefix = "Covariance";
	//mind that the covariance is a float of the same size as the input
	test(test_prefix, covariance_test(0::int32, 0::float32), 0::int32);
	test(test_prefix, covariance_test(0::int64, 0::float64), 0::int64);
	
	test_prefix = "Covariance(filter)";
	test(test_prefix, covariance_test_filter(0::int32, 0::float32), 0::int32);
	test(test_prefix, covariance_test_filter(0::int64, 0::float64), 0::int64);	
	
	test_report();

}