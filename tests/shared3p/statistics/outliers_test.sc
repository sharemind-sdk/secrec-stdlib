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
import shared3p_statistics_outliers;
import shared3p_statistics_common;
import test_utility;

domain pd_shared3p shared3p;

template <type T, type G>
bool outliersMAD_test(T data, G data2) {
	pd_shared3p T[[1]] a = {1, 3, 7, 15, 31, 63, 127};
	pd_shared3p bool[[1]] mask (7) = true;
	mask[0] = false;

	pd_shared3p bool[[1]] result = outlierDetectionMAD(a, mask, 2::G);
	
	T x = declassify(sum(cut(a, result)));
	
	if (x != 56)
		return false;
	
	return true;
}


template <type T, type G>
bool outliersQuantiles_test(T data, G data2) {
	pd_shared3p T[[1]] a = {1, 3, 7, 15, 31, 63, 127};
	pd_shared3p bool[[1]] mask (7) = true;
	mask[0] = false;

	pd_shared3p bool[[1]] result = outlierDetectionQuantiles(0.10::G, a, mask);
	
	T x = declassify(sum(cut(a, result)));

	if (x != 116)
		return false;
	
	return true;
}


void main() {
	string test_prefix = "OutlierDetectionMAD";
	test(test_prefix, outliersMAD_test(0::int32, 0::float32), 0::int32, 0::float32);
	test(test_prefix, outliersMAD_test(0::int64, 0::float64), 0::int64, 0::float64);

	test_prefix = "OutlierDetectionQuantiles";
	test(test_prefix, outliersQuantiles_test(0::int32, 0::float32), 0::int32, 0::float32);
	test(test_prefix, outliersQuantiles_test(0::int64, 0::float64), 0::int64, 0::float64);
	
	test_report();
}
