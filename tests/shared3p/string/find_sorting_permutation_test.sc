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
import shared3p_string;
import test_utility;

domain pd_shared3p shared3p;


bool find_sorting_permutation_test () {
	pd_shared3p bool[[1]] a = {false, true, true, false};

	pd_shared3p uint[[1]] result = findSortingPermutation (a);
	uint[[1]] expected_result = {2, 0, 1, 3};

	return all (declassify (result) == expected_result);
}


void main () {
	string test_prefix = "FindSortingPermutation";
	test (test_prefix, find_sorting_permutation_test (), true);

	test_report ();
}