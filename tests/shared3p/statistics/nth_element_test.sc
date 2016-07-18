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
import shared3p_statistics_common;
import test_utility;

domain pd_shared3p shared3p;

/*
 * This function no longer exists
 */

template<type T>
bool nth_element_test(T data) {
	pd_shared3p T[[1]] a (9) = {15, 10, 8, 6, 5, 4, 3, 2, 1};

	uint x = 1;
	uint y = 2;

	pd_shared3p T b = nthElement(a, x, true);
	pd_shared3p T c = nthElement(a, y, false);

	if (declassify(b) != 2 || declassify(c) != 3)
		return false;

	return true;
}


template<type T>
bool nth_element_test_indexes(T data) {
	pd_shared3p T[[1]] a (9) = {15, 10, 8, 6, 5, 4, 3, 2, 1};

	uint64 x = 1;
	uint64 y = 2;

	uint64 left = 2;
	uint64 right = 6;

	pd_shared3p T b = nthElement(a, left, right, x, false);
	pd_shared3p T c = nthElement(a, left, right, y, false);

	print(declassify(b));
	print(declassify(c));

	if (declassify(b) != 4 || declassify(c) != 5)
		return false;

	return true;
}


void main() {
	/*
	string test_prefix = "NthElement";
	test(test_prefix, nth_element_test(0::int32), 0::int32);
	test(test_prefix, nth_element_test(0::int64), 0::int64);

	test_prefix = "NthElement(with indexes)";
	test(test_prefix, nth_element_test_indexes(0::int32), 0::int32);
	test(test_prefix, nth_element_test_indexes(0::int64), 0::int64);

	test_report();

	*/
}
