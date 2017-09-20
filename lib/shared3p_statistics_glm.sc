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

/** \cond */
module shared3p_statistics_glm;

import shared3p_matrix;
import shared3p_oblivious;
import shared3p_statistics_common;
import shared3p_statistics_regression;
import shared3p;
import matrix;
import oblivious;
import stdlib;
/** \endcond */

/**
 * @file
 * \defgroup shared3p_statistics_glm shared3p_statistics_glm.sc
 * \defgroup shared3p_glm_constants constants
 * \defgroup shared3p_glm_result GLMResult
 * \defgroup shared3p_generalized_linear_model generalizedLinearModel
 * \defgroup shared3p_generalized_linear_model_method generalizedLinearModel(with method parameter)
 * \defgroup shared3p_params_stderr parametersStandardErrors
 * \defgroup shared3p_glm_aic GLMAIC
 * \defgroup shared3p_glm_aic_direct GLMAIC(direct)
 */

/**
 * \addtogroup shared3p_statistics_glm
 * @{
 * @brief Module for performing regression analysis of generalized
 * linear models.
 */

/**
 * \addtogroup shared3p_glm_result
 * @{
 * @brief GLM result type
 */
template<domain D, type T>
struct GLMResult {
    /** family parameter used in the GLM call */
    int64 family;
    /** fitted coefficients */
    D T[[1]] coefficients;
    /** means calculated from the fitted coefficients */
    D T[[1]] means;
    /** linear predictors calculated from the fitted coefficients */
    D T[[1]] linearPredictors;
}
/** @} */

/**
 * \addtogroup shared3p_glm_constants
 * @{
 * @brief constants
 * @note The "family" constants are used to specify the distribution
 * of the dependent variable. The "SOLE method" constants specify the
 * algorithm used to solve systems of linear equations.
 */
int64 GLM_FAMILY_GAUSSIAN       = 0;
int64 GLM_FAMILY_BINOMIAL_LOGIT = 1;

int64 GLM_SOLE_METHOD_INVERT             = 0;
int64 GLM_SOLE_METHOD_LU_DECOMPOSITION   = 1;
int64 GLM_SOLE_METHOD_GAUSS              = 2;
int64 GLM_SOLE_METHOD_CONJUGATE_GRADIENT = 3;
/** @} */

/** \cond */

/*
 * Fitting of generalized linear models. Read "Generalized Linear
 * Models" by McCullagh and Nelder.
 */
