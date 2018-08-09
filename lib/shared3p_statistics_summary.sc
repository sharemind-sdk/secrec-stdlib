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

/**
 * \cond
 */
module shared3p_statistics_summary;

import shared3p_matrix;
import shared3p_sort;
import shared3p_statistics_common;
import shared3p;
import oblivious;
import stdlib;
/**
 * \endcond
 */


/**
 * @file
 * \defgroup shared3p_statistics_summary shared3p_statistics_summary.sc
 * \defgroup minimum minimum
 * \defgroup maximum maximum
 * \defgroup mean mean
 * \defgroup mean_filter mean(filter)
 * \defgroup variance variance
 * \defgroup variance_filter variance(filter)
 * \defgroup standard_dev standardDev
 * \defgroup standard_dev_filter standardDev(filter)
 * \defgroup mad MAD
 * \defgroup mad_constant MAD(constant)
 * \defgroup mad_filter MAD(filter)
 * \defgroup mad_filter_constant MAD(filter, constant)
 * \defgroup five_number_summary fiveNumberSummary
 * \defgroup covariance covariance
 * \defgroup covariance_filter covariance(filter)
 */

/** \addtogroup shared3p_statistics_summary
 *  @{
 *  @brief Module for finding the main percentiles in statistics.
 */


/** \addtogroup minimum
 *  @{
 *  @brief Find the smallest element of a vector
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input vector
 *  @param isAvailable - vector indicating which elements of the input vector are available
 *  @return returns the smallest element of the input vector
 *  @leakage{Leaks the number of true values in isAvailable}
 */
template <domain D : shared3p>
D int32 minimum (D int32[[1]] data, D bool[[1]] isAvailable) {
    data = cut (data, isAvailable);
    return min (data);
}

template <domain D : shared3p>
D int64 minimum (D int64[[1]] data, D bool[[1]] isAvailable) {
    data = cut (data, isAvailable);
    return min (data);
}

template <domain D : shared3p>
D float32 minimum (D float32[[1]] data, D bool[[1]] isAvailable) {
    data = cut (data, isAvailable);
    return min (data);
}

template <domain D : shared3p>
D float64 minimum (D float64[[1]] data, D bool[[1]] isAvailable) {
    data = cut (data, isAvailable);
    return min (data);
}
/** @} */


/** \addtogroup maximum
 *  @{
 *  @brief Find the largest element of a vector
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input vector
 *  @param isAvailable - vector indicating which elements of the input vector are available
 *  @return returns the largest element of the input vector
 *  @leakage{Leaks the number of true values in isAvailable}
 */
template <domain D : shared3p>
D int32 maximum (D int32[[1]] data, D bool[[1]] isAvailable) {
    data = cut (data, isAvailable);
    return max (data);
}

template <domain D : shared3p>
D int64 maximum (D int64[[1]] data, D bool[[1]] isAvailable) {
    data = cut (data, isAvailable);
    return max (data);
}

template <domain D : shared3p>
D float32 maximum (D float32[[1]] data, D bool[[1]] isAvailable) {
    data = cut (data, isAvailable);
    return max (data);
}

template <domain D : shared3p>
D float64 maximum (D float64[[1]] data, D bool[[1]] isAvailable) {
    data = cut (data, isAvailable);
    return max (data);
}
/** @} */


/** \addtogroup mean
 *  @{
 *  @brief Find the mean of a vector
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int8 "int8" \ref int16 "int16" \ref int32 "int32" \ref int64 "int64" \ref uint8 "uint8" \ref uint16 "uint16" \ref uint32 "uint32" \ref uint64 "uint64" \ref float32 "float32" \ref float64 "float64"
 *  @param data - input vector (the function may overflow if the input is too big)
 *  @return returns the mean of the input vector
 *  @leakage{None}
 */
template <domain D>
D float32 mean (D int8[[1]] data) {
    return _mean (data);
}
template <domain D>
D float32 mean (D int16[[1]] data) {
    return _mean (data);
}
template <domain D>
D float32 mean (D int32[[1]] data) {
    return _mean (data);
}
template <domain D>
D float64 mean (D int64[[1]] data) {
    return _mean (data);
}
template <domain D>
D float32 mean (D uint8[[1]] data) {
    return _mean (data);
}
template <domain D>
D float32 mean (D uint16[[1]] data) {
    return _mean (data);
}
template <domain D>
D float32 mean (D uint32[[1]] data) {
    return _mean (data);
}
template <domain D>
D float64 mean (D uint64[[1]] data) {
    return _mean (data);
}
template <domain D>
D float32 mean (D float32[[1]] data) {
    return _mean (data);
}
template <domain D>
D float64 mean (D float64[[1]] data) {
    return _mean (data);
}
/** @} */

