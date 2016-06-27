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
bool test_lg(T data, G data2, int64 algorithm) {
	pd_shared3p T[[2]] variables = reshape(
		{1, 1, 2, 3, 1, 4, 
		 2, 2, 1, 6, 2, 3,
		 2, 3, 6, 1, 5, 2,
		 2, 1, 2, 5, 3, 1,
		 1, 1, 1, 1, 4, 3,
		 4, 3, 1, 1, 3, 6}, 6, 6);
	
	variables = transpose(variables);
	
	pd_shared3p T[[1]] dependent = {1, 2, 2, 4, 6, 1};
	pd_shared3p G[[1]] result = linearRegression(variables, dependent, algorithm);
	
	G[[2]] x = (G) declassify(variables);
	G[[2]] y = declassify(reshape(result, 7, 1));
	G[[2]] output = matrixMultiplication(x, reshape(y[0:6, 0], 6, 1)) + y[6, 0];	
	G[[2]] expected = (G) reshape(declassify(dependent), 6, 1);
	
	G relative_error = sum(colSums(expected-output)) / sum(colSums(expected));
	
	printMatrix(output);
	print(relative_error);
	
	if (!isNegligible(relative_error))
		return false;
		
	return true;
}


template<type T, type G>
bool test_lg_invert(T data, G data2) {
	pd_shared3p T[[2]] variables = reshape(
		{1, 2, 1, 5, 3,
		 2, 2, 5, 5, 1,
		 1, 1, 5, 1, 3}, 3, 5);
	
	variables = transpose(variables);
	
	pd_shared3p T[[1]] dependent = {1, 2, 2, 4, 1};
	pd_shared3p G[[1]] result = linearRegression(variables, dependent, LINEAR_REGRESSION_INVERT);
	
	G[[2]] x = (G) declassify(variables);
	G[[2]] y = declassify(reshape(result, 4, 1));
	G[[2]] output = matrixMultiplication(x, reshape(y[0:3, 0], 3, 1)) + y[3, 0];	
	G[[2]] expected = (G) reshape(declassify(dependent), 5, 1);
	
	G relative_error = sum(colSums(expected-output)) / sum(colSums(expected));
	
	printMatrix(output);
	print(relative_error);
	
	if (!isNegligible(relative_error))
		return false;
		
	return true;
}


template<type T, type G>
bool test_lg_cg(T data, G data2) {
	pd_shared3p T[[2]] variables = reshape(
		{1, 1, 2,
		 2, 2, 1,
		 2, 3, 6,
		 2, 1, 2,
		 1, 1, 1}, 5, 3);
	
	variables = transpose(variables);
	
	pd_shared3p T[[1]] dependent = {1, 2, 2};
	pd_shared3p G[[1]] result = linearRegressionCG(variables, dependent, 10::uint64);
	
	G[[2]] x = (G) declassify(variables);
	G[[2]] y = declassify(reshape(result, 6, 1));
	G[[2]] output = matrixMultiplication(x, reshape(y[0:5, 0], 5, 1)) + y[5, 0];	
	G[[2]] expected = (G) reshape(declassify(dependent), 3, 1);
	
	G relative_error = sum(colSums(expected-output)) / sum(colSums(expected));
	
	printMatrix(output);
	print(relative_error);
	
	//the relative error is about 1e-8 for 32 bit and 1e-14 for 64 bit inputs
	if (!isNegligible(relative_error))
		return false;
		
	return true;
}


void main() {
	string test_prefix = "LinearRegression (Gauss)";
	test(test_prefix, test_lg(0::int32, 0::float32, LINEAR_REGRESSION_GAUSS), 0::int32);
	test(test_prefix, test_lg(0::int64, 0::float64, LINEAR_REGRESSION_GAUSS), 0::int64);
	
	test_prefix = "LinearRegression (Lu Decomposition)";
	test(test_prefix, test_lg(0::int32, 0::float32, LINEAR_REGRESSION_LU_DECOMPOSITION), 0::int32);
	test(test_prefix, test_lg(0::int64, 0::float64, LINEAR_REGRESSION_LU_DECOMPOSITION), 0::int64);
	
	test_prefix = "LinearRegression (Invert)";
	test(test_prefix, test_lg_invert(0::int32, 0::float32), 0::int32);
	test(test_prefix, test_lg_invert(0::int64, 0::float64), 0::int64);
	
	test_prefix = "LinearRegressionCG";
	test(test_prefix, test_lg_cg(0::int32, 0::float32), 0::int32);
	test(test_prefix, test_lg_cg(0::int64, 0::float64), 0::int64);
	
	test_report();
}