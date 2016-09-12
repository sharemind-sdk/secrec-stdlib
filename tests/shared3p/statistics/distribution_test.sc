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
import shared3p_statistics_distribution;
import shared3p_statistics_summary;
import test_utility;
import matrix;

domain pd_shared3p shared3p;


template<type T>
bool discrete_distribution_test (T data) {
	pd_shared3p T[[1]] a = {1, 1, 2, 2, 1, 1, 2, 2, 2, 3, 3};
	pd_shared3p bool[[1]] mask (11) = true;
	mask[0] = false;

	pd_shared3p T x = 2;
	pd_shared3p T[[2]] distribution = discreteDistributionCount (a, mask, x, maximum(a, mask));

	if (declassify (sum (distribution[1, :])) != 7)
		return false;

	return true;
}


template<type T>
bool discrete_distribution_test_step (T data) {
	pd_shared3p T[[1]] a = {2, 2, 1, 3, 3, 4, 4, 5};
	pd_shared3p bool[[1]] mask (8) = true;
	mask[0] = false;

	pd_shared3p T x = 2;
	pd_shared3p T step = 3;
	pd_shared3p T[[2]] distribution = discreteDistributionCount (a, mask, x, maximum (a, mask), step);

	if (declassify (sum (distribution[1, :])) != 2)
		return false;

	return true;
}

template<type T>
bool heatmap_test (T data) {
	pd_shared3p T[[1]] a = {1, 1, 2, 2, 3, 3};
	pd_shared3p T[[1]] b = {1, 1, 2, 2, 3, 3};

	pd_shared3p bool[[1]] mask (6) = true;
	mask[0] = false;

	T[[2]] result = declassify (heatmap (a, b, mask, mask, 1::uint));

	T columns = result[0, 7];
	T rows = result[0, 6];

	T[[1]] expected_heatmap = {1, 0, 0,
							   0, 2, 0,
							   0, 0, 2};

	if (columns != 3 || rows != 3)
		return false;

	if (all (expected_heatmap != result[1, :]))
		return false;

	return true;
}


template<type T>
bool histogram_test (T data) {
	pd_shared3p T[[1]] a = {1, 1, 1, 2, 2, 3, 3, 3, 4};
	pd_shared3p bool[[1]] mask (9) = true;

	T[[2]] result = declassify (histogram (a, mask));
	T[[1]] expected_histogram = {5, 3, 1, 0};


	if (all (expected_histogram != result[1, :]))
		return false;

	return true;
}


void main() {
	string test_prefix = "DiscreteDistributionCount";
	test (test_prefix, discrete_distribution_test (0::int32), 0::int32);
	test (test_prefix, discrete_distribution_test (0::int64), 0::int64);

	test_prefix = "DiscreteDistributionCount(with stepSize)";
	test (test_prefix, discrete_distribution_test_step (0::int32), 0::int32);
	test (test_prefix, discrete_distribution_test_step (0::int64), 0::int64);

	test_prefix = "Heatmap";
	test (test_prefix, heatmap_test (0::int32), 0::int32);
	test (test_prefix, heatmap_test (0::int64), 0::int64);

	test_prefix = "Histogram";
	test (test_prefix, histogram_test(0::int32), 0::int32);
	test (test_prefix, histogram_test(0::int64), 0::int64);

	test_report();
}
