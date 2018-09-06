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
module shared3p_statistics_pca;

import matrix;
import shared3p;
import shared3p_matrix;
import stdlib;
/** \endcond */

/**
 * @file
 * \defgroup shared3p_statistics_pca shared3p_statistics_pca.sc
 * \defgroup shared3p_pca_result PCAResult
 * \defgroup shared3p_pca_return_values constants
 * \defgroup shared3p_gspca gspca
 */

/**
 * \addtogroup shared3p_statistics_pca
 * @{
 * @brief Module for performing principal component analysis
 */

/**
 * \addtogroup shared3p_pca_result
 * @{
 * @brief Structure containing the results of the analysis. Note that
 * each field only has a reasonable value if it was requested when
 * calling gspca. See also \ref shared3p_pca_return_values "return value constants".
 */
template<domain D : shared3p, type T>
struct PCAResult {
    D T[[2]] residual;
    D T[[2]] loads;
    D T[[2]] scores;
    D T[[1]] variances;
    D T[[1]] proportions;
}
/** @} */

/**
 * \addtogroup shared3p_pca_return_values
 * @{
 * @brief Constants used to specify which result values are needed
 * from the analysis.
 *
 * @note PCA_RETURN_RESIDUAL - residual matrix.
 * @note PCA_RETURN_LOADS - loads. The columns are eigenvectors of the
 * covariance matrix. Can be used to project data to the principal
 * component space.
 * @note PCA_RETURN_SCORES - transformed input values.
 * @note PCA_RETURN_VARIANCES - variances of principal components.
 * @note PCA_RETURN_PROPORTIONS - proportion of variance explained by principal component.
 */
uint8 PCA_RETURN_RESIDUAL    = 1;
uint8 PCA_RETURN_LOADS       = 2;
uint8 PCA_RETURN_SCORES      = 4;
uint8 PCA_RETURN_VARIANCES   = 8;
uint8 PCA_RETURN_PROPORTIONS = 16;
/** @} */

/** \cond */
template <type T, type UT, dim N, domain D>
D T[[N]] _power(D T[[N]] x, UT e) {
    if (e == 0) {
        D T[[N]] one = 1;
        return one;
    }

    D T[[N]] pow = x;
    while (e > 1) {
        pow = pow * x;
        e--;
    }

    return pow;
}

template<domain D>
D fix64 _getDivisor(D fix64 fix) {
    // This is 1/2^12 as a fixed point number
    D uint64 x = 1 << (32 - 12);
    __syscall("shared3p::assign_uint64_vec", __domainid(D), x, fix);
    return fix;
}

template<domain D>
D fix32 _getDivisor(D fix32 fix) {
    // This is 1/2^6 as a fixed point number
    D uint32 x = 1 << (16 - 6);
    __syscall("shared3p::assign_uint32_vec", __domainid(D), x, fix);
    return fix;
}

/*
 * GS-PCA algorithm from "Parallel GPU Implementation of Iterative PCA
 * Algorithms".
 */
