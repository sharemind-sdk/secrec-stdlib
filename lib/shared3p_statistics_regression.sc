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
module shared3p_statistics_regression;

import matrix;
import oblivious;
import shared3p;
import shared3p_matrix;
import shared3p_oblivious;
import shared3p_random;
import shared3p_statistics_common;
import shared3p_statistics_summary;
import stdlib;
/** \endcond */

/**
 * @file
 * \defgroup shared3p_statistics_regression shared3p_statistics_regression.sc
 * \defgroup shared3p_simple_linear_regression simpleLinearRegression
 * \defgroup shared3p_linear_regression linearRegression
 * \defgroup shared3p_linear_regression_cg linearRegressionCG
 * \defgroup shared3p_weighted_linear_regression weightedLinearRegression
 * \defgroup shared3p_regression_method constants
 */

/** \addtogroup shared3p_statistics_regression
 *  @{
 *  @brief Module for performing regression analysis
 */

/**
 * \addtogroup shared3p_regression_method
 * @{
 * @brief Constants used for specifying the method used in linear
 * regression modeling with multiple explanatory variables.
 */
int64 LINEAR_REGRESSION_INVERT             = 0;
int64 LINEAR_REGRESSION_LU_DECOMPOSITION   = 1;
int64 LINEAR_REGRESSION_GAUSS              = 2;
int64 LINEAR_REGRESSION_CONJUGATE_GRADIENT = 3;
/** @} */

/** \cond */
template<domain D : shared3p, type FT, type T>
D FT[[1]] _simpleLinear(D T[[1]] x, D T[[1]] y, D bool[[1]] filter) {
    assert(size(x) == size(y));
    assert(size(x) == size(filter));

    D T[[2]] mat(size(x), 2);
    mat[:, 0] = x;
    mat[:, 1] = y;
    mat = _cut(mat, filter);

    uint n = shape(mat)[0];

    // Calculate means
    D T[[1]] sums = colSums(mat);
    D FT[[1]] lens = {(FT)(uint32) n, (FT)(uint32) n};
    D FT[[1]] meanVec = (FT) sums / lens;

    // Calculate covariance
    D FT[[1]] samples(n * 2);
    D FT[[1]] means(n * 2);
    samples[:n] = (FT) mat[:, 0];
    samples[n:] = (FT) mat[:, 1];
    means[:n] = meanVec[0];
    means[n:] = meanVec[1];

    D FT[[1]] diff(n * 2) = samples - means;
    D FT[[1]] mul = diff[:n] * diff[n:];
    D FT cov = sum(mul) / (FT) n;

    // Calculate variance of sample 1
    D FT var1 = sum(diff[:n] * diff[:n]) / (FT) n;

    D FT beta = cov / var1;
    D FT alpha = meanVec[1] - beta * meanVec[0];
    D FT[[1]] res = {alpha, beta};

    return res;
}
/** \endcond */

/**
 * \addtogroup shared3p_simple_linear_regression
 * @{
 * @brief Fitting of simple linear models
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 * @param x - explanatory variable sample
 * @param y - dependent variable sample
 * @param filter - filter indicating which elements of the samples are
 * available
 * @return returns vector {α, β} where α, β are such that y ≈ α + β · x
 * @leakage{Leaks the number of missing values}
 */
template<domain D : shared3p>
D float32[[1]] simpleLinearRegression(D int32[[1]] x, D int32[[1]] y, D bool[[1]] filter) {
    return _simpleLinear(x, y, filter);
}

template<domain D : shared3p>
D float64[[1]] simpleLinearRegression(D int64[[1]] x, D int64[[1]] y, D bool[[1]] filter) {
    return _simpleLinear(x, y, filter);
}

template<domain D : shared3p>
D float32[[1]] simpleLinearRegression(D float32[[1]] x, D float32[[1]] y, D bool[[1]] filter) {
    return _simpleLinear(x, y, filter);
}

template<domain D : shared3p>
D float64[[1]] simpleLinearRegression(D float64[[1]] x, D float64[[1]] y, D bool[[1]] filter) {
    return _simpleLinear(x, y, filter);
}
/** @} */

