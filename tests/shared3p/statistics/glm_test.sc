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

float64[[1]] gaussDependent = {806, 386, -276, -515, 114};
float64[[2]] gaussVariables = reshape({151, 25, 53, 60, -43, -31, -162, 147, -47, 174}, 5, 2);

float64[[1]] binomialDependent = {0.00113367185625001, 0.00311180891921009, 0.997806847193992, 0.922963127457546, 0.782679248264553, 0.43361973990276, 0.946321200496039, 0.115200611835863, 0.963834734242139, 0.999988944605178};
float64[[2]] binomialVariables = reshape({-1.42149803885464, -0.557889658238812, -1.49378731776404, -0.100165942116239, 1.01591698897896, -0.319788457730307, -0.118780028290195, 0.359068418383317, 0.385724119986289, -0.882424004426778, 0.212590405381168, -1.11001658081987, 0.380191620724843, -0.343798061757185, -0.718598533796575, -0.148564403655281, 0.250528207632885, 0.0100598851614594, 1.78537081279023, 0.161908970189403}, 10, 2);

template<type T, type G>
bool glm_test_gaussian (T proxy, G proxy2) {
    pd_shared3p T[[1]] dependent = (T) gaussDependent;
    pd_shared3p T[[2]] variables = (T) gaussVariables;

    G[[1]] correct = {5, 2, 1};
    G[[1]] result = declassify (generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_GAUSSIAN,
                                                        1 :: uint));

    // We have decided that this is OK.
    return sum(abs(result - correct)) < 1e-4;
}


template<type T>
bool glm_test_binomial_logit (T proxy) {
    pd_shared3p T[[1]] dependent = (T) binomialDependent;
    pd_shared3p T[[2]] variables = (T) binomialVariables;

    T[[1]] correct = {5, 3, 2};
    T[[1]] result = declassify(generalizedLinearModel(dependent,
                                                      variables,
                                                      GLM_FAMILY_BINOMIAL_LOGIT,
                                                      10 :: uint));

    return isNegligible(sum(abs(result - correct)));
}

template<type T>
bool glm_test_gaussian_standard_errors (T proxy) {
    pd_shared3p T[[1]] dependent = (T) gaussDependent;
    pd_shared3p T[[2]] variables = (T) gaussVariables;

    pd_shared3p T[[1]] params = generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_GAUSSIAN,
                                                        1 :: uint);

    T[[1]] errors = declassify (parametersStandardErrors (dependent, variables,
                                                          params,
                                                          GLM_FAMILY_GAUSSIAN));
    return isNegligible(sum(errors));
}

template<type T>
bool glm_test_binomial_logit_standard_errors (T proxy) {
    pd_shared3p T[[1]] dependent = (T) binomialDependent;
    pd_shared3p T[[2]] variables = (T) binomialVariables;

    pd_shared3p T[[1]] params = generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_BINOMIAL_LOGIT,
                                                        10 :: uint);

    T[[1]] correct = {3.67933325304129, 2.81926692420377, 2.03066423930818};
    T[[1]] errors = declassify (parametersStandardErrors (dependent, variables,
                                                          params,
                                                          GLM_FAMILY_BINOMIAL_LOGIT));

    return isNegligible(sum(abs(correct - errors)));
}

void main () {
    string test_prefix = "GeneralizedLinearModel(Gaussian)";
    test (test_prefix, glm_test_gaussian (0::int32, 0::float32), 0::int32);
    test (test_prefix, glm_test_gaussian (0::int64, 0::float64), 0::int64);

    test_prefix = "GeneralizedLinearModel(Binomial Logit)";
    test (test_prefix, glm_test_binomial_logit (0::float32), 0::float32);
    test (test_prefix, glm_test_binomial_logit (0::float64), 0::float64);

    test_prefix = "GeneralizedLinearModelStandardErrors(Gaussian)";
    test (test_prefix, glm_test_gaussian_standard_errors (0 :: float32), 0 :: float32);
    test (test_prefix, glm_test_gaussian_standard_errors (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelStandardErrors(Binomial Logit)";
    test (test_prefix, glm_test_binomial_logit_standard_errors (0 :: float32), 0 :: float32);
    test (test_prefix, glm_test_binomial_logit_standard_errors (0 :: float64), 0 :: float64);

    test_report ();
}
