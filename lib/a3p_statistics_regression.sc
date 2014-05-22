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
import a3p_statistics_common;
import a3p_statistics_summary;
/** \endcond */

/**
 * @file
 * \defgroup a3p_statistics_regression a3p_statistics_regression.sc
 * \defgroup simple_linear simpleLinearRegression
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
 * \addtogroup <simple_linear>
 * @brief Simple linear regression analysis
 * @note **D** - additive3pp protection domain
 * @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 * @note This procedure uses a simple method based on covariance,
 * variance and means.
 * @param x - first sample
 * @param y - second sample
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

/** @} */
