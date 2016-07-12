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
import shared3p_statistics_glm;
import test_utility;
import matrix;
import shared3p_matrix;

domain pd_shared3p shared3p;


template<type T, type G>
bool glm_test_gaussian (T data, G data2) {
	pd_shared3p T[[1]] dependent = {1, 2, 1, 5};
	pd_shared3p T[[2]] variables = reshape ({2, 2, 3,
											 1, 3, 3,
											 4, 1, 5,
											 5, 1, 2
											 }, 4, 3);
	
	G[[1]] result = declassify (generalizedLinearModel (dependent, variables, 0, 5::uint));
	
	// Y = aX + b, where Y is the dependent, X is variables, a and b are the results from the variable
	// b is the last element of the result
	G[[2]] test = matrixMultiplication((G) declassify (variables), reshape (result[0:3], 3, 1)) + result[3];
	
	G relative_error = sum (abs (transpose (test)[0, :] - (G) declassify (dependent))) / sum ((G) declassify (dependent));
	
	
	
	if (!isNegligible (relative_error))
		return false;
	
	return true;
}


template<type T, type G>
bool glm_test_binomial_logit (T data, G data2) {
	pd_shared3p T[[1]] dependent = {0, 1, 0, 0, 1, 0, 0};
	pd_shared3p T[[2]] variables = reshape ({0.3, 0.8, 0.35, 0.4, 0.7, 0.2, 0.5}, 7, 1);
	
	G[[1]] result = declassify (generalizedLinearModel (dependent, variables, 1, 15::uint));

	//p = exp(ax + b) / (1 + exp(ax + b))
	//where p is the dependent, x is the variable, a and b are the regression results
	G a = result[0];
	G b = result[1];
	
	bool[[1]] test_results (7) = false;
	G probability;
	G c;

	//test every element individually if they match the dependent
	for (uint i = 0; i < 7; i++) {
		c = exp (declassify (variables[i, 0]) * a + b);
		probability = c / (1 + c);
		test_results[i] = isNegligible (probability - declassify (dependent[i]));
	}	
	if(!all (test_results))
		return false;
	
	return true;
}



void main () {
	string test_prefix = "GeneralizedLinearModel(Gaussian)";
	test (test_prefix, glm_test_gaussian (0::int32, 0::float32), 0::int32);
	test (test_prefix, glm_test_gaussian (0::int64, 0::float64), 0::int64);
	
	test_prefix = "GeneralizedLinearModel(Binomial Logit)";
	test (test_prefix, glm_test_binomial_logit (0::float32, 0::float32), 0::int32);
	test (test_prefix, glm_test_binomial_logit (0::float64, 0::float64), 0::int64);
	
	
	
	test_report ();
}