/** \cond */
template<domain D : shared3p, type T>
uint _maxFirstLoc(D T[[1]] vec) {
    D uint[[1]] idx = iota(size(vec));
    D uint resIdx;

    while (true) {
        uint len = size(vec);

        if (len == 1) {
            resIdx = idx[0];
            break;
        }

        uint n = len / 2;
        D T[[1]] left = vec[:n];
        D uint[[1]] leftIdx = idx[:n];
        D T[[1]] right = vec[n : 2*n];
        D uint[[1]] rightIdx = idx[n : 2*n];
        D bool[[1]] greater = left > right | (left == right & leftIdx < rightIdx);

        D uint[[1]] newIdx = choose(greater, leftIdx, rightIdx);
        D T[[1]] newVec = choose(greater, left, right);

        if (len % 2 != 0) {
            idx = cat(newIdx, {idx[len - 1]});
            vec = cat(newVec, {vec[len - 1]});
        } else {
            idx = newIdx;
            vec = newVec;
        }
    }

    return declassify(resIdx);
}

template<domain D, type T>
uint _firstNonZero(D T[[1]] x) {
    D bool found = false;
    D uint idx = 0;

    for (uint i = 0; i < size(x); i++) {
        D bool cond = x[i] != 0 & !found;
        D bool t = true;
        D uint secret = i;
        found = choose(cond, t, found);
        idx = choose(cond, secret, idx);
    }

    return declassify(idx);
}

// Works with square matrices
template<domain D : shared3p, type T>
D T[[1]] _gaussianElimination(D T[[2]] a, D T[[1]] b) {
    assert(shape(a)[0] == shape(a)[1]);
    assert(size(b) == shape(a)[0]);

    uint n = shape(a)[0];

    // Shuffle a, b
    D T[[2]] mat(n, n + 1);
    mat[:, :n] = a;
    mat[:, n] = b;
    mat = shuffleRows(mat);
    a = mat[:, :n];
    b = mat[:, n];

    // Main loop over the columns to be reduced
    for (uint i = 0; i < n - 1; i++) {
        uint icol = i;
        // Search for a pivot element
        uint irow = _maxFirstLoc(abs(a[i + 1:, i])) + i + 1;

        // Interchange rows
        if (irow != icol) {
            D T[[1]] tmpVec = a[irow, :];
            a[irow, :] = a[i, :];
            a[i, :] = tmpVec;

            D T tmp = b[irow];
            b[irow] = b[icol];
            b[icol] = tmp;
        }

        // Divide the pivot row by the pivot element
        D T pivinv = inv(a[icol, icol]);
        a[icol, icol] = 1;
        D T[[1]] mult(n) = pivinv;
        mult[icol] = 1;
        a[icol, :] = a[icol, :] * mult;
        b[icol] *= pivinv;

        /*
         * Reduce the rows below the pivot row
         *
         * Leaving the original reduction loop as a comment because
         * the optimised thing is completely unreadable. It is derived
         * from this loop.
         *
         * for (uint ll = icol + 1; ll < n; ll++) {
         *     D T dum = a[ll, icol];
         *     a[ll, icol] = 0;
         *
         *     D T[[1]] sub(n) = a[icol, :] * dum;
         *     sub[icol] = 0;
         *     a[ll, :] = a[ll, :] - sub;
         *
         *     b[ll] -= b[icol] * dum;
         * }
         */

        if (icol + 1 < n) {
            uint rows = n - (icol + 1);

            D T[[2]] aleft(rows, n);
            D T[[2]] aright(rows, n);
            D T[[2]] dum(rows, n);

            uint[[1]] idx = iota(rows * n) + (icol + 1) * n;
            __syscall("shared3p::gather_$T\_vec", __domainid(D), a, aleft, __cref idx);

            D T[[1]] asubvec(n);
            uint[[1]] asubvecidx(n) = iota(n) + icol * n;
            __syscall("shared3p::gather_$T\_vec", __domainid(D), a, asubvec, __cref asubvecidx);
            asubvec[icol] = 0;

            for (uint j = 0; j < rows; ++j) {
                uint[[1]] idx(n) = iota(n) + j * n;
                __syscall("shared3p::scatter_$T\_vec", __domainid(D), asubvec, aright, __cref idx);
                D T[[1]] dumTmp(n) = a[icol + 1 + j, icol];
                __syscall("shared3p::scatter_$T\_vec", __domainid(D), dumTmp, dum, __cref idx);
            }

            aleft = aleft - aright * dum;

            __syscall("shared3p::scatter_$T\_vec", __domainid(D), aleft, a, __cref idx);

            D T[[1]] bleft(rows);
            idx = iota(rows) + icol + 1;
            __syscall("shared3p::gather_$T\_vec", __domainid(D), b, bleft, __cref idx);

            D T[[1]] bright(rows);
            idx = iota(rows) * n + (icol + 1) * n + icol;
            __syscall("shared3p::gather_$T\_vec", __domainid(D), a, bright, __cref idx);
            bright = bright * b[icol];

            bleft = bleft - bright;
            uint[[1]] bIdx(rows) = iota(rows) + icol + 1;
            __syscall("shared3p::scatter_$T\_vec", __domainid(D), bleft, b, __cref bIdx);
        }
    }

    // Backsubstitution
    b[n - 1] /= a[n - 1, n - 1];
    for (int i = (int) n - 1; i >= 0; i--) {
        uint ui = (uint) i;
        b[ui] -= sum(a[ui, ui + 1:] * b[ui + 1:]);
    }

    return b;
}

