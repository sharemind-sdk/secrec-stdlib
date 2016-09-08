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

//five number summary using a sorting network
template<type T, type G>
bool fns_sort_test(T data, G data2) {
	pd_shared3p T[[1]] a (10) = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	pd_shared3p bool[[1]] mask (10) = true;
	mask[0] = false;

	G[[1]] result (5) = declassify (fiveNumberSummarySn (a, mask));

	bool[[1]] test_results = {
		result[0] == 2,
		result[1] == 4,
		result[2] == 6,
		result[3] == 8,
		result[4] == 10
	};

	return all (test_results);
}


//five number summary using the nthElement function
template<type T, type G>
bool fns_nth_test(T data, G data2) {
	pd_shared3p T[[1]] a (10) = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	pd_shared3p bool[[1]] mask (10) = true;
	mask[0] = false;

	G[[1]] result (5) = declassify (fiveNumberSummaryNth (a, mask));

	bool[[1]] test_results = {
		result[0] == 2,
		result[1] == 4,
		result[2] == 6,
		result[3] == 8,
		result[4] == 10
	};

	return all (test_results);
}


void main() {
	string test_prefix = "FiveNumberSummarySn";
	test (test_prefix, fns_sort_test (0::int32, 0::float32), 0::int32);
	test (test_prefix, fns_sort_test (0::int64, 0::float64), 0::int64);

	test_prefix = "FiveNumberSummaryNth";
	test (test_prefix, fns_nth_test (0::int32, 0::float32), 0::int32);
	test (test_prefix, fns_nth_test (0::int64, 0::float64), 0::int64);


	test_report();

}
