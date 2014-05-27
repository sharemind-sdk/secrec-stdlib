/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

/** \cond */
module a3p_statistics_regression;

import a3p_matrix;
import a3p_random;
import a3p_statistics_common;
import a3p_statistics_summary;
import a3p_oblivious;
import additive3pp;
import matrix;
import oblivious;
/** \endcond */

/**
 * @file
 * \defgroup a3p_statistics_regression a3p_statistics_regression.sc
 * \defgroup a3p_simple_linear_regression simpleLinearRegression
 * \defgroup a3p_linear_regression linearRegression
 */

/** \addtogroup <a3p_statistics_regression>
 *  @{
 *  @brief Module for performing regression analysis
 */

/** \cond */
template<domain D : additive3pp, type FT, type T>
D FT[[1]] _simpleLinear(D T[[1]] x, D T[[1]] y, D bool[[1]] filter) {
    assert(size(x) == size(y));
    assert(size(x) == size(filter));

    D T[[2]] mat (size(x), 2);
    mat[:, 0] = x;
    mat[:, 1] = y;
    mat = _cut(mat, filter);
    D T[[1]] sums = colSums(mat);
    D FT[[1]] lens = {(FT)(uint32) shape(mat)[0], (FT)(uint32) shape(mat)[0]};
    D FT[[1]] means = (FT) sums / lens;

    D FT beta = covariance(x, y, filter) / _variance(mat[:, 0], means[0]);

    D FT alpha = means[1] - beta * means[0];
    D FT[[1]] res = {alpha, beta};

    return res;
}
/** \endcond */

/**
 * \addtogroup <a3p_simple_linear_regression>
 * @{
 * @brief Simple linear regression analysis
 * @note **D** - additive3pp protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 * @param x - explanatory variable sample
 * @param y - dependent variable sample
 * @param filter - filter indicating which elements of the samples are
 * available
 * @return returns vector {α, β} where α, β are such that y ≈ α + β · x
 */
template<domain D : additive3pp>
D float32[[1]] simpleLinearRegression(D int32[[1]] x, D int32[[1]] y, D bool[[1]] filter) {
    return _simpleLinear(x, y, filter);
}

template<domain D : additive3pp>
D float64[[1]] simpleLinearRegression(D int64[[1]] x, D int64[[1]] y, D bool[[1]] filter) {
    return _simpleLinear(x, y, filter);
}
/** @} */

/** \cond */
template<domain D, type T>
D T[[2]] _invert2by2 (D T[[2]] mat) {
    assert(shape(mat)[0] == 2);
    assert(shape(mat)[1] == 2);

    D T a = mat[0, 0],
        b = mat[0, 1],
        c = mat[1, 0],
        d = mat[1, 1];

    D T detInv = 1 / (a*d - b*c);
    mat[0, 0] = d;
    mat[0, 1] = -b;
    mat[1, 0] = -c;
    mat[1, 1] = a;

    return detInv * mat;
}

template<domain D, type T>
D T[[2]] _invert3by3 (D T[[2]] mat) {
    assert(shape(mat)[0] == 3);
    assert(shape(mat)[1] == 3);

    D T a11 = mat[0, 0],
        a12 = mat[0, 1],
        a13 = mat[0, 2],
        a21 = mat[1, 0],
        a22 = mat[1, 1],
        a23 = mat[1, 2],
        a31 = mat[2, 0],
        a32 = mat[2, 1],
        a33 = mat[2, 2];

    T[[1]] signs = {1, 1, 1, -1, -1, -1};
    D T[[1]] mulL = {a11, a21, a31, a11, a31, a21};
    D T[[1]] mulR = {a22, a32, a12, a32, a22, a12};
    D T[[1]] res = mulL * mulR;
    mulR = {a33, a13, a23, a23, a13, a33};
    D T det = sum(res * mulR * signs);

    mulL = {a22, a23, a13, a12, a12, a13,
            a23, a21, a11, a13, a13, a11,
            a21, a22, a12, a11, a11, a12};

    mulR = {a33, a32, a32, a33, a23, a22,
            a31, a33, a33, a31, a21, a23,
            a32, a31, a31, a32, a22, a21};

    res = mulL * mulR;
    D T[[2]] subMat = reshape(res, 9, 2);
    res = subMat[:, 0] - subMat[:, 1];
    mat = reshape(res, 3, 3);

    return (1 / det) * mat;
}