template<domain D : shared3p, type FT>
GLMResult<D, FT>
_glm(D FT[[1]] dependent,
     D FT[[2]] vars,
     int64 family,
     uint iterations,
     int64 SOLEmethod,
     uint SOLEiterations)
{
    assert(family == GLM_FAMILY_GAUSSIAN ||
           family == GLM_FAMILY_BINOMIAL_LOGIT);

    assert(SOLEmethod == GLM_SOLE_METHOD_INVERT ||
           SOLEmethod == GLM_SOLE_METHOD_LU_DECOMPOSITION ||
           SOLEmethod == GLM_SOLE_METHOD_GAUSS ||
           SOLEmethod == GLM_SOLE_METHOD_CONJUGATE_GRADIENT);

    // Add a variable with all observations set to one. The parameter
    // for this variable is the intercept.
    D FT[[2]] ones(shape(vars)[0], 1) = 1;
    vars = cat(vars, ones, 1);

    D FT[[2]] z;
    uint varCount = shape(vars)[1];
    uint observationCount = size(dependent);
    D FT[[2]] y = _toCol(dependent);
    D FT[[2]] p(varCount, 1);
    D FT[[2]] mu = _initialmu(y, family);
    // Calculate initial eta from mu
    D FT[[2]] eta = _link(mu, family);
    D FT[[2]] varsTransposed = transpose(vars);

    uint iteration = 1;
    while (true) {
        // Calculate the derivative of the inverse of the link function at the current eta
        D FT[[2]] derivative = _derivative(eta, family);

        // Calculate weights
        D FT[[2]] variance = _variance(mu, family);
        D FT[[2]] weight = derivative * derivative / variance;

        // Calculate z
        D FT[[2]] z = eta + (y - mu) / derivative;

        // Solve X^T * W * X * p = X^T * W * z to find new estimation
        // of parameters
        D FT[[2]] varsTransWeight;
        D FT[[2]] mulR(varCount, observationCount);

        for (uint i = 0; i < varCount; ++i)
            mulR[i, :] = weight[:, 0];

        varsTransWeight = varsTransposed * mulR;

        D FT[[2]] varsSOLE = matrixMultiplication(varsTransWeight, vars);
        D FT[[2]] dependentSOLE = matrixMultiplication(varsTransWeight, z);

        if (SOLEmethod == GLM_SOLE_METHOD_INVERT) {
            assert(varCount <= 4);

            if (varCount == 1) {
                p = matrixMultiplication(inv(varsSOLE), dependentSOLE);
            } else if (varCount == 2) {
                p = matrixMultiplication(_invert2by2(varsSOLE), dependentSOLE);
            } else if (varCount == 3) {
                p = matrixMultiplication(_invert3by3(varsSOLE), dependentSOLE);
            } else if (varCount == 4) {
                p = matrixMultiplication(_invert4by4(varsSOLE), dependentSOLE);
            } else {
                assert(false); // Can't use method INVERT with more than 4 variables!
            }
        } else if (SOLEmethod == GLM_SOLE_METHOD_GAUSS) {
            p[:, 0] = _gaussianElimination(varsSOLE, dependentSOLE[:, 0]);
        } else if (SOLEmethod == GLM_SOLE_METHOD_LU_DECOMPOSITION) {
            p[:, 0] = _solveLU(varsSOLE, dependentSOLE[:, 0]);
        } else if (SOLEmethod == GLM_SOLE_METHOD_CONJUGATE_GRADIENT) {
            p[:, 0] = _conjugateGradient(varsSOLE, dependentSOLE[:, 0], SOLEiterations);
        }

        // Update eta
        eta = matrixMultiplication(vars, p);

        // Update mu
        mu = _linkInverse(eta, family);

        if (!(iteration < iterations))
            break;

        ++iteration;
    }

    public GLMResult<D, FT> res;
    res.family = family;
    res.coefficients = p[:, 0];
    res.means = mu[:, 0];
    res.linearPredictors = eta[:, 0];

    return res;
}

template<domain D, type FT>
D FT[[2]] _initialmu(D FT[[2]] y, int64 family) {
    D FT[[2]] res(shape(y)[0], 1);

    if (family == GLM_FAMILY_GAUSSIAN)
        res = y;
    else if (family == GLM_FAMILY_BINOMIAL_LOGIT)
        res = (y + 0.5) / 2;

    return res;
}

template<domain D, type FT>
D FT[[2]] _link(D FT[[2]] mu, int64 family) {
    D FT[[2]] res(size(mu), 1);

    if (family == GLM_FAMILY_GAUSSIAN) {
        // Link is id
        res = mu;
    } else if (family == GLM_FAMILY_BINOMIAL_LOGIT) {
        // Link is ln(mu / (1 - mu))
        res = ln(mu / (1 - mu));
    }

    return res;
}

template<domain D, type FT>
D FT[[2]] _linkInverse(D FT[[2]] eta, int64 family) {
    D FT[[2]] res(size(eta), 1);

    if (family == GLM_FAMILY_GAUSSIAN) {
        // id^-1
        res = eta;
    } else if (family == GLM_FAMILY_BINOMIAL_LOGIT) {
        // exp(eta) / (exp(eta) + 1)
        D FT[[2]] x = exp(eta);
        res = x / (x + 1);
    }

    return res;
}

// mu = g^-1(eta). This is d(mu) / d(eta) evaluated at the given eta.
template<domain D, type FT>
D FT[[2]] _derivative(D FT[[2]] eta, int64 family) {
    D FT[[2]] res(size(eta), 1);

    if (family == GLM_FAMILY_GAUSSIAN) {
        // d(eta) / d(eta) = 1
        res = 1;
    } else if (family == GLM_FAMILY_BINOMIAL_LOGIT) {
        // exp(eta) / (exp(eta) + 1)^2
        D FT[[2]] x = exp(eta);
        D FT[[2]] xp1 = x + 1;
        res = x / (xp1 * xp1);
    }

    return res;
}