/** \addtogroup mean_filter
 *  @{
 *  @brief Find the mean of a filtered vector
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int8 "int8" \ref int16 "int16" \ref int32 "int32" \ref int64 "int64" \ref uint8 "uint8" \ref uint16 "uint16" \ref uint32 "uint32" \ref uint64 "uint64" \ref float32 "float32" \ref float64 "float64"
 *  @param data - input vector (the function may overflow if the input is too big)
 *  @param mask - mask indicating which elements of the input vector to include when computing the mean
 *  @return returns the mean of the filtered input vector
 *  @leakage{None}
 */
template <domain D>
D float32 mean (D int8[[1]] data, D bool[[1]] mask) {
    return _mean (data, mask);
}
template <domain D>
D float32 mean (D int16[[1]] data, D bool[[1]] mask) {
    return _mean (data, mask);
}
template <domain D>
D float32 mean (D int32[[1]] data, D bool[[1]] mask) {
    return _mean (data, mask);
}
template <domain D>
D float64 mean (D int64[[1]] data, D bool[[1]] mask) {
    return _mean (data, mask);
}
template <domain D>
D float32 mean (D uint8[[1]] data, D bool[[1]] mask) {
    return _mean (data, mask);
}
template <domain D>
D float32 mean (D uint16[[1]] data, D bool[[1]] mask) {
    return _mean (data, mask);
}
template <domain D>
D float32 mean (D uint32[[1]] data, D bool[[1]] mask) {
    return _mean (data, mask);
}
template <domain D>
D float64 mean (D uint64[[1]] data, D bool[[1]] mask) {
    return _mean (data, mask);
}
template <domain D>
D float32 mean (D float32[[1]] data, D bool[[1]] mask) {
    return _meanF (data, mask);
}
template <domain D>
D float64 mean (D float64[[1]] data, D bool[[1]] mask) {
    return _meanF (data, mask);
}
/** @} */

/** \addtogroup variance
 *  @{
 *  @brief Find the variance of a vector
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input vector (the function may overflow if the input is too big)
 *  @return returns the variance of the input vector
 *  @leakage{None}
 */
template <domain D>
D float32 variance (D int32[[1]] data) {
	D float32 meanValue = _mean (data);
    return _variance (data, meanValue);
}
template <domain D>
D float64 variance (D int64[[1]] data) {
	D float64 meanValue = _mean (data);
    return _variance (data, meanValue);
}
template <domain D>
D float32 variance (D float32[[1]] data) {
	D float32 meanValue = _mean (data);
    return _variance (data, meanValue);
}
template <domain D>
D float64 variance (D float64[[1]] data) {
	D float64 meanValue = _mean (data);
    return _variance (data, meanValue);
}
/** @} */

/** \addtogroup variance_filter
 *  @{
 *  @brief Find the variance of a filtered vector
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input vector (the function may overflow if the input is too big)
 *  @param mask - mask indicating which elements of the input vector to include when computing the variance
 *  @return returns the variance of the input vector
 *  @leakage{None}
 */
template <domain D>
D float32 variance (D int32[[1]] data, D bool[[1]] mask) {
    assert (shapesAreEqual (data, mask));
    data = data * (int32) mask;
	D float32 meanValue = _mean (data, (uint32) sum (mask));
    return _variance (data, mask, meanValue);
}

template <domain D>
D float64 variance (D int64[[1]] data, D bool[[1]] mask) {
    assert (shapesAreEqual (data, mask));
    data = data * (int64) mask;
	D float64 meanValue = _mean (data, sum (mask));
    return _variance (data, mask, meanValue);
}

template <domain D>
D float32 variance (D float32[[1]] data, D bool[[1]] mask) {
    assert (shapesAreEqual (data, mask));
    data = data * (float32) mask;
	D float32 meanValue = _mean (data, (float32) sum (mask));
    return _variance (data, mask, meanValue);
}

template <domain D>
D float64 variance (D float64[[1]] data, D bool[[1]] mask) {
    assert (shapesAreEqual (data, mask));
    data = data * (float64) mask;
	D float64 meanValue = _mean (data, (float64) sum (mask));
    return _variance (data, mask, meanValue);
}
/** @} */


