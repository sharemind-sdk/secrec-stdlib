/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

/**
 * \cond
 */
module a3p_statistics_outliers;

import a3p_sort;
import a3p_statistics_common;
import additive3pp;
/**
 * \endcond
 */

/**
 * @file
 * \defgroup a3p_statistics_outliers a3p_statistics_outliers.sc
 * \defgroup quantiles outlierDetectionQuantiles
 */

/** \addtogroup <a3p_statistics_outliers>
 *  @{
 *  @brief Module with functions for detecting unexpected elements in a dataset.
 */

/**
 * \cond
 */

/*
 * TODO: instead of floor and ceiling, use floats and compare directly
 * to Q_p and Q_(1-p)
 * TODO: check that 0 < p < 1
 */
template<domain D : additive3pp, type T, type FT>
D bool[[1]] _outlierDetectionQuantiles (FT p, D T[[1]] data, D bool[[1]] isAvailable) {
    D T[[1]] cutData = cut (data, isAvailable);
    D T[[1]] sortedData = sortingNetworkSort (cutData);
    uint cutSize = size (cutData);
    uint dataSize = size (data);
    D bool[[1]] result;

    if (cutSize < 5)
        return result;

    // Look at fiveNumberSummary if you want to understand what's going on
    FT[[1]] p2 = {p, 1 - p};
    FT[[1]] pSize = (FT) (cutSize - 1) * p2;
    uint[[1]] floorP = (uint64) (pSize - 0.5);
    uint[[1]] j = floorP;
    FT[[1]] gamma = pSize - (FT) floorP;

    D T q = floor ((1 - gamma[0]) * (FT) sortedData[j[0]] +
                    gamma[0] * (FT) sortedData[j[0] + 1]);
    D T[[1]] quantiles (dataSize) = q;

    D bool[[1]] lowFilter = data > quantiles;

    q = ceiling ((1 - gamma[1]) * (FT) sortedData[j[1]] +
                  gamma[1] * (FT) sortedData[j[1] + 1]);
    quantiles = q;
    D bool[[1]] highFilter = data < quantiles;

    return lowFilter && highFilter && isAvailable;
}
/*
 * \endcond
 */

/** \addtogroup <quantiles>
 *  @{
 *  @brief Outlier detection (using quantiles)
 *  @note **D** - additive3pp protection domain
 *  @param p - quantile probability (between 0 and 1). Quantile Q<sub>p</sub> is
 *  a value such that a random variable with the same distribution as
 *  the sample points will be less than Q<sub>p</sub> with probability p.
 *  @param data - input vector
 *  @param isAvailable - vector indicating which elements of the input vector are available
 *  @return returns a boolean mask vector. For each sample point x,
 *  the corresponding mask element is true if the corresponding
 *  isAvailable element is true and Q<sub>p</sub> < x <
 *  Q<sub>1-p</sub>
 */
template<domain D : additive3pp>
D bool[[1]] outlierDetectionQuantiles (float64 p, D int64[[1]] data, D bool[[1]] isAvailable) {
    return _outlierDetectionQuantiles (p, data, isAvailable);
}

template<domain D : additive3pp>
D bool[[1]] outlierDetectionQuantiles (float32 p, D int32[[1]] data, D bool[[1]] isAvailable) {
    return _outlierDetectionQuantiles (p, data, isAvailable);
}
/**
 * @}
 */