template<domain D, type FT>
D FT[[2]] _variance(D FT[[2]] mu, int64 family) {
   D FT[[2]] res(size(mu), 1);

    if (family == GLM_FAMILY_GAUSSIAN) {
        res = 1;
    } else if (family == GLM_FAMILY_BINOMIAL_LOGIT) {
        res = mu * (1 - mu);
    }

    return res;
}

template<domain D, type T>
D T[[2]] _toCol(D T[[1]] vec) {
    return reshape(vec, size(vec), 1);
}

template<domain D : shared3p, type T>
GLMResult<D, T>
_dispatch(D T[[1]] dependent,
          D T[[2]] variables,
          int64 family,
          uint iterations)
{
    uint varCount = shape(variables)[1];

    if (varCount < 4) {
        return _glm(dependent, variables, family, iterations, GLM_SOLE_METHOD_INVERT, 0 :: uint);
    } else {
        return _glm(dependent, variables, family, iterations, GLM_SOLE_METHOD_LU_DECOMPOSITION, 0 :: uint);
    }
}

template<domain D : shared3p, type T>
D T[[1]] _parametersStandardErrors(D T[[1]] dependent,
                                   D T[[2]] vars,
                                   D T[[1]] params,
                                   int64 family)
{
    // Add a variable with all observations set to one. The parameter
    // for this variable is the intercept.
    D T[[2]] ones(shape(vars)[0], 1) = 1;
    vars = cat(vars, ones, 1);

    uint varCount = shape(vars)[1];
    uint observationCount = shape(vars)[0];
    D T[[2]] eta = matrixMultiplication(vars, _toCol(params));
    D T[[2]] mu = _linkInverse(eta, family);
    D T[[2]] derivative = _derivative(eta, family);
    D T[[2]] variance = _variance(mu, family);
    D T[[2]] weight = derivative * derivative / variance;
    D T[[2]] mulR(varCount, observationCount);

    for (uint i = 0; i < varCount; ++i)
        mulR[i, :] = weight[:, 0];

    D T[[2]] varsTransWeight = transpose(vars) * mulR;
    D T[[2]] X = matrixMultiplication(varsTransWeight, vars);
    D T[[2]] covMat(varCount, varCount) = borderingInverse(X);
    D T[[1]] res(varCount);

    for (uint i = 0; i < varCount; ++i) {
        res[i] = covMat[i, i];
    }

    if (family == GLM_FAMILY_GAUSSIAN) {
        // Estimate dispersion
        D T[[2]] residuals = _toCol(dependent) - eta;
        // No weights since they are all 1 for Gaussian
        D T disp = sum((residuals * residuals)[:, 0]) / (T) (observationCount - varCount);
        res *= disp;
    } else if (family == GLM_FAMILY_BINOMIAL_LOGIT) {
        // Dispersion is 1
    } else {
        assert(false); // Unknown family
    }

    return sqrt(res);
}
/** \endcond */

/**
 * \addtogroup shared3p_generalized_linear_model
 * @{
 * @brief Fitting of generalized linear models
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64" /
 * \ref float32 "float32" / \ref float64 "float64"
 * @param dependent - sample vector of the dependent variable
 * @param variables - a matrix where each column is a sample of an
 * explanatory variable
 * @param family - indicates the distribution of the dependent
 * variable
 * @return returns \ref GLMResult structure
 * @param iterations - number of iterations of the GLM algorithm
 * @leakage{None}
 */
template<domain D : shared3p>
GLMResult<D, float32>
generalizedLinearModel(D int32[[1]] dependent, D int32[[2]] variables, int64 family, uint iterations) {
    return _dispatch((float32) dependent, (float32) variables, family, iterations);
}

template<domain D : shared3p>
GLMResult<D, float64>
generalizedLinearModel(D int64[[1]] dependent, D int64[[2]] variables, int64 family, uint iterations) {
    return _dispatch((float64) dependent, (float64) variables, family, iterations);
}