/** \addtogroup standard_dev
 *  @{
 *  @brief Find the standard deviation of a sample
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input sample (the function may overflow if the input is too big)
 *  @return returns the standard deviation of the input sample
 *  @leakage{None}
 */
template <domain D>
D float32 standardDev (D int32[[1]] data) {
	D float32 var = variance (data);
	return sqrt (var);
}

template <domain D>
D float64 standardDev (D int64[[1]] data) {
	D float64 var = variance (data);
	return sqrt (var);
}

template <domain D>
D float32 standardDev (D float32[[1]] data) {
	D float32 var = variance (data);
	return sqrt (var);
}

template <domain D>
D float64 standardDev (D float64[[1]] data) {
	D float64 var = variance (data);
	return sqrt (var);
}
/** @} */


/** \addtogroup standard_dev_filter
 *  @{
 *  @brief Find the standard deviation of a filtered sample
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input sample (the function may overflow if the input is too big)
 *  @param mask - mask vector indicating which elements of the input
 *  sample to include when computing the standard deviation
 *  @return returns the standard deviation of the filtered input vector
 *  @leakage{None}
 */
template <domain D>
D float32 standardDev (D int32[[1]] data, D bool[[1]] mask){
    assert (shapesAreEqual (data, mask));
	D float32 var = variance (data, mask);
	return sqrt (var);
}

template <domain D>
D float64 standardDev (D int64[[1]] data, D bool[[1]] mask){
    assert (shapesAreEqual (data, mask));
	D float64 var = variance (data, mask);
	return sqrt (var);
}

template <domain D>
D float32 standardDev (D float32[[1]] data, D bool[[1]] mask){
    assert (shapesAreEqual (data, mask));
	D float32 var = variance (data, mask);
	return sqrt (var);
}

template <domain D>
D float64 standardDev (D float64[[1]] data, D bool[[1]] mask){
    assert (shapesAreEqual (data, mask));
	D float64 var = variance (data, mask);
	return sqrt (var);
}
/** @} */


/** \addtogroup mad
 *  @{
 *  @brief Find the median absolute deviation of a sample
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input sample
 *  @return returns the median absolute deviation of the input multiplied by 1.4826
 *  @leakage{None}
 */
template<domain D : shared3p>
D float32 MAD (D int32[[1]] data) {
    return _MAD (data);
}

template<domain D : shared3p>
D float64 MAD (D int64[[1]] data) {
    return _MAD (data);
}

template<domain D : shared3p>
D float32 MAD (D float32[[1]] data) {
    return _MAD (data);
}

template<domain D : shared3p>
D float64 MAD (D float64[[1]] data) {
    return _MAD (data);
}
/** @} */

/** \addtogroup mad_constant
 *  @{
 *  @brief Find the median absolute deviation of a sample
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input sample
 *  @param constant - scale factor
 *  @return returns the median absolute deviation of the input
 *  multiplied by the scale factor
 *  @leakage{None}
 */
template<domain D : shared3p>
D float32 MAD (D int32[[1]] data, float32 constant) {
    return _MAD (data, constant);
}

template<domain D : shared3p>
D float64 MAD (D int64[[1]] data, float64 constant) {
    return _MAD (data, constant);
}

template<domain D : shared3p>
D float32 MAD (D float32[[1]] data, float32 constant) {
    return _MAD (data, constant);
}

template<domain D : shared3p>
D float64 MAD (D float64[[1]] data, float64 constant) {
    return _MAD (data, constant);
}
/** @} */

/** \addtogroup mad_filter
 *  @{
 *  @brief Find the median absolute deviation of a filtered sample
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input sample
 *  @param mask - mask vector indicating which elements of the input
 *  sample to include when computing MAD
 *  @return returns the median absolute deviation of the filtered
 *  input multiplied by 1.4826
 *  @leakage{Leaks the number of true values in the mask}
 */
template<domain D : shared3p>
D float32 MAD (D int32[[1]] data, D bool[[1]] mask) {
    return _MAD (data, mask);
}

template<domain D : shared3p>
D float64 MAD (D int64[[1]] data, D bool[[1]] mask) {
    return _MAD (data, mask);
}

template<domain D : shared3p>
D float32 MAD (D float32[[1]] data, D bool[[1]] mask) {
    return _MAD (data, mask);
}

template<domain D : shared3p>
D float64 MAD (D float64[[1]] data, D bool[[1]] mask) {
    return _MAD (data, mask);
}
/** @} */