template<domain D : additive3pp, type T>
D T[[2]] _invert4by4(D T[[2]] mat) {
    assert(shape(mat)[0] == 4);
    assert(shape(mat)[1] == 4);

    D T a11 = mat[0, 0];
    D T a12 = mat[0, 1];
    D T a13 = mat[0, 2];
    D T a14 = mat[0, 3];
    D T a21 = mat[1, 0];
    D T a22 = mat[1, 1];
    D T a23 = mat[1, 2];
    D T a24 = mat[1, 3];
    D T a31 = mat[2, 0];
    D T a32 = mat[2, 1];
    D T a33 = mat[2, 2];
    D T a34 = mat[2, 3];
    D T a41 = mat[3, 0];
    D T a42 = mat[3, 1];
    D T a43 = mat[3, 2];
    D T a44 = mat[3, 3];

    T[[1]] signs = {1, -1, -1, 1, 1, -1, 1, -1, 1, -1, 1, -1, -1, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, -1};
    D T[[1]] mulA = {a11, a33, a12, a33, a13, a31, a12, a31, a13, a32, a11, a32, a14, a32, a13, a32, a13, a34, a14, a33, a12, a33, a12, a34, a14, a32, a11, a32, a11, a34, a14, a31, a12, a31, a12, a34, a14, a33, a11, a33, a11, a34, a14, a31, a13, a31, a13, a34};
    D T[[1]] mulB = {a22, a44, a21, a44, a22, a44, a23, a44, a21, a44, a23, a44, a23, a41, a24, a41, a22, a41, a22, a41, a24, a41, a23, a41, a21, a43, a24, a43, a22, a43, a22, a43, a24, a43, a21, a43, a21, a42, a24, a42, a23, a42, a23, a42, a24, a42, a21, a42};
    D T[[1]] mul = mulA * mulB;
    D T[[2]] mulMat = reshape(mul, 24, 2);
    mul = mulMat[:, 0] * mulMat[:, 1];
    D T det = sum(signs * mul);

    signs = {1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1};
    mulA = {a22, a32, a42, a32, a42, a22, a12, a32, a42, a32, a42, a12, a12, a22, a42, a22, a42, a12, a12, a22, a32, a22, a32, a12, a21, a31, a41, a31, a41, a21, a11, a31, a41, a31, a41, a11, a11, a21, a41, a21, a41, a11, a11, a21, a31, a21, a31, a11, a21, a31, a41, a31, a41, a21, a11, a31, a41, a31, a41, a11, a11, a21, a41, a21, a41, a11, a11, a21, a31, a21, a31, a11, a21, a31, a41, a31, a41, a21, a11, a31, a41, a31, a41, a11, a11, a21, a41, a21, a41, a11, a11, a21, a31, a21, a31, a11};
    mulB = {a33, a23, a33, a43, a23, a43, a33, a13, a33, a43, a13, a43, a23, a13, a23, a43, a13, a43, a23, a13, a23, a33, a13, a33, a33, a23, a33, a43, a23, a43, a33, a13, a33, a43, a13, a43, a23, a13, a23, a43, a13, a43, a23, a13, a23, a33, a13, a33, a32, a22, a32, a42, a22, a42, a32, a12, a32, a42, a12, a42, a22, a12, a22, a42, a12, a42, a22, a12, a22, a32, a12, a32, a32, a22, a32, a42, a22, a42, a32, a12, a32, a42, a12, a42, a22, a12, a22, a42, a12, a42, a22, a12, a22, a32, a12, a32};
    mul = mulA * mulB;
    mulB = {a44, a44, a24, a24, a34, a34, a44, a44, a14, a14, a34, a34, a44, a44, a14, a14, a24, a24, a34, a34, a14, a14, a24, a24, a44, a44, a24, a24, a34, a34, a44, a44, a14, a14, a34, a34, a44, a44, a14, a14, a24, a24, a34, a34, a14, a14, a24, a24, a44, a44, a24, a24, a34, a34, a44, a44, a14, a14, a34, a34, a44, a44, a14, a14, a24, a24, a34, a34, a14, a14, a24, a24, a43, a43, a23, a23, a33, a33, a43, a43, a13, a13, a33, a33, a43, a43, a13, a13, a23, a23, a33, a33, a13, a13, a23, a23};
    mul = sum(mul * mulB * signs, 16 :: uint);
    mat = reshape(mul, 4, 4);

    return 1 / det * mat;
}

template<domain D : additive3pp, type T>
D T[[1]] _solveInvert(D T[[2]] a, D T[[1]] b) {
    uint n = shape(a)[0];
    assert(n > 1);
    assert(n < 5);

    D T[[2]] invA;

    if (n == 2)
        invA = _invert2by2(a);
    else if (n == 3)
        invA = _invert3by3(a);
    else if (n == 4)
        invA = _invert4by4(a);

    D T[[2]] matB = reshape(b, size(b), 1);
    return matrixMultiplication(invA, matB)[:, 0];
}

template<domain D : additive3pp, type T>
uint _maxFirstLoc(D T[[1]] vec) {
    D T best = vec[0];
    D uint idx = 0;

    for (uint i = 1; i < size(vec); i++) {
        D bool comp = vec[i] > best;
        best = choose(comp, vec[i], best);
        D uint secret = i;
        idx = choose(comp, secret, idx);
    }

    return declassify(idx);
}