template<domain D : shared3p>
GLMResult<D, float32>
generalizedLinearModel(D float32[[1]] dependent, D float32[[2]] variables, int64 family, uint iterations) {
    return _dispatch(dependent, variables, family, iterations);
}

template<domain D : shared3p>
GLMResult<D, float64>
generalizedLinearModel(D float64[[1]] dependent, D float64[[2]] variables, int64 family, uint iterations) {
    return _dispatch(dependent, variables, family, iterations);
}
/** @} */

/**
 * \addtogroup shared3p_generalized_linear_model_method
 * @{
 * @brief Fitting of generalized linear models
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64" /
 * \ref float32 "float32" / \ref float64 "float64"
 * @param dependent - sample vector of the dependent variable
 * @param variables - a matrix where each column is a sample of an
 * explanatory variable
 * @param family - indicates the distribution of the dependent
 * variable
 * @param iterations - number of iterations of the GLM algorithm
 * @param SOLEmethod - method to use for solving systems of linear equations
 * @param SOLEiterations - if the conjugate gradient method is used
 * for solving systems of linear equations, this parameter is the
 * number of iterations to use
 * @return returns \ref GLMResult structure
 * @leakage{None}
 */
template<domain D : shared3p>
GLMResult<D, float32>
generalizedLinearModel(D int32[[1]] dependent, D int32[[2]] variables, int64 family, uint iterations, int64 SOLEmethod, uint SOLEiterations) {
    return _glm((float32) dependent, (float32) variables, family, iterations, SOLEmethod, SOLEiterations);
}

template<domain D : shared3p>
GLMResult<D, float64>
generalizedLinearModel(D int64[[1]] dependent, D int64[[2]] variables, int64 family, uint iterations, int64 SOLEmethod, uint SOLEiterations) {
    return _glm((float64) dependent, (float64) variables, family, iterations, SOLEmethod, SOLEiterations);
}

template<domain D : shared3p>
GLMResult<D, float32>
generalizedLinearModel(D float32[[1]] dependent, D float32[[2]] variables, int64 family, uint iterations, int64 SOLEmethod, uint SOLEiterations) {
    return _glm(dependent, variables, family, iterations, SOLEmethod, SOLEiterations);
}

template<domain D : shared3p>
GLMResult<D, float64>
generalizedLinearModel(D float64[[1]] dependent, D float64[[2]] variables, int64 family, uint iterations, int64 SOLEmethod, uint SOLEiterations) {
    return _glm(dependent, variables, family, iterations, SOLEmethod, SOLEiterations);
}
/** @} */

/**
 * \addtogroup shared3p_params_stderr
 * @{
 * @brief Estimate the standard errors of parameters of generalized
 * linear models.
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64" /
 * \ref float32 "float32" / \ref float64 "float64"
 * @param dependent - sample vector of dependent variable
 * @param variables - a matrix where each column is a sample of an
 * explanatory variable
 * @param parameters - parameters estimated by the GLM fitting
 * procedure
 * @param family - indicates the distribution of the dependent
 * variable
 * @return returns a vector with the standard errors of each
 * parameter
 *
 * @leakage{None}
 */
template<domain D : shared3p>
D float32[[1]] parametersStandardErrors(D int32[[1]] dependent,
                                        D int32[[2]] variables,
                                        D float32[[1]] parameters,
                                        int64 family)
{
    return _parametersStandardErrors((float32) dependent, (float32) variables, parameters, family);
}

template<domain D : shared3p>
D float64[[1]] parametersStandardErrors(D int64[[1]] dependent,
                                        D int64[[2]] variables,
                                        D float64[[1]] parameters,
                                        int64 family)
{
    return _parametersStandardErrors((float64) dependent, (float64) variables, parameters, family);
}

template<domain D : shared3p>
D float32[[1]] parametersStandardErrors(D float32[[1]] dependent,
                                        D float32[[2]] variables,
                                        D float32[[1]] parameters,
                                        int64 family)
{
    return _parametersStandardErrors(dependent, variables, parameters, family);
}

template<domain D : shared3p>
D float64[[1]] parametersStandardErrors(D float64[[1]] dependent,
                                        D float64[[2]] variables,
                                        D float64[[1]] parameters,
                                        int64 family)
{
    return _parametersStandardErrors(dependent, variables, parameters, family);
}
/** @} */