/** \addtogroup mad_filter_constant
 *  @{
 *  @brief Find the median absolute deviation of a filtered sample
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param data - input sample
 *  @param mask - mask vector indicating which elements of the input
 *  sample to include when computing MAD
 *  @param constant - scale factor
 *  @return returns the median absolute deviation of the filtered
 *  input multiplied by the scale factor
 *  @leakage{Leaks the number of true values in the mask}
 */
template<domain D : shared3p>
D float32 MAD (D int32[[1]] data, D bool[[1]] mask, float32 constant) {
    return _MAD (data, mask, constant);
}

template<domain D : shared3p>
D float64 MAD (D int64[[1]] data, D bool[[1]] mask, float64 constant) {
    return _MAD (data, mask, constant);
}

template<domain D : shared3p>
D float32 MAD (D float32[[1]] data, D bool[[1]] mask, float32 constant) {
    return _MAD (data, mask, constant);
}

template<domain D : shared3p>
D float64 MAD (D float64[[1]] data, D bool[[1]] mask, float64 constant) {
    return _MAD (data, mask, constant);
}
/** @} */

/** \cond */
template <domain D>
D float32[[1]] _sortHelper (D float32[[1]] x) {
    return quickquicksort (x);
}

template <domain D>
D float64[[1]] _sortHelper (D float64[[1]] x) {
    return quickquicksort (x);
}

template <domain D, type T>
D T[[1]] _sortHelper (D T[[1]] x) {
    return quicksort (x);
}

// This algorithm is Q7 from the article Sample Quantiles in Statistical Packages
template <domain D, type T, type FT>
D FT[[1]] _fiveNumberSummary (D T[[1]] data, D bool[[1]] isAvailable) {
	D FT [[1]] result (5);
	D T[[1]] cutDatabase = cut (data, isAvailable);
	D T[[1]] sortedDatabase = _sortHelper (cutDatabase);

 	uint sortedSize = size (sortedDatabase);

	if (sortedSize < 5){
		return result;
	}

 	result[0] = (FT) sortedDatabase [0]; // minimum
	result[4] = (FT) sortedDatabase [sortedSize - 1]; // maximum

	// We are using integer division.
	// Note that the formula gives us which position holds the value but we have to subtract 1 as our indices start from 0

	// Q_p = (1 - gamma) * X[j] + gamma * X[j + 1]
	// j = floor((n - 1) * p) + 1
	// gamma = (n-1) * p - floor((n - 1) * p)
	// gamma is the fraction part of ((n-1) * p)

	// lq(0.25), me(0.5), uq(0.75)
	uint[[1]] parA (3), parB (3), floorP (3), j (3);
	parA[0 : 2] = (sortedSize - 1);
	parA[2] = (sortedSize - 1) * 3;
	parB[0] = 4;
	parB[1] = 2;
	parB[2] = 4;
	floorP = parA / parB;
    j = floorP;


	FT[[1]] gammaA (3), gammaB (3), gammaRes (3), oneMinusGamma (3);
	gammaA[0 : 3] = (FT) (sortedSize - 1);
	gammaB[0] = 0.25;
	gammaB[1] = 0.5;
	gammaB[2] = 0.75;
	gammaRes = gammaA * gammaB;
	gammaRes = gammaRes - (FT) floorP;
	oneMinusGamma = 1.0 - gammaRes;

	D FT[[1]] floatParA (6), floatParB (6), floatParRes (6);
	for (uint i = 0; i < 3; i = i + 1) {
		floatParA[i] = oneMinusGamma[i];
		floatParA[i + 3] = gammaRes[i];
		floatParB[i] = (FT) sortedDatabase[(j[i])];
		if (j[i] + 1 >= size (sortedDatabase))
    		floatParB[i + 3] = (FT) sortedDatabase[(j[i])];
    	else
        	floatParB[i + 3] = (FT) sortedDatabase[(j[i] + 1)];
	}
	floatParRes = floatParA * floatParB;
	result[1 : 4] = floatParRes[0 : 3] + floatParRes[3 : 6];

	return result;
}
/** \endcond */

/** \addtogroup five_number_summary
 *  @{
 *  @brief Find the minimum, lower quartile, median, upper quartile and maximum of a sample
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @note A version of this function which hides the sample size was
 *  implemented for the paper "Going Beyond Millionaires:
 *  Privacy-Preserving Statistical Analysis" but is not included due
 *  to private integer division not working.
 *  @param data - input sample
 *  @param isAvailable - vector indicating which elements of the input sample are available
 *  @return returns a vector containing the minimum, lower quartile,
 *  median, upper quartile and maximum of the input sample (in that
 *  order). If the input size is less than five, a vector of zeroes is
 *  returned.
 *  @leakage{Leaks the number of true values in isAvailable}
 */
