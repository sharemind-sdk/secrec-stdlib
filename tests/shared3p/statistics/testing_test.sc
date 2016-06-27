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
import shared3p_statistics_testing;
import test_utility;

domain pd_shared3p shared3p;


template<type T, type G>
bool tTest_test_samples(T data, G data2) {
	pd_shared3p T[[1]] a = {2, 2, 3, 3, 4, 5, 3, 2, 2, 1, 1};
	pd_shared3p T[[1]] b = {2, 2, 4, 4, 3, 5, 3, 2, 2, 1, 1};

	pd_shared3p bool[[1]] mask (11) = true;
	G test_result = declassify(tTest(a, mask, b, mask, false));
	
	G relative_error = (test_result - (-0.170498584867618)) / 0.170498584867618;
	
	//the relative error is around 1e-16 for 64 bit input	
	if (!isNegligible(relative_error))
		return false;
	
	return true;
}


template<type T, type G>
bool tTest_test(T data, G data2) {
	pd_shared3p T[[1]] a = {2, 2, 3, 3, 4, 5, 3, 2, 2, 1, 1, 2, 2, 4, 4, 3, 5, 3, 2, 2, 1, 1};

	pd_shared3p bool[[1]] cases (22) = true;
	cases[11:] = false;
	pd_shared3p bool[[1]] controls = !cases;

	G test_result = declassify(tTest(a, cases, controls, false));
	
	G relative_error = (test_result - (-0.170498584867618)) / 0.170498584867618;
	
	//the relative error is around 1e-16 for 64 bit input	
	if (!isNegligible(relative_error))
		return false;
	
	return true;
}


template<type T, type G>
bool tTest_test_paired(T data, G data2) {
	pd_shared3p T[[1]] a = {2, 2, 3, 3, 4, 5, 3, 2, 2, 1, 1};
	pd_shared3p T[[1]] b = {2, 2, 4, 4, 3, 5, 3, 2, 2, 1, 1};

	pd_shared3p bool[[1]] mask (11) = true;
	G test_result = declassify(pairedTTest(a, b, mask, 0::G));
	
	G relative_error = (test_result - (-0.559016994374947)) / 0.559016994374947;
	
	//the relative error is around 1e-16 for 64 bit input	
	if (!isNegligible(relative_error))
		return false;
	
	return true;
}


template<type T>
bool multiple_testing_test(T data) {
	pd_shared3p T[[1]] a = {0.15, 0.51, 0.99};
	T[[1]] quantiles = {0.75, 0.75, 0.75};

	//broken algorithm
	return false;
}


template<type T, type G>
bool mann_whitney_u_test(T data, G data2) {
	pd_shared3p T[[1]] a = {2, 2, 3, 3, 4, 5, 3, 2, 2, 1, 1};
	pd_shared3p T[[1]] b = {2, 2, 4, 4, 3, 5, 3, 2, 2, 1, 1};

	pd_shared3p bool[[1]] mask (11) = true;
	G[[1]] test_result = declassify(mannWhitneyU(a, mask, b, mask, false, 1));
	
	G error1 = (test_result[0] - 58.5) / 58.5;
	G error2 = (test_result[1] - 0.55408061721400503) / 0.55408061721400503;
	
	if (!isNegligible(error1) || !isNegligible(error1))
		return false;
	
	return true;
}


template<type T, type G>
bool wilcoxon_rank_sum_test(T data, G data2) {
	pd_shared3p T[[1]] a = {2, 2, 3, 3, 4, 5, 3, 2, 2, 1, 1};
	pd_shared3p T[[1]] b = {2, 2, 4, 4, 3, 5, 3, 2, 2, 1, 1};

	pd_shared3p bool[[1]] mask (11)= true;
	
	G[[1]] test_result = declassify(wilcoxonRankSum(a, mask, b, mask, false, 1));

	G error1 = (test_result[0] - 58.5) / 58.5;
	G error2 = (test_result[1] - 0.55408061721400503) / 0.55408061721400503;
	
	if (!isNegligible(error1) || !isNegligible(error1))
		return false;
	
	return true;
}


template<type T, type G>
bool wilcoxon_signed_rank_test(T data, G data2) {
	pd_shared3p T[[1]] a = {2, 2, 3, 3, 4, 5, 3, 2, 2, 1, 1};
	pd_shared3p T[[1]] b = {2, 2, 4, 4, 3, 5, 3, 2, 2, 1, 1};

	pd_shared3p bool[[1]] mask (11)= true;
	
	G[[1]] test_result = declassify(wilcoxonSignedRank(a, b, mask, false, 1));

	printVector(test_result);
	
	G error1 = (test_result[0] - 2) / 2;
	G error2 = (test_result[1] - 0.806761884614384) / 0.806761884614384;
	
	if (!isNegligible(error1) || !isNegligible(error1))
		return false;
	
	return true;
}


void main() {
	string test_prefix = "tTest(two sample vectors)";
	test(test_prefix, tTest_test_samples(0::int32, 0::float32), 0::int32);
	test(test_prefix, tTest_test_samples(0::int64, 0::float64), 0::int64);

	test_prefix = "tTest";
	test(test_prefix, tTest_test(0::int32, 0::float32), 0::int32);
	test(test_prefix, tTest_test(0::int64, 0::float64), 0::int64);
	
	test_prefix = "PairedTTest";
	test(test_prefix, tTest_test_paired(0::int32, 0::float32), 0::int32);
	test(test_prefix, tTest_test_paired(0::int64, 0::float64), 0::int64);
	
	test_prefix = "multipleTesting";
	test(test_prefix, multiple_testing_test(0::float32), 0::float32);
	test(test_prefix, multiple_testing_test(0::float64), 0::float64);
	
	test_prefix = "MannWhitneyU";
	test(test_prefix, mann_whitney_u_test(0::int32, 0::float32), 0::float32);
	test(test_prefix, mann_whitney_u_test(0::int64, 0::float64), 0::float64);

	test_prefix = "WilcoxonRankSum";
	test(test_prefix, wilcoxon_rank_sum_test(0::int32, 0::float32), 0::int32);
	test(test_prefix, wilcoxon_rank_sum_test(0::int64, 0::float64), 0::int64);
	
	test_prefix = "WilcoxonSignedRank";
	test(test_prefix, wilcoxon_signed_rank_test(0::int32, 0::float32), 0::int32);
	test(test_prefix, wilcoxon_signed_rank_test(0::int64, 0::float64), 0::int64);
	
	test_report();

}