template<domain D : shared3p, type T>
struct _luResult {
    D T[[2]] mat;
    uint[[1]] perm;
}

// Works with square matrices
template<domain D : shared3p, type T>
_luResult<D, T> _ludecomp(D T[[2]] a) {
    uint n = shape(a)[0];
    uint[[1]] rowPerms(n);

    for (uint i = 0; i < n; i++) {
        uint irow = _maxFirstLoc(a[i:, i]) + i;

        if (irow != i) {
            D T[[1]] tmp = a[irow, :];
            a[irow, :] = a[i, :];
            a[i, :] = tmp;
        }

        rowPerms[i] = irow;
        D T ipiv = inv(a[i, i]);

        for (uint m = i + 1; m < n; m++) {
            a[m, i] *= ipiv;
            for (uint j = i + 1; j < n; j++) {
                a[m, j] -= a[m, i] * a[i, j];
            }
        }
    }

    public _luResult<D, T> res;
    res.mat = a;
    res.perm = rowPerms;

    return res;
}

// Works with square matrices
template<domain D : shared3p, type T>
D T[[1]] _solveLU(D T[[2]] a, D T[[1]] b) {
    assert(shape(a)[0] == shape(a)[1]);
    assert(size(b) == shape(a)[0]);

    uint n = shape(a)[0];

    // Shuffle a, b
    D T[[2]] mat(n, n + 1);
    mat[:, :n] = a;
    mat[:, n] = b;
    mat = shuffleRows(mat);
    a = mat[:, :n];
    b = mat[:, n];

    public _luResult<D, T> luRes = _ludecomp(a);
    D T[[2]] lu = luRes.mat;
    uint[[1]] q = luRes.perm;

    // Reorder b
    for (uint i = 0; i < n; i++) {
        D T tmp = b[i];
        b[i] = b[q[i]];
        b[q[i]] = tmp;
    }

    uint m = _firstNonZero(b) + 1 :: uint;

    for (uint i = m; i < n; i++) {
        b[i] -= sum(lu[i, m-1:i] * b[m-1:i]);
    }

    for (int i = (int) n - 1; i >= 0; i--) {
        uint ui = (uint) i;
        b[ui] = (b[ui] - sum(lu[ui, ui+1 : n] * b[ui+1 : n])) / lu[ui, ui];
    }

    return b;
}