template<domain D : shared3p, type Fix, type Float>
PCAResult<D, Fix> _gspca(Float proxy, D Fix[[2]] X, uint n_components,
                         uint iterations, uint8 returnValues)
{
    bool wantResidual = (bool) (returnValues & PCA_RETURN_RESIDUAL);
    bool wantLoads = (bool) (returnValues & PCA_RETURN_LOADS);
    bool wantScores = (bool) (returnValues & PCA_RETURN_SCORES);
    bool wantVariances = (bool) (returnValues & PCA_RETURN_VARIANCES);
    bool wantProportions = (bool) (returnValues & PCA_RETURN_PROPORTIONS);

    assert (wantResidual || wantLoads || wantScores || wantVariances ||
            (wantVariances && wantProportions));
    assert (n_components >= 1);
    assert (iterations >= 1);

    D Fix[[2]] R;
    D Fix[[2]] P(shape(X)[1], n_components);
    D Fix[[2]] T(shape(X)[0], n_components);
    D Fix[[2]] V(shape(X)[0], n_components);
    D Fix[[2]] pDivisor(shape(X)[1], 1);
    D Fix[[2]] vDivisor(shape(X)[0], 1);

    D Fix proxy;
    // The divisor is used to shorten vectors before normalisation to
    // avoid overflow.
    D Fix divisor = _getDivisor(proxy);
    pDivisor = divisor;
    vDivisor = divisor;

    // todo: optimize, use k sum
    // Center
    D Fix invRows = 1.0 / (Float) shape(X)[0];
    for (uint i = 0; i < shape(X)[1]; ++i) {
        D Fix mu = sum(X[:, i]) * invRows;
        X[:, i] = X[:, i] - mu;
    }
    R = X;

    for (uint k = 0; k < n_components; ++k) {
        D Fix[[2]] t(shape(X)[0], 1);
        D Fix[[2]] v = reshape(R[:, k], shape(X)[0], 1);
        D Fix[[2]] Rtrans = transpose(R);
        D Fix[[2]] p(shape(X)[1], 1);

        for (uint i = 0; i < iterations; ++i) {
            p = matrixMultiplication(Rtrans, v);
            p = p * pDivisor;

            if (k > 0) {
                // Orthogonality correction
                D Fix[[2]] A = matrixMultiplication(transpose(P[:, 0:k]), p);
                p = p - matrixMultiplication(P[:, 0:k], A);
            }

            {
                D Fix invSqrt = inv(sqrt(sum((p * p)[:, 0])));
                D Fix[[2]] invSqrtMat(shape(X)[1], 1) = invSqrt;
                p = p * invSqrtMat;
                v = matrixMultiplication(R, p);
            }

            if (k > 0) {
                // Orthogonality correction
                D Fix[[2]] B = matrixMultiplication(transpose(V[:, 0:k]), v);
                v = v - matrixMultiplication(V[:, 0:k], B);
            }

            if (i == iterations - 1) {
                t = v;
                T[:, k] = t[:, 0];
            }

            {
                v = v * vDivisor;
                D Fix invSqrt = inv(sqrt(sum((v * v)[:, 0])));
                D Fix[[2]] invSqrtMat(shape(X)[0], 1) = invSqrt;
                v = v * invSqrtMat;
            }
        }

        if (k == n_components - 1) {
            if (wantResidual)
                R = R - matrixMultiplication(t, transpose(p));
            if (wantLoads)
                P[:, k] = p[:, 0];
        } else {
            R = R - matrixMultiplication(t, transpose(p));
            P[:, k] = p[:, 0];
            V[:, k] = v[:, 0];
        }
    }

    PCAResult<D, Fix> res;
    if (wantResidual)
        res.residual = R;
    if (wantScores)
        res.scores = T;
    if (wantLoads)
        res.loads = P;

    if (wantVariances || wantProportions) {
        T = T * T;
        D Fix div = 1 / (Float) shape(T)[0];
        D Fix[[1]] divs(shape(T)[1]) = div;
        D Fix[[1]] vars = colSums(T);
        // We don't need to subtract means because PCs are centered
        res.variances = vars * divs;
    }

    if (wantProportions) {
        assert(wantVariances);
        // X is already centered
        D Fix[[1]] div(shape(X)[1]) = invRows;
        D Fix[[1]] variances = colSums(X * X) * div;
        D Fix totalVarInv = inv(sum(variances));
        D Fix[[1]] pcdiv(n_components) = totalVarInv;
        res.proportions = res.variances * pcdiv;
    }

    return res;
}
/** \endcond */

/**
 * \addtogroup shared3p_gspca
 * @{
 * @brief Principal component analysis. Note that this method is
 * relatively efficient and precise when a low number of components is
 * required. It uses fixed point numbers internally so it may fail on
 * large inputs due to overflow.
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 * @param X - data matrix where the columns are variables
 * @param n_components - how many components to compute
 * @param iterations - how many iterations to run the algorithm
 * @param returnValues - indicates which results to return. Use
 * bitwise or if you want multiple results.
 * @return \ref PCAResult structure
 * @leakage{None}
 */
template<domain D : shared3p>
PCAResult<D, float32> gspca(D float32[[2]] X, uint n_components,
                            uint iterations, uint8 returnValues)
{
    float32 floatProxy;
    D fix32[[2]] fixX = (fix32) X;
    PCAResult<D, fix32> resFix = _gspca(floatProxy, fixX, n_components, iterations, returnValues);
    PCAResult<D, float32> res;

    res.residual = (float32) resFix.residual;
    res.scores = (float32) resFix.scores;
    res.loads = (float32) resFix.loads;
    res.variances = (float32) resFix.variances;
    res.proportions = (float32) resFix.proportions;

    return res;
}

template<domain D : shared3p>
PCAResult<D, float64> gspca(D float64[[2]] X, uint n_components,
                            uint iterations, uint8 returnValues)
{
    float64 floatProxy;
    D fix64[[2]] fixX = (fix64) X;
    PCAResult<D, fix64> resFix = _gspca(floatProxy, fixX, n_components, iterations, returnValues);
    PCAResult<D, float64> res;

    res.residual = (float64) resFix.residual;
    res.scores = (float64) resFix.scores;
    res.loads = (float64) resFix.loads;
    res.variances = (float64) resFix.variances;
    res.proportions = (float64) resFix.proportions;

    return res;
}
/** @} */

/** @} */
