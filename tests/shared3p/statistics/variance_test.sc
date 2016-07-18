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


template<type T, type G>
bool variance_test (T data, G data2) {
	pd_shared3p T[[1]] a (11) = {1, 1, 2, 3, 6, 10, 6, 3, 2, 1, 1};
	pd_shared3p G result = variance (a);

	G relative_error = (abs (declassify (result) - 8.41818181818182))/8.4182;

	//the relative error is about 1e-16 for 64 bit floats, 1e-7 for 32 bit floats
	if (!isNegligible (relative_error))
		return false;

	return true;
}


template<type T, type G>
bool variance_test_filter (T data, G data2) {
	pd_shared3p T[[1]] a (11) = {1, 1, 2, 3, 6, 10, 6, 3, 2, 1, 1};
	pd_shared3p bool[[1]] mask (11) = true;
	mask[0:2] = false;

	pd_shared3p G result = variance (a, mask);

	G relative_error = (abs (declassify (result) - 8.94444444444444))/8.944444;

	//the relative error is about 1e-16 for 64 bit floats, 1e-8 for 32 bit floats
	if (!isNegligible (relative_error))
		return false;

	return true;
}

void main () {
	string test_prefix = "Variance";
	test (test_prefix, variance_test (0::int32, 0::float32), 0::int32);
	test (test_prefix, variance_test (0::int64, 0::float64), 0::int64);

	test_prefix = "Variance (filter)";
	test (test_prefix, variance_test_filter (0::int32, 0::float32), 0::int32);
	test (test_prefix, variance_test_filter (0::int64, 0::float64), 0::int64);

	test_report ();

}