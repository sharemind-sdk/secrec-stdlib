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
import matrix;

domain pd_shared3p shared3p;

template<type T, type G>
bool test_lg (T data, G data2, int64 algorithm) {
	pd_shared3p T[[2]] variables = reshape (
		{1, 1, 2, 3, 1, 4,
		 2, 2, 1, 6, 2, 3,
		 2, 3, 6, 1, 5, 2,
		 2, 1, 2, 5, 3, 1,
		 1, 1, 1, 1, 4, 3,
		 4, 3, 1, 1, 3, 6}, 6, 6);

	variables = transpose (variables);

	pd_shared3p T[[1]] dependent = {1, 2, 2, 4, 6, 1};
	pd_shared3p G[[1]] result = linearRegression(variables, dependent, algorithm);

	G[[2]] x = (G) declassify (variables);
	G[[2]] y = declassify (reshape (result, 7, 1));

	//Y = aX + b where Y is the dependent, X is the variables, a and b are the results from linear regression
	G[[2]] output = matrixMultiplication (x, reshape(y[0:6, 0], 6, 1)) + y[6, 0];
	G[[2]] expected = (G) reshape (declassify (dependent), 6, 1);

	G relative_error = sum (colSums (expected-output)) / sum (colSums (expected));

	if (!isNegligible (relative_error))
		return false;

	return true;
}

template<type T, type G>
bool test_lg_invert (T data, G data2) {
	pd_shared3p T[[2]] variables = reshape (
		{1, 2, 1, 5, 3,
		 2, 2, 5, 5, 1,
		 1, 1, 5, 1, 3}, 3, 5);

	variables = transpose (variables);

	pd_shared3p T[[1]] dependent = {1, 2, 2, 4, 1};
	pd_shared3p G[[1]] result = linearRegression (variables, dependent, LINEAR_REGRESSION_INVERT);

	G[[2]] x = (G) declassify (variables);
	G[[2]] y = declassify (reshape (result, 4, 1));

	//Y = aX + b where Y is the dependent, X is the variables, a and b are the results from linear regression
	G[[2]] output = matrixMultiplication (x, reshape (y[0:3, 0], 3, 1)) + y[3, 0];
	G[[2]] expected = (G) reshape (declassify (dependent), 5, 1);

	G relative_error = sum (colSums (expected-output)) / sum (colSums (expected));

	if (!isNegligible (relative_error))
		return false;

	return true;
}

template<type T, type G>
bool test_lg_cg (T data, G data2) {
	pd_shared3p T[[2]] variables = reshape(
		{1, 1, 2,
		 2, 2, 1,
		 2, 3, 6,
		 2, 1, 2,
		 1, 1, 1}, 5, 3);

	variables = transpose (variables);

	pd_shared3p T[[1]] dependent = {1, 2, 2};
	pd_shared3p G[[1]] result = linearRegressionCG (variables, dependent, 10::uint64);

	G[[2]] x = (G) declassify(variables);
	G[[2]] y = declassify(reshape(result, 6, 1));

	//Y = aX + b where Y is the dependent, X is the variables, a and b are the results from linear regression
	G[[2]] output = matrixMultiplication (x, reshape (y[0:5, 0], 5, 1)) + y[5, 0];
	G[[2]] expected = (G) reshape (declassify (dependent), 3, 1);

	G relative_error = sum (colSums (expected-output)) / sum (colSums (expected));

	//the relative error is about 1e-8 for 32 bit and 1e-14 for 64 bit inputs
	return isNegligible (relative_error);
}

template<type T>
bool test_weighted_lm (T proxy) {
    uint n = 20;
    pd_shared3p T[[2]] vars(n, 1);
    pd_shared3p T[[1]] dependent = {5026.0459111305, 4754.73215035163, 1925.14353839215, 4625.57224661577, 3249.94637351483, 2852.58563782554, 3878.04330675863, 1126.20205164421, 3508.44626605976, 3943.3260501828, 2657.50669012778, 4001.08882889617, 4867.41537717171, 1619.7289864067, 2313.43828211538, 5116.53065390419, 4894.79921536986, 691.266294685192, 2828.28611170407, 3107.55305294879};
    pd_shared3p T[[1]] weights = {0.379559240536764, 0.435771584976465, 0.0374310328625143, 0.973539913771674, 0.431751248892397, 0.957576596643776, 0.887754905503243, 0.639978769468144, 0.970966610359028, 0.618838207330555, 0.333427211269736, 0.346748248208314, 0.39848541142419, 0.784692775690928, 0.038936491124332, 0.748795386170968, 0.67727683018893, 0.171264330390841, 0.261087963823229, 0.514412934659049};
    vars[:, 0] = {914.806043496355, 937.075413297862, 286.139534786344, 830.447626067325, 641.745518893003, 519.095949130133, 736.588314641267, 134.66659723781, 656.992290401831, 705.064784036949, 457.74177624844, 719.112251652405, 934.672247152776, 255.428824340925, 462.292822543532, 940.014522755519, 978.226428385824, 117.487361654639, 474.997081561014, 560.332746244967};
    T[[1]] output = declassify(weightedLinearRegression(vars, dependent, weights));
    T[[1]] expected = {4.86539991397082, 380.603620293051};
    return isNegligible(sum(abs(output - expected) / expected));
}

void main() {
	string test_prefix = "LinearRegression (Gauss)";
	test (test_prefix, test_lg (0::int32, 0::float32, LINEAR_REGRESSION_GAUSS), 0::int32);
	test (test_prefix, test_lg (0::int64, 0::float64, LINEAR_REGRESSION_GAUSS), 0::int64);

	test_prefix = "LinearRegression (Lu Decomposition)";
	test (test_prefix, test_lg (0::int32, 0::float32, LINEAR_REGRESSION_LU_DECOMPOSITION), 0::int32);
	test (test_prefix, test_lg (0::int64, 0::float64, LINEAR_REGRESSION_LU_DECOMPOSITION), 0::int64);

	test_prefix = "LinearRegression (Invert)";
	test (test_prefix, test_lg_invert (0::int32, 0::float32), 0::int32);
	test (test_prefix, test_lg_invert (0::int64, 0::float64), 0::int64);

	test_prefix = "LinearRegressionCG";
	test (test_prefix, test_lg_cg (0::int32, 0::float32), 0::int32);
	test (test_prefix, test_lg_cg (0::int64, 0::float64), 0::int64);

    test_prefix = "Weighted linear regression";
    test (test_prefix, test_weighted_lm (0 :: float32), 0 :: float32);
    test (test_prefix, test_weighted_lm (0 :: float64), 0 :: float64);

	test_report();
}
