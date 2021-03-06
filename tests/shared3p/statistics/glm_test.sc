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

float64[[2]] gammaVariables = reshape({5.19249190648507, 3.55329819366649, 4.67681946595237, 6.62229611652493, 4.31097587640364, 7.04212222261495, 5.94377911190294, 7.0819268787991, 6.91711727878331, 4.58518776040707, 6.03285349943413, 3.32143040780473, 5.15754969034543, 6.48913611644558, 4.92421043745088, 6.27178094415894, 5.64167340767218, 5.80076125493716, 6.86265922566283, 4.45464397323112, 5.61927781075118, 5.12264042551008, 4.20901583457392, 4.50022832981444, 3.36484798620759, 6.78456731932523, 5.29887827213017, 5.75250489413431, 3.9253978565637, 5.06427374428524, 6.26264947910893, 6.09994929620937, 3.8764448712077, 8.44607885263655, 4.28027782802434, 4.67314590246194, 4.47022603006039, 5.52415274444922, 3.92039035289544, 4.94488898079874, 7.19868418440574, 3.22609067138439, 5.06503222649685, 5.65497038222718, 4.28713089101526, 6.02056753963664, 5.63161471910403, 5.44743335271558, 5.46653706410743, 4.3017393921987}, 50, 1);
float64[[1]] gammaDependent = {0.0344909998167791, 0.0492724666832504, 0.0372015362971099, 0.0265679761839915, 0.0401120360466308, 0.0271831835275016, 0.0282195086712689, 0.0266253976547209, 0.0269250781549351, 0.0392730158613867, 0.028775477804606, 0.05131444348819, 0.0358578469537841, 0.0286840131237716, 0.0373319541966633, 0.0297822597374725, 0.0335529289433364, 0.033307317726675, 0.0261497822774473, 0.0382433364346987, 0.0328175459387675, 0.0347274769556521, 0.040626391830439, 0.0394013908370966, 0.0504555404878971, 0.0277957933542635, 0.0331510253626339, 0.0315218335225801, 0.0453734302844344, 0.0351558241089978, 0.0289815656641404, 0.0313112342847484, 0.0466877703009289, 0.0220310886056453, 0.0423356154899231, 0.0369008393838242, 0.038404797859964, 0.0317869556484552, 0.045097096864194, 0.0354464921744227, 0.0268610617323276, 0.0506512677703062, 0.0352357251594805, 0.0319115889404451, 0.0419995144309442, 0.0313379912748246, 0.0325445570861518, 0.0331224680712466, 0.0328434070403025, 0.041713854233018};

float64[[2]] poissonVariables = reshape({0.192491906485068, -1.44670180633351, -0.323180534047634, 1.62229611652493, -0.689024123596357, 2.04212222261495, 0.94377911190294, 2.0819268787991, 1.91711727878331, -0.414812239592928, 1.03285349943413, -1.67856959219527, 0.157549690345431, 1.48913611644558, -0.0757895625491196, 1.27178094415894, 0.641673407672177, 0.800761254937157, 1.86265922566283, -0.545356026768875, 0.619277810751181, 0.122640425510084, -0.790984165426082, -0.499771670185565, -1.63515201379241, 1.78456731932523, 0.298878272130168, 0.752504894134308, -1.0746021434363, 0.0642737442852427, 1.26264947910893, 1.09994929620937, -1.1235551287923, 3.44607885263655, -0.719722171975663, -0.326854097538061, -0.529773969939608, 0.524152744449217, -1.07960964710456, -0.0551110192012596, 2.19868418440574, -1.77390932861561, 0.0650322264968479, 0.654970382227184, -0.712869108984741, 1.02056753963664, 0.631614719104035, 0.447433352715578, 0.46653706410743, -0.698260607801304}, 50, 1);
float64[[1]] poissonDependent = {53, 0, 4, 66935, 1, 546129, 2250, 666393, 292315, 3, 3513, 0, 44, 34395, 14, 11602, 497, 1101, 222637, 1, 444, 37, 0, 2, 0, 150668, 90, 865, 0, 28, 11084, 4914, 0, 610869716, 1, 4, 1, 276, 0, 15, 1194718, 0, 28, 531, 1, 3304, 473, 188, 207, 1};

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
                                                      10u64).coefficients);
    return checkError (result, correct);
}

template<type T>
bool glm_test_gamma (T proxy) {
    pd_shared3p T[[1]] dependent = (T) gammaDependent;
    pd_shared3p T[[2]] variables = (T) gammaVariables;

    T[[1]] correct = {4.93079909029115, 3.20732255964112};
    T[[1]] result = declassify(generalizedLinearModel(dependent, variables,
                                                      GLM_FAMILY_GAMMA,
                                                      10u64).coefficients);

    return checkError (result, correct);
}

template<type T>
bool glm_test_poisson (T proxy) {
    pd_shared3p T[[1]] dependent = (T) poissonDependent;
    pd_shared3p T[[2]] variables = (T) poissonVariables;

    T[[1]] correct = {5, 3};
    T[[1]] result = declassify(generalizedLinearModel(dependent, variables,
                                                      GLM_FAMILY_POISSON,
                                                      10u64).coefficients);

    return checkError (result, correct);
}