template<domain D, type T>
uint _firstNonZero(D T[[1]] x) {
    D bool found = false;
    D uint idx = 0;

    for (uint i = 0; i < size(x); i++) {
        D bool cond = x[i] != 0 && !found;
        D bool t = true;
        D uint secret = i;
        found = choose(cond, t, found);
        idx = choose(cond, secret, idx);
    }

    return declassify(idx);
}

// Works with square matrices
template<domain D : additive3pp, type T>
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

        // Divide the pivot row by the pivot element, located at
        // (irow, icol).
        D T pivinv = inv(a[icol, icol]);
        a[icol, icol] = 1;
        D T[[1]] mult(n) = pivinv;
        mult[icol] = 1;
        a[icol, :] *= mult;
        b[icol] *= pivinv;

        // Reduce the rows below the pivot row
        for (uint ll = icol + 1; ll < n; ll++) {
            D T dum = a[ll, icol];
            a[ll, icol] = 0;

            D T[[1]] sub(n) = a[icol, :] * dum;
            sub[icol] = 0;
            a[ll, :] -= sub;

            b[ll] -= b[icol] * dum;
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

// Works with square matrices
template<domain D : additive3pp, type T>
D T[[2]] _ludecomp(D T[[2]] a) {
    uint n = shape(a)[0];
    T[[1]] rowPerms(n);

    for (uint i = 0; i < n; i++) {
        uint irow = _maxFirstLoc(a[i:, i]) + i;

        if (irow != i) {
            D T[[1]] tmp = a[irow, :];
            a[irow, :] = a[i, :];
            a[i, :] = tmp;
        }

        rowPerms[i] = (T) irow;
        D T ipiv = inv(a[i, i]);

        for (uint m = i + 1; m < n; m++) {
            a[m, i] *= ipiv;
            for (uint j = i + 1; j < n; j++) {
                a[m, j] -= a[m, i] * a[i, j];
            }
        }
    }

    D T[[2]] ret(n, n + 1);
    ret[:, :n] = a;
    ret[:, n] = rowPerms;

    return ret;
}

// Works with square matrices
template<domain D : additive3pp, type T>
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

    mat = _ludecomp(a);
    D T[[2]] lu = mat[:, :n];
    D uint[[1]] q = (uint) floor(mat[:, n]);

    for (uint i = 0; i < n; i++) {
        // Exchange b[i], b[q[i]]

        uint[[1]] indices(n);
        for (uint i = 0; i < n; i++) {
            indices[i] = i;
        }
        D bool[[1]] filter = indices == q[i];

        D T tmp = b[i];
        b[i] = sum((T) filter * b);
        D T[[1]] newb(n) = tmp;
        b = choose(filter, newb, b);
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
template<domain D : additive3pp, type T>
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
template<domain D : additive3pp, type T>
D T[[1]] _linearRegression(D T[[2]] variables, D T[[1]] dependent) {
    assert(shape(variables)[0] == size(dependent));
    uint vars = shape(variables)[1];
    assert(vars > 1);

    D T[[2]] xt = transpose(variables);
    D T[[2]] a = matrixMultiplication(xt, variables);
    D T[[2]] b = matrixMultiplication(xt, reshape(dependent, size(dependent), 1));

    if (vars == 2) {
        return matrixMultiplication(_invert2by2(a), b)[:, 0];
    } else if (vars == 3) {
        return matrixMultiplication(_invert3by3(a), b)[:, 0];
    } else if (vars == 4) {
        return matrixMultiplication(_invert4by4(a), b)[:, 0];
    } else {
        D T[[1]] bvec = b[:, 0];
        return _solveLU(a, bvec);
    }
}
/** \endcond */

/**
 * \addtogroup <a3p_linear_regression>
 * @{
 * @brief Linear regression analysis with multiple explanatory variables
 * @note **D** - additive3pp protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 * @param variables - a matrix where each column is a sample of an
 * explanatory variable
 * @param dependent - sample vector of dependent variable
 * @return returns vector {β₁, β₂, …, βₙ} such that y ≈ β₁x₁ + β₂x₂ +
 * … + βₙxₙ where y is the dependent variable and xᵢ are the explanatory
 * variables.
 */
template<domain D : additive3pp>
D float32[[1]] linearRegression(D int32[[2]] variables, D int32[[1]] dependent) {
    return _linearRegression((float32) variables, (float32) dependent);
}

template<domain D : additive3pp>
D float64[[1]] linearRegression(D int64[[2]] variables, D int64[[1]] dependent) {
    return _linearRegression((float64) variables, (float64) dependent);
}
/** @} */


/** @} */