// Works with square matrices
template<domain D : shared3p, type T>
D T[[1]] _conjugateGradient(D T[[2]] a, D T[[1]] b, uint iterations) {
    assert(shape(a)[0] == shape(a)[1]);
    assert(shape(a)[0] == size(b));

    uint n = shape(a)[0];
    D T[[2]] bmat = reshape(b, n, 1);
    D T[[2]] x(n, 1);
    D T[[2]] r = bmat - matrixMultiplication(a, x);
    D T[[2]] p = r;

    for (uint k = 0; k < iterations; k++) {
        D T[[2]] ap = matrixMultiplication(a, p);
        D T[[2]] rTr = matrixMultiplication(transpose(r), r);

        D T alpha = (rTr / matrixMultiplication(transpose(p), ap))[0, 0];

        x += alpha * p;
        r -= alpha * ap;

        D T beta = (matrixMultiplication(transpose(r), r) / rTr)[0, 0];
        p = r + beta * p;
    }

    return x[:, 0];
}

// variable samples as columns
template<domain D : shared3p, type T, type FT>
D FT[[1]] _linearRegression(D T[[2]] variables, D T[[1]] dependent, int64 method, uint iterations) {
    uint obs = size(dependent);
    uint vars = shape(variables)[1];

    // Modify a and b to account for the intercept. To get the
    // intercept, a column of ones should be added as the last column
    // of variables. Instead, we can do multiplications without it and
    // then extend a and b to account for the ones "variable".
    D T[[2]] extendedA;
    D T[[2]] extendedB;
    D T[[2]] depSum(1, 1);
    depSum[0, 0] = sum(dependent);

    if (vars > 0) {
        D T[[2]] xt = transpose(variables);
        D T[[2]] a = leftTransposedMultiplication(variables);
        D T[[2]] b = matrixMultiplication(xt, reshape(dependent, size(dependent), 1));

        D T[[2]] extA(vars + 1, vars + 1);
        extA[:vars, :vars] = a;
        extA[vars, vars] = (T) obs;
        extendedA = extA;

        extendedB = cat(b, depSum, 0);
    } else {
        D T[[2]] extA(1, 1);
        extA[0, 0] = (T) obs;
        extendedA = extA;
        extendedB = depSum;
    }

    for (uint i = 0; i < vars; i++) {
        extendedA[vars, i] = sum(variables[:, i]);
        extendedA[i, vars] = sum(variables[:, i]);
    }

    D T[[1]] bvec = extendedB[:, 0];

    if (method == LINEAR_REGRESSION_INVERT) {
        assert(vars <= 3);

        if (vars == 0) {
            return {1 / (FT) extendedA[0, 0] * (FT) extendedB[0, 0]};
        } else if (vars == 1) {
            return matrixMultiplication(_invert2by2((FT) extendedA), (FT) extendedB)[:, 0];
        } else if (vars == 2) {
            return matrixMultiplication(_invert3by3((FT) extendedA), (FT) extendedB)[:, 0];
        } else if (vars == 3) {
            return matrixMultiplication(_invert4by4((FT) extendedA), (FT) extendedB)[:, 0];
        } else {
            assert(false); // Can't use INVERT with more than 3 variables!
            D FT[[1]] res;
            return res;
        }
    } else if (method == LINEAR_REGRESSION_GAUSS) {
        return _gaussianElimination((FT) extendedA, (FT) bvec);
    } else if (method == LINEAR_REGRESSION_LU_DECOMPOSITION) {
        return _solveLU((FT) extendedA, (FT) bvec);
    } else if (method == LINEAR_REGRESSION_CONJUGATE_GRADIENT) {
        return _conjugateGradient((FT) extendedA, (FT) bvec, iterations);
    } else {
        assert(false); // Bad method argument!
        D FT[[1]] res;
        return res;
    }
}
/** \endcond */

