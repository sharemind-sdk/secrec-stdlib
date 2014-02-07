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
import a3p_statistics_summary;
import additive3pp;
/**
 * \endcond
 */

/**
 * @file
 * \defgroup a3p_statistics_outliers a3p_statistics_outliers.sc
 * \defgroup outliers_quantiles outlierDetectionQuantiles
 * \defgroup outliers_mad outlierDetectionMAD
 */

/** \addtogroup <a3p_statistics_outliers>
 *  @{
 *  @brief Module with functions for detecting unexpected elements in a dataset.
 */

/** \cond */
template<domain D : additive3pp, type T, type FT>
D bool[[1]] _outlierDetectionQuantiles (FT p, D T[[1]] data, D bool[[1]] isAvailable) {
    assert (0 < p);
    assert (p < 1);

    D T[[1]] cutData = cut (data, isAvailable);
    D T[[1]] sortedData = sortingNetworkSort (cutData);
    uint cutSize = size (cutData);
    uint dataSize = size (data);
    D bool[[1]] result;

    if (cutSize < 5)
        return result;

    // Look at fiveNumberSummary if you want to understand what's going on
    FT[[1]] p2 = {p, 1 - p};
    FT[[1]] pSize = p2 * ((FT) cutSize - 1);
    uint[[1]] floorP = (uint64) (pSize - 0.5);
    uint[[1]] j = floorP;
    FT[[1]] gamma = pSize - (FT) floorP;

    D FT q = (1 - gamma[0]) * (FT) sortedData[j[0]] +
              gamma[0] * (FT) sortedData[j[0] + 1];
    D FT[[1]] quantiles (dataSize) = q;

    D bool[[1]] lowFilter = (FT) data > quantiles;

    q = (1 - gamma[1]) * (FT) sortedData[j[1]] +
         gamma[1] * (FT) sortedData[j[1] + 1];
    quantiles = q;

    D bool[[1]] highFilter = (FT) data < quantiles;

    return lowFilter && highFilter && isAvailable;
}
/** \endcond */

/** \addtogroup <outliers_quantiles>
 *  @{
 *  @brief Outlier detection (using quantiles)
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
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
/** @} */

/** \cond */
template<domain D : additive3pp, type T, type FT>
D bool[[1]] _outlierDetectionMAD (D T[[1]] data,
                                  D bool[[1]] isAvailable,
                                  FT lambda)
{
    D T[[1]] cutData = cut (data, isAvailable);
    uint cutSize = size (cutData);
    D bool[[1]] result;

    if (cutSize < 5)
        return result;

    D FT m = _median (cutData);
    D FT mad = _MAD (cutData);
    D FT[[1]] dist = abs ((FT) cutData - m);

    return (dist < lambda * mad) && isAvailable;
}
/** \endcond */

/** \addtogroup <outliers_mad>
 *  @{
 *  @brief Outlier detection (using median absolute deviation)
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input vector
 *  @param isAvailable - vector indicating which elements of the input
 *  vector are available
 *  @param lambda - constant. The value of lambda depends on the
 *  dataset. Anything from 3 to 5 can be used as a starting value.
 *  @return returns a boolean mask vector. For each sample point x,
 *  the corresponding mask element is true if the corresponding
 *  isAvailable element is true and its absolute deviation from the
 *  median of the sample does not exceed lambda · MAD where MAD is the
 *  median absolute deviation of the sample.
 */
template<domain D : additive3pp>
D bool[[1]] outlierDetectionMAD (D int32[[1]] data,
                                 D bool[[1]] isAvailable,
                                 float32 lambda)
{
    return _outlierDetectionMAD (data, isAvailable, lambda);
}

template<domain D : additive3pp>
D bool[[1]] outlierDetectionMAD (D int64[[1]] data,
                                 D bool[[1]] isAvailable,
                                 float64 lambda)
{
    return _outlierDetectionMAD (data, isAvailable, lambda);
}
/** @} */

/** @} */
