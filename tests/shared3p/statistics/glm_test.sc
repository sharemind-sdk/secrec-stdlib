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

import matrix;
import shared3p;
import shared3p_matrix;
import shared3p_statistics_glm;
import stdlib;
import test_utility;
import test_utility_float;

domain pd_shared3p shared3p;

float64[[1]] gaussDependent = {808, 372, -279, -499, 107};
float64[[2]] gaussVariables = reshape({151, 25, 53, 60, -43, -31, -162, 147, -47, 174}, 5, 2);

float64[[1]] binomialDependent = {1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1};
float64[[2]] binomialVariables = reshape({0.192491906485068, -1.44670180633351, -0.323180534047634, 1.62229611652493, -0.689024123596357, 2.04212222261495, 0.94377911190294, 2.0819268787991, 1.91711727878331, -0.414812239592928, 1.03285349943413, -1.67856959219527, 0.157549690345431, 1.48913611644558, -0.0757895625491196, 1.27178094415894, 0.641673407672177, 0.800761254937157, 1.86265922566283, -0.545356026768875, 0.619277810751181, 0.122640425510084, -0.790984165426082, -0.499771670185565, -1.63515201379241, 1.78456731932523, 0.298878272130168, 0.752504894134308, -1.0746021434363, 0.0642737442852427, 1.26264947910893, 1.09994929620937, -1.1235551287923, 3.44607885263655, -0.719722171975663, -0.326854097538061, -0.529773969939608, 0.524152744449217, -1.07960964710456, -0.0551110192012596, 2.19868418440574, -1.77390932861561, 0.0650322264968479, 0.654970382227184, -0.712869108984741, 1.02056753963664, 0.631614719104035, 0.447433352715578, 0.46653706410743, -0.698260607801304}, 50, 1);

template<type T, dim N>
bool checkError (T[[N]] res, T[[N]] correct) {
    return all ((abs (res - correct) / correct) < 1e-5);
}

template<type T, type G>
bool glm_test_gaussian (T proxy, G proxy2) {
    pd_shared3p T[[1]] dependent = (T) gaussDependent;
    pd_shared3p T[[2]] variables = (T) gaussVariables;

    G[[1]] correct = {4.95141885879185, 1.99690198083159, -0.434027517967568};
    G[[1]] result = declassify (generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_GAUSSIAN,
                                                        1 :: uint).coefficients);

    return checkError (result, correct);
}

template<type T>
bool glm_test_binomial_logit (T proxy) {
    pd_shared3p T[[1]] dependent = (T) binomialDependent;
    pd_shared3p T[[2]] variables = (T) binomialVariables;

    T[[1]] correct = {4.1795259575741, 2.90761690234768};
    T[[1]] result = declassify(generalizedLinearModel(dependent, variables,
                                                      GLM_FAMILY_BINOMIAL_LOGIT,
                                                      10 :: uint).coefficients);

    print("glm logit coefficients");
    printVector(result);

    return checkError (result, correct);
}

template<type T>
bool glm_test_gaussian_standard_errors (T proxy) {
    pd_shared3p T[[1]] dependent = (T) gaussDependent;
    pd_shared3p T[[2]] variables = (T) gaussVariables;

    pd_shared3p T[[1]] params = generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_GAUSSIAN,
                                                        1 :: uint).coefficients;

    T[[1]] correct = {0.0678241596985888, 0.0938740634943971, 9.14127043177142};
    T[[1]] result = declassify (glmStandardErrors (dependent, variables,
                                                   params, GLM_FAMILY_GAUSSIAN));
    return checkError (result, correct);
}

template<type T>
bool glm_test_binomial_logit_standard_errors (T proxy) {
    pd_shared3p T[[1]] dependent = (T) binomialDependent;
    pd_shared3p T[[2]] variables = (T) binomialVariables;

    pd_shared3p T[[1]] params = generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_BINOMIAL_LOGIT,
                                                        10 :: uint).coefficients;

    T[[1]] correct = {1.52981053976479, 1.08391410852815};
    T[[1]] errors = declassify (glmStandardErrors (dependent, variables,
                                                   params, GLM_FAMILY_BINOMIAL_LOGIT));

    return checkError (errors, correct);
}

template<type T>
bool glm_test_gauss_aic (T proxy) {
    pd_shared3p T[[1]] dependent = (T) gaussDependent;
    pd_shared3p T[[2]] variables = (T) gaussVariables;
    GLMResult<pd_shared3p, T> m = generalizedLinearModel (dependent, variables,
                                                          GLM_FAMILY_GAUSSIAN, 1 :: uint);
    T aic = declassify (GLMAIC (dependent, m));
    T correct = 27.6760089698144;

    return checkError (aic, correct);
}

template<type T>
bool glm_test_binomial_aic (T proxy) {
    pd_shared3p T[[1]] dependent = (T) binomialDependent;
    pd_shared3p T[[2]] variables = (T) binomialVariables;
    GLMResult<pd_shared3p, T> m = generalizedLinearModel (dependent, variables,
                                                          GLM_FAMILY_BINOMIAL_LOGIT, 10 :: uint);
    T aic = declassify (GLMAIC (dependent, m));
    T correct = 24.2553922448486;

    return checkError (aic, correct);
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

    test_prefix = "GeneralizedLinearModelAIC(Gaussian)";
    test (test_prefix, glm_test_gauss_aic (0 :: float32), 0 :: float32);
    test (test_prefix, glm_test_gauss_aic (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelAIC(Binomial)";
    // 32-bit version runs out of bits at some point :(
    //test (test_prefix, glm_test_binomial_aic (0 :: float32), 0 :: float32);
    test (test_prefix, glm_test_binomial_aic (0 :: float64), 0 :: float64);

    test_report ();
}