/**
 * \addtogroup shared3p_linear_regression
 * @{
 * @brief Fitting of linear models with multiple explanatory variables
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 * @note You can pass an empty matrix as the variables argument to
 * specify a null model with just intercept.
 * @param variables - a matrix where each column is a sample of an
 * explanatory variable
 * @param dependent - sample vector of dependent variable
 * @param method - a constant indicating which algorithm to use
 * (LINEAR_REGRESSION_INVERT, LINEAR_REGRESSION_LU_DECOMPOSITION or
 * LINEAR_REGRESSION_GAUSS)
 * @return returns vector {β_1, β_1, …, β_n} such that y ≈ β_1 * x_1 +
 * β_2 * x_2 + … + β_(n-1) * x_(n-1) + β_n where y is the dependent
 * variable and x_i are the explanatory variables.
 * @leakage{None}
 */
template<domain D : shared3p>
D float32[[1]] linearRegression(D int32[[2]] variables, D int32[[1]] dependent, int64 method) {
    assert(method == LINEAR_REGRESSION_GAUSS || method == LINEAR_REGRESSION_INVERT || method == LINEAR_REGRESSION_LU_DECOMPOSITION);
    return _linearRegression(variables, dependent, method, 0 :: uint);
}

template<domain D : shared3p>
D float64[[1]] linearRegression(D int64[[2]] variables, D int64[[1]] dependent, int64 method) {
    assert(method == LINEAR_REGRESSION_GAUSS || method == LINEAR_REGRESSION_INVERT || method == LINEAR_REGRESSION_LU_DECOMPOSITION);
    return _linearRegression(variables, dependent, method, 0 :: uint);
}

template<domain D : shared3p>
D float32[[1]] linearRegression(D float32[[2]] variables, D float32[[1]] dependent, int64 method) {
    assert(method == LINEAR_REGRESSION_GAUSS || method == LINEAR_REGRESSION_INVERT || method == LINEAR_REGRESSION_LU_DECOMPOSITION);
    return _linearRegression(variables, dependent, method, 0 :: uint);
}

template<domain D : shared3p>
D float64[[1]] linearRegression(D float64[[2]] variables, D float64[[1]] dependent, int64 method) {
    assert(method == LINEAR_REGRESSION_GAUSS || method == LINEAR_REGRESSION_INVERT || method == LINEAR_REGRESSION_LU_DECOMPOSITION);
    return _linearRegression(variables, dependent, method, 0 :: uint);
}
/** @} */

/**
 * \addtogroup shared3p_linear_regression_cg
 * @{
 * @brief Fitting of linear models with multiple explanatory variables
 * using the conjugate gradient method
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 * @note You can pass an empty matrix as the variables argument to
 * specify a null model with just intercept.
 * @param variables - a matrix where each column is a sample of an
 * explanatory variable
 * @param dependent - sample vector of dependent variable
 * @param iterations - number of iterations to use. Empirical testing
 * showed that 10 iterations provides better accuracy than LU
 * decomposition and Gaussian elimination and with a high number of
 * variables it is also faster.
 * @return returns vector {β_1, β_1, …, β_n} such that y ≈ β_1 * x_1 +
 * β_2 * x_2 + … + β_(n-1) * x_(n-1) + β_n where y is the dependent
 * variable and x_i are the explanatory variables.
 * @leakage{None}
 */
template<domain D : shared3p>
D float32[[1]] linearRegressionCG(D int32[[2]] variables, D int32[[1]] dependent, uint iterations) {
    assert(iterations > 0);
    return _linearRegression(variables, dependent, LINEAR_REGRESSION_CONJUGATE_GRADIENT, iterations);
}

template<domain D : shared3p>
D float64[[1]] linearRegressionCG(D int64[[2]] variables, D int64[[1]] dependent, uint iterations) {
    assert(iterations > 0);
    return _linearRegression(variables, dependent, LINEAR_REGRESSION_CONJUGATE_GRADIENT, iterations);
}

template<domain D : shared3p>
D float32[[1]] linearRegressionCG(D float32[[2]] variables, D float32[[1]] dependent, uint iterations) {
    assert(iterations > 0);
    return _linearRegression(variables, dependent, LINEAR_REGRESSION_CONJUGATE_GRADIENT, iterations);
}