template<type T>
bool glm_test_gaussian_standard_errors (T proxy) {
    pd_shared3p T[[1]] dependent = (T) gaussDependent;
    pd_shared3p T[[2]] variables = (T) gaussVariables;

    pd_shared3p T[[1]] params = generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_GAUSSIAN,
                                                        1u64).coefficients;

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
                                                        10u64).coefficients;

    T[[1]] correct = {1.52981053976479, 1.08391410852815};
    T[[1]] errors = declassify (glmStandardErrors (dependent, variables,
                                                   params, GLM_FAMILY_BINOMIAL_LOGIT));

    return checkError (errors, correct);
}

template<type T>
bool glm_test_gamma_standard_errors (T proxy) {
    pd_shared3p T[[1]] dependent = (T) gammaDependent;
    pd_shared3p T[[2]] variables = (T) gammaVariables;

    pd_shared3p T[[1]] params = generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_GAMMA,
                                                        10u64).coefficients;

    T[[1]] correct = {0.10380816933941, 0.51836580460864};
    T[[1]] errors = declassify (glmStandardErrors (dependent, variables,
                                                   params, GLM_FAMILY_GAMMA));

    return checkError (errors, correct);
}

template<type T>
bool glm_test_poisson_standard_errors (T proxy) {
    pd_shared3p T[[1]] dependent = (T) poissonDependent;
    pd_shared3p T[[2]] variables = (T) poissonVariables;

    pd_shared3p T[[1]] params = generalizedLinearModel (dependent, variables,
                                                        GLM_FAMILY_POISSON,
                                                        10u64).coefficients;

    T[[1]] correct = {0.000395844328169872, 0.0013618066855353};
    T[[1]] errors = declassify (glmStandardErrors (dependent, variables,
                                                   params, GLM_FAMILY_POISSON));

    return checkError (errors, correct);
}

template<type T>
bool glm_test_gauss_aic (T proxy) {
    pd_shared3p T[[1]] dependent = (T) gaussDependent;
    pd_shared3p T[[2]] variables = (T) gaussVariables;
    GLMResult<pd_shared3p, T> m = generalizedLinearModel (dependent, variables,
                                                          GLM_FAMILY_GAUSSIAN, 1u64);
    T aic = declassify (GLMAIC (dependent, m));
    T correct = 27.6760089698144;

    return checkError (aic, correct);
}

template<type T>
bool glm_test_binomial_aic (T proxy) {
    pd_shared3p T[[1]] dependent = (T) binomialDependent;
    pd_shared3p T[[2]] variables = (T) binomialVariables;
    GLMResult<pd_shared3p, T> m = generalizedLinearModel (dependent, variables,
                                                          GLM_FAMILY_BINOMIAL_LOGIT, 10u64);
    T aic = declassify (GLMAIC (dependent, m));
    T correct = 24.2553922448486;

    return checkError (aic, correct);
}

template<type T>
bool glm_test_gamma_aic (T proxy) {
    pd_shared3p T[[1]] dependent = (T) gammaDependent;
    pd_shared3p T[[2]] variables = (T) gammaVariables;
    GLMResult<pd_shared3p, T> m = generalizedLinearModel (dependent, variables,
                                                          GLM_FAMILY_GAMMA, 10u64);
    T aic = declassify (GLMAIC (dependent, m));
    T correct = -544.72219707391;

    return checkError (aic, correct);
}

template<type T>
bool glm_test_poisson_aic (T proxy) {
    pd_shared3p T[[1]] dependent = (T) poissonDependent;
    pd_shared3p T[[2]] variables = (T) poissonVariables;
    GLMResult<pd_shared3p, T> m = generalizedLinearModel (dependent, variables,
                                                          GLM_FAMILY_POISSON, 10u64);
    T aic = declassify (GLMAIC (dependent, m));
    T correct = 342.630355248294;

    return checkError (aic, correct);
}

void main () {
    string test_prefix = "GeneralizedLinearModel(Gaussian)";

    test (test_prefix, glm_test_gaussian (0::int32, 0::float32), 0::int32);
    test (test_prefix, glm_test_gaussian (0::int64, 0::float64), 0::int64);

    test_prefix = "GeneralizedLinearModel(Binomial Logit)";
    test (test_prefix, glm_test_binomial_logit (0::float32), 0::float32);
    test (test_prefix, glm_test_binomial_logit (0::float64), 0::float64);

    test_prefix = "GeneralizedLinearModel(Gamma)";
    test (test_prefix, glm_test_gamma (0::float64), 0::float64);

    test_prefix = "GeneralizedLinearModel(Poisson)";
    test (test_prefix, glm_test_poisson (0::float64), 0::float64);

    test_prefix = "GeneralizedLinearModelStandardErrors(Gaussian)";
    test (test_prefix, glm_test_gaussian_standard_errors (0 :: float32), 0 :: float32);
    test (test_prefix, glm_test_gaussian_standard_errors (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelStandardErrors(Binomial Logit)";
    test (test_prefix, glm_test_binomial_logit_standard_errors (0 :: float32), 0 :: float32);
    test (test_prefix, glm_test_binomial_logit_standard_errors (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelStandardErrors(Gamma)";
    test (test_prefix, glm_test_gamma_standard_errors (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelStandardErrors(Poisson)";
    test (test_prefix, glm_test_poisson_standard_errors (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelAIC(Gaussian)";
    test (test_prefix, glm_test_gauss_aic (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelAIC(Binomial)";
    test (test_prefix, glm_test_binomial_aic (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelAIC(Gamma)";
    test (test_prefix, glm_test_gamma_aic (0 :: float64), 0 :: float64);

    test_prefix = "GeneralizedLinearModelAIC(Poisson)";
    test (test_prefix, glm_test_poisson_aic (0 :: float64), 0 :: float64);

    test_report ();
}