template<domain D>
D float32[[1]] fiveNumberSummary (D int32[[1]] data, D bool[[1]] isAvailable) {
    return _fiveNumberSummary (data, isAvailable);
}
template<domain D>
D float64[[1]] fiveNumberSummary (D int64[[1]] data, D bool[[1]] isAvailable) {
    return _fiveNumberSummary (data, isAvailable);
}
template<domain D>
D float32[[1]] fiveNumberSummary (D float32[[1]] data, D bool[[1]] isAvailable) {
    return _fiveNumberSummary (data, isAvailable);
}
template<domain D>
D float64[[1]] fiveNumberSummary (D float64[[1]] data, D bool[[1]] isAvailable) {
    return _fiveNumberSummary (data, isAvailable);
}
/** @} */

/** \addtogroup covariance
 *  @{
 *  @brief Estimate the Pearson covariance of two samples.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param sample1 - first sample
 *  @param sample2 - second sample
 *  are available
 *  @return returns the covariance
 *  @leakage{None}
 */
template<domain D : shared3p>
D float32 covariance (D int32[[1]] sample1, D int32[[1]] sample2) {
    return _covariance (sample1, sample2);
}

template<domain D : shared3p>
D float64 covariance (D int64[[1]] sample1, D int64[[1]] sample2) {
    return _covariance (sample1, sample2);
}

template<domain D : shared3p>
D float32 covariance (D float32[[1]] sample1, D float32[[1]] sample2) {
    return _covariance (sample1, sample2);
}

template<domain D : shared3p>
D float64 covariance (D float64[[1]] sample1, D float64[[1]] sample2) {
    return _covariance (sample1, sample2);
}
/** @} */

/** \addtogroup covariance_filter
 *  @{
 *  @brief Estimate the Pearson covariance of two samples.
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64" / \ref float32 "float32" / \ref float64 "float64"
 *  @param sample1 - first sample
 *  @param sample2 - second sample
 *  @param filter - filter indicating which elements of the samples
 *  are available
 *  @return returns the covariance
 *  @leakage{None}
 */
template<domain D : shared3p>
D float32 covariance (D int32[[1]] sample1, D int32[[1]] sample2, D bool[[1]] filter) {
    return _covariance (sample1, sample2, filter);
}

template<domain D : shared3p>
D float64 covariance (D int64[[1]] sample1, D int64[[1]] sample2, D bool[[1]] filter) {
    return _covariance (sample1, sample2, filter);
}

template<domain D : shared3p>
D float32 covariance (D float32[[1]] sample1, D float32[[1]] sample2, D bool[[1]] filter) {
    return _covariance (sample1, sample2, filter);
}

template<domain D : shared3p>
D float64 covariance (D float64[[1]] sample1, D float64[[1]] sample2, D bool[[1]] filter) {
    return _covariance (sample1, sample2, filter);
}
/** @} */

/** @} */

// Internal functions - not to be documented and exported

/**
 * \cond
 */

template <domain D, type FT>
D FT _meanF (D FT[[1]] data, D bool[[1]] mask) {
    assert (shapesAreEqual (data, mask));
    data = data * (FT) mask;
    return sum (data) / (FT) sum((uint64) mask);
}

template <domain D, type T, type FT>
D FT _mean (D T[[1]] data, D bool[[1]] mask) {
    assert (shapesAreEqual (data, mask));
    data = data * (T) mask;
    return (FT) sum (data) / (FT) sum ((uint64) mask);
}

template <domain D, type T, type FT>
D FT _mean (D T[[1]] data) {
	return (FT) sum (data) / (FT) size (data);
}

template <domain D, type FT>
D FT _meanF (D FT[[1]] data) {
	return sum (data) / (FT) size (data);
}

// This INTERNAL version of mean assumes that the data is already filtered
template <domain D, type T, type FT, type UT>
D FT _mean (D T[[1]] data, D UT count) {

	D T dataSum = sum (data);

	// Private division
	D FT result = (FT) dataSum / (FT) count;

	return result;
}

// This INTERNAL version of variance takes data without filters and a known mean value
template <domain D, type T, type FT>
D FT _variance (D T[[1]] data, D FT meanValue) {
    // Set up means
	D FT[[1]] means (size(data));
	means = meanValue;

    // Compute differences
	D FT[[1]] differences (size(data));
	differences = ((FT) data) - means;
	differences = differences * differences;

	D FT diffSum = sum (differences);

	// Private division
	D FT result = diffSum / (FT) (size(data) - 1);

	return result;
}