/** \cond */
template<domain D : shared3p, type T>
D T _glmaic(D T[[1]] y, GLMResult<D, T> glm) {
    D T aic;
    if (glm.family == GLM_FAMILY_GAUSSIAN) {
        // The formula used in SPSS and in different course
        // materials. Does not match R!
        //
        // AIC = n * ln(SSE / n) + 2 * k
        D T[[1]] x = y - glm.means;
        x = x * x;
        D T n = (T) size(x);
        aic = ln(sum(x) / n) * n + 2 * (T) size(glm.coefficients);
    } else if (glm.family == GLM_FAMILY_BINOMIAL_LOGIT) {
        D T ll = sum(y * ln(glm.means / (1 - glm.means)) + ln(1 - glm.means));
        aic = 2 * (T) size(glm.coefficients) - 2 * ll;
    } else {
        assert(false); // Unknown family
    }
    return aic;
}

template<domain D : shared3p, type T>
D T _glmaic(D T[[1]] y, D T[[2]] X, D T[[1]] coefficients, int64 family) {
    uint rows = shape(X)[0];
    uint cols = shape(X)[1];
    D T[[2]] extended(rows, cols + 1);
    extended[:, :cols] = X;
    extended[:, cols] = 1;
    D T[[2]] eta = _matrixMultiplication(extended, _toCol(coefficients));
    D T[[1]] mu = _linkInverse(eta, family)[:, 0];
    GLMResult<D, T> m;
    m.family = family;
    m.coefficients = coefficients;
    m.means = mu;
    return _glmaic(y, m);
}
/** \endcond */

/** \addtogroup shared3p_glm_aic
 *  @{
 *  @brief Compute the Akaike information criterion of a generalized
 *  linear model
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" /
 *  \ref float32 "float32" / \ref float64 "float64"
 *  @param dependent - dependent variable
 *  @param glm - structure returned by the model fitting function
 */
template<domain D : shared3p>
D float32 GLMAIC(D int32[[1]] dependent, GLMResult<D, float32> glm) {
    return _glmaic((float32) dependent, glm);
}

template<domain D : shared3p>
D float64 GLMAIC(D int64[[1]] dependent, GLMResult<D, float64> glm) {
    return _glmaic((float64) dependent, glm);
}

template<domain D : shared3p>
D float32 GLMAIC(D float32[[1]] dependent, GLMResult<D, float32> glm) {
    return _glmaic(dependent, glm);
}

template<domain D : shared3p>
D float64 GLMAIC(D float64[[1]] dependent, GLMResult<D, float64> glm) {
    return _glmaic(dependent, glm);
}
/** @} */

/** \addtogroup shared3p_glm_aic_direct
 *  @{
 *  @brief Compute the Akaike information criterion of a generalized
 *  linear model
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" /
 *  \ref float32 "float32" / \ref float64 "float64"
 *  @param dependent - dependent variable
 *  @param vars - independent variables (each column is a variable)
 *  @param coefficients - model coefficients
 *  @param family - indicates the distribution of the dependent
 *  variable
 */
template<domain D : shared3p>
D float32 GLMAIC(D int32[[1]] dependent, D int32[[2]] vars, D float32[[1]] coefficients, int64 family) {
    return _glmaic((float32) dependent, (float32) vars, coefficients, family);
}

template<domain D : shared3p>
D float64 GLMAIC(D int64[[1]] dependent, D int64[[2]] vars, D float64[[1]] coefficients, int64 family) {
    return _glmaic((float64) dependent, (float64) vars, coefficients, family);
}

template<domain D : shared3p>
D float32 GLMAIC(D float32[[1]] dependent, D float32[[2]] vars, D float32[[1]] coefficients, int64 family) {
    return _glmaic(dependent, vars, coefficients, family);
}

template<domain D : shared3p>
D float64 GLMAIC(D float64[[1]] dependent, D float64[[2]] vars, D float64[[1]] coefficients, int64 family) {
    return _glmaic(dependent, vars, coefficients, family);
}
/** @} */

/** @} */