template<domain D : shared3p>
D float64[[1]] linearRegressionCG(D float64[[2]] variables, D float64[[1]] dependent, uint iterations) {
    assert(iterations > 0);
    return _linearRegression(variables, dependent, LINEAR_REGRESSION_CONJUGATE_GRADIENT, iterations);
}
/** @} */

/** \cond */
template<domain D : shared3p, type T>
D T[[1]] _weightedLinearRegression(D T[[2]] variables, D T[[1]] dependent, D T[[1]] weights) {
    uint obs = size(dependent);
    uint vars = shape(variables)[1];
    uint varsRows = shape(variables)[0];

    assert(varsRows == 0 || varsRows == obs);
    assert(obs == size(weights));

    D T[[1]] sqrtWeights = sqrt(weights);

    if (varsRows > 0) {
        D T[[2]] W(obs, vars);

        for (uint i = 0; i < vars; ++i) {
            // W[:, i] = weights;
            uint[[1]] indices = iota(obs) * vars + i;
            __syscall("shared3p::scatter_$T\_vec", __domainid(D),
                      sqrtWeights, W, __cref indices);
        }

        variables = variables * W;
    }

    dependent = dependent * sqrtWeights;

    // Modify a and b to account for the intercept. To get the
    // intercept, a column of ones should be added as the last column
    // of variables. Instead, we can do multiplications without it and
    // then extend a and b to account for the ones "variable".
    D T[[2]] extendedA;
    D T[[2]] extendedB;
    D T[[2]] depSum(1, 1);
    depSum[0, 0] = sum(dependent * sqrtWeights);

    if (vars > 0) {
        D T[[2]] xt = transpose(variables);
        D T[[2]] a = leftTransposedMultiplication(variables);
        D T[[2]] b = matrixMultiplication(xt, reshape(dependent, size(dependent), 1));

        D T[[2]] extA(vars + 1, vars + 1);
        extA[:vars, :vars] = a;
        extA[vars, vars] = sum(weights);
        extendedA = extA;

        extendedB = cat(b, depSum, 0);
    } else {
        D T[[2]] extA(1, 1);
        extA[0, 0] = sum(weights * weights);
        extendedA = extA;
        extendedB = depSum;
    }

    for (uint i = 0; i < vars; i++) {
        D T s = sum(variables[:, i] * sqrtWeights);
        extendedA[vars, i] = s;
        extendedA[i, vars] = s;
    }

    D T[[1]] bvec = extendedB[:, 0];

    if (vars == 0) {
        return {1 / extendedA[0, 0] * extendedB[0, 0]};
    } else if (vars == 1) {
        return matrixMultiplication(_invert2by2(extendedA), extendedB)[:, 0];
    } else if (vars == 2) {
        return matrixMultiplication(_invert3by3(extendedA), extendedB)[:, 0];
    } else if (vars == 3) {
        return matrixMultiplication(_invert4by4(extendedA), extendedB)[:, 0];
    } else {
        return _gaussianElimination(extendedA, bvec);
    }
}
/** \endcond */

/**
 * \addtogroup shared3p_weighted_linear_regression
 * @{
 * @brief Fitting of linear models with observation weights
 * @note **D** - shared3p protection domain
 * @note Supported types - \ref float32 "float32" / \ref float64 "float64"
 * @param variables - independent variable samples as column vectors
 * @param dependent - dependent variable sample
 * @param weights - observation weight vector
 * @return returns coefficient vector. The last element is the
 * intercept.
 * @leakage{None}
 */
template<domain D : shared3p>
D float32[[1]] weightedLinearRegression(D float32[[2]] variables,
                                        D float32[[1]] dependent,
                                        D float32[[1]] weights)
{
    return _weightedLinearRegression(variables, dependent, weights);
}

template<domain D : shared3p>
D float64[[1]] weightedLinearRegression(D float64[[2]] variables,
                                        D float64[[1]] dependent,
                                        D float64[[1]] weights)
{
    return _weightedLinearRegression(variables, dependent, weights);
}
/** @} */

/** @} */