// This INTERNAL version of variance takes filtered data and a known mean value
template <domain D, type T, type FT>
D FT _variance (D T[[1]] data, D bool[[1]] mask, D FT meanValue) {
    // Apply filter to means
	D FT[[1]] means (size(data));
	means = (FT) meanValue;
    means = means * (FT) mask;

    // Compute differences
	D FT[[1]] differences (size (data));
	differences = ((FT) data) - means;
	differences = differences * differences;

	D FT diffSum = sum (differences);

	// Private division
	D FT result = diffSum / (FT) (sum ((uint32) mask) - 1);

	return result;
}

template<domain D : shared3p, type T>
D T[[1]] _summarySortHelper (D T[[1]] x) {
    return quicksort (x);
}

template<domain D : shared3p>
D float32[[1]] _summarySortHelper (D float32[[1]] x) {
    return sortingNetworkSort (x);
}

template<domain D : shared3p>
D float64[[1]] _summarySortHelper (D float64[[1]] x) {
    return sortingNetworkSort (x);
}

/* Data must be shuffled! */
template<domain D : shared3p, type T, type FT>
D FT _median (D T[[1]] data) {
    uint dataSize = size (data);
    D T[[1]] sortedData = _summarySortHelper (data);

    if (dataSize % 2 == 0) {
        uint j = dataSize / 2;
        return (FT) (sortedData[j - 1] + sortedData[j]) / 2.0;
    } else {
        return (FT) sortedData[dataSize / 2];
    }
}

template<domain D : shared3p, type T, type FT>
D FT _median (D T[[1]] data, D bool[[1]] isAvailable) {
    D T[[1]] cutData = cut (data, isAvailable);
    return _median (cutData);
}

template<domain D : shared3p, type T, type FT>
D FT _MAD (D T[[1]] data) {
    FT constant = 1.4826;
    return _MAD (data, constant);
}

template<domain D : shared3p, type T, type FT>
D FT _MAD (D T[[1]] data, FT constant) {
    D FT x = _median (data);
    D FT[[1]] deviation = abs ((FT) data - x);
    return _median (deviation) * constant;
}

template<domain D : shared3p, type T, type FT>
D FT _MAD (D T[[1]] data, D bool[[1]] isAvailable) {
    FT constant = 1.4826;
    return _MAD (data, isAvailable, constant);
}

template<domain D : shared3p, type T, type FT>
D FT _MAD (D T[[1]] data, D bool[[1]] isAvailable, FT constant) {
    D T[[1]] cutData = cut (data, isAvailable);
    return _MAD (cutData, constant);
}

template<domain D : shared3p, type T, type FT>
D FT _covariance (D T[[1]] sample1,
                  D T[[1]] sample2,
                  D bool[[1]] filter)
{
    assert (size (sample1) == size (sample2));
    assert (size (sample2) == size (filter));

    uint n = size (sample1);

    D T[[1]] filter_ = (T) filter;
    D T count = sum (filter_);

    D T[[1]] mulL (n * 2);
    D T[[1]] mulR (n * 2);
    mulL[:n] = sample1;
    mulL[n:] = sample2;

    mulR[:n] = filter_;
    mulR[n:] = filter_;

    mulL *= mulR;

    D T[[2]] mat (n, 3);
    mat[:, 0] = mulL[:n] * mulL[n:];
    mat[:, 1] = mulL[:n];
    mat[:, 2] = mulL[n:];

    D T[[1]] sums = colSums (mat);
    D FT a = (FT) (count - 1);
    D FT b = (FT) (count * (count - 1));

    return ((FT) sums[0] / a) - ((FT) (sums[1] * sums[2]) / b);
}

template<domain D : shared3p, type T, type FT>
D FT _covariance (D T[[1]] sample1, D T[[1]] sample2) {
    assert (size (sample1) == size (sample2));

    uint n = size (sample1);

    D T[[2]] mat (n, 3);
    mat[:, 0] = sample1 * sample2;
    mat[:, 1] = sample1;
    mat[:, 2] = sample2;

    D T[[1]] sums = colSums (mat);
    D FT a = (FT) (n - 1);
    D FT b = (FT) (n * (n - 1));

    return ((FT) sums[0] / a) - ((FT) (sums[1] * sums[2]) / b);
}
/**
 * \endcond
 */
