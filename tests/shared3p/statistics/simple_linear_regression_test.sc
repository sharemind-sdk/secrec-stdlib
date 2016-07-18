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
import shared3p_statistics_regression;
import test_utility;

domain pd_shared3p shared3p;

template<type T, type G>
bool test_simple_lg (T data, G data2) {
	pd_shared3p T[[1]] sample_x (10) = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	pd_shared3p T[[1]] sample_y (10) = {1, 2, 3 ,4, 5, 6, 7, 8, 9, 10};

	pd_shared3p bool[[1]] mask (10) = true;
	mask[0] = false;

	pd_shared3p G[[1]] result = simpleLinearRegression (sample_x, sample_y, mask);

	G error1 = abs(declassify (result[1]) - 1);
	G error2 = abs(declassify (result[0]) - 0);

	if (!isNegligible (error1) || !isNegligible (error2))
		return false;

	return true;
}


void main() {
	string test_prefix = "SimpleLinearRegression";
	test (test_prefix, test_simple_lg (0::int32, 0::float32), 0::int32);
	test (test_prefix, test_simple_lg (0::int32, 0::float32), 0::int32);

	test_report